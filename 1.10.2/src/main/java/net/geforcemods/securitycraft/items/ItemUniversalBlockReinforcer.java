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
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.BlockSandStone;
import net.minecraft.block.BlockStone;
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
		super(2.0F, -5.0F, ToolMaterial.GOLD, getBreakableBlocks());

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
			Block block = world.getBlockState(pos).getBlock();

			if(block instanceof BlockDirt || block instanceof BlockGrass)
				world.setBlockState(pos, mod_SecurityCraft.reinforcedDirt.getDefaultState());
			else if(block instanceof BlockStone)
				world.setBlockState(pos, mod_SecurityCraft.reinforcedStone.getDefaultState());
			else if(block instanceof BlockPlanks)
				world.setBlockState(pos, player.getEntityWorld().getBlockState(pos).getActualState(player.getEntityWorld(), pos));
			else if(block instanceof BlockGlass)
				world.setBlockState(pos, mod_SecurityCraft.reinforcedGlass.getDefaultState());
			else if(block.getUnlocalizedName().equals(Blocks.COBBLESTONE.getUnlocalizedName())) //cobblestone doesn't have its own class
				world.setBlockState(pos, mod_SecurityCraft.reinforcedCobblestone.getDefaultState());
			else if(block.getUnlocalizedName().equals(Blocks.IRON_BARS.getUnlocalizedName())) //glass panes and iron bars share the same class
				world.setBlockState(pos, mod_SecurityCraft.unbreakableIronBars.getDefaultState());
			else if(block instanceof BlockSandStone)
				world.setBlockState(pos, player.getEntityWorld().getBlockState(pos).getActualState(player.getEntityWorld(), pos));
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
				Blocks.GLASS_PANE,
				Blocks.COBBLESTONE,
				Blocks.IRON_BARS,
				Blocks.SANDSTONE
		});
	}
}
