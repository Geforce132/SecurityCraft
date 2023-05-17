package net.geforcemods.securitycraft.api;

import java.util.List;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

/**
 * Allows a tile entity to check if an entity is looking at it
 */
public interface IViewActivated {
	/**
	 * Performs checks to determine whether an entity is looking at the tile entity
	 *
	 * @param world The level of the tile entity
	 * @param pos The position of the tile entity
	 */
	default void checkView(World world, BlockPos pos) {
		if (!world.isRemote) {
			if (getViewCooldown() > 0) {
				setViewCooldown(getViewCooldown() - 1);
				return;
			}

			double maximumDistance = getMaximumDistance();
			List<EntityLivingBase> entities = world.getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(pos).grow(maximumDistance));

			for (EntityLivingBase entity : entities) {
				double eyeHeight = entity.getEyeHeight();
				Vec3d lookVec = new Vec3d(entity.posX + (entity.getLookVec().x * maximumDistance), (eyeHeight + entity.posY) + (entity.getLookVec().y * maximumDistance), entity.posZ + (entity.getLookVec().z * maximumDistance));
				RayTraceResult mop = world.rayTraceBlocks(new Vec3d(entity.posX, entity.posY + entity.getEyeHeight(), entity.posZ), lookVec);

				if (mop != null && mop.typeOfHit == Type.BLOCK && mop.getBlockPos().getX() == pos.getX() && mop.getBlockPos().getY() == pos.getY() && mop.getBlockPos().getZ() == pos.getZ()) {
					if (onEntityViewed(entity, mop))
						setViewCooldown(getDefaultViewCooldown());
				}
			}
		}
	}

	/**
	 * @return The default amount of ticks to pass between two view checks
	 */
	public default int getDefaultViewCooldown() {
		return 30;
	}

	/**
	 * @return The amount of ticks left before the next view check is performed
	 */
	public int getViewCooldown();

	/**
	 * Sets the ticks left before the next view check
	 *
	 * @param viewCooldown The amount of ticks left before the next view check
	 */
	public void setViewCooldown(int viewCooldown);

	/**
	 * Called when a view check is successful, aka when an entity is looking at this tile entity
	 *
	 * @param entity The entity that is looking at this tile entity
	 * @param rayTraceResult The context with which the entity is looking at this block entity
	 * @return true if the block entity's view cooldown should be updated
	 */
	public boolean onEntityViewed(EntityLivingBase entity, RayTraceResult rayTraceResult);

	/**
	 * @return true if the view check should only pass if a player is looking at this tile entity, false otherwise
	 */
	public default boolean activatedOnlyByPlayer() {
		return true;
	}

	/**
	 * Returns the maximum distance from which a view check is performed. If an entity is further away than this distance, this
	 * block entity cannot be activated
	 *
	 * @return The maximum distance in blocks from which a view check is performed
	 */
	public double getMaximumDistance();
}
