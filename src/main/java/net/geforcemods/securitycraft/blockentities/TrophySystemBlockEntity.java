package net.geforcemods.securitycraft.blockentities;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.ILockable;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.entity.Bullet;
import net.geforcemods.securitycraft.entity.IMSBomb;
import net.geforcemods.securitycraft.entity.Sentry;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.network.client.SetTrophySystemTarget;
import net.geforcemods.securitycraft.network.server.SyncTrophySystem;
import net.geforcemods.securitycraft.util.ITickingBlockEntity;
import net.geforcemods.securitycraft.util.ModuleUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ThrownExperienceBottle;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.fmllegacy.network.PacketDistributor;

public class TrophySystemBlockEntity extends DisguisableBlockEntity implements ITickingBlockEntity, ILockable {

	/* The range (in blocks) that the trophy system will search for projectiles in */
	public static final int RANGE = 10;

	/* The number of blocks away from the trophy system you can be for
	 * the laser beam between itself and the projectile to be rendered */
	public static final int RENDER_DISTANCE = 50;

	private final Map<EntityType<?>, Boolean> projectileFilter = new LinkedHashMap<>();
	public Projectile entityBeingTargeted = null;
	public int cooldown = getCooldownTime();
	private final Random random = new Random();

	public TrophySystemBlockEntity(BlockPos pos, BlockState state)
	{
		super(SCContent.beTypeTrophySystem, pos, state);
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
	public void tick(Level world, BlockPos pos, BlockState state)
	{
		if (!world.isClientSide) {
			// If the trophy does not have a target, try looking for one
			if(entityBeingTargeted == null) {
				Projectile target = getPotentialTarget(world, pos);

				if(target != null) {
					Entity shooter = target.getOwner();

					//only allow targeting projectiles that were not shot by the owner or a player on the allowlist
					if(!(shooter != null &&
							((ConfigHandler.SERVER.enableTeamOwnership.get() && PlayerUtils.areOnSameTeam(shooter.getName().getString(), getOwner().getName()))
									|| (shooter.getUUID() != null && shooter.getUUID().toString().equals(getOwner().getUUID()))
									|| ModuleUtils.isAllowed(this, shooter.getName().getString())))) {
						setTarget(target);
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
	public AABB getRenderBoundingBox() {
		return new AABB(getBlockPos()).inflate(RENDER_DISTANCE);
	}

	@Override
	public CompoundTag save(CompoundTag tag) {
		super.save(tag);

		CompoundTag projectilesNBT = new CompoundTag();
		int i = 0;

		for (boolean b : projectileFilter.values()) {
			projectilesNBT.putBoolean("projectile" + i, b);
			i++;
		}

		tag.put("projectiles", projectilesNBT);
		return tag;
	}

	@Override
	public void load(CompoundTag tag) {
		super.load(tag);

		if (tag.contains("projectiles", Tag.TAG_COMPOUND)) {
			CompoundTag projectilesNBT = tag.getCompound("projectiles");
			int i = 0;

			for (EntityType<?> projectileType : projectileFilter.keySet()) {
				projectileFilter.put(projectileType, projectilesNBT.getBoolean("projectile" + i));
				i++;
			}
		}
	}

	public void setTarget(Projectile target) {
		this.entityBeingTargeted = target;

		if (!level.isClientSide) {
			SecurityCraft.channel.send(PacketDistributor.ALL.noArg(), new SetTrophySystemTarget(worldPosition, target.getId()));
		}
	}

	/**
	 * Deletes the targeted entity and creates a small explosion where it last was
	 */
	private void destroyTarget() {
		entityBeingTargeted.remove(RemovalReason.KILLED);

		if(!level.isClientSide)
			level.explode(null, entityBeingTargeted.getX(), entityBeingTargeted.getY(), entityBeingTargeted.getZ(), 0.1F, Explosion.BlockInteraction.NONE);

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
	private Projectile getPotentialTarget(Level world, BlockPos pos) {
		List<Projectile> potentialTargets = new ArrayList<>();
		AABB area = new AABB(pos).inflate(RANGE);

		potentialTargets.addAll(world.getEntitiesOfClass(Projectile.class, area, this::isAllowedToTarget));

		//remove bullets shot by sentries/IMSs of this trophy system's owner or players on the allowlist
		potentialTargets = potentialTargets.stream().filter(this::filterSCProjectiles).collect(Collectors.toList());

		// If there are no projectiles, return
		if(potentialTargets.size() <= 0) return null;

		// Return a random entity to target from the list of all possible targets
		int target = random.nextInt(potentialTargets.size());

		return potentialTargets.get(target);
	}

	private boolean isAllowedToTarget(Projectile target) {
		if (target instanceof ThrownTrident || target instanceof FishingHook || target instanceof ThrownPotion || target instanceof ThrownExperienceBottle)
			return false;

		//try to get the target's type filter first. if not found, it's a modded projectile and the return value falls back to the modded filter (designated by the PIG entity type)
		return projectileFilter.getOrDefault(target.getType(), projectileFilter.get(EntityType.PIG));
	}

	private boolean filterSCProjectiles(Projectile projectile) {
		Owner owner = null;

		if(projectile instanceof Bullet bullet)
			owner = bullet.getSCOwner();
		else if(projectile instanceof IMSBomb imsBomb)
			owner = imsBomb.getSCOwner();
		else if(projectile.getOwner() instanceof Sentry sentry)
			owner = sentry.getOwner();

		return owner == null || (!owner.owns(this) && !ModuleUtils.isAllowed(this, owner.getName()));
	}

	public void toggleFilter(EntityType<?> projectileType) {
		setFilter(projectileType, !projectileFilter.get(projectileType));
	}

	public void setFilter(EntityType<?> projectileType, boolean allowed) {
		if(projectileFilter.containsKey(projectileType))
		{
			projectileFilter.put(projectileType, allowed);

			if (level.isClientSide) {
				SecurityCraft.channel.send(PacketDistributor.SERVER.noArg(), new SyncTrophySystem(worldPosition, projectileType, allowed));
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
