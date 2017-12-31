package net.geforcemods.securitycraft.blocks.mines;

import java.util.Random;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.imc.waila.ICustomWailaDisplay;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

public class BlockFullMineBase extends BlockExplosive implements ICustomWailaDisplay {

	private final Block blockDisguisedAs;

	public BlockFullMineBase(Material par2Material, Block disguisedBlock) {
		super(par2Material);
		blockDisguisedAs = disguisedBlock;
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World par1World, int par2, int par3, int par4)
	{
		return null;
	}

	/**
	 * Triggered whenever an entity collides with this block (enters into the block). Args: world, x, y, z, entity
	 */
	@Override
	public void onEntityCollidedWithBlock(World par1World, int par2, int par3, int par4, Entity par5Entity){
		if(par1World.isRemote)
			return;
		else if(par5Entity instanceof EntityCreeper || par5Entity instanceof EntityOcelot || par5Entity instanceof EntityEnderman || par5Entity instanceof EntityItem)
			return;
		else if(par5Entity instanceof EntityLivingBase && !PlayerUtils.isPlayerMountedOnCamera((EntityLivingBase)par5Entity))
			explode(par1World, par2, par3, par4);
	}

	/**
	 * Called upon the block being destroyed by an explosion
	 */
	@Override
	public void onBlockDestroyedByExplosion(World par1World, int par2, int par3, int par4, Explosion par5Explosion)
	{
		if (!par1World.isRemote)
		{
			Random random = new Random();

			if(random.nextInt(3) == 1)
				explode(par1World, par2, par3, par4);
		}
	}

	@Override
	public void onBlockDestroyedByPlayer(World par1World, int par2, int par3, int par4, int par5){
		if (!par1World.isRemote)
			explode(par1World, par2, par3, par4);
	}

	@Override
	public void activateMine(World world, int par2, int par3, int par4) {}

	@Override
	public void defuseMine(World world, int par2, int par3, int par4) {}

	@Override
	public void explode(World world, int par2, int par3, int par4) {
		world.breakBlock(par2, par3, par4, false);

		if(SecurityCraft.config.smallerMineExplosion)
			world.createExplosion((Entity)null, par2, par3 + 0.5D, par4, 2.5F, true);
		else
			world.createExplosion((Entity)null, par2, par3 + 0.5D, par4, 5.0F, true);
	}

	/**
	 * Return whether this block can drop from an explosion.
	 */
	@Override
	public boolean canDropFromExplosion(Explosion par1Explosion)
	{
		return false;
	}

	@Override
	public boolean isActive(World world, int par2, int par3, int par4) {
		return true;
	}

	@Override
	public boolean isDefusable(){
		return false;
	}

	@Override
	public boolean explodesWhenInteractedWith() {
		return false;
	}

	@Override
	public ItemStack getDisplayStack(World world, int x, int y, int z) {
		return new ItemStack(blockDisguisedAs);
	}

	@Override
	public boolean shouldShowSCInfo(World world, int x, int y, int z) {
		return false;
	}
}