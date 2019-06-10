package net.geforcemods.securitycraft.items;

import net.geforcemods.securitycraft.SecurityCraft;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.BlockItem;

public class ItemReinforcedBlock extends BlockItem
{
	public ItemReinforcedBlock(Block block)
	{
		super(block, new Item.Properties().group(SecurityCraft.groupSCDecoration));

		setRegistryName(block.getRegistryName());
	}
}
