package net.geforcemods.securitycraft.blocks.reinforced;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import net.geforcemods.securitycraft.tileentity.TileEntityOwnable;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPane;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockReinforcedIronBars extends BlockPane implements ITileEntityProvider, IReinforcedBlock {

	public BlockReinforcedIronBars(Material par1Material, boolean par2) {
		super(par1Material, par2);
		ObfuscationReflectionHelper.setPrivateValue(Block.class, this, Block.soundTypeMetal, 33);
	}

	@Override
	public void updateTick(World par1World, BlockPos pos, IBlockState state, Random par5Random) {
		BlockUtils.setBlock(par1World, pos, Blocks.iron_bars);
	}

	@Override
	public void breakBlock(World par1World, BlockPos pos, IBlockState state){
		super.breakBlock(par1World, pos, state);
		par1World.removeTileEntity(pos);
	}

	@Override
	public boolean onBlockEventReceived(World par1World, BlockPos pos, IBlockState state, int par5, int par6){
		super.onBlockEventReceived(par1World, pos, state, par5, par6);
		TileEntity tileentity = par1World.getTileEntity(pos);
		return tileentity != null ? tileentity.receiveClientEvent(par5, par6) : false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Item getItem(World p_149694_1_, BlockPos pos){
		return Item.getItemFromBlock(this);
	}

	/**
	 * only called by clickMiddleMouseButton , and passed to inventory.setCurrentItem (along with isCreative)
	 */
	@Override
	@SideOnly(Side.CLIENT)
	public Item getItemDropped(IBlockState state, Random par2Random, int par3){
		return Item.getItemFromBlock(this);
	}

	@Override
	public TileEntity createNewTileEntity(World par1, int par2) {
		return new TileEntityOwnable();
	}

	@Override
	public List<Block> getVanillaBlocks()
	{
		return Arrays.asList(new Block[] {
				Blocks.iron_bars
		});
	}

	@Override
	public int getAmount()
	{
		return 1;
	}
}
