package net.geforcemods.securitycraft.tileentity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.OwnableTileEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.DamagingProjectileEntity;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.entity.projectile.ShulkerBulletEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.Explosion;

public class TrophySystemTileEntity extends OwnableTileEntity implements ITickableTileEntity {

	/* The range (in blocks) that the trophy system will search for projectiles in */
	public static final int RANGE = 10;
	/* Number of ticks that the trophy takes to "charge" */
	public static final int COOLDOWN_TIME = 8;
	public Entity entityBeingTargeted = null;
	public int cooldown = COOLDOWN_TIME;
	private final Random random = new Random();

	public TrophySystemTileEntity()
	{
		super(SCContent.teTypeTrophySystem);
	}

	@Override
	public void tick() {
		// If the trophy does not have a target, try looking for one
		if(entityBeingTargeted == null) {
			Entity target = getTarget();
			UUID shooterUUID = getShooterUUID(target);

			if(target != null && (shooterUUID == null || !shooterUUID.toString().equals(getOwner().getUUID()))) {
				entityBeingTargeted = target;
			}
		}

		// If there are no entities to target, return
		if(entityBeingTargeted == null)
			return;

		if(!entityBeingTargeted.isAlive())
		{
			resetTarget();
			return;
		}

		// If the cooldown hasn't finished yet, don't destroy any projectiles
		if(cooldown > 0) {
			cooldown--;
			return;
		}

		destroyTarget();
	}

	/**
	 * Deletes the targeted entity and creates a small explosion where it last was
	 */
	private void destroyTarget() {
		entityBeingTargeted.remove();

		if(!world.isRemote)
			world.createExplosion(null, entityBeingTargeted.getPosX(), entityBeingTargeted.getPosY(), entityBeingTargeted.getPosZ(), 0.1F, Explosion.Mode.NONE);

		resetTarget();
	}

	/**
	 * Resets the cooldown and targeted entity variables
	 */
	private void resetTarget() {
		cooldown = COOLDOWN_TIME;
		entityBeingTargeted = null;
	}

	/**
	 * Randomly returns a new Entity target from the list of all entities
	 * within range of the trophy
	 */
	private Entity getTarget() {
		List<Entity> potentialTargets = new ArrayList<>();
		AxisAlignedBB area = new AxisAlignedBB(pos).grow(RANGE, RANGE, RANGE);

		// Add all arrows and fireballs to the targets list. Could always add more
		// projectile types if we think of any
		potentialTargets.addAll(world.getEntitiesWithinAABB(AbstractArrowEntity.class, area, e -> !(e instanceof TridentEntity))); //ignore tridents
		potentialTargets.addAll(world.getEntitiesWithinAABB(DamagingProjectileEntity.class, area));
		potentialTargets.addAll(world.getEntitiesWithinAABB(ShulkerBulletEntity.class, area));

		// If there are no projectiles, return
		if(potentialTargets.size() <= 0) return null;

		// Return a random entity to target from the list of all possible targets
		int target = random.nextInt(potentialTargets.size());

		return potentialTargets.get(target);
	}

	/**
	 * Returns the UUID of the player who shot the given Entity
	 */
	public UUID getShooterUUID(Entity entity) {
		if(entity instanceof AbstractArrowEntity && ((AbstractArrowEntity) entity).shootingEntity != null)
			return ((AbstractArrowEntity) entity).shootingEntity;
		else if(entity instanceof FireballEntity && ((FireballEntity) entity).shootingEntity != null  && ((FireballEntity) entity).shootingEntity.getUniqueID() != null)
			return ((FireballEntity) entity).shootingEntity.getUniqueID();
		else
			return null;
	}

}
