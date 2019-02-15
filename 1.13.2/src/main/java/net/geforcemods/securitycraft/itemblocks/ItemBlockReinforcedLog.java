package net.geforcemods.securitycraft.itemblocks;

import net.geforcemods.securitycraft.blocks.reinforced.BlockReinforcedOldLog;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemBlockReinforcedLog extends ItemBlock
{
	private Block block;

	public ItemBlockReinforcedLog(Block block)
	{
		super(block);

		setHasSubtypes(true);
		this.block = block;
	}

	@Override
	public int getMetadata(int meta)
	{
		return meta;
	}

	@Override
	public String getTranslationKey(ItemStack stack)
	{
		String name = getTranslationKey();

		switch(stack.getDamage())
		{
			case 0: return block instanceof BlockReinforcedOldLog ? name + "_oak" : name + "_acacia";
			case 1: return block instanceof BlockReinforcedOldLog ? name + "_spruce" : name + "_big_oak";
			case 2: return name + "_birch";
			case 3: return name + "_jungle";
			case 4: return block instanceof BlockReinforcedOldLog ? name + "_oak" : name + "_acacia";
			case 5: return block instanceof BlockReinforcedOldLog ? name + "_spruce" : name + "_big_oak";
			case 6: return name + "_birch";
			case 7: return name + "_jungle";
			case 8: return block instanceof BlockReinforcedOldLog ? name + "_oak" : name + "_acacia";
			case 9: return block instanceof BlockReinforcedOldLog ? name + "_spruce" : name + "_big_oak";
			case 10: return name + "_birch";
			case 11: return name + "_jungle";
			default: return name;
		}
	}
}
