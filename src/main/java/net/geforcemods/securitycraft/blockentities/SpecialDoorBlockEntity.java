package net.geforcemods.securitycraft.blockentities;

import java.util.List;

import net.geforcemods.securitycraft.api.ILinkedAction;
import net.geforcemods.securitycraft.api.ILockable;
import net.geforcemods.securitycraft.api.LinkableBlockEntity;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.DisabledOption;
import net.geforcemods.securitycraft.api.Option.IntOption;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;

public abstract class SpecialDoorBlockEntity extends LinkableBlockEntity implements ILockable {
	protected IntOption signalLength = new IntOption("signalLength", defaultSignalLength(), 0, 400, 5); //20 seconds max
	protected DisabledOption disabled = new DisabledOption(false);

	protected SpecialDoorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	@Override
	public void onOwnerChanged(BlockState state, Level level, BlockPos pos, Player player) {
		pos = state.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.UPPER ? pos.below() : pos.above();

		if (level.getBlockEntity(pos) instanceof SpecialDoorBlockEntity be && isLinkedWith(this, be)) {
			be.setOwner(getOwner().getUUID(), getOwner().getName());

			if (!level.isClientSide)
				level.getServer().getPlayerList().broadcastAll(be.getUpdatePacket());
		}

		super.onOwnerChanged(state, level, pos, player);
	}

	@Override
	protected void onLinkedBlockAction(ILinkedAction action, List<LinkableBlockEntity> excludedBEs) {
		if (action instanceof ILinkedAction.OptionChanged<?> optionChanged) {
			Option<?> option = optionChanged.option();

			for (Option<?> customOption : customOptions()) {
				if (customOption.getName().equals(option.getName())) {
					customOption.copy(option);
					break;
				}
			}

			setChanged();
		}
		else if (action instanceof ILinkedAction.ModuleInserted moduleInserted)
			insertModule(moduleInserted.stack(), moduleInserted.wasModuleToggled());
		else if (action instanceof ILinkedAction.ModuleRemoved moduleRemoved)
			removeModule(moduleRemoved.moduleType(), moduleRemoved.wasModuleToggled());
	}

	public int getSignalLength() {
		return signalLength.get();
	}

	public boolean isDisabled() {
		return disabled.get();
	}

	public abstract int defaultSignalLength();
}
