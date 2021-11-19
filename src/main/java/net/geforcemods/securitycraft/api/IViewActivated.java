package net.geforcemods.securitycraft.api;

import java.util.List;

import net.geforcemods.securitycraft.util.EntityUtils;
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
	 * Performs checks to determine whether an entity is looking at the block entity
	 *
	 * @param level The level of the block entity
	 * @param pos The position of the block entity
	 */
	default void checkView(Level level, BlockPos pos) {
		if(getViewCooldown() > 0){
			setViewCooldown(getViewCooldown() - 1);
			return;
		}

		List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, new AABB(pos).inflate(5), e -> !e.isSpectator() && !EntityUtils.isInvisible(e) && (!activatedOnlyByPlayer() || e instanceof Player));

		for (LivingEntity entity : entities)
		{
			double eyeHeight = entity.getEyeHeight();
			Vec3 lookVec = new Vec3(entity.getX() + (entity.getLookAngle().x * 5), (eyeHeight + entity.getY()) + (entity.getLookAngle().y * 5), entity.getZ() + (entity.getLookAngle().z * 5));

			BlockHitResult mop = level.clip(new ClipContext(new Vec3(entity.getX(), entity.getY() + entity.getEyeHeight(), entity.getZ()), lookVec, Block.COLLIDER, Fluid.NONE, entity));

			if(mop != null)
			{
				if(mop.getBlockPos().getX() == pos.getX() && mop.getBlockPos().getY() == pos.getY() && mop.getBlockPos().getZ() == pos.getZ())
				{
					onEntityViewed(entity);
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
	 * Called when a view check is successful, aka when an entity is looking at this block entity
	 *
	 * @param entity The entity that is looking at this block entity
	 */
	public void onEntityViewed(LivingEntity entity);

	/**
	 * @return true if the view check should only pass if a player is looking at this block entity, false otherwise
	 */
	public default boolean activatedOnlyByPlayer() {
		return true;
	}
}
