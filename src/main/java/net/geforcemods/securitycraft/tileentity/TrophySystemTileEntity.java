package net.geforcemods.securitycraft.tileentity;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.entity.BulletEntity;
import net.geforcemods.securitycraft.entity.IMSBombEntity;
import net.geforcemods.securitycraft.entity.SentryEntity;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.network.client.SetTrophySystemTarget;
import net.geforcemods.securitycraft.network.server.SyncTrophySystem;
import net.geforcemods.securitycraft.util.ModuleUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.item.ExperienceBottleEntity;
import net.minecraft.entity.item.FireworkRocketEntity;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.DamagingProjectileEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.entity.projectile.PotionEntity;
import net.minecraft.entity.projectile.ThrowableEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.Explosion;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.fml.network.PacketDistributor;

public class TrophySystemTileEntity extends DisguisableTileEntity implements ITickableTileEntity {

	/* The range (in blocks) that the trophy system will search for projectiles in */
	public static final int RANGE = 10;

	/* The number of blocks away from the trophy system you can be for
	 * the laser beam between itself and the projectile to be rendered */
	public static final int RENDER_DISTANCE = 50;

	private final Map<EntityType<?>, Boolean> projectileFilter = new LinkedHashMap<>();
	public Entity entityBeingTargeted = null;
	public int cooldown = getCooldownTime();
	private final Random random = new Random();

	public TrophySystemTileEntity()
	{
		super(SCContent.teTypeTrophySystem);
		//when adding new types ONLY ADD TO THE END. anything else will break saved data.
		//ordering is done in TrophySystemScreen based on the user's current language
		projectileFilter.put(SCContent.eTypeBullet, true);
		projectileFilter.put(EntityType.SPECTRAL_ARROW, true);
		projectileFilter.put(EntityType.ARROW, true);
		projectileFilter.put(EntityType.SMALL_FIREBALL, true);
		projectileFilter.put(SCContent.eTypeImsBomb, true);
		projectileFilter.put(EntityType.FIREBALL, true);
		projectileFilter.put(EntityType.DRAGON_FIREBALL, true);
		projectileFilter.put(EntityType.WITHER_SKULL, true);
		projectileFilter.put(EntityType.SHULKER_BULLET, true);
		projectileFilter.put(EntityType.LLAMA_SPIT, true);
		projectileFilter.put(EntityType.EGG, true);
		projectileFilter.put(EntityType.ENDER_PEARL, true);
		projectileFilter.put(EntityType.SNOWBALL, true);
		projectileFilter.put(EntityType.FIREWORK_ROCKET, true);
		projectileFilter.put(EntityType.PIG, false); //modded projectiles
	}

	@Override
	public void tick() {
		if (!world.isRemote) {
			// If the trophy does not have a target, try looking for one
			if(entityBeingTargeted == null) {
				Entity target = getPotentialTarget();

				if(target != null) {
					Entity shooter = getShooter(target);

					if(shooter == null)
						setTarget(target);
					else
					{
						UUID uuid = shooter instanceof SentryEntity ? UUID.fromString(((SentryEntity)shooter).getOwner().getUUID()) : shooter.getUniqueID();
						String name = shooter instanceof SentryEntity ? ((SentryEntity)shooter).getOwner().getName() : shooter.getName().getString();

						//only allow targeting projectiles that were not shot by the owner or a player on the allowlist
						if(!((ConfigHandler.SERVER.enableTeamOwnership.get() && PlayerUtils.areOnSameTeam(shooter.getName().getString(), getOwner().getName()))
								|| (uuid != null && uuid.toString().equals(getOwner().getUUID()))
								|| ModuleUtils.isAllowed(this, name))) {
							setTarget(target);
						}
					}
				}
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

	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return new AxisAlignedBB(getPos()).grow(RENDER_DISTANCE);
	}

	@Override
	public CompoundNBT write(CompoundNBT tag) {
		super.write(tag);

		CompoundNBT projectilesNBT = new CompoundNBT();
		int i = 0;

		for (boolean b : projectileFilter.values()) {
			projectilesNBT.putBoolean("projectile" + i, b);
			i++;
		}

		tag.put("projectiles", projectilesNBT);
		return tag;
	}

	@Override
	public void read(CompoundNBT tag) {
		super.read(tag);

		if (tag.contains("projectiles", NBT.TAG_COMPOUND)) {
			CompoundNBT projectilesNBT = tag.getCompound("projectiles");
			int i = 0;

			for (EntityType<?> projectileType : projectileFilter.keySet()) {
				projectileFilter.put(projectileType, projectilesNBT.getBoolean("projectile" + i));
				i++;
			}
		}
	}

	public void setTarget(Entity target) {
		this.entityBeingTargeted = target;

		if (!world.isRemote) {
			SecurityCraft.channel.send(PacketDistributor.ALL.noArg(), new SetTrophySystemTarget(pos, target.getEntityId()));
		}
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
		cooldown = getCooldownTime();
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

		//remove bullets shot by sentries/IMSs of this trophy system's owner or players on the allowlist
		potentialTargets = potentialTargets.stream().filter(this::filterSCProjectiles).collect(Collectors.toList());

		// If there are no projectiles, return
		if(potentialTargets.size() <= 0) return null;

		// Return a random entity to target from the list of all possible targets
		int target = random.nextInt(potentialTargets.size());

		return potentialTargets.get(target);
	}

	private boolean isAllowedToTarget(Entity target) {
		if (target instanceof TridentEntity || target instanceof FishingBobberEntity || target instanceof PotionEntity || target instanceof ExperienceBottleEntity || target instanceof PigEntity)
			return false;

		if (projectileFilter.containsKey(target.getType()) || target instanceof IProjectile) {
			//try to get the target's type filter first. if not found, it's a modded projectile and the return value falls back to the modded filter (designated by the PIG entity type)
			return projectileFilter.getOrDefault(target.getType(), projectileFilter.get(EntityType.PIG));
		}

		return false;
	}

	private boolean filterSCProjectiles(Entity projectile) {
		Owner owner = null;

		if(projectile instanceof BulletEntity)
			owner = ((BulletEntity)projectile).getOwner();
		else if(projectile instanceof IMSBombEntity)
			owner = ((IMSBombEntity)projectile).getOwner();

		return owner == null || (!owner.owns(this) && !ModuleUtils.isAllowed(this, owner.getName()));
	}

	/**
	 * Returns the entity who shot the given Entity
	 */
	public Entity getShooter(Entity projectile) {
		Entity shooter = null;

		if(projectile instanceof AbstractArrowEntity) //arrows, spectral arrows and sentry bullets
			shooter = ((AbstractArrowEntity)projectile).getShooter();
		else if(projectile instanceof DamagingProjectileEntity) //small fireballs, fireballs, dragon fireballs, wither skulls and IMS bombs
			shooter = ((DamagingProjectileEntity) projectile).shootingEntity;
		else if (projectile instanceof FireworkRocketEntity)
			shooter = ((FireworkRocketEntity)projectile).boostedEntity;
		else if (projectile instanceof ThrowableEntity) //eggs, snowballs and ender pearls
			shooter = ((ThrowableEntity)projectile).getThrower();

		return shooter;
	}

	public void toggleFilter(EntityType<?> projectileType) {
		setFilter(projectileType, !projectileFilter.get(projectileType));
	}

	public void setFilter(EntityType<?> projectileType, boolean allowed) {
		if(projectileFilter.containsKey(projectileType))
		{
			projectileFilter.put(projectileType, allowed);

			if (world.isRemote) {
				SecurityCraft.channel.send(PacketDistributor.SERVER.noArg(), new SyncTrophySystem(pos, projectileType, allowed));
			}
		}
	}

	public boolean getFilter(EntityType<?> projectileType) {
		return projectileFilter.get(projectileType);
	}

	public Map<EntityType<?>,Boolean> getFilters()
	{
		return projectileFilter;
	}

	@Override
	public void onModuleRemoved(ItemStack stack, ModuleType module) {
		super.onModuleRemoved(stack, module);

		if (module == ModuleType.SMART) {
			for (EntityType<?> projectileType : projectileFilter.keySet()) {
				projectileFilter.put(projectileType, projectileType != EntityType.PIG);
			}
		}
	}

	@Override
	public ModuleType[] acceptedModules() {
		return new ModuleType[]{ModuleType.SMART, ModuleType.SPEED, ModuleType.ALLOWLIST, ModuleType.DISGUISE};
	}

	@Override
	public Option<?>[] customOptions() {
		return null;
	}

	/*
	 * @return The number of ticks that the trophy takes to "charge"
	 */
	public int getCooldownTime()
	{
		return hasModule(ModuleType.SPEED) ? 4 : 8;
	}
}
