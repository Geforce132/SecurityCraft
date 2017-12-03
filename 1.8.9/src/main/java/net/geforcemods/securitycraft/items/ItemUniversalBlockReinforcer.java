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
import net.minecraft.block.BlockNewLog;
import net.minecraft.block.BlockOldLog;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.BlockPrismarine;
import net.minecraft.block.BlockQuartz;
import net.minecraft.block.BlockRedSandstone;
import net.minecraft.block.BlockSandStone;
import net.minecraft.block.BlockStone;
import net.minecraft.block.BlockStoneBrick;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.util.BlockPos;
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
	public boolean onBlockStartBreak(ItemStack stack, BlockPos pos, EntityPlayer player)
	{
		if(!player.capabilities.isCreativeMode)
		{
			World world = player.getEntityWorld();
			Block block = world.getBlockState(pos).getBlock();

			if(block instanceof BlockDirt || block instanceof BlockGrass)
				world.setBlockState(pos, mod_SecurityCraft.reinforcedDirt.getDefaultState());
			else if(block instanceof BlockStone)
				world.setBlockState(pos, mod_SecurityCraft.reinforcedStone.getStateFromMeta(block.getDamageValue(world, pos)), 2);
			else if(block instanceof BlockPlanks)
				world.setBlockState(pos, mod_SecurityCraft.reinforcedWoodPlanks.getStateFromMeta(block.getDamageValue(world, pos)), 2);
			else if(block instanceof BlockGlass)
				world.setBlockState(pos, mod_SecurityCraft.reinforcedGlass.getDefaultState());
			else if(block.getUnlocalizedName().equals(Blocks.glass_pane.getUnlocalizedName())) //glass panes and iron bars share the same class
				world.setBlockState(pos, mod_SecurityCraft.reinforcedGlassPane.getDefaultState());
			else if(block.getUnlocalizedName().equals(Blocks.cobblestone.getUnlocalizedName())) //cobblestone doesn't have its own class
				world.setBlockState(pos, mod_SecurityCraft.reinforcedCobblestone.getDefaultState());
			else if(block.getUnlocalizedName().equals(Blocks.iron_bars.getUnlocalizedName())) //glass panes and iron bars share the same class
				world.setBlockState(pos, mod_SecurityCraft.unbreakableIronBars.getDefaultState());
			else if(block instanceof BlockSandStone)
				world.setBlockState(pos, mod_SecurityCraft.reinforcedSandstone.getStateFromMeta(block.getDamageValue(world, pos)), 2);
			else if(block instanceof BlockStoneBrick)
				world.setBlockState(pos, mod_SecurityCraft.reinforcedStoneBrick.getStateFromMeta(block.getDamageValue(world, pos)), 2);
			else if(block.getUnlocalizedName().equals(Blocks.mossy_cobblestone.getUnlocalizedName())) //mossy cobblestone doesn't have its own class
				world.setBlockState(pos, mod_SecurityCraft.reinforcedMossyCobblestone.getDefaultState());
			else if(block.getUnlocalizedName().equals(Blocks.brick_block.getUnlocalizedName())) //brick doesn't have its own class
				world.setBlockState(pos, mod_SecurityCraft.reinforcedBrick.getDefaultState());
			else if(block.getUnlocalizedName().equals(Blocks.nether_brick.getUnlocalizedName())) //nether brick doesn't have its own class
				world.setBlockState(pos, mod_SecurityCraft.reinforcedNetherBrick.getDefaultState());
			else if(block instanceof BlockHardenedClay)
				world.setBlockState(pos, mod_SecurityCraft.reinforcedHardenedClay.getDefaultState());
			else if(block.getUnlocalizedName().startsWith(Blocks.stained_hardened_clay.getUnlocalizedName()))
				world.setBlockState(pos, mod_SecurityCraft.reinforcedStainedHardenedClay.getStateFromMeta(block.getDamageValue(world, pos)), 2);
			else if(block instanceof BlockOldLog)
				world.setBlockState(pos, mod_SecurityCraft.reinforcedOldLogs.getStateFromMeta(block.getDamageValue(world, pos)), 2);
			else if(block instanceof BlockNewLog)
				world.setBlockState(pos, mod_SecurityCraft.reinforcedNewLogs.getStateFromMeta(block.getDamageValue(world, pos)), 2);
			else if(block.getUnlocalizedName().equals(Blocks.lapis_block.getUnlocalizedName()))
				world.setBlockState(pos, mod_SecurityCraft.reinforcedCompressedBlocks.getStateFromMeta(0), 2);
			else if(block.getUnlocalizedName().equals(Blocks.coal_block.getUnlocalizedName()))
				world.setBlockState(pos, mod_SecurityCraft.reinforcedCompressedBlocks.getStateFromMeta(1), 2);
			else if(block.getUnlocalizedName().equals(Blocks.gold_block.getUnlocalizedName()))
				world.setBlockState(pos, mod_SecurityCraft.reinforcedMetals.getStateFromMeta(0), 2);
			else if(block.getUnlocalizedName().equals(Blocks.iron_block.getUnlocalizedName()))
				world.setBlockState(pos, mod_SecurityCraft.reinforcedMetals.getStateFromMeta(1), 2);
			else if(block.getUnlocalizedName().equals(Blocks.diamond_block.getUnlocalizedName()))
				world.setBlockState(pos, mod_SecurityCraft.reinforcedMetals.getStateFromMeta(2), 2);
			else if(block.getUnlocalizedName().equals(Blocks.emerald_block.getUnlocalizedName()))
				world.setBlockState(pos, mod_SecurityCraft.reinforcedMetals.getStateFromMeta(3), 2);
			else if(block.getUnlocalizedName().startsWith(Blocks.wool.getUnlocalizedName()))
				world.setBlockState(pos, mod_SecurityCraft.reinforcedWool.getStateFromMeta(block.getDamageValue(world, pos)), 2);
			else if(block instanceof BlockQuartz)
				world.setBlockState(pos, mod_SecurityCraft.reinforcedQuartz.getStateFromMeta(block.getDamageValue(world, pos)), 2);
			else if(block instanceof BlockPrismarine)
				world.setBlockState(pos, mod_SecurityCraft.reinforcedPrismarine.getStateFromMeta(block.getDamageValue(world, pos)), 2);
			else if(block instanceof BlockRedSandstone)
				world.setBlockState(pos, mod_SecurityCraft.reinforcedRedSandstone.getStateFromMeta(block.getDamageValue(world, pos)), 2);
			else
			{
				world.destroyBlock(pos, true); //destroy the block without the ubr taking damage
				return true;
			}

			//the following only happens if a block has been changed, as the else statement terminates in itself
			((IOwnable)world.getTileEntity(pos)).getOwner().set(player.getGameProfile().getId().toString(), player.getCommandSenderName());
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
				Blocks.hardened_clay,
				Blocks.stained_hardened_clay,
				Blocks.log,
				Blocks.log2,
				Blocks.lapis_block,
				Blocks.coal_block,
				Blocks.gold_block,
				Blocks.iron_block,
				Blocks.diamond_block,
				Blocks.emerald_block,
				Blocks.wool,
				Blocks.quartz_block,
				Blocks.prismarine,
				Blocks.red_sandstone
		});
	}
}
