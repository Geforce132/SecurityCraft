package net.geforcemods.securitycraft.tileentity;

import java.util.ArrayList;
import java.util.function.Predicate;

import net.geforcemods.securitycraft.api.EnumLinkedAction;
import net.geforcemods.securitycraft.api.Option;
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

public abstract class TileEntitySpecialDoor extends TileEntityLinkable {
	private OptionBoolean sendMessage = new OptionBoolean("sendMessage", true);
	private OptionInt signalLength = new OptionInt(this::getPos, "signalLength", defaultSignalLength(), 0, 400, 5, true); //20 seconds max

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
	public void onModuleInserted(ItemStack stack, EnumModuleType module, boolean toggled) {
		super.onModuleInserted(stack, module, toggled);
		handleModule(stack, module, false, toggled);
	}

	@Override
	public void onModuleRemoved(ItemStack stack, EnumModuleType module, boolean toggled) {
		super.onModuleRemoved(stack, module, toggled);
		handleModule(stack, module, true, toggled);
	}

	private void handleModule(ItemStack stack, EnumModuleType module, boolean removed, boolean toggled) {
		EnumDoorHalf myHalf = world.getBlockState(pos).getValue(BlockDoor.HALF);
		BlockPos otherPos;

		if (myHalf == EnumDoorHalf.UPPER)
			otherPos = getPos().down();
		else
			otherPos = getPos().up();

		IBlockState other = world.getBlockState(otherPos);

		if (other.getValue(BlockDoor.HALF) != myHalf) {
			TileEntity otherTe = world.getTileEntity(otherPos);

			if (otherTe instanceof TileEntitySpecialDoor) {
				TileEntitySpecialDoor otherDoorTe = (TileEntitySpecialDoor) otherTe;
				Predicate<EnumModuleType> test = toggled ? otherDoorTe::isModuleEnabled : otherDoorTe::hasModule;
				boolean result = test.test(module);

				if (!removed && !result)
					otherDoorTe.insertModule(stack, toggled);
				else if (removed && result)
					otherDoorTe.removeModule(module, toggled);
			}
		}
	}

	@Override
	protected void onLinkedBlockAction(EnumLinkedAction action, Object[] parameters, ArrayList<TileEntityLinkable> excludedTEs) {
		if (action == EnumLinkedAction.OPTION_CHANGED) {
			Option<?> option = (Option<?>) parameters[0];

			if (option.getName().equals(sendMessage.getName()))
				sendMessage.copy(option);
			else if (option.getName().equals(signalLength.getName()))
				signalLength.copy(option);
		}
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
				sendMessage, signalLength
		};
	}

	public boolean sendsMessages() {
		return sendMessage.get();
	}

	public int getSignalLength() {
		return signalLength.get();
	}

	public abstract int defaultSignalLength();
}
