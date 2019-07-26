package net.geforcemods.securitycraft.network;

import java.util.List;

import net.geforcemods.securitycraft.items.ItemCameraMonitor;
import net.geforcemods.securitycraft.tileentity.TileEntitySecretSign;
import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.World;
import net.minecraftforge.event.RegistryEvent.Register;

public interface IProxy
{
	public void clientSetup();
	public void registerKeypadChestItem(Register<Item> event);
	public List<Block> getOrPopulateToTint();
	public void cleanup();
	public void registerKeybindings();
	public World getClientWorld();
	public PlayerEntity getClientPlayer();
	public void displayMRATGui(ItemStack stack);
	public void displayEditModuleGui(ItemStack stack);
	public void displayCameraMonitorGui(PlayerInventory inv, ItemCameraMonitor item, CompoundNBT stackTag);
	public void displaySCManualGui();
	public void displayEditSecretSignGui(TileEntitySecretSign te);
}
