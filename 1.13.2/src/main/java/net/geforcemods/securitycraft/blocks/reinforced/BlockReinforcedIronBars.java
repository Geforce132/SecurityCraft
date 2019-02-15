package net.geforcemods.securitycraft.blocks.reinforced;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import javafx.geometry.Side;
import net.geforcemods.securitycraft.tileentity.TileEntityOwnable;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPane;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockReinforcedIronBars extends BlockPane implements ITileEntityProvider, IReinforcedBlock {

	public BlockReinforcedIronBars(Material material, boolean par2) {
		super(material, par2);
		ObfuscationReflectionHelper.setPrivateValue(Block.class, this, SoundType.METAL, 16);
	}

	@Override
	public void updateTick(World world, BlockPos pos, IBlockState state, Random random) {
		BlockUtils.setBlock(world, pos, Blocks.IRON_BARS);
	}

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state){
		super.breakBlock(world, pos, state);
		world.removeTileEntity(pos);
	}

	@Override
	public boolean eventReceived(IBlockState state, World world, BlockPos pos, int id, int param){
		super.eventReceived(state, world, pos, id, param);
		TileEntity tileentity = world.getTileEntity(pos);
		return tileentity != null ? tileentity.receiveClientEvent(id, param) : false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public ItemStack getItem(World p_149694_1_, BlockPos pos, IBlockState state){
		return new ItemStack(Item.getItemFromBlock(this));
	}

	/**
	 * only called by clickMiddleMouseButton , and passed to inventory.setCurrentItem (along with isCreative)
	 */
	@Override
	@SideOnly(Side.CLIENT)
	public Item getItemDropped(IBlockState state, Random random, int par3){
		return Item.getItemFromBlock(this);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileEntityOwnable();
	}

	@Override
	public List<Block> getVanillaBlocks()
	{
		return Arrays.asList(new Block[] {
				Blocks.IRON_BARS
		});
	}

	@Override
	public int getAmount()
	{
		return 1;
	}
}
