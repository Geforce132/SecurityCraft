package net.geforcemods.securitycraft.items;

import net.geforcemods.securitycraft.SecurityCraft;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;

public class ReinforcedBlockItem extends BlockItem
{
	public ReinforcedBlockItem(Block block)
	{
		super(block, new Item.Properties().group(SecurityCraft.groupSCDecoration).isImmuneToFire());

		setRegistryName(block.getRegistryName());
	}
}
