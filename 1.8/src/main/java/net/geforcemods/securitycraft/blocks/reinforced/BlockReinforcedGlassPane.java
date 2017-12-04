package net.geforcemods.securitycraft.blocks.reinforced;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import net.geforcemods.securitycraft.tileentity.TileEntityOwnable;
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
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockReinforcedGlassPane extends BlockPane implements ITileEntityProvider, IReinforcedBlock {

	public BlockReinforcedGlassPane(Material par1Material, boolean par2) {
		super(par1Material, par2);
		ReflectionHelper.setPrivateValue(Block.class, this, true, 26);
	}

	@Override
	public void breakBlock(World p_149749_1_, BlockPos pos, IBlockState state){
		super.breakBlock(p_149749_1_, pos, state);
		p_149749_1_.removeTileEntity(pos);
	}

	@Override
	public boolean onBlockEventReceived(World p_149696_1_, BlockPos pos, IBlockState state, int p_149696_5_, int p_149696_6_){
		super.onBlockEventReceived(p_149696_1_, pos, state, p_149696_5_, p_149696_6_);
		TileEntity tileentity = p_149696_1_.getTileEntity(pos);
		return tileentity != null ? tileentity.receiveClientEvent(p_149696_5_, p_149696_6_) : false;
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
	public TileEntity createNewTileEntity(World var1, int var2) {
		return new TileEntityOwnable();
	}

	@Override
	public int quantityDropped(Random random)
	{
		return 1;
	}

	@Override
	public List<Block> getVanillaBlocks()
	{
		return Arrays.asList(new Block[] {
				Blocks.glass_pane
		});
	}

	@Override
	public int getAmount()
	{
		return 1;
	}
}
