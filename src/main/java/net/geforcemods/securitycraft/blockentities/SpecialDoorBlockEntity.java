package net.geforcemods.securitycraft.blockentities;

import java.util.List;

import net.geforcemods.securitycraft.api.ILinkedAction;
import net.geforcemods.securitycraft.api.ILockable;
import net.geforcemods.securitycraft.api.LinkableBlockEntity;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.DisabledOption;
import net.geforcemods.securitycraft.api.Option.IntOption;
import net.geforcemods.securitycraft.api.Owner;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class SpecialDoorBlockEntity extends LinkableBlockEntity implements ILockable {
	protected IntOption signalLength = new IntOption(this::getBlockPos, "signalLength", defaultSignalLength(), 0, 400, 5); //20 seconds max
	protected DisabledOption disabled = new DisabledOption(false);

	protected SpecialDoorBlockEntity(TileEntityType<?> type) {
		super(type);
	}

	@Override
	public void onOwnerChanged(BlockState state, World level, BlockPos pos, PlayerEntity player, Owner oldOwner, Owner newOwner) {
		TileEntity be;

		pos = state.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.UPPER ? pos.below() : pos.above();
		be = level.getBlockEntity(pos);

		if (be instanceof SpecialDoorBlockEntity && isLinkedWith(this, (SpecialDoorBlockEntity) be)) {
			((SpecialDoorBlockEntity) be).setOwner(getOwner().getUUID(), getOwner().getName());

			if (!level.isClientSide)
				level.getServer().getPlayerList().broadcastAll(be.getUpdatePacket());
		}

		super.onOwnerChanged(state, level, pos, player, oldOwner, newOwner);
	}

	@Override
	protected void onLinkedBlockAction(ILinkedAction action, List<LinkableBlockEntity> excludedBEs) {
		if (action instanceof ILinkedAction.OptionChanged) {
			Option<?> option = ((ILinkedAction.OptionChanged<?>) action).option;

			for (Option<?> customOption : customOptions()) {
				if (customOption.getName().equals(option.getName())) {
					customOption.copy(option);
					break;
				}
			}

			level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 2);
		}
		else if (action instanceof ILinkedAction.ModuleInserted) {
			ILinkedAction.ModuleInserted moduleInserted = (ILinkedAction.ModuleInserted) action;

			insertModule(moduleInserted.stack, moduleInserted.wasModuleToggled);
		}
		else if (action instanceof ILinkedAction.ModuleRemoved) {
			ILinkedAction.ModuleRemoved moduleRemoved = (ILinkedAction.ModuleRemoved) action;

			removeModule(moduleRemoved.moduleType, moduleRemoved.wasModuleToggled);
		}
	}

	public int getSignalLength() {
		return signalLength.get();
	}

	public boolean isDisabled() {
		return disabled.get();
	}

	public abstract int defaultSignalLength();
}
