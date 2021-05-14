package net.geforcemods.securitycraft.tileentity;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.CustomizableSCTE;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.entity.EntityBullet;
import net.geforcemods.securitycraft.entity.EntityIMSBomb;
import net.geforcemods.securitycraft.misc.EnumModuleType;
import net.geforcemods.securitycraft.network.client.SetTrophySystemTarget;
import net.geforcemods.securitycraft.network.server.SyncTrophySystem;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.item.EntityExpBottle;
import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class TileEntityTrophySystem extends CustomizableSCTE implements ITickable {

	/* The range (in blocks) that the trophy system will search for projectiles in */
	public static final int RANGE = 10;

	/* Number of ticks that the trophy takes to "charge" */
	public static final int COOLDOWN_TIME = 8;

	/* The number of blocks away from the trophy system you can be for
	 * the laser beam between itself and the projectile to be rendered */
	public static final int RENDER_DISTANCE = 50;
	public static final EntityEntry MODDED_PROJECTILES = ForgeRegistries.ENTITIES.getValue(new ResourceLocation("minecraft:pig"));

	private final Map<EntityEntry, Boolean> projectileFilter = new LinkedHashMap<>();
	public Entity entityBeingTargeted = null;
	public int cooldown = COOLDOWN_TIME;
	private final Random random = new Random();

	public TileEntityTrophySystem() {
		//when adding new types ONLY ADD TO THE END. anything else will break saved data.
		//ordering is done in TrophySystemScreen based on the user's current language
		projectileFilter.put(ForgeRegistries.ENTITIES.getValue(new ResourceLocation(SecurityCraft.MODID, "bullet")), true);
		projectileFilter.put(ForgeRegistries.ENTITIES.getValue(new ResourceLocation("minecraft:spectral_arrow")), true);
		projectileFilter.put(ForgeRegistries.ENTITIES.getValue(new ResourceLocation("minecraft:arrow")), true);
		projectileFilter.put(ForgeRegistries.ENTITIES.getValue(new ResourceLocation("minecraft:small_fireball")), true);
		projectileFilter.put(ForgeRegistries.ENTITIES.getValue(new ResourceLocation(SecurityCraft.MODID, "imsbomb")), true);
		projectileFilter.put(ForgeRegistries.ENTITIES.getValue(new ResourceLocation("minecraft:fireball")), true);
		projectileFilter.put(ForgeRegistries.ENTITIES.getValue(new ResourceLocation("minecraft:dragon_fireball")), true);
		projectileFilter.put(ForgeRegistries.ENTITIES.getValue(new ResourceLocation("minecraft:wither_skull")), true);
		projectileFilter.put(ForgeRegistries.ENTITIES.getValue(new ResourceLocation("minecraft:shulker_bullet")), true);
		projectileFilter.put(ForgeRegistries.ENTITIES.getValue(new ResourceLocation("minecraft:llama_spit")), true);
		projectileFilter.put(ForgeRegistries.ENTITIES.getValue(new ResourceLocation("minecraft:egg")), true);
		projectileFilter.put(ForgeRegistries.ENTITIES.getValue(new ResourceLocation("minecraft:ender_pearl")), true);
		projectileFilter.put(ForgeRegistries.ENTITIES.getValue(new ResourceLocation("minecraft:snowball")), true);
		projectileFilter.put(ForgeRegistries.ENTITIES.getValue(new ResourceLocation("minecraft:fireworks_rocket")), true);
		projectileFilter.put(MODDED_PROJECTILES, false); //modded projectiles
	}

	@Override
	public void update() {
		// If the trophy does not have a target, try looking for one
		if (!world.isRemote) {
			//If the trophy does not have a target, try looking for one
			if (entityBeingTargeted == null) {
				Entity target = getPotentialTarget();

				if (target != null) {
					UUID shooterUUID = getShooterUUID(target);

					if (shooterUUID == null || !shooterUUID.toString().equals(getOwner().getUUID())) {
						setTarget(target);
					}
				}
			}
		}

		// If there are no entities to target, return
		if(entityBeingTargeted == null)
			return;

		if(!entityBeingTargeted.isEntityAlive())
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

	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return new AxisAlignedBB(getPos()).grow(RENDER_DISTANCE);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);

		NBTTagCompound projectilesNBT = new NBTTagCompound();
		int i = 0;

		for (boolean b : projectileFilter.values()) {
			projectilesNBT.setBoolean("projectile" + i, b);
			i++;
		}

		tag.setTag("projectiles", projectilesNBT);
		return tag;
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);

		if (tag.hasKey("projectiles", NBT.TAG_COMPOUND)) {
			NBTTagCompound projectilesNBT = tag.getCompoundTag("projectiles");
			int i = 0;

			for (EntityEntry projectileType : projectileFilter.keySet()) {
				projectileFilter.put(projectileType, projectilesNBT.getBoolean("projectile" + i));
				i++;
			}
		}
	}

	public void setTarget(Entity target) {
		this.entityBeingTargeted = target;
		if (!world.isRemote) {
			SecurityCraft.network.sendToAll(new SetTrophySystemTarget(pos, target.getEntityId()));
		}
	}

	/**
	 * Deletes the targeted entity and creates a small explosion where it last was
	 */
	private void destroyTarget() {
		entityBeingTargeted.setDead();

		if(!world.isRemote)
			world.createExplosion(null, entityBeingTargeted.posX, entityBeingTargeted.posY, entityBeingTargeted.posZ, 0.1F, false);

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
	private Entity getPotentialTarget() {
		List<Entity> potentialTargets = new ArrayList<>();
		AxisAlignedBB area = new AxisAlignedBB(pos).grow(RANGE, RANGE, RANGE);

		potentialTargets.addAll(world.getEntitiesWithinAABB(Entity.class, area, this::isAllowedToTarget));

		//remove bullets/IMS bombs shot by sentries/IMS of this trophy system's owner
		potentialTargets = potentialTargets.stream().filter(this::filterSCProjectiles).collect(Collectors.toList());

		// If there are no projectiles, return
		if(potentialTargets.size() <= 0) return null;

		// Return a random entity to target from the list of all possible targets
		int target = random.nextInt(potentialTargets.size());

		return potentialTargets.get(target);
	}

	private boolean isAllowedToTarget(Entity target) {
		if (target instanceof EntityFishHook || target instanceof EntityPotion || target instanceof EntityExpBottle || target instanceof EntityPig)
			return false;

		EntityEntry targetEntry = EntityRegistry.getEntry(target.getClass());

		if (projectileFilter.containsKey(targetEntry) || target instanceof IProjectile) {
			//try to get the target's type filter first. if not found, it's a modded projectile and the return value falls back to the modded filter (designated by the PIG entity type)
			return projectileFilter.getOrDefault(targetEntry, projectileFilter.get(MODDED_PROJECTILES));
		}

		return false;
	}

	private boolean filterSCProjectiles(Entity projectile) {
		if (projectile instanceof EntityBullet)
			return !((EntityBullet)projectile).getOwner().equals(getOwner());
		else if (projectile instanceof EntityIMSBomb)
			return !((EntityIMSBomb)projectile).getOwner().equals(getOwner());

		return true;
	}

	/**
	 * Returns the UUID of the player who shot the given Entity
	 */
	public UUID getShooterUUID(Entity projectile) {
		EntityLivingBase shooter = null;

		if(projectile instanceof EntityArrow) //arrows, spectral arrows and sentry bullets
			shooter = (EntityLivingBase)((EntityArrow) projectile).shootingEntity;
		if(projectile instanceof EntityFireball) //small fireballs, fireballs, dragon fireballs, wither skulls and IMS bombs
			shooter = ((EntityFireball) projectile).shootingEntity;
		else if (projectile instanceof EntityFireworkRocket)
			shooter = ((EntityFireworkRocket)projectile).boostedEntity;
		else if (projectile instanceof EntityThrowable) //eggs, snowballs and ender pearls
			shooter = ((EntityThrowable)projectile).getThrower();

		return shooter != null ? shooter.getUniqueID() : null;
	}

	public void toggleFilter(EntityEntry projectileType) {
		setFilter(projectileType, !projectileFilter.get(projectileType));
	}

	public void setFilter(EntityEntry projectileType, boolean allowed) {
		if(projectileFilter.containsKey(projectileType))
		{
			projectileFilter.put(projectileType, allowed);

			if (world.isRemote) {
				SecurityCraft.network.sendToServer(new SyncTrophySystem(pos, projectileType, allowed));
			}
		}
	}

	public boolean getFilter(EntityEntry projectileType) {
		return projectileFilter.get(projectileType);
	}

	public Map<EntityEntry,Boolean> getFilters()
	{
		return projectileFilter;
	}


	@Override
	public void onModuleRemoved(ItemStack stack, EnumModuleType module) {
		super.onModuleRemoved(stack, module);

		if (module == EnumModuleType.SMART) {
			for (EntityEntry projectileType : projectileFilter.keySet()) {
				projectileFilter.put(projectileType, projectileType != MODDED_PROJECTILES);
			}
		}
	}

	@Override
	public EnumModuleType[] acceptedModules() {
		return new EnumModuleType[]{EnumModuleType.SMART};
	}

	@Override
	public Option<?>[] customOptions() {
		return null;
	}

}
