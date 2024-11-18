package net.geforcemods.securitycraft.blockentities;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import net.geforcemods.securitycraft.SCContent;
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
import net.geforcemods.securitycraft.inventory.TrophySystemMenu;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.network.client.SetTrophySystemTarget;
import net.geforcemods.securitycraft.network.server.SyncTrophySystem;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.IToggleableEntries;
import net.geforcemods.securitycraft.util.TeamUtils;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.ExperienceBottleEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.entity.projectile.PotionEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.IInventoryChangedListener;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.Explosion;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

public class TrophySystemBlockEntity extends DisguisableBlockEntity implements ITickableTileEntity, ILockable, IToggleableEntries<EntityType<?>>, INamedContainerProvider, IInventoryChangedListener {
	/** The range (in blocks) that the trophy system will search for projectiles in */
	public static final int RANGE = 10;
	/**
	 * The number of blocks away from the trophy system you can be for the laser beam between itself and the projectile to be
	 * rendered
	 */
	public static final int RENDER_DISTANCE = 50;
	private final Map<EntityType<?>, Boolean> projectileFilter = new LinkedHashMap<>();
	private ProjectileEntity entityBeingTargeted = null;
	private int cooldown = getCooldownTime();
	private DisabledOption disabled = new DisabledOption(false);
	private IgnoreOwnerOption ignoreOwner = new IgnoreOwnerOption(true);
	private LazyOptional<IItemHandler> insertOnlyHandler, lensHandler;
	private LensContainer lens = new LensContainer(1);

	public TrophySystemBlockEntity() {
		super(SCContent.TROPHY_SYSTEM_BLOCK_ENTITY.get());
		lens.addListener(this);
		//when adding new types ONLY ADD TO THE END. anything else will break saved data.
		//ordering is done in ToggleListScreen based on the user's current language
		projectileFilter.put(SCContent.BULLET_ENTITY.get(), true);
		projectileFilter.put(EntityType.SPECTRAL_ARROW, true);
		projectileFilter.put(EntityType.ARROW, true);
		projectileFilter.put(EntityType.SMALL_FIREBALL, true);
		projectileFilter.put(SCContent.IMS_BOMB_ENTITY.get(), true);
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
		if (isDisabled())
			return;

		// If the trophy does not have a target, try looking for one
		if (!level.isClientSide && getTarget() == null) {
			ProjectileEntity target = getPotentialTarget();

			if (target != null) {
				Entity shooter = target.getOwner();
				boolean shouldTarget = true;

				if (shooter != null) {
					if (shooter.getUUID() != null && shooter.getUUID().toString().equals(getOwner().getUUID()))
						shouldTarget = !ignoresOwner();
					else if (isAllowed(shooter.getName().toString()) || TeamUtils.areOnSameTeam(new Owner(shooter), getOwner()))
						shouldTarget = false;
				}

				if (shouldTarget)
					setTarget(target);
			}
		}

		// If there are no entities to target, return
		if (getTarget() == null)
			return;

		if (!getTarget().isAlive()) {
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
		return new AxisAlignedBB(getBlockPos()).inflate(RENDER_DISTANCE);
	}

	@Override
	public CompoundNBT save(CompoundNBT tag) {
		super.save(tag);

		CompoundNBT projectilesNBT = new CompoundNBT();
		int i = 0;

		for (boolean b : projectileFilter.values()) {
			projectilesNBT.putBoolean("projectile" + i, b);
			i++;
		}

		tag.put("projectiles", projectilesNBT);
		tag.put("lens", lens.createTag());
		return tag;
	}

	@Override
	public void load(BlockState state, CompoundNBT tag) {
		super.load(state, tag);

		if (tag.contains("projectiles", NBT.TAG_COMPOUND)) {
			CompoundNBT projectilesNBT = tag.getCompound("projectiles");
			int i = 0;

			for (EntityType<?> projectileType : projectileFilter.keySet()) {
				projectileFilter.put(projectileType, projectilesNBT.getBoolean("projectile" + i));
				i++;
			}
		}

		lens.fromTag(tag.getList("lens", Constants.NBT.TAG_COMPOUND));
	}

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
			return BlockUtils.isAllowedToExtractFromProtectedObject(side, this) ? getNormalHandler().cast() : getInsertOnlyHandler().cast();
		else
			return super.getCapability(cap, side);
	}

	@Override
	public void invalidateCaps() {
		if (insertOnlyHandler != null)
			insertOnlyHandler.invalidate();

		if (lensHandler != null)
			lensHandler.invalidate();

		super.invalidateCaps();
	}

	@Override
	public void reviveCaps() {
		insertOnlyHandler = null;
		lensHandler = null;
		super.reviveCaps();
	}

	private LazyOptional<IItemHandler> getInsertOnlyHandler() {
		if (insertOnlyHandler == null)
			insertOnlyHandler = LazyOptional.of(() -> new InsertOnlyInvWrapper(lens));

		return insertOnlyHandler;
	}

	private LazyOptional<IItemHandler> getNormalHandler() {
		if (lensHandler == null)
			lensHandler = LazyOptional.of(() -> new InvWrapper(lens));

		return lensHandler;
	}

	public Inventory getLensContainer() {
		return lens;
	}

	@Override
	public void containerChanged(IInventory container) {
		if (level == null)
			return;

		level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 2);
	}

	@Override
	public Container createMenu(int id, PlayerInventory inventory, PlayerEntity player) {
		return new TrophySystemMenu(id, level, worldPosition, inventory);
	}

	@Override
	public ITextComponent getDisplayName() {
		return super.getDisplayName();
	}

	public void setTarget(ProjectileEntity target) {
		entityBeingTargeted = target;

		if (!level.isClientSide)
			SecurityCraft.channel.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(worldPosition)), new SetTrophySystemTarget(worldPosition, target.getId()));
	}

	/**
	 * Deletes the targeted entity and creates a small explosion where it last was
	 */
	private void destroyTarget() {
		getTarget().remove();

		if (!level.isClientSide)
			level.explode(null, getTarget().getX(), getTarget().getY(), getTarget().getZ(), 0.1F, Explosion.Mode.NONE);

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
	private ProjectileEntity getPotentialTarget() {
		List<ProjectileEntity> potentialTargets = new ArrayList<>();
		AxisAlignedBB area = new AxisAlignedBB(worldPosition).inflate(RANGE, RANGE, RANGE);

		potentialTargets.addAll(level.getEntitiesOfClass(ProjectileEntity.class, area, this::isAllowedToTarget));
		//remove bullets shot by sentries/IMSs of this trophy system's owner or players on the allowlist
		potentialTargets = potentialTargets.stream().filter(this::filterSCProjectiles).collect(Collectors.toList());

		// If there are no projectiles, return
		if (potentialTargets.isEmpty())
			return null;

		// Return a random entity to target from the list of all possible targets
		int target = SecurityCraft.RANDOM.nextInt(potentialTargets.size());

		return potentialTargets.get(target);
	}

	private boolean isAllowedToTarget(ProjectileEntity target) {
		if (target instanceof TridentEntity || target instanceof FishingBobberEntity || target instanceof PotionEntity || target instanceof ExperienceBottleEntity)
			return false;

		//try to get the target's type filter first. if not found, it's a modded projectile and the return value falls back to the modded filter (designated by the PIG entity type)
		return projectileFilter.getOrDefault(target.getType(), projectileFilter.get(EntityType.PIG));
	}

	private boolean filterSCProjectiles(ProjectileEntity projectile) {
		Owner owner = null;

		if (projectile instanceof Bullet)
			owner = ((Bullet) projectile).getSCOwner();
		else if (projectile instanceof IMSBomb)
			owner = ((IMSBomb) projectile).getSCOwner();
		else if (projectile.getOwner() instanceof Sentry)
			owner = ((Sentry) projectile.getOwner()).getOwner();

		return owner == null || (!owner.owns(this) && !isAllowed(owner.getName()));
	}

	@Override
	public void setFilter(EntityType<?> projectileType, boolean allowed) {
		if (projectileFilter.containsKey(projectileType)) {
			projectileFilter.put(projectileType, allowed);

			if (level.isClientSide)
				SecurityCraft.channel.sendToServer(new SyncTrophySystem(worldPosition, projectileType, allowed));
		}
	}

	@Override
	public boolean getFilter(EntityType<?> projectileType) {
		return projectileFilter.get(projectileType);
	}

	@Override
	public Map<EntityType<?>, Boolean> getFilters() {
		return projectileFilter;
	}

	@Override
	public EntityType<?> getDefaultType() {
		return EntityType.PIG;
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
			for (EntityType<?> projectileType : projectileFilter.keySet()) {
				projectileFilter.put(projectileType, projectileType != EntityType.PIG);
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
	 * @return The number of ticks that the trophy takes to charge
	 */
	public int getCooldownTime() {
		return isModuleEnabled(ModuleType.SPEED) ? 4 : 8;
	}

	public ProjectileEntity getTarget() {
		return entityBeingTargeted;
	}
}
