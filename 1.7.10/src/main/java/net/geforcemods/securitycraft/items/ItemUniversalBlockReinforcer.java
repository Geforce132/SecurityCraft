package net.geforcemods.securitycraft.items;

import java.util.HashSet;

import com.google.common.collect.Sets;

import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.gui.GuiHandler;
import net.geforcemods.securitycraft.main.mod_SecurityCraft;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.BlockGlass;
import net.minecraft.block.BlockGrass;
import net.minecraft.block.BlockHardenedClay;
import net.minecraft.block.BlockSandStone;
import net.minecraft.block.BlockStone;
import net.minecraft.block.BlockStoneBrick;
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
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
	{
		if(!world.isRemote)
			player.openGui(mod_SecurityCraft.MODID, GuiHandler.BLOCK_REINFORCER, world, (int)player.posX, (int)player.posY, (int)player.posZ);
		return super.onItemRightClick(stack, world, player);
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
			else if(block instanceof BlockStoneBrick)
				world.setBlock(x, y, z, mod_SecurityCraft.reinforcedStoneBrick, block.getDamageValue(world, x, y, z), 2);
			else if(block.getUnlocalizedName().equals(Blocks.mossy_cobblestone.getUnlocalizedName())) //mossy cobblestone doesn't have its own class
				world.setBlock(x, y, z, mod_SecurityCraft.reinforcedMossyCobblestone);
			else if(block.getUnlocalizedName().equals(Blocks.brick_block.getUnlocalizedName())) //brick doesn't have its own class
				world.setBlock(x, y, z, mod_SecurityCraft.reinforcedBrick);
			else if(block.getUnlocalizedName().equals(Blocks.nether_brick.getUnlocalizedName())) //nether brick doesn't have its own class
				world.setBlock(x, y, z, mod_SecurityCraft.reinforcedNetherBrick);
			else if(block.getUnlocalizedName().equals(Blocks.nether_brick.getUnlocalizedName())) //hardened clay doesn't have its own class
				world.setBlock(x, y, z, mod_SecurityCraft.reinforcedNetherBrick);
			else if(block instanceof BlockHardenedClay)
				world.setBlock(x, y, z, mod_SecurityCraft.reinforcedHardenedClay);
			else
			{
				world.func_147480_a(x, y, z, true); //destroy the block without the ubr taking damage
				return true;
			}
			
			//the following only happens if a block has been changed, as the else statement terminates in itself
			((IOwnable)world.getTileEntity(x, y, z)).getOwner().set(player.getGameProfile().getId().toString(), player.getCommandSenderName());
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
				Blocks.sandstone,
				Blocks.stonebrick,
				Blocks.mossy_cobblestone,
				Blocks.brick_block,
				Blocks.nether_brick,
				Blocks.hardened_clay
		});
	}
}
