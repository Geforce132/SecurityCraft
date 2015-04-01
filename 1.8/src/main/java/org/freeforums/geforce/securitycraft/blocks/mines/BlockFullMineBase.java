package org.freeforums.geforce.securitycraft.blocks.mines;

import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

import org.freeforums.geforce.securitycraft.interfaces.IHelpInfo;
import org.freeforums.geforce.securitycraft.interfaces.IIntersectable;
import org.freeforums.geforce.securitycraft.main.mod_SecurityCraft;
import org.freeforums.geforce.securitycraft.tileentity.TileEntityOwnable;

public class BlockFullMineBase extends BlockExplosive implements IIntersectable, IHelpInfo{

	private final String mineName;

	public BlockFullMineBase(Material par1Material, String mineName) {
		super(par1Material);
		this.mineName = mineName;
	}

	public int getRenderType(){
		return 3;
	}

	public AxisAlignedBB getCollisionBoundingBox(World par1World, BlockPos pos, IBlockState state){
		return null;
	}

	public void onEntityIntersected(World world, BlockPos pos, Entity entity){
		if(entity instanceof EntityItem){
			return;
		}else{
			this.explode(world, pos);
		}
	}

	/**
	 * Called upon the block being destroyed by an explosion
	 */
	public void onBlockDestroyedByExplosion(World par1World, BlockPos pos, Explosion par5Explosion){
		if (!par1World.isRemote)
		{
			this.explode(par1World, pos);
		}
	}

	public void onBlockDestroyedByPlayer(World par1World, BlockPos pos, IBlockState state){
		if (!par1World.isRemote)
		{
			this.explode(par1World, pos);
		}
	}	
	
	public void activateMine(World world, BlockPos pos) {}

	public void defuseMine(World world, BlockPos pos) {}

	public void explode(World par1World, BlockPos pos) {
		par1World.destroyBlock(pos, false);

		if(mod_SecurityCraft.configHandler.smallerMineExplosion){
			par1World.createExplosion((Entity)null, pos.getX(), (double) pos.getY() + 0.5D, pos.getZ(), 2.5F, true);
		}else{
			par1World.createExplosion((Entity)null, pos.getX(), (double) pos.getY() + 0.5D, pos.getZ(), 5.0F, true);
		}
	}

	/**
	 * Return whether this block can drop from an explosion.
	 */
	public boolean canDropFromExplosion(Explosion par1Explosion){
		return false;
	}

	/**
	 * Returns the quantity of items to drop on block destruction.
	 */
	public int quantityDropped(Random par1Random){
		return 1;
	}
	
	public boolean isActive(World world, BlockPos pos) {
		return true;
	}

	public boolean isDefusable() {
		return false;
	}

	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileEntityOwnable().intersectsEntities();
	}

	public String getHelpInfo() {
		return "The " + mineName + " mine is a standard block mine. Walking into it or mining it will cause it to explode.";
	}

	public String[] getRecipe() {
		if(mineName.matches("dirt")){
			return new String[]{"The dirt mine requires: 1 dirt, 1 mine. This is a shapeless recipe."};
		}else if(mineName.matches("stone")){
			return new String[]{"The stone mine requires: 1 stone, 1 mine. This is a shapeless recipe."};
		}else if(mineName.matches("cobblestone")){
			return new String[]{"The cobblestone mine requires: 1 cobblestone, 1 mine. This is a shapeless recipe."};
		}else if(mineName.matches("sand")){
			return new String[]{"The sand mine requires: 1 sand, 1 mine. This is a shapeless recipe."};
		}else if(mineName.matches("diamond ore")){
			return new String[]{"The diamond ore mine requires: 1 diamond ore (use a Silk Touch-enchanted pickaxe), 1 mine. This is a shapeless recipe."};
		}else{
			return null;
		}
	}

}