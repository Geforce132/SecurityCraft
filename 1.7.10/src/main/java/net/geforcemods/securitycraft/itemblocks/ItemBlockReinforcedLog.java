package net.geforcemods.securitycraft.itemblocks;

import net.geforcemods.securitycraft.blocks.reinforced.BlockReinforcedNewLog;
import net.geforcemods.securitycraft.blocks.reinforced.BlockReinforcedOldLog;
import net.minecraft.block.Block;
import net.minecraft.block.BlockNewLog;
import net.minecraft.block.BlockOldLog;
import net.minecraft.item.ItemBlockWithMetadata;
import net.minecraft.item.ItemStack;

public class ItemBlockReinforcedLog extends ItemBlockWithMetadata
{
	private Block block;

	public ItemBlockReinforcedLog(Block block)
	{
		super(block, block);

		this.block = block;
	}

	@Override
	public String getUnlocalizedName(ItemStack stack)
	{
		int dmg = stack.getItemDamage();

		if(block instanceof BlockReinforcedOldLog)
			return getUnlocalizedName() + "_" + BlockOldLog.field_150168_M[dmg & 3];
		else if(block instanceof BlockReinforcedNewLog)
			return getUnlocalizedName() + "_" + BlockNewLog.field_150169_M[dmg & 3];

		return getUnlocalizedName();
	}
}
