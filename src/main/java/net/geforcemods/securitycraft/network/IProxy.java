package net.geforcemods.securitycraft.network;

import net.geforcemods.securitycraft.items.CameraMonitorItem;
import net.geforcemods.securitycraft.tileentity.SecretSignTileEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;

public interface IProxy
{
	public void tint();
	public Player getClientPlayer();
	public void displayMRATGui(ItemStack stack);
	public void displaySRATGui(ItemStack stack, int viewDistance);
	public void displayEditModuleGui(ItemStack stack);
	public void displayCameraMonitorGui(Inventory inv, CameraMonitorItem item, CompoundTag stackTag);
	public void displaySCManualGui();
	public void displayEditSecretSignGui(SecretSignTileEntity te);
}
