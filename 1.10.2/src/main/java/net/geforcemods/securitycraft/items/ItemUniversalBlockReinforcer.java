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
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemUniversalBlockReinforcer extends ItemTool
{
	public ItemUniversalBlockReinforcer(int damage)
	{
		super(2.0F, 5.0F, ToolMaterial.GOLD, getBreakableBlocks());

		setMaxDamage(damage);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(ItemStack stack, World world, EntityPlayer player, EnumHand hand)
	{
		if(!world.isRemote)
			player.openGui(mod_SecurityCraft.MODID, GuiHandler.BLOCK_REINFORCER, world, (int)player.posX, (int)player.posY, (int)player.posZ);
		return super.onItemRightClick(stack, world, player, hand);
	}
	
	@Override
	public boolean onBlockStartBreak(ItemStack stack, BlockPos pos, EntityPlayer player)
	{
		if(!player.capabilities.isCreativeMode)
		{
			World world = player.getEntityWorld();
			IBlockState state = world.getBlockState(pos);
			Block block = state.getBlock();

			if(block instanceof BlockDirt || block instanceof BlockGrass)
				world.setBlockState(pos, mod_SecurityCraft.reinforcedDirt.getDefaultState());
			else if(block instanceof BlockStone)
				world.setBlockState(pos, mod_SecurityCraft.reinforcedStone.getDefaultState());
			else if(block instanceof BlockPlanks)
				world.setBlockState(pos, mod_SecurityCraft.reinforcedWoodPlanks.getStateFromMeta(block.getMetaFromState(block.getActualState(state, world, pos))));
			else if(block instanceof BlockGlass)
				world.setBlockState(pos, mod_SecurityCraft.reinforcedGlass.getDefaultState());
			else if(block.getUnlocalizedName().equals(Blocks.COBBLESTONE.getUnlocalizedName())) //cobblestone doesn't have its own class
				world.setBlockState(pos, mod_SecurityCraft.reinforcedCobblestone.getDefaultState());
			else if(block.getUnlocalizedName().equals(Blocks.IRON_BARS.getUnlocalizedName())) //glass panes and iron bars share the same class
				world.setBlockState(pos, mod_SecurityCraft.unbreakableIronBars.getDefaultState());
			else if(block instanceof BlockSandStone)
				world.setBlockState(pos, mod_SecurityCraft.reinforcedSandstone.getStateFromMeta(block.getMetaFromState(block.getActualState(state, world, pos))));
			else if(block instanceof BlockStoneBrick)
				world.setBlockState(pos, mod_SecurityCraft.reinforcedStoneBrick.getStateFromMeta(block.getMetaFromState(block.getActualState(state, world, pos))));
			else if(block.getUnlocalizedName().equals(Blocks.MOSSY_COBBLESTONE.getUnlocalizedName())) //mossy cobblestone doesn't have its own class
				world.setBlockState(pos, mod_SecurityCraft.reinforcedMossyCobblestone.getDefaultState());
			else if(block.getUnlocalizedName().equals(Blocks.BRICK_BLOCK.getUnlocalizedName())) //brick doesn't have its own class
				world.setBlockState(pos, mod_SecurityCraft.reinforcedBrick.getDefaultState());
			else if(block.getUnlocalizedName().equals(Blocks.NETHER_BRICK.getUnlocalizedName())) //nether brick doesn't have its own class
				world.setBlockState(pos, mod_SecurityCraft.reinforcedNetherBrick.getDefaultState());
			else if(block instanceof BlockHardenedClay)
				world.setBlockState(pos, mod_SecurityCraft.reinforcedHardenedClay.getDefaultState());
			else if(block.getUnlocalizedName().startsWith(Blocks.STAINED_HARDENED_CLAY.getUnlocalizedName()))
				world.setBlockState(pos, mod_SecurityCraft.reinforcedStainedHardenedClay.getStateFromMeta(block.getMetaFromState(block.getActualState(state, world, pos))));
			else if(block instanceof BlockOldLog)
				world.setBlockState(pos, mod_SecurityCraft.reinforcedOldLogs.getStateFromMeta(block.getMetaFromState(block.getActualState(state, world, pos))));
			else if(block instanceof BlockNewLog)
				world.setBlockState(pos, mod_SecurityCraft.reinforcedNewLogs.getStateFromMeta(block.getMetaFromState(block.getActualState(state, world, pos))));
			else if(block.getUnlocalizedName().equals(Blocks.LAPIS_BLOCK.getUnlocalizedName()))
				world.setBlockState(pos, mod_SecurityCraft.reinforcedCompressedBlocks.getStateFromMeta(0));
			else if(block.getUnlocalizedName().equals(Blocks.COAL_BLOCK.getUnlocalizedName()))
				world.setBlockState(pos, mod_SecurityCraft.reinforcedCompressedBlocks.getStateFromMeta(1));
			else if(block.getUnlocalizedName().equals(Blocks.GOLD_BLOCK.getUnlocalizedName()))
				world.setBlockState(pos, mod_SecurityCraft.reinforcedMetals.getStateFromMeta(0));
			else if(block.getUnlocalizedName().equals(Blocks.IRON_BLOCK.getUnlocalizedName()))
				world.setBlockState(pos, mod_SecurityCraft.reinforcedMetals.getStateFromMeta(1));
			else if(block.getUnlocalizedName().equals(Blocks.DIAMOND_BLOCK.getUnlocalizedName()))
				world.setBlockState(pos, mod_SecurityCraft.reinforcedMetals.getStateFromMeta(2));
			else if(block.getUnlocalizedName().equals(Blocks.EMERALD_BLOCK.getUnlocalizedName()))
				world.setBlockState(pos, mod_SecurityCraft.reinforcedMetals.getStateFromMeta(3));
			else if(block.getUnlocalizedName().startsWith(Blocks.WOOL.getUnlocalizedName()))
				world.setBlockState(pos, mod_SecurityCraft.reinforcedWool.getStateFromMeta(block.getMetaFromState(block.getActualState(state, world, pos))));
			else if(block instanceof BlockQuartz)
				world.setBlockState(pos, mod_SecurityCraft.reinforcedQuartz.getStateFromMeta(block.getMetaFromState(block.getActualState(state, world, pos))));
			else if(block instanceof BlockPrismarine)
				world.setBlockState(pos, mod_SecurityCraft.reinforcedPrismarine.getStateFromMeta(block.getMetaFromState(block.getActualState(state, world, pos))));
			else if(block instanceof BlockRedSandstone)
				world.setBlockState(pos, mod_SecurityCraft.reinforcedRedSandstone.getStateFromMeta(block.getMetaFromState(block.getActualState(state, world, pos))));
			else if(block.getUnlocalizedName().equals(Blocks.END_BRICKS.getUnlocalizedName()))
				world.setBlockState(pos, mod_SecurityCraft.reinforcedEndStoneBricks.getDefaultState());
			else if(block.getUnlocalizedName().equals(Blocks.RED_NETHER_BRICK.getUnlocalizedName()))
				world.setBlockState(pos, mod_SecurityCraft.reinforcedRedNetherBrick.getDefaultState());
			else if(block.getUnlocalizedName().equals(Blocks.PURPUR_BLOCK.getUnlocalizedName()))
				world.setBlockState(pos, mod_SecurityCraft.reinforcedPurpur.getDefaultState());
			else if(block.getUnlocalizedName().equals(Blocks.PURPUR_PILLAR.getUnlocalizedName()))
				world.setBlockState(pos, mod_SecurityCraft.reinforcedPurpur.getStateFromMeta(1));
			else
			{
				world.destroyBlock(pos, true); //destroy the block without the ubr taking damage
				return true;
			}
			
			//the following only happens if a block has been changed, as the else statement terminates in itself
			((IOwnable)world.getTileEntity(pos)).getOwner().set(player.getGameProfile().getId().toString(), player.getName());
			stack.damageItem(1, player);
			return true;
		}

		return false;
	}

	private static HashSet<Block> getBreakableBlocks()
	{
		return Sets.newHashSet(new Block[]{
				Blocks.DIRT,
				Blocks.GRASS,
				Blocks.STONE,
				Blocks.PLANKS,
				Blocks.GLASS,
				Blocks.COBBLESTONE,
				Blocks.IRON_BARS,
				Blocks.SANDSTONE,
				Blocks.STONEBRICK,
				Blocks.MOSSY_COBBLESTONE,
				Blocks.BRICK_BLOCK,
				Blocks.NETHER_BRICK,
				Blocks.HARDENED_CLAY,
				Blocks.STAINED_HARDENED_CLAY,
				Blocks.LOG,
				Blocks.LOG2,
				Blocks.LAPIS_BLOCK,
				Blocks.COAL_BLOCK,
				Blocks.GOLD_BLOCK,
				Blocks.IRON_BLOCK,
				Blocks.DIAMOND_BLOCK,
				Blocks.EMERALD_BLOCK,
				Blocks.WOOL,
				Blocks.QUARTZ_BLOCK,
				Blocks.PRISMARINE,
				Blocks.RED_SANDSTONE,
				Blocks.END_BRICKS,
				Blocks.RED_NETHER_BRICK,
				Blocks.PURPUR_BLOCK,
				Blocks.PURPUR_PILLAR
		});
	}
}
