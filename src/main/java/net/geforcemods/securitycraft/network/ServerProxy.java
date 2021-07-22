package net.geforcemods.securitycraft.network;

import net.geforcemods.securitycraft.items.CameraMonitorItem;
import net.geforcemods.securitycraft.tileentity.SecretSignTileEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;

public class ServerProxy implements IProxy
{
	@Override
	public void tint() {}

	@Override
	public Player getClientPlayer()
	{
		return null;
	}

	@Override
	public void displayMRATGui(ItemStack stack) {}

	@Override
	public void displaySRATGui(ItemStack stack, int viewDistance) {}

	@Override
	public void displayEditModuleGui(ItemStack stack) {}

	@Override
	public void displayCameraMonitorGui(Inventory inv, CameraMonitorItem item, CompoundTag stackTag) {}

	@Override
	public void displaySCManualGui() {}

	@Override
	public void displayEditSecretSignGui(SecretSignTileEntity te) {}
}
