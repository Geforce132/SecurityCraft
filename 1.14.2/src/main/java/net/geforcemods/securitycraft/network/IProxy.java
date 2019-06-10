package net.geforcemods.securitycraft.network;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent.Register;

public interface IProxy
{
	public void registerKeypadChestItem(Register<Item> event);
	public List<Block> getOrPopulateToTint();
	public void cleanup();
	public void registerKeybindings();
}
