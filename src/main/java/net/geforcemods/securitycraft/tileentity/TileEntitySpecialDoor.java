package net.geforcemods.securitycraft.tileentity;

import java.util.ArrayList;

import net.geforcemods.securitycraft.api.EnumLinkedAction;
import net.geforcemods.securitycraft.api.ILockable;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.DisabledOption;
import net.geforcemods.securitycraft.api.Option.OptionBoolean;
import net.geforcemods.securitycraft.api.Option.OptionInt;
import net.geforcemods.securitycraft.api.TileEntityLinkable;
import net.geforcemods.securitycraft.misc.EnumModuleType;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockDoor.EnumDoorHalf;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class TileEntitySpecialDoor extends TileEntityLinkable implements ILockable {
	private OptionBoolean sendMessage = new OptionBoolean("sendMessage", true);
	private OptionInt signalLength = new OptionInt(this::getPos, "signalLength", defaultSignalLength(), 0, 400, 5, true); //20 seconds max
	private DisabledOption disabled = new DisabledOption(false);

	@Override
	public void onOwnerChanged(IBlockState state, World world, BlockPos pos, EntityPlayer player) {
		TileEntity te;

		pos = state.getValue(BlockDoor.HALF) == EnumDoorHalf.UPPER ? pos.down() : pos.up();
		te = world.getTileEntity(pos);

		if (te instanceof TileEntitySpecialDoor && isLinkedWith(this, (TileEntitySpecialDoor) te)) {
			((TileEntitySpecialDoor) te).setOwner(getOwner().getUUID(), getOwner().getName());

			if (!world.isRemote)
				world.getMinecraftServer().getPlayerList().sendPacketToAllPlayers(te.getUpdatePacket());
		}
	}

	@Override
	protected void onLinkedBlockAction(EnumLinkedAction action, Object[] parameters, ArrayList<TileEntityLinkable> excludedTEs) {
		if (action == EnumLinkedAction.OPTION_CHANGED) {
			Option<?> option = (Option<?>) parameters[0];

			for (Option<?> customOption : customOptions()) {
				if (customOption.getName().equals(option.getName())) {
					customOption.copy(option);
					break;
				}
			}
		}
		else if (action == EnumLinkedAction.MODULE_INSERTED)
			insertModule((ItemStack) parameters[0], (boolean) parameters[2]);
		else if (action == EnumLinkedAction.MODULE_REMOVED)
			removeModule((EnumModuleType) parameters[1], (boolean) parameters[2]);
	}

	@Override
	public EnumModuleType[] acceptedModules() {
		return new EnumModuleType[] {
				EnumModuleType.ALLOWLIST
		};
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
