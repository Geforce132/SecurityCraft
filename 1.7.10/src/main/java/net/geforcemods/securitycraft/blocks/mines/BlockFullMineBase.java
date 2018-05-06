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

	public BlockFullMineBase(Material material, Block disguisedBlock) {
		super(material);
		blockDisguisedAs = disguisedBlock;
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z)
	{
		return null;
	}

	/**
	 * Triggered whenever an entity collides with this block (enters into the block). Args: world, x, y, z, entity
	 */
	@Override
	public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity){
		if(world.isRemote)
			return;
		else if(entity instanceof EntityCreeper || entity instanceof EntityOcelot || entity instanceof EntityEnderman || entity instanceof EntityItem)
			return;
		else if(entity instanceof EntityLivingBase && !PlayerUtils.isPlayerMountedOnCamera((EntityLivingBase)entity))
			explode(world, x, y, z);
	}

	/**
	 * Called upon the block being destroyed by an explosion
	 */
	@Override
	public void onBlockDestroyedByExplosion(World world, int x, int y, int z, Explosion explosion)
	{
		if (!world.isRemote)
		{
			Random random = new Random();

			if(random.nextInt(3) == 1)
				explode(world, x, y, z);
		}
	}

	@Override
	public void onBlockDestroyedByPlayer(World world, int x, int y, int z, int meta){
		if (!world.isRemote)
			explode(world, x, y, z);
	}

	@Override
	public void activateMine(World world, int x, int y, int z) {}

	@Override
	public void defuseMine(World world, int x, int y, int z) {}

	@Override
	public void explode(World world, int x, int y, int z) {
		world.breakBlock(x, y, z, false);

		if(SecurityCraft.config.smallerMineExplosion)
			world.createExplosion((Entity)null, x, y + 0.5D, z, 2.5F, true);
		else
			world.createExplosion((Entity)null, x, y + 0.5D, z, 5.0F, true);
	}

	/**
	 * Return whether this block can drop from an explosion.
	 */
	@Override
	public boolean canDropFromExplosion(Explosion explosion)
	{
		return false;
	}

	@Override
	public boolean isActive(World world, int x, int y, int z) {
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