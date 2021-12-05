package net.geforcemods.securitycraft.api;

import java.util.List;

import net.geforcemods.securitycraft.util.EntityUtils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceContext.BlockMode;
import net.minecraft.util.math.RayTraceContext.FluidMode;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.util.math.vector.Vector3d;
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
		if(getViewCooldown() > 0){
			setViewCooldown(getViewCooldown() - 1);
			return;
		}

		List<LivingEntity> entities = world.getEntitiesWithinAABB(LivingEntity.class, new AxisAlignedBB(pos).grow(5), e -> !e.isSpectator() && !EntityUtils.isInvisible(e) && (!activatedOnlyByPlayer() || e instanceof PlayerEntity));

		for (LivingEntity entity : entities)
		{
			double eyeHeight = entity.getEyeHeight();
			Vector3d lookVec = new Vector3d(entity.getPosX() + (entity.getLookVec().x * 5), (eyeHeight + entity.getPosY()) + (entity.getLookVec().y * 5), entity.getPosZ() + (entity.getLookVec().z * 5));
			RayTraceResult rtr = world.rayTraceBlocks(new RayTraceContext(new Vector3d(entity.getPosX(), entity.getPosY() + entity.getEyeHeight(), entity.getPosZ()), lookVec, BlockMode.COLLIDER, FluidMode.NONE, entity));

			if(rtr != null && rtr.getType() == Type.BLOCK) {
				BlockRayTraceResult brtr = (BlockRayTraceResult)rtr;

				if(brtr.getPos().getX() == pos.getX() && brtr.getPos().getY() == pos.getY() && brtr.getPos().getZ() == pos.getZ()) {
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
