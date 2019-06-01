package net.geforcemods.securitycraft.tileentity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.geforcemods.securitycraft.SCContent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.util.math.AxisAlignedBB;

public class TileEntityTrophySystem extends TileEntityOwnable {
	
	// The range (in blocks) that the trophy will search for projectiles in
	public static final int range = 10;
	// Number of ticks that the trophy takes to "charge"
	public static final int cooldownTime = 8;

	public Entity entityBeingTargeted = null;
	public int cooldown = cooldownTime;

	public TileEntityTrophySystem()
	{
		super(SCContent.teTypeTrophySystem);
	}
	
	public void tick() {		
		// If the trophy does not have a target, try looking for one
		if(entityBeingTargeted == null) {
			Entity target = getTarget();
			
			// second condition is disabled right now for testing
			if(target != null /*&& !target.shootingEntity.toString().equals(getOwner().getUUID())*/) {
				entityBeingTargeted = target;
			}
		}

		// If there are no entities to target, return
		if(entityBeingTargeted == null)
			return;
		
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
			world.createExplosion(null, entityBeingTargeted.posX, entityBeingTargeted.posY, entityBeingTargeted.posZ, 1.0F, true);
	
		resetTarget();
	}
	
	/**
	 * Resets the cooldown and targeted entity variables
	 */
	private void resetTarget() {
		cooldown = cooldownTime;
		entityBeingTargeted = null;
	}
	
	/**
	 * Randomly returns a new Entity target from the list of all entities
	 * within range of the trophy
	 */
	private Entity getTarget() {
		List<Entity> potentialTargets = new ArrayList<Entity>();
		AxisAlignedBB area = new AxisAlignedBB(pos).grow(range, range, range);
		
		// Add all arrows and fireballs to the targets list. Could always add more
		// projectile types if we think of any
		potentialTargets.addAll(world.getEntitiesWithinAABB(EntityArrow.class, area));
		potentialTargets.addAll(world.getEntitiesWithinAABB(EntityFireball.class, area));
		
		// If there are no projectiles, return
		if(potentialTargets.size() <= 0) return null;
		
		// Return a random entity to target from the list of all possible targets
		Random random = new Random();
		int target = random.nextInt(potentialTargets.size());

		return potentialTargets.get(target);
	}

}
