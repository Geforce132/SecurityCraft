package net.geforcemods.securitycraft.entity.sentry;

import java.util.List;
import java.util.Optional;
import java.util.Random;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IEMPAffected;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.blockentities.DisguisableBlockEntity;
import net.geforcemods.securitycraft.blocks.DisguisableBlock;
import net.geforcemods.securitycraft.blocks.SometimesVisibleBlock;
import net.geforcemods.securitycraft.items.ModuleItem;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.misc.TargetingMode;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.material.PushReaction;
import net.minecraft.dispenser.IDispenseItemBehavior;
import net.minecraft.dispenser.ProjectileDispenseBehavior;
import net.minecraft.dispenser.ProxyBlockSource;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.Pose;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class Sentry extends CreatureEntity implements IRangedAttackMob, IEMPAffected, IOwnable { //needs to be a creature so it can target a player, ai is also only given to living entities
	private static final DataParameter<Owner> OWNER = EntityDataManager.<Owner>defineId(Sentry.class, Owner.getSerializer());
	private static final DataParameter<CompoundNBT> ALLOWLIST = EntityDataManager.<CompoundNBT>defineId(Sentry.class, DataSerializers.COMPOUND_TAG);
	private static final DataParameter<Boolean> HAS_SPEED_MODULE = EntityDataManager.<Boolean>defineId(Sentry.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Integer> MODE = EntityDataManager.<Integer>defineId(Sentry.class, DataSerializers.INT);
	private static final DataParameter<Boolean> HAS_TARGET = EntityDataManager.<Boolean>defineId(Sentry.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Boolean> SHUT_DOWN = EntityDataManager.<Boolean>defineId(Sentry.class, DataSerializers.BOOLEAN);
	public static final DataParameter<Float> HEAD_ROTATION = EntityDataManager.<Float>defineId(Sentry.class, DataSerializers.FLOAT);
	public static final float MAX_TARGET_DISTANCE = 20.0F;
	private static final float ANIMATION_STEP_SIZE = 0.025F;
	private static final float UPWARDS_ANIMATION_LIMIT = 0.025F;
	private static final float DOWNWARDS_ANIMATION_LIMIT = 0.9F;
	private float headYTranslation = 0.9F;
	private float oHeadYTranslation = 0.9F;
	private boolean animateUpwards = false;
	private boolean animate = false;
	private float headRotation;
	private float oHeadRotation;
	private boolean hasReceivedEntityData = false;
	/**
	 * @deprecated Only used for upgrading old sentries
	 */
	@Deprecated
	private ItemStack oldModule = ItemStack.EMPTY;

	public Sentry(EntityType<? extends Sentry> type, World level) {
		super(type, level);
	}

	public Sentry(World level) {
		this(SCContent.SENTRY_ENTITY.get(), level);
	}

	public void setUpSentry(PlayerEntity player) {
		entityData.set(OWNER, new Owner(player.getName().getString(), PlayerEntity.createPlayerUUID(player.getGameProfile()).toString()));
		entityData.set(ALLOWLIST, new CompoundNBT());
		entityData.set(HAS_SPEED_MODULE, false);
		entityData.set(MODE, SentryMode.CAMOUFLAGE_HP.ordinal());
		entityData.set(HAS_TARGET, false);
		entityData.set(SHUT_DOWN, false);
		entityData.set(HEAD_ROTATION, (float) (MathHelper.atan2(player.getX() - getX(), -(player.getZ() - getZ())) * (180D / Math.PI)));
		getSentryDisguiseBlockEntity(); //here to set the disguise block and its owner
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		entityData.define(OWNER, new Owner());
		entityData.define(ALLOWLIST, new CompoundNBT());
		entityData.define(HAS_SPEED_MODULE, false);
		entityData.define(MODE, SentryMode.CAMOUFLAGE_HP.ordinal());
		entityData.define(HAS_TARGET, false);
		entityData.define(SHUT_DOWN, false);
		entityData.define(HEAD_ROTATION, 0.0F);
	}

	@Override
	protected void registerGoals() {
		goalSelector.addGoal(1, new AttackRangedIfEnabledGoal(this, this::getShootingSpeed, 10.0F));
		targetSelector.addGoal(1, new TargetNearestPlayerOrMobGoal(this));
	}

	@Override
	public void tick() {
		super.tick();

		if (!level.isClientSide) {
			BlockPos downPos = getBlockPosBelowThatAffectsMyMovement();

			if (level.getBlockState(downPos).isAir() || level.noCollision(new AxisAlignedBB(downPos)))
				remove();

			if (!oldModule.isEmpty()) {
				getSentryDisguiseBlockEntity().ifPresent(be -> {
					//put the old module, if it exists, into the new disguise block
					if (!oldModule.isEmpty() && oldModule.getItem() instanceof ModuleItem && ModuleItem.getBlockAddon(oldModule) != null) {
						be.insertModule(oldModule, false);
						level.setBlockAndUpdate(blockPosition(), level.getBlockState(blockPosition()).setValue(SometimesVisibleBlock.INVISIBLE, false));
					}

					oldModule = ItemStack.EMPTY;
				});
			}
		}
		else {
			oHeadRotation = getHeadRotation();
			headRotation = entityData.get(HEAD_ROTATION);
			oHeadYTranslation = headYTranslation;

			if (shouldHeadBeUp()) {
				if (headYTranslation > UPWARDS_ANIMATION_LIMIT) {
					setAnimateUpwards(true);
					setAnimate(true);
				}
			}
			else if (headYTranslation < DOWNWARDS_ANIMATION_LIMIT) {
				setAnimateUpwards(false);
				setAnimate(true);
			}

			if (animate) { //no else if because animate can be changed in the above if statement
				if (animateUpwards && headYTranslation > UPWARDS_ANIMATION_LIMIT) {
					headYTranslation -= ANIMATION_STEP_SIZE;

					if (headYTranslation <= UPWARDS_ANIMATION_LIMIT) {
						setAnimateUpwards(false);
						setAnimate(false);
					}
				}
				else if (!animateUpwards && headYTranslation < DOWNWARDS_ANIMATION_LIMIT) {
					headYTranslation += ANIMATION_STEP_SIZE;

					if (headYTranslation >= DOWNWARDS_ANIMATION_LIMIT) {
						setAnimateUpwards(true);
						setAnimate(false);
					}
				}
			}
		}
	}

	@Override
	public ItemStack getPickedResult(RayTraceResult target) {
		return new ItemStack(SCContent.SENTRY.get());
	}

	@Override
	public ActionResultType mobInteract(PlayerEntity player, Hand hand) {
		BlockPos pos = blockPosition();

		if (isOwnedBy(player) && hand == Hand.MAIN_HAND) {
			Item item = player.getMainHandItem().getItem();

			player.closeContainer();

			if (player.isCrouching())
				remove();
			else if (item == Items.REDSTONE && isShutDown()) {
				reactivate();

				if (!player.isCreative())
					player.getMainHandItem().shrink(1);
			}
			else if (item == SCContent.UNIVERSAL_BLOCK_REMOVER.get() && !ConfigHandler.SERVER.vanillaToolBlockBreaking.get()) {
				remove();

				if (!player.isCreative())
					player.getMainHandItem().hurtAndBreak(1, player, p -> p.broadcastBreakEvent(hand));
			}
			else if (item == SCContent.DISGUISE_MODULE.get()) {
				ItemStack module = getDisguiseModule();

				if (!module.isEmpty()) //drop the old module as to not override it with the new one
					Block.popResource(level, pos, module);

				addDisguiseModule(player.getMainHandItem());

				if (!player.isCreative())
					player.setItemSlot(EquipmentSlotType.MAINHAND, ItemStack.EMPTY);
			}
			else if (item == SCContent.ALLOWLIST_MODULE.get()) {
				ItemStack module = getAllowlistModule();

				if (!module.isEmpty())
					Block.popResource(level, pos, module);

				setAllowlistModule(player.getMainHandItem());

				if (!player.isCreative())
					player.setItemSlot(EquipmentSlotType.MAINHAND, ItemStack.EMPTY);
			}
			else if (item == SCContent.SPEED_MODULE.get()) {
				if (!hasSpeedModule()) {
					setHasSpeedModule(true);

					if (!player.isCreative())
						player.setItemSlot(EquipmentSlotType.MAINHAND, ItemStack.EMPTY);
				}
			}
			else if (item == SCContent.UNIVERSAL_BLOCK_MODIFIER.get()) {
				Block.popResource(level, pos, getDisguiseModule());
				Block.popResource(level, pos, getAllowlistModule());

				if (hasSpeedModule())
					Block.popResource(level, pos, new ItemStack(SCContent.SPEED_MODULE.get()));

				getSentryDisguiseBlockEntity().ifPresent(be -> be.removeModule(ModuleType.DISGUISE, false));
				level.setBlockAndUpdate(blockPosition(), level.getBlockState(blockPosition()).setValue(SometimesVisibleBlock.INVISIBLE, true));
				entityData.set(ALLOWLIST, new CompoundNBT());
				entityData.set(HAS_SPEED_MODULE, false);
			}
			else if (item == SCContent.SENTRY_REMOTE_ACCESS_TOOL.get())
				item.useOn(new ItemUseContext(player, hand, new BlockRayTraceResult(new Vector3d(0.0D, 0.0D, 0.0D), Direction.NORTH, pos, false)));
			else if (item == SCContent.UNIVERSAL_OWNER_CHANGER.get()) {
				String newOwner = player.getMainHandItem().getHoverName().getString();

				entityData.set(OWNER, new Owner(newOwner, PlayerUtils.isPlayerOnline(newOwner) ? PlayerUtils.getPlayerFromName(newOwner).getUUID().toString() : "ownerUUID"));
				PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.UNIVERSAL_OWNER_CHANGER.get().getDescriptionId()), Utils.localize("messages.securitycraft:universalOwnerChanger.changed", newOwner), TextFormatting.GREEN);
			}
			else
				toggleMode(player);

			player.swing(Hand.MAIN_HAND);
			return ActionResultType.SUCCESS;
		}
		else if (!isOwnedBy(player) && hand == Hand.MAIN_HAND && player.isCreative() && (player.isCrouching() || player.getMainHandItem().getItem() == SCContent.UNIVERSAL_BLOCK_REMOVER.get()))
			remove();

		return super.mobInteract(player, hand);
	}

	/**
	 * Cleanly removes this sentry from the world, dropping the module and removing the block the sentry is disguised with
	 */
	@Override
	public void remove() {
		BlockPos pos = blockPosition();
		ItemStack sentryStack = new ItemStack(SCContent.SENTRY.get());

		if (hasCustomName())
			sentryStack.setHoverName(getCustomName());

		super.remove();
		Block.popResource(level, pos, sentryStack);
		Block.popResource(level, pos, getDisguiseModule()); //if there is none, nothing will drop
		Block.popResource(level, pos, getAllowlistModule()); //if there is none, nothing will drop
		level.setBlockAndUpdate(pos, level.getFluidState(pos).createLegacyBlock());

		if (hasSpeedModule())
			Block.popResource(level, pos, new ItemStack(SCContent.SPEED_MODULE.get()));
	}

	@Override
	public void kill() {
		remove();
	}

	/**
	 * Sets this sentry's mode to the next one and sends the player a message about the switch
	 *
	 * @param player The player to send the message to
	 */
	public void toggleMode(PlayerEntity player) {
		toggleMode(player, entityData.get(MODE) + 1, true);
	}

	/**
	 * Sets this sentry's mode to the given mode (or 0 if the mode is not one of 0, 1, 2) and sends the player a message about
	 * the switch if wanted
	 *
	 * @param player The player to send the message to
	 * @param mode The mode (int) to switch to (instead of sequentially toggling)
	 */
	public void toggleMode(PlayerEntity player, int mode, boolean sendMessage) {
		if (mode < 0 || mode >= SentryMode.values().length)
			mode = 0;

		entityData.set(MODE, mode);

		if (sendMessage)
			player.displayClientMessage(Utils.localize(SentryMode.values()[mode].getModeKey()).append(Utils.localize(SentryMode.values()[mode].getDescriptionKey())), true);
	}

	@Override
	public void setTarget(LivingEntity target) {
		if (isShutDown()) {
			super.setTarget(null);
			return;
		}

		entityData.set(HAS_TARGET, target != null);
		super.setTarget(target);
	}

	public boolean hasTarget() {
		return entityData.get(HAS_TARGET);
	}

	@Override
	public float getStandingEyeHeight(Pose pose, EntitySize size) { //the sentry's eyes are higher so that it can see players even if it's inside a block when disguised - this also makes bullets spawn higher
		return size.height * 1.6F;
	}

	@Override
	public void performRangedAttack(LivingEntity target, float distanceFactor) {
		//don't shoot if somehow a non player is a target, or if the player is in spectator or creative mode
		if (target instanceof PlayerEntity && (((PlayerEntity) target).isSpectator() || ((PlayerEntity) target).isCreative()))
			return;

		//also don't shoot if the target is too far away
		if (distanceToSqr(target) > MAX_TARGET_DISTANCE * MAX_TARGET_DISTANCE)
			return;

		if (isShutDown())
			return;

		TileEntity blockEntity = level.getBlockEntity(blockPosition().below());
		ProjectileEntity throwableEntity = null;
		SoundEvent shootSound = SoundEvents.ARROW_SHOOT;
		ProjectileDispenseBehavior pdb = null;
		LazyOptional<IItemHandler> optional = LazyOptional.empty();

		if (blockEntity instanceof ISentryBulletContainer)
			optional = ((ISentryBulletContainer) blockEntity).getHandlerForSentry(this);
		else if (blockEntity != null)
			optional = blockEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, Direction.UP);

		if (optional.isPresent()) {
			IItemHandler handler = optional.orElse(null); //this is safe, because the presence was checked beforehand

			for (int i = 0; i < handler.getSlots(); i++) {
				ItemStack stack = handler.getStackInSlot(i);

				if (!stack.isEmpty()) {
					IDispenseItemBehavior dispenseBehavior = ((DispenserBlock) Blocks.DISPENSER).getDispenseMethod(stack);

					if (dispenseBehavior instanceof ProjectileDispenseBehavior) {
						ItemStack extracted = handler.extractItem(i, 1, false);

						pdb = ((ProjectileDispenseBehavior) dispenseBehavior);
						throwableEntity = pdb.getProjectile(level, position().add(0.0D, 1.6D, 0.0D), extracted);
						throwableEntity.setOwner(this);
						shootSound = null;
						break;
					}
				}
			}
		}

		if (throwableEntity == null)
			throwableEntity = new Bullet(level, this);

		double baseY = target.getY() + target.getEyeHeight() - 1.100000023841858D;
		double x = target.getX() - getX();
		double y = baseY - throwableEntity.getY();
		double z = target.getZ() - getZ();
		float yOffset = MathHelper.sqrt(x * x + z * z) * 0.2F;

		entityData.set(HEAD_ROTATION, (float) (MathHelper.atan2(x, -z) * (180D / Math.PI)));
		throwableEntity.shoot(x, y + yOffset, z, 1.6F, 0.0F); //no inaccuracy for sentries!

		if (shootSound == null) {
			if (!level.isClientSide && pdb != null)
				pdb.playSound(new ProxyBlockSource((ServerWorld) level, blockPosition()));
		}
		else
			playSound(shootSound, 1.0F, 1.0F / (getRandom().nextFloat() * 0.4F + 0.8F));

		level.addFreshEntity(throwableEntity);
	}

	@Override
	public void addAdditionalSaveData(CompoundNBT tag) {
		tag.put("TileEntityData", getOwnerTag());
		tag.put("InstalledWhitelist", getAllowlistModule().save(new CompoundNBT()));
		tag.putBoolean("HasSpeedModule", hasSpeedModule());
		tag.putInt("SentryMode", entityData.get(MODE));
		tag.putBoolean("HasTarget", hasTarget());
		tag.putFloat("HeadRotation", entityData.get(HEAD_ROTATION));
		tag.putBoolean("ShutDown", isShutDown());
		super.addAdditionalSaveData(tag);
	}

	private CompoundNBT getOwnerTag() {
		CompoundNBT tag = new CompoundNBT();
		Owner owner = entityData.get(OWNER);

		owner.save(tag, needsValidation());
		return tag;
	}

	@Override
	public void readAdditionalSaveData(CompoundNBT tag) {
		CompoundNBT teTag = tag.getCompound("TileEntityData");
		Owner owner = Owner.fromCompound(teTag);
		float savedHeadRotation = tag.getFloat("HeadRotation");

		entityData.set(OWNER, owner);

		if (tag.contains("InstalledModule"))
			oldModule = ItemStack.of(tag.getCompound("InstalledModule"));
		else
			oldModule = ItemStack.EMPTY;

		entityData.set(ALLOWLIST, tag.getCompound("InstalledWhitelist"));
		entityData.set(HAS_SPEED_MODULE, tag.getBoolean("HasSpeedModule"));
		entityData.set(MODE, tag.getInt("SentryMode"));
		entityData.set(HAS_TARGET, tag.getBoolean("HasTarget"));
		entityData.set(SHUT_DOWN, tag.getBoolean("ShutDown"));
		entityData.set(HEAD_ROTATION, savedHeadRotation);
		oHeadRotation = savedHeadRotation;
		headRotation = savedHeadRotation;
		super.readAdditionalSaveData(tag);
	}

	@Override
	public void onSyncedDataUpdated(DataParameter<?> data) {
		super.onSyncedDataUpdated(data);

		if (level.isClientSide && data.equals(HAS_TARGET) && !hasReceivedEntityData) {
			if (shouldHeadBeUp())
				headYTranslation = UPWARDS_ANIMATION_LIMIT; //skip upwards animation when the sentry spawns on the client

			hasReceivedEntityData = true;
		}
	}

	private boolean shouldHeadBeUp() {
		return !isShutDown() && (getMode().isAggressive() || (getMode().isCamouflage() && hasTarget()));
	}

	@Override
	public void setOwner(String uuid, String name) {
		entityData.set(OWNER, new Owner(name, uuid));
	}

	@Override
	public Owner getOwner() {
		return entityData.get(OWNER);
	}

	@Override
	public void onOwnerChanged(BlockState state, World level, BlockPos pos, PlayerEntity player, Owner oldOwner, Owner newOwner) {}

	/**
	 * Adds a disguise module to the sentry and places a block if possible
	 *
	 * @param module The module to set
	 */
	public void addDisguiseModule(ItemStack module) {
		if (ModuleItem.getBlockAddon(module) != null) {
			getSentryDisguiseBlockEntity().ifPresent(be -> {
				//remove a possibly existing old disguise module
				be.removeModule(ModuleType.DISGUISE, false);
				be.insertModule(module, false);
				level.setBlockAndUpdate(blockPosition(), level.getBlockState(blockPosition()).setValue(SometimesVisibleBlock.INVISIBLE, false));
			});
		}
	}

	/**
	 * Sets the sentry's allowlist module
	 *
	 * @param module The module to set
	 */
	public void setAllowlistModule(ItemStack module) {
		entityData.set(ALLOWLIST, module.save(new CompoundNBT()));
	}

	/**
	 * Sets whether this sentry has a speed module installed
	 *
	 * @param hasSpeedModule true to set that this sentry has a speed module, false otherwise
	 */
	public void setHasSpeedModule(boolean hasSpeedModule) {
		entityData.set(HAS_SPEED_MODULE, hasSpeedModule);
	}

	/**
	 * @return The disguise module that is added to this sentry. ItemStack.EMPTY if none available
	 */
	public ItemStack getDisguiseModule() {
		Optional<DisguisableBlockEntity> be = getSentryDisguiseBlockEntity();

		if (be.isPresent())
			return be.get().getModule(ModuleType.DISGUISE);
		else
			return ItemStack.EMPTY;
	}

	/**
	 * @return The allowlist module that is added to this sentry. ItemStack.EMPTY if none available
	 */
	public ItemStack getAllowlistModule() {
		CompoundNBT tag = entityData.get(ALLOWLIST);

		if (tag == null || tag.isEmpty())
			return ItemStack.EMPTY;
		else
			return ItemStack.of(tag);
	}

	public boolean hasSpeedModule() {
		return entityData.get(HAS_SPEED_MODULE);
	}

	/**
	 * @return The mode in which the sentry is currently in, CAMOUFLAGE_HP as a fallback if the saved mode is not a valid mode
	 */
	public SentryMode getMode() {
		int mode = entityData.get(MODE);

		return mode < 0 || mode >= SentryMode.values().length ? SentryMode.CAMOUFLAGE_HP : SentryMode.values()[mode];
	}

	/**
	 * @param partialTicks Partial ticks
	 * @return The amount of y translation from the head's default position, used for animation
	 */
	public float getHeadYTranslation(float partialTicks) {
		return MathHelper.lerp(partialTicks, oHeadYTranslation, headYTranslation);
	}

	/**
	 * @return An optional containing the block entity of the block that the sentry uses to disguise itself, or an empty optional
	 *         if it doesn't exist
	 */
	public Optional<DisguisableBlockEntity> getSentryDisguiseBlockEntity() {
		TileEntity be;
		Block blockAtSentryPos = level.getBlockState(blockPosition()).getBlock();

		if (blockAtSentryPos != SCContent.SENTRY_DISGUISE.get()) {
			level.setBlockAndUpdate(blockPosition(), SCContent.SENTRY_DISGUISE.get().defaultBlockState().setValue(DisguisableBlock.WATERLOGGED, blockAtSentryPos == Blocks.WATER));
			be = level.getBlockEntity(blockPosition());

			if (be instanceof IOwnable) {
				Owner owner = getOwner();

				((IOwnable) be).setOwner(owner.getUUID(), owner.getName());
			}
		}
		else
			be = level.getBlockEntity(blockPosition());

		if (be instanceof DisguisableBlockEntity)
			return Optional.of((DisguisableBlockEntity) be);
		else
			return Optional.empty();
	}

	public boolean isTargetingAllowedPlayer(LivingEntity potentialTarget) {
		if (potentialTarget != null) {
			ItemStack allowlistModule = getAllowlistModule();

			if (allowlistModule.hasTag() && allowlistModule.getTag().getBoolean("affectEveryone"))
				return true;

			List<String> players = ModuleItem.getPlayersFromModule(allowlistModule);

			for (String s : players) {
				if (potentialTarget.getName().getContents().equalsIgnoreCase(s))
					return true;
			}

			return ModuleItem.doesModuleHaveTeamOf(allowlistModule, potentialTarget.getName().getString(), level);
		}

		return false;
	}

	public int getShootingSpeed() {
		return hasSpeedModule() ? 10 : 20;
	}

	@Override
	public void shutDown() {
		IEMPAffected.super.shutDown();
		setTarget(null);
	}

	@Override
	public boolean isShutDown() {
		return entityData.get(SHUT_DOWN);
	}

	@Override
	public void setShutDown(boolean shutDown) {
		entityData.set(SHUT_DOWN, shutDown);
	}

	//start: disallow sentry to take damage
	@Override
	public boolean doHurtTarget(Entity entity) {
		return false;
	}

	@Override
	public boolean hurt(DamageSource source, float amount) {
		return false;
	}

	@Override
	public boolean isAttackable() {
		return false;
	}

	@Override
	public boolean attackable() {
		return false;
	}
	//end: disallow sentry to take damage

	@Override
	public boolean checkSpawnRules(IWorld level, SpawnReason reason) {
		return false;
	}

	@Override
	public void jumpFromGround() {} //sentries don't jump!

	@Override
	public boolean isPathFinding() {
		return false;
	}

	@Override
	public void checkDespawn() {} //sentries don't despawn

	@Override
	public boolean removeWhenFarAway(double distanceClosestToPlayer) {
		return false; //sentries don't despawn
	}

	//sentries are heavy, so don't push them around!
	@Override
	public void playerTouch(PlayerEntity entity) {}

	@Override
	public void move(MoverType type, Vector3d vec) {} //no moving sentries!

	@Override
	protected void doPush(Entity entity) {}

	@Override
	protected void pushEntities() {}

	@Override
	public boolean ignoreExplosion() {
		return true; //does not get pushed around by explosions
	}

	@Override
	public boolean isPickable() {
		return true; //needs to stay true so blocks can't be broken through the sentry
	}

	@Override
	public boolean isPushable() {
		return false;
	}

	@Override
	public PushReaction getPistonPushReaction() {
		return PushReaction.IGNORE;
	}

	@Override
	public void tickLeash() {} //no leashing for sentry

	//this last code is here so the ai task gets executed, which it doesn't for some weird reason
	@Override
	public Random getRandom() {
		return notRandom;
	}

	@Override
	public IPacket<?> getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}

	public void setAnimate(boolean animate) {
		this.animate = animate;
	}

	public void setAnimateUpwards(boolean animateUpwards) {
		this.animateUpwards = animateUpwards;
	}

	public float getOriginalHeadRotation() {
		return oHeadRotation;
	}

	public float getHeadRotation() {
		return headRotation;
	}

	private static Random notRandom = new NotRandom();

	private static class NotRandom extends Random {
		@Override
		public int nextInt(int bound) {
			return 0;
		}
	}

	public enum SentryMode {
		CAMOUFLAGE_HP(1, TargetingMode.PLAYERS_AND_MOBS, 1),
		CAMOUFLAGE_H(1, TargetingMode.MOBS, 3),
		CAMOUFLAGE_P(1, TargetingMode.PLAYERS, 5),
		AGGRESSIVE_HP(0, TargetingMode.PLAYERS_AND_MOBS, 0),
		AGGRESSIVE_H(0, TargetingMode.MOBS, 2),
		AGGRESSIVE_P(0, TargetingMode.PLAYERS, 4),
		IDLE(-1, null, 6);

		private final int type;
		private final TargetingMode targetingMode;
		private final int descriptionKeyIndex;

		SentryMode(int type, TargetingMode targetingMode, int descriptionKeyIndex) {
			this.type = type;
			this.targetingMode = targetingMode;
			this.descriptionKeyIndex = descriptionKeyIndex;
		}

		public boolean isAggressive() {
			return type == 0;
		}

		public boolean isCamouflage() {
			return type == 1;
		}

		public boolean attacksHostile() {
			return targetingMode != null && targetingMode.allowsMobs();
		}

		public boolean attacksPlayers() {
			return targetingMode != null && targetingMode.allowsPlayers();
		}

		public String getModeKey() {
			String key = "messages.securitycraft:sentry.mode";

			return isAggressive() ? key + "0" : (isCamouflage() ? key + "1" : key + "2");
		}

		public String getTargetKey() {
			String key = "gui.securitycraft:srat.targets";

			switch (targetingMode) {
				case PLAYERS_AND_MOBS:
					return key + "1";
				case MOBS:
					return key + "2";
				case PLAYERS:
					return key + "3";
				default:
					return "";
			}
		}

		public String getDescriptionKey() {
			return "messages.securitycraft:sentry.descriptionMode" + descriptionKeyIndex;
		}
	}
}
