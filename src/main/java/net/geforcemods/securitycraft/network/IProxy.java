package net.geforcemods.securitycraft.network;

import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent.Register;

public interface IProxy
{
	public void registerKeypadChestItem(Register<Item> event);
	public Map<Block,Integer> getOrPopulateToTint();
	public void cleanup();
	public void clientSetup();
}
