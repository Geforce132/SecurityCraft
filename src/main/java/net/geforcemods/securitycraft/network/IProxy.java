package net.geforcemods.securitycraft.network;

import net.geforcemods.securitycraft.items.CameraMonitorItem;
import net.geforcemods.securitycraft.tileentity.SecretSignTileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.World;

public interface IProxy
{
	public void clientSetup();
	//	public void registerKeypadChestItem(Register<Item> event);
	public void tint();
	public World getClientWorld();
	public PlayerEntity getClientPlayer();
	public void displayMRATGui(ItemStack stack);
	public void displaySRATGui(ItemStack stack);
	public void displayEditModuleGui(ItemStack stack);
	public void displayCameraMonitorGui(PlayerInventory inv, CameraMonitorItem item, CompoundNBT stackTag);
	public void displaySCManualGui();
	public void displayEditSecretSignGui(SecretSignTileEntity te);
}
