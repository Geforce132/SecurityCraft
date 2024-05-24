package net.geforcemods.securitycraft.api;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

/**
 * Allows a block entity to check if an entity is looking at it
 */
public interface IViewActivated {
	/**
	 * Performs checks to determine whether an entity is looking at the block entity. Serverside only.
	 *
	 * @param level The level of the block entity
	 * @param pos The position of the block entity
	 */
	default void checkView(Level level, BlockPos pos) {
		if (getViewCooldown() > 0) {
			setViewCooldown(getViewCooldown() - 1);
			return;
		}

		double maximumDistance = getMaximumDistance();
		List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, new AABB(pos).inflate(maximumDistance), e -> e.canBeSeenByAnyone() && !isConsideredInvisible(e) && (!activatedOnlyByPlayer() || e instanceof Player));

		for (LivingEntity entity : entities) {
			double eyeHeight = entity.getEyeHeight();
			Vec3 lookVec = new Vec3(entity.getX() + (entity.getLookAngle().x * maximumDistance), (eyeHeight + entity.getY()) + (entity.getLookAngle().y * maximumDistance), entity.getZ() + (entity.getLookAngle().z * maximumDistance));
			BlockHitResult hitResult = level.clip(new ClipContext(new Vec3(entity.getX(), entity.getY() + entity.getEyeHeight(), entity.getZ()), lookVec, Block.COLLIDER, Fluid.NONE, entity));

			if (hitResult != null && hitResult.getBlockPos().getX() == pos.getX() && hitResult.getBlockPos().getY() == pos.getY() && hitResult.getBlockPos().getZ() == pos.getZ() && onEntityViewed(entity, hitResult))
				setViewCooldown(getDefaultViewCooldown());
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
	 * Called when a view check is successful, aka when an entity is looking at this block entity
	 *
	 * @param entity The entity that is looking at this block entity
	 * @param hitResult The context with which the entity is looking at this block entity
	 * @return true if the block entity's view cooldown should be updated
	 */
	public boolean onEntityViewed(LivingEntity entity, BlockHitResult hitResult);

	/**
	 * @return true if the view check should only pass if a player is looking at this block entity, false otherwise
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

	/**
	 * Returns whether the given entity is treated as being invisible. This does not necessarily need to match whether the entity
	 * has the invisibility effect
	 *
	 * @param entity The living entity to check
	 * @return true if the entity is considered invisible, false otherwise
	 */
	public boolean isConsideredInvisible(LivingEntity entity);
}
