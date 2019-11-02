package net.geforcemods.securitycraft.blocks.reinforced;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import cpw.mods.fml.common.ObfuscationReflectionHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.geforcemods.securitycraft.tileentity.TileEntityOwnable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPane;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockReinforcedIronBars extends BlockPane implements ITileEntityProvider, IReinforcedBlock {

	public BlockReinforcedIronBars(String frontIcon, String sideIcon, Material material, boolean drop) {
		super(frontIcon, sideIcon, material, drop);
		ObfuscationReflectionHelper.setPrivateValue(Block.class, this, Block.soundTypeMetal, 32);
	}

	@Override
	public boolean canEntityDestroy(IBlockAccess world, int x, int y, int z, Entity entity)
	{
		return !(entity instanceof EntityWither);
	}

	@Override
	public void updateTick(World world, int x, int y, int z, Random par5Random) {
		world.setBlock(x, y, z, Blocks.iron_bars);
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, Block par5Block, int par6){
		super.breakBlock(world, x, y, z, par5Block, par6);
		world.removeTileEntity(x, y, z);
	}

	@Override
	public boolean onBlockEventReceived(World world, int x, int y, int z, int par5, int par6){
		super.onBlockEventReceived(world, x, y, z, par5, par6);
		TileEntity tileentity = world.getTileEntity(x, y, z);
		return tileentity != null ? tileentity.receiveClientEvent(par5, par6) : false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Item getItem(World world, int x, int y, int z){
		return Item.getItemFromBlock(this);
	}

	/**
	 * only called by clickMiddleMouseButton , and passed to inventory.setCurrentItem (along with isCreative)
	 */
	@Override
	@SideOnly(Side.CLIENT)
	public Item getItemDropped(int meta, Random random, int fortune){
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
				Blocks.iron_bars
		});
	}

	@Override
	public int getAmount()
	{
		return 1;
	}
}
