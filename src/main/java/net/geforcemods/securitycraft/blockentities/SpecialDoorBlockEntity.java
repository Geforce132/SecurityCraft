package net.geforcemods.securitycraft.blockentities;

import java.util.ArrayList;

import net.geforcemods.securitycraft.api.ILockable;
import net.geforcemods.securitycraft.api.LinkableBlockEntity;
import net.geforcemods.securitycraft.api.LinkedAction;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.BooleanOption;
import net.geforcemods.securitycraft.api.Option.DisabledOption;
import net.geforcemods.securitycraft.api.Option.IntOption;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class SpecialDoorBlockEntity extends LinkableBlockEntity implements ILockable {
	private BooleanOption sendMessage = new BooleanOption("sendMessage", true);
	private IntOption signalLength = new IntOption(this::getBlockPos, "signalLength", defaultSignalLength(), 0, 400, 5, true); //20 seconds max
	private DisabledOption disabled = new DisabledOption(false);

	public SpecialDoorBlockEntity(TileEntityType<?> type) {
		super(type);
	}

	@Override
	public void onOwnerChanged(BlockState state, World world, BlockPos pos, PlayerEntity player) {
		TileEntity te;

		pos = state.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.UPPER ? pos.below() : pos.above();
		te = world.getBlockEntity(pos);

		if (te instanceof SpecialDoorBlockEntity && isLinkedWith(this, (SpecialDoorBlockEntity) te)) {
			((SpecialDoorBlockEntity) te).setOwner(getOwner().getUUID(), getOwner().getName());

			if (!world.isClientSide)
				world.getServer().getPlayerList().broadcastAll(te.getUpdatePacket());
		}
	}

	@Override
	protected void onLinkedBlockAction(LinkedAction action, Object[] parameters, ArrayList<LinkableBlockEntity> excludedTEs) {
		if (action == LinkedAction.OPTION_CHANGED) {
			Option<?> option = (Option<?>) parameters[0];

			for (Option<?> customOption : customOptions()) {
				if (customOption.getName().equals(option.getName())) {
					customOption.copy(option);
					break;
				}
			}

			level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 2);
		}
		else if (action == LinkedAction.MODULE_INSERTED)
			insertModule((ItemStack) parameters[0], (boolean) parameters[2]);
		else if (action == LinkedAction.MODULE_REMOVED)
			removeModule((ModuleType) parameters[1], (boolean) parameters[2]);
	}

	@Override
	public Option<?>[] customOptions() {
		return new Option[] {
				sendMessage, signalLength, disabled
		};
	}

	public boolean sendsMessages() {
		return sendMessage.get();
	}

	public int getSignalLength() {
		return signalLength.get();
	}

	public boolean isDisabled() {
		return disabled.get();
	}

	public abstract int defaultSignalLength();
}
