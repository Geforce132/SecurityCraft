package net.geforcemods.securitycraft.entity.sentry;

import java.util.List;
import java.util.Optional;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IEMPAffected;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.blockentities.DisguisableBlockEntity;
import net.geforcemods.securitycraft.blocks.DisguisableBlock;
import net.geforcemods.securitycraft.blocks.SometimesVisibleBlock;
import net.geforcemods.securitycraft.components.ListModuleData;
import net.geforcemods.securitycraft.items.ModuleItem;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.misc.TargetingMode;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.core.dispenser.ProjectileDispenseBehavior;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;

public class Sentry extends PathfinderMob implements RangedAttackMob, IEMPAffected, IOwnable { //needs to be a pathfinder mob so it can target a player, ai is also only given to living entities
	private static final EntityDataAccessor<Owner> OWNER = SynchedEntityData.<Owner>defineId(Sentry.class, Owner.getSerializer());
	private static final EntityDataAccessor<ItemStack> ALLOWLIST = SynchedEntityData.<ItemStack>defineId(Sentry.class, EntityDataSerializers.ITEM_STACK);
	private static final EntityDataAccessor<Boolean> HAS_SPEED_MODULE = SynchedEntityData.<Boolean>defineId(Sentry.class, EntityDataSerializers.BOOLEAN);
	private static final EntityDataAccessor<Integer> MODE = SynchedEntityData.<Integer>defineId(Sentry.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Boolean> HAS_TARGET = SynchedEntityData.<Boolean>defineId(Sentry.class, EntityDataSerializers.BOOLEAN);
	private static final EntityDataAccessor<Boolean> SHUT_DOWN = SynchedEntityData.<Boolean>defineId(Sentry.class, EntityDataSerializers.BOOLEAN);
	public static final EntityDataAccessor<Float> HEAD_ROTATION = SynchedEntityData.<Float>defineId(Sentry.class, EntityDataSerializers.FLOAT);
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

	public Sentry(EntityType<Sentry> type, Level level) {
		super(SCContent.SENTRY_ENTITY.get(), level);
	}

	public void setUpSentry(Player player) {
		entityData.set(OWNER, new Owner(player.getName().getString(), player.getGameProfile().getId().toString()));
		entityData.set(ALLOWLIST, ItemStack.EMPTY);
		entityData.set(HAS_SPEED_MODULE, false);
		entityData.set(MODE, SentryMode.CAMOUFLAGE_HP.ordinal());
		entityData.set(HAS_TARGET, false);
		entityData.set(SHUT_DOWN, false);
		entityData.set(HEAD_ROTATION, (float) (Mth.atan2(player.getX() - getX(), -(player.getZ() - getZ())) * (180D / Math.PI)));
		getSentryDisguiseBlockEntity(); //here to set the disguise block and its owner
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		super.defineSynchedData(builder);
		builder.define(OWNER, new Owner());
		builder.define(ALLOWLIST, ItemStack.EMPTY);
		builder.define(HAS_SPEED_MODULE, false);
		builder.define(MODE, SentryMode.CAMOUFLAGE_HP.ordinal());
		builder.define(HAS_TARGET, false);
		builder.define(SHUT_DOWN, false);
		builder.define(HEAD_ROTATION, 0.0F);
	}

	@Override
	protected void registerGoals() {
		goalSelector.addGoal(1, new AttackRangedIfEnabledGoal(this, this::getShootingSpeed, 10.0F));
		targetSelector.addGoal(1, new TargetNearestPlayerOrMobGoal(this));
	}

	@Override
	public void tick() {
		super.tick();

		if (!level().isClientSide) {
			BlockPos downPos = getBlockPosBelowThatAffectsMyMovement();

			if (level().getBlockState(downPos).isAir() || level().noCollision(new AABB(downPos)))
				discard();
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

			if (isAnimating()) { //no else if because animate can be changed in the above if statement
				if (animatesUpwards() && headYTranslation > UPWARDS_ANIMATION_LIMIT) {
					headYTranslation -= ANIMATION_STEP_SIZE;

					if (headYTranslation <= UPWARDS_ANIMATION_LIMIT) {
						setAnimateUpwards(false);
						setAnimate(false);
					}
				}
				else if (!animatesUpwards() && headYTranslation < DOWNWARDS_ANIMATION_LIMIT) {
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
	public ItemStack getPickResult() {
		return new ItemStack(SCContent.SENTRY.get());
	}

	@Override
	public InteractionResult mobInteract(Player player, InteractionHand hand) {
		BlockPos pos = blockPosition();

		if (isOwnedBy(player) && hand == InteractionHand.MAIN_HAND) {
			Item item = player.getMainHandItem().getItem();

			player.closeContainer();

			if (player.isCrouching())
				kill(null);
			else if (item == Items.REDSTONE && isShutDown()) {
				reactivate();

				if (!player.isCreative())
					player.getMainHandItem().shrink(1);
			}
			else if (item == SCContent.UNIVERSAL_BLOCK_REMOVER.get()) {
				kill(null);

				if (!player.isCreative())
					player.getMainHandItem().hurtAndBreak(1, player, LivingEntity.getSlotForHand(hand));
			}
			else if (item == SCContent.DISGUISE_MODULE.get()) {
				ItemStack module = getDisguiseModule();

				//drop the old module as to not override it with the new one
				if (!module.isEmpty())
					Block.popResource(level(), pos, module);

				addDisguiseModule(player.getMainHandItem());

				if (!player.isCreative())
					player.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
			}
			else if (item == SCContent.ALLOWLIST_MODULE.get()) {
				ItemStack module = getAllowlistModule();

				if (!module.isEmpty())
					Block.popResource(level(), pos, module);

				setAllowlistModule(player.getMainHandItem());

				if (!player.isCreative())
					player.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
			}
			else if (item == SCContent.SPEED_MODULE.get()) {
				if (!hasSpeedModule()) {
					setHasSpeedModule(true);

					if (!player.isCreative())
						player.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
				}
			}
			else if (item == SCContent.UNIVERSAL_BLOCK_MODIFIER.get()) {
				Block.popResource(level(), pos, getDisguiseModule());
				Block.popResource(level(), pos, getAllowlistModule());

				if (hasSpeedModule())
					Block.popResource(level(), pos, new ItemStack(SCContent.SPEED_MODULE.get()));

				getSentryDisguiseBlockEntity().ifPresent(be -> be.removeModule(ModuleType.DISGUISE, false));
				level().setBlockAndUpdate(blockPosition(), level().getBlockState(blockPosition()).setValue(SometimesVisibleBlock.INVISIBLE, true));
				entityData.set(ALLOWLIST, ItemStack.EMPTY);
				entityData.set(HAS_SPEED_MODULE, false);
			}
			else if (item == SCContent.SENTRY_REMOTE_ACCESS_TOOL.get())
				item.useOn(new UseOnContext(player, hand, new BlockHitResult(new Vec3(0.0D, 0.0D, 0.0D), Direction.NORTH, pos, false)));
			else if (item == SCContent.UNIVERSAL_OWNER_CHANGER.get()) {
				String newOwner = player.getMainHandItem().getHoverName().getString();

				entityData.set(OWNER, new Owner(newOwner, PlayerUtils.isPlayerOnline(newOwner) ? PlayerUtils.getPlayerFromName(newOwner).getUUID().toString() : "ownerUUID"));
				PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.UNIVERSAL_OWNER_CHANGER.get().getDescriptionId()), Utils.localize("messages.securitycraft:universalOwnerChanger.changed", newOwner), ChatFormatting.GREEN);
			}
			else
				toggleMode(player);

			player.swing(InteractionHand.MAIN_HAND);
			return InteractionResult.SUCCESS;
		}
		else if (!isOwnedBy(player) && hand == InteractionHand.MAIN_HAND && player.isCreative() && (player.isCrouching() || player.getMainHandItem().getItem() == SCContent.UNIVERSAL_BLOCK_REMOVER.get()))
			kill(null);

		return super.mobInteract(player, hand);
	}

	/**
	 * Cleanly removes this sentry from the world, dropping the module and removing the block the sentry is disguised with
	 */
	@Override
	public void remove(RemovalReason reason) {
		BlockPos pos = blockPosition();
		ItemStack sentryStack = new ItemStack(SCContent.SENTRY.get());

		super.remove(reason);
		sentryStack.set(DataComponents.CUSTOM_NAME, getCustomName());
		Block.popResource(level(), pos, sentryStack);
		Block.popResource(level(), pos, getDisguiseModule()); //if there is none, nothing will drop
		Block.popResource(level(), pos, getAllowlistModule()); //if there is none, nothing will drop
		level().setBlockAndUpdate(pos, level().getFluidState(pos).createLegacyBlock());

		if (hasSpeedModule())
			Block.popResource(level(), pos, new ItemStack(SCContent.SPEED_MODULE.get()));
	}

	@Override
	public void kill(ServerLevel level) {
		remove(RemovalReason.KILLED);
		gameEvent(GameEvent.ENTITY_DIE);
	}

	/**
	 * Sets this sentry's mode to the next one and sends the player a message about the switch
	 *
	 * @param player The player to send the message to
	 */
	public void toggleMode(Player player) {
		toggleMode(player, entityData.get(MODE) + 1, true);
	}

	/**
	 * Sets this sentry's mode to the given mode (or 0 if the mode is not one of 0, 1, 2) and sends the player a message about
	 * the switch if wanted
	 *
	 * @param player The player to send the message to
	 * @param mode The mode (int) to switch to (instead of sequentially toggling)
	 */
	public void toggleMode(Player player, int mode, boolean sendMessage) {
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
	public void performRangedAttack(LivingEntity target, float distanceFactor) {
		//don't shoot if somehow a non player is a target, or if the player is in spectator or creative mode
		if (target instanceof Player player && (player.isSpectator() || player.isCreative()))
			return;

		//also don't shoot if the target is too far away
		if (distanceToSqr(target) > MAX_TARGET_DISTANCE * MAX_TARGET_DISTANCE)
			return;

		if (isShutDown())
			return;

		Level level = level();
		BlockEntity blockEntity = level.getBlockEntity(blockPosition().below());
		Projectile throwableEntity = null;
		SoundEvent shootSound = SoundEvents.ARROW_SHOOT;
		ProjectileDispenseBehavior pdb = null;
		IItemHandler handler = null;
		double baseY = target.getY() + target.getEyeHeight() - 1.100000023841858D;
		double x = target.getX() - getX();
		double projectileY = getEyeHeight() - 0.1F;
		double y = baseY - (getY() + projectileY);
		double z = target.getZ() - getZ();
		float yOffset = Mth.sqrt((float) (x * x + z * z)) * 0.2F;

		if (blockEntity instanceof ISentryBulletContainer be)
			handler = be.getHandlerForSentry(this);
		else if (blockEntity != null)
			handler = level.getCapability(Capabilities.ItemHandler.BLOCK, blockEntity.getBlockPos(), blockEntity.getBlockState(), blockEntity, Direction.UP);

		if (handler != null) {
			for (int i = 0; i < handler.getSlots(); i++) {
				ItemStack stack = handler.getStackInSlot(i);

				if (!stack.isEmpty()) {
					DispenseItemBehavior dispenseBehavior = ((DispenserBlock) Blocks.DISPENSER).getDispenseMethod(level, stack);

					if (dispenseBehavior instanceof ProjectileDispenseBehavior projectileDispenseBehavior) {
						ItemStack extracted = handler.extractItem(i, 1, false);

						pdb = projectileDispenseBehavior;
						throwableEntity = pdb.projectileItem.asProjectile(level, position().add(0.0D, projectileY, 0.0D), extracted, Direction.getApproximateNearest(x, y, z));
						throwableEntity.setOwner(this);
						shootSound = null;
						break;
					}
				}
			}
		}

		if (throwableEntity == null)
			throwableEntity = new Bullet(level, this);

		entityData.set(HEAD_ROTATION, (float) (Mth.atan2(x, -z) * (180D / Math.PI)));
		throwableEntity.shoot(x, y + yOffset, z, 1.6F, 0.0F); //no inaccuracy for sentries!

		if (shootSound == null) {
			if (!level.isClientSide && pdb != null)
				pdb.playSound(new BlockSource((ServerLevel) level, blockPosition(), null, null)); //probably safe as long as playSound does not call the state and blockEntity methods.
		}
		else
			playSound(shootSound, 1.0F, 1.0F / (getRandom().nextFloat() * 0.4F + 0.8F));

		level.addFreshEntity(throwableEntity);
	}

	@Override
	public void addAdditionalSaveData(CompoundTag tag) {
		ItemStack allowlistModule = getAllowlistModule();

		tag.put("TileEntityData", getOwnerTag());

		if (!allowlistModule.isEmpty())
			tag.put("InstalledWhitelist", allowlistModule.save(level().registryAccess()));

		tag.putBoolean("HasSpeedModule", hasSpeedModule());
		tag.putInt("SentryMode", entityData.get(MODE));
		tag.putBoolean("HasTarget", hasTarget());
		tag.putFloat("HeadRotation", entityData.get(HEAD_ROTATION));
		tag.putBoolean("ShutDown", isShutDown());
		super.addAdditionalSaveData(tag);
	}

	private CompoundTag getOwnerTag() {
		CompoundTag tag = new CompoundTag();
		Owner owner = entityData.get(OWNER);

		owner.save(tag, needsValidation());
		return tag;
	}

	@Override
	public void readAdditionalSaveData(CompoundTag tag) {
		CompoundTag teTag = tag.getCompoundOrEmpty("TileEntityData");
		Owner owner = Owner.fromCompound(teTag);
		float savedHeadRotation = tag.getFloatOr("HeadRotation", 0.0F);

		entityData.set(OWNER, owner);
		getSentryDisguiseBlockEntity().ifPresent(be -> {
			//put the old module, if it exists, into the new disguise block
			if (tag.contains("InstalledModule")) {
				ItemStack module = Utils.parseOptional(level().registryAccess(), tag.getCompoundOrEmpty("InstalledModule"));

				if (!module.isEmpty() && module.getItem() instanceof ModuleItem && ModuleItem.getBlockAddon(module) != null) {
					be.insertModule(module, false);
					level().setBlockAndUpdate(blockPosition(), level().getBlockState(blockPosition()).setValue(SometimesVisibleBlock.INVISIBLE, false));
				}
			}
		});
		entityData.set(ALLOWLIST, Utils.parseOptional(level().registryAccess(), tag.getCompoundOrEmpty("InstalledWhitelist")));
		entityData.set(HAS_SPEED_MODULE, tag.getBooleanOr("HasSpeedModule", false));
		entityData.set(MODE, tag.getIntOr("SentryMode", 0));
		entityData.set(HAS_TARGET, tag.getBooleanOr("HasTarget", false));
		entityData.set(SHUT_DOWN, tag.getBooleanOr("ShutDown", false));
		entityData.set(HEAD_ROTATION, savedHeadRotation);
		oHeadRotation = savedHeadRotation;
		headRotation = savedHeadRotation;
		super.readAdditionalSaveData(tag);
	}

	@Override
	public void onSyncedDataUpdated(List<SynchedEntityData.DataValue<?>> dataList) {
		super.onSyncedDataUpdated(dataList);

		if (level().isClientSide && !hasReceivedEntityData) {
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
	public void onOwnerChanged(BlockState state, Level level, BlockPos pos, Player player, Owner oldOwner, Owner newOwner) {}

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
				level().setBlockAndUpdate(blockPosition(), level().getBlockState(blockPosition()).setValue(SometimesVisibleBlock.INVISIBLE, false));
			});
		}
	}

	/**
	 * Sets the sentry's allowlist module
	 *
	 * @param module The module to set
	 */
	public void setAllowlistModule(ItemStack module) {
		entityData.set(ALLOWLIST, module);
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
		return entityData.get(ALLOWLIST);
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
		return Mth.lerp(partialTicks, oHeadYTranslation, headYTranslation);
	}

	/**
	 * @return An optional containing the block entity of the block that the sentry uses to disguise itself, or an empty optional
	 *         if it doesn't exist
	 */
	public Optional<DisguisableBlockEntity> getSentryDisguiseBlockEntity() {
		BlockEntity be;
		Block blockAtSentryPos = level().getBlockState(blockPosition()).getBlock();

		if (blockAtSentryPos != SCContent.SENTRY_DISGUISE.get()) {
			level().setBlockAndUpdate(blockPosition(), SCContent.SENTRY_DISGUISE.get().defaultBlockState().setValue(DisguisableBlock.WATERLOGGED, blockAtSentryPos == Blocks.WATER));
			be = level().getBlockEntity(blockPosition());

			if (be instanceof IOwnable ownable) {
				Owner owner = getOwner();

				ownable.setOwner(owner.getUUID(), owner.getName());
			}
		}
		else
			be = level().getBlockEntity(blockPosition());

		if (be instanceof DisguisableBlockEntity dbe)
			return Optional.of(dbe);
		else
			return Optional.empty();
	}

	public boolean isTargetingAllowedPlayer(LivingEntity potentialTarget) {
		if (potentialTarget != null) {
			ListModuleData listModuleData = getAllowlistModule().get(SCContent.LIST_MODULE_DATA);
			String targetName = potentialTarget.getName().getString();

			return listModuleData != null && (listModuleData.affectEveryone() || listModuleData.isPlayerOnList(targetName) || listModuleData.isTeamOfPlayerOnList(level(), targetName));
		}

		return false;
	}

	public int getShootingSpeed() {
		return hasSpeedModule() ? 5 : 10;
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
	public boolean doHurtTarget(ServerLevel level, Entity entity) {
		return false;
	}

	@Override
	public boolean hurtServer(ServerLevel level, DamageSource damageSource, float amount) {
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
	public boolean checkSpawnRules(LevelAccessor level, EntitySpawnReason reason) {
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
	public void playerTouch(Player entity) {}

	@Override
	public void move(MoverType type, Vec3 vec) {} //no moving sentries!

	@Override
	protected void doPush(Entity entity) {}

	@Override
	protected void pushEntities() {}

	@Override
	public boolean ignoreExplosion(Explosion explosion) {
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
	public boolean canBeLeashed() { //no leashing for sentry
		return false;
	}

	@Override
	public Packet<ClientGamePacketListener> getAddEntityPacket(ServerEntity serverEntity) {
		return new ClientboundAddEntityPacket(this, serverEntity);
	}

	public void setAnimateUpwards(boolean animateUpwards) {
		this.animateUpwards = animateUpwards;
	}

	public boolean animatesUpwards() {
		return animateUpwards;
	}

	public void setAnimate(boolean animate) {
		this.animate = animate;
	}

	public boolean isAnimating() {
		return animate;
	}

	public float getOriginalHeadRotation() {
		return oHeadRotation;
	}

	public float getHeadRotation() {
		return headRotation;
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

			return switch (targetingMode) {
				case PLAYERS_AND_MOBS -> key + "1";
				case MOBS -> key + "2";
				case PLAYERS -> key + "3";
				default -> "";
			};
		}

		public String getDescriptionKey() {
			return "messages.securitycraft:sentry.descriptionMode" + descriptionKeyIndex;
		}
	}
}
