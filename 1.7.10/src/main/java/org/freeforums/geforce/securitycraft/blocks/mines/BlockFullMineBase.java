package org.freeforums.geforce.securitycraft.blocks.mines;

import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

import org.freeforums.geforce.securitycraft.api.IHelpInfo;
import org.freeforums.geforce.securitycraft.main.mod_SecurityCraft;

public class BlockFullMineBase extends BlockExplosive implements IHelpInfo {

	private final String mineName;

	public BlockFullMineBase(Material par2Material, String mineName) {
		super(par2Material);
		this.mineName = mineName;
	}

	public AxisAlignedBB getCollisionBoundingBoxFromPool(World par1World, int par2, int par3, int par4)
	{
		return null;
	}

	/**
	 * Triggered whenever an entity collides with this block (enters into the block). Args: world, x, y, z, entity
	 */
	public void onEntityCollidedWithBlock(World par1World, int par2, int par3, int par4, Entity par5Entity){
		if(par1World.isRemote){
			return;
		}else{

			if(par5Entity instanceof EntityCreeper || par5Entity instanceof EntityOcelot || par5Entity instanceof EntityEnderman || par5Entity instanceof EntityItem){
				return;
			}else{
				this.explode(par1World, par2, par3, par4);
			}

		}


	}

	/**
	 * Called upon the block being destroyed by an explosion
	 */
	public void onBlockDestroyedByExplosion(World par1World, int par2, int par3, int par4, Explosion par5Explosion)
	{
		if (!par1World.isRemote)
		{
			this.explode(par1World, par2, par3, par4);
		}
	}

	public void onBlockDestroyedByPlayer(World par1World, int par2, int par3, int par4, int par5){
		if (!par1World.isRemote)
		{
			this.explode(par1World, par2, par3, par4);
		}
	}	

	public void activateMine(World world, int par2, int par3, int par4) {}

	public void defuseMine(World world, int par2, int par3, int par4) {}

	public void explode(World world, int par2, int par3, int par4) {
		world.func_147480_a(par2, par3, par4, false);

		if(mod_SecurityCraft.configHandler.smallerMineExplosion){
			world.createExplosion((Entity)null, par2, (double)par3 + 0.5D, par4, 2.5F, true);
		}else{
			world.createExplosion((Entity)null, par2, (double)par3 + 0.5D, par4, 5.0F, true);
		}
	}

	/**
	 * Return whether this block can drop from an explosion.
	 */
	public boolean canDropFromExplosion(Explosion par1Explosion)
	{
		return false;
	}
	
	public boolean isActive(World world, int par2, int par3, int par4) {
		return true;
	}
	
	public boolean isDefusable(){
		return false;
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