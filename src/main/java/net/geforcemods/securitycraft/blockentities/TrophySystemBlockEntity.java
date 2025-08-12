package net.geforcemods.securitycraft.blockentities;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.ILockable;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.DisabledOption;
import net.geforcemods.securitycraft.api.Option.IgnoreOwnerOption;
import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.entity.IMSBomb;
import net.geforcemods.securitycraft.entity.sentry.Bullet;
import net.geforcemods.securitycraft.entity.sentry.Sentry;
import net.geforcemods.securitycraft.inventory.InsertOnlyInvWrapper;
import net.geforcemods.securitycraft.inventory.LensContainer;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.network.client.SetTrophySystemTarget;
import net.geforcemods.securitycraft.network.server.SyncTrophySystem;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.IToggleableEntries;
import net.geforcemods.securitycraft.util.TeamUtils;
import net.minecraft.block.state.IBlockState;
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
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.IInventoryChangedListener;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

public class TrophySystemBlockEntity extends DisguisableBlockEntity implements ITickable, ILockable, IToggleableEntries<EntityEntry>, IInventoryChangedListener {
	/* The range (in blocks) that the trophy system will search for projectiles in */
	public static final int RANGE = 10;

	/*
	 * The number of blocks away from the trophy system you can be for the laser beam between itself and the projectile to be
	 * rendered
	 */
	public static final int RENDER_DISTANCE = 50;
	public static final EntityEntry MODDED_PROJECTILES = ForgeRegistries.ENTITIES.getValue(new ResourceLocation("minecraft:pig"));
	private final Map<EntityEntry, Boolean> projectileFilter = new LinkedHashMap<>();
	private Entity entityBeingTargeted = null;
	private int cooldown = getCooldownTime();
	private DisabledOption disabled = new DisabledOption(false);
	private IgnoreOwnerOption ignoreOwner = new IgnoreOwnerOption(true);
	private IItemHandler insertOnlyHandler, lensHandler;
	private LensContainer lens = new LensContainer(1);

	public TrophySystemBlockEntity() {
		lens.addInventoryChangeListener(this);
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
		if (isDisabled())
			return;

		// If the trophy does not have a target, try looking for one
		if (!world.isRemote && getTarget() == null) {
			Entity target = getPotentialTarget();

			if (target != null) {
				Entity shooter = getShooter(target);
				boolean shouldTarget = true;

				if (shooter != null) {
					UUID uuid = shooter instanceof Sentry ? UUID.fromString(((Sentry) shooter).getOwner().getUUID()) : shooter.getUniqueID();
					String name = shooter instanceof Sentry ? ((Sentry) shooter).getOwner().getName() : shooter.getName();

					if (uuid != null && uuid.toString().equals(getOwner().getUUID()))
						shouldTarget = !ignoresOwner();
					else if (isAllowed(name) || TeamUtils.areOnSameTeam(new Owner(shooter), getOwner()))
						shouldTarget = false;
				}

				if (shouldTarget)
					setTarget(target);
			}
		}

		// If there are no entities to target, return
		if (getTarget() == null)
			return;

		if (!getTarget().isEntityAlive()) {
			resetTarget();
			return;
		}

		// If the cooldown hasn't finished yet, don't destroy any projectiles
		if (cooldown > 0) {
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
		tag.setTag("lens", lens.getStackInSlot(0).writeToNBT(new NBTTagCompound()));
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

		lens.setInventorySlotContents(0, new ItemStack(tag.getCompoundTag("lens")));
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
			return BlockUtils.isAllowedToExtractFromProtectedObject(facing, this) ? (T) getNormalHandler() : (T) getInsertOnlyHandler();
		else
			return super.getCapability(capability, facing);
	}

	private IItemHandler getInsertOnlyHandler() {
		if (insertOnlyHandler == null)
			insertOnlyHandler = new InsertOnlyInvWrapper(lens);

		return insertOnlyHandler;
	}

	private IItemHandler getNormalHandler() {
		if (lensHandler == null)
			lensHandler = new InvWrapper(lens);

		return lensHandler;
	}

	public InventoryBasic getLensContainer() {
		return lens;
	}

	@Override
	public void onInventoryChanged(IInventory container) {
		if (world == null)
			return;

		IBlockState state = world.getBlockState(pos);

		markDirty();
		world.notifyBlockUpdate(pos, state, state, 2);
	}

	public void setTarget(Entity target) {
		entityBeingTargeted = target;

		if (!world.isRemote)
			SecurityCraft.network.sendToAllTracking(new SetTrophySystemTarget(pos, target.getEntityId()), new TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 0));
	}

	/**
	 * Deletes the targeted entity and creates a small explosion where it last was
	 */
	private void destroyTarget() {
		getTarget().setDead();

		if (!world.isRemote)
			world.createExplosion(null, getTarget().posX, getTarget().posY, getTarget().posZ, 0.1F, false);

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
	 * Randomly returns a new Entity target from the list of all entities within range of the trophy
	 */
	private Entity getPotentialTarget() {
		List<Entity> potentialTargets = new ArrayList<>();
		AxisAlignedBB area = new AxisAlignedBB(pos).grow(RANGE, RANGE, RANGE);

		potentialTargets.addAll(world.getEntitiesWithinAABB(Entity.class, area, this::isAllowedToTarget));

		//remove bullets/IMS bombs shot by sentries/IMS of this trophy system's owner or players on the allowlist
		potentialTargets = potentialTargets.stream().filter(this::filterSCProjectiles).collect(Collectors.toList());

		// If there are no projectiles, return
		if (potentialTargets.isEmpty())
			return null;

		// Return a random entity to target from the list of all possible targets
		int target = SecurityCraft.RANDOM.nextInt(potentialTargets.size());

		return potentialTargets.get(target);
	}

	private boolean isAllowedToTarget(Entity target) {
		if (target instanceof EntityFishHook || target instanceof EntityPotion || target instanceof EntityExpBottle || target instanceof EntityPig)
			return false;

		EntityEntry targetEntry = EntityRegistry.getEntry(target.getClass());

		if (projectileFilter.containsKey(targetEntry) || target instanceof IProjectile)
			//try to get the target's type filter first. if not found, it's a modded projectile and the return value falls back to the modded filter (designated by the PIG entity type)
			return projectileFilter.getOrDefault(targetEntry, projectileFilter.get(MODDED_PROJECTILES));

		return false;
	}

	private boolean filterSCProjectiles(Entity projectile) {
		Owner owner = null;

		if (projectile instanceof Bullet)
			owner = ((Bullet) projectile).getOwner();
		else if (projectile instanceof IMSBomb)
			owner = ((IMSBomb) projectile).getOwner();

		return owner == null || (!owner.owns(this) && !isAllowed(owner.getName()));
	}

	/**
	 * Returns the entity who shot the given projectile
	 */
	public EntityLivingBase getShooter(Entity projectile) {
		EntityLivingBase shooter = null;

		if (projectile instanceof EntityArrow)
			shooter = (EntityLivingBase) ((EntityArrow) projectile).shootingEntity;
		else if (projectile instanceof EntityFireball)
			shooter = ((EntityFireball) projectile).shootingEntity;
		else if (projectile instanceof EntityFireworkRocket)
			shooter = ((EntityFireworkRocket) projectile).boostedEntity;
		else if (projectile instanceof EntityThrowable)
			shooter = ((EntityThrowable) projectile).getThrower();

		return shooter;
	}

	@Override
	public void setFilter(EntityEntry projectileType, boolean allowed) {
		if (projectileFilter.containsKey(projectileType)) {
			projectileFilter.put(projectileType, allowed);

			if (world.isRemote)
				SecurityCraft.network.sendToServer(new SyncTrophySystem(pos, projectileType, allowed));
		}
	}

	@Override
	public boolean getFilter(EntityEntry projectileType) {
		return projectileFilter.get(projectileType);
	}

	@Override
	public Map<EntityEntry, Boolean> getFilters() {
		return projectileFilter;
	}

	@Override
	public String getTypeName(EntityEntry type) {
		return type.getName();
	}

	@Override
	public EntityEntry getDefaultType() {
		return MODDED_PROJECTILES;
	}

	@Override
	public String getDefaultTypeName() {
		return "gui.securitycraft:trophy_system.moddedProjectiles";
	}

	public boolean isDisabled() {
		return disabled.get();
	}

	@Override
	public boolean ignoresOwner() {
		return ignoreOwner.get();
	}

	@Override
	public void onModuleRemoved(ItemStack stack, ModuleType module, boolean toggled) {
		super.onModuleRemoved(stack, module, toggled);

		if (module == ModuleType.SMART) {
			for (EntityEntry projectileType : projectileFilter.keySet()) {
				projectileFilter.put(projectileType, projectileType != MODDED_PROJECTILES);
			}
		}
	}

	@Override
	public ModuleType[] acceptedModules() {
		return new ModuleType[] {
				ModuleType.SMART, ModuleType.SPEED, ModuleType.ALLOWLIST, ModuleType.DISGUISE
		};
	}

	@Override
	public Option<?>[] customOptions() {
		return new Option[] {
				disabled, ignoreOwner
		};
	}

	/*
	 * @return The number of ticks that the trophy takes to "charge"
	 */
	public int getCooldownTime() {
		return isModuleEnabled(ModuleType.SPEED) ? 4 : 8;
	}

	public Entity getTarget() {
		return entityBeingTargeted;
	}
}
