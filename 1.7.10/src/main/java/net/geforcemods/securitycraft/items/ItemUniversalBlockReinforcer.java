package net.geforcemods.securitycraft.items;

import java.util.HashSet;

import com.google.common.collect.Sets;

import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.main.mod_SecurityCraft;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.BlockGlass;
import net.minecraft.block.BlockGrass;
import net.minecraft.block.BlockSandStone;
import net.minecraft.block.BlockStone;
import net.minecraft.block.BlockWood;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.world.World;

public class ItemUniversalBlockReinforcer extends ItemTool
{
	public ItemUniversalBlockReinforcer(int damage)
	{
		super(2.0F, ToolMaterial.GOLD, getBreakableBlocks());

		setMaxDamage(damage);
	}

	@Override
	public boolean onBlockStartBreak(ItemStack stack, int x, int y, int z, EntityPlayer player)
	{
		if(!player.capabilities.isCreativeMode)
		{
			World world = player.getEntityWorld();
			Block block = world.getBlock(x, y, z);

			if(block instanceof BlockDirt || block instanceof BlockGrass)
				world.setBlock(x, y, z, mod_SecurityCraft.reinforcedDirt);
			else if(block instanceof BlockStone)
				world.setBlock(x, y, z, mod_SecurityCraft.reinforcedStone);
			else if(block instanceof BlockWood)
				world.setBlock(x, y, z, mod_SecurityCraft.reinforcedWoodPlanks, block.getDamageValue(world, x, y, z), 2);
			else if(block instanceof BlockGlass)
				world.setBlock(x, y, z, mod_SecurityCraft.reinforcedGlass);
			else if(block.getUnlocalizedName().equals(Blocks.glass_pane.getUnlocalizedName())) //glass panes and iron bars share the same class
				world.setBlock(x, y, z, mod_SecurityCraft.reinforcedGlassPane);
			else if(block.getUnlocalizedName().equals(Blocks.cobblestone.getUnlocalizedName())) //cobblestone doesn't have its own class
				world.setBlock(x, y, z, mod_SecurityCraft.reinforcedCobblestone);
			else if(block.getUnlocalizedName().equals(Blocks.iron_bars.getUnlocalizedName())) //glass panes and iron bars share the same class
				world.setBlock(x, y, z, mod_SecurityCraft.unbreakableIronBars);
			else if(block instanceof BlockSandStone)
				world.setBlock(x, y, z, mod_SecurityCraft.reinforcedSandstone, block.getDamageValue(world, x, y, z), 2);
			else
			{
				world.func_147480_a(x, y, z, true); //destroy the block without the ubr taking damage
				return true;
			}
			
			//the following only happens if a block has been changed, as the else statement terminates in itself
			((IOwnable)world.getTileEntity(x, y, z)).setOwner(player.getGameProfile().getId().toString(), player.getCommandSenderName());
			stack.damageItem(1, player);
			return true;
		}

		return false;
	}

	private static HashSet<Block> getBreakableBlocks()
	{
		return Sets.newHashSet(new Block[]{
				Blocks.dirt,
				Blocks.grass,
				Blocks.stone,
				Blocks.planks,
				Blocks.glass,
				Blocks.glass_pane,
				Blocks.cobblestone,
				Blocks.iron_bars,
				Blocks.sandstone
		});
	}
}
