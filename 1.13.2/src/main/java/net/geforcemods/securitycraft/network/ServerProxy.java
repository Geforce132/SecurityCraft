package net.geforcemods.securitycraft.network;

import java.util.ArrayList;
import java.util.List;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.event.RegistryEvent.Register;

public class ServerProxy implements IProxy
{
	@Override
	public void registerKeypadChestItem(Register<Item> event)
	{
		event.getRegistry().register(new ItemBlock(SCContent.keypadChest, new Item.Properties().group(SecurityCraft.groupSCTechnical)).setRegistryName(SCContent.keypadChest.getRegistryName()));
	}

	@Override
	public List<Block> getOrPopulateToTint()
	{
		return new ArrayList<>();
	}

	@Override
	public void cleanup() {}

	@Override
	public void registerKeybindings() {}
}
