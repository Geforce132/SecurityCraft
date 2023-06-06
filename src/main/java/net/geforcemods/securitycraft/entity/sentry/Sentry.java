package net.geforcemods.securitycraft.entity.sentry;

import java.util.List;
import java.util.Optional;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.IEMPAffected;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.blockentities.DisguisableBlockEntity;
import net.geforcemods.securitycraft.blocks.SentryDisguiseBlock;
import net.geforcemods.securitycraft.items.ModuleItem;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.network.client.InitSentryAnimation;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSourceImpl;
import net.minecraft.core.Direction;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.dispenser.AbstractProjectileDispenseBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
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
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.network.PacketDistributor;

public class Sentry extends PathfinderMob implements RangedAttackMob, IEMPAffected, IOwnable { //needs to be a creature so it can target a player, ai is also only given to living entities
	private static final EntityDataAccessor<Owner> OWNER = SynchedEntityData.<Owner>defineId(Sentry.class, Owner.getSerializer());
	private static final EntityDataAccessor<CompoundTag> ALLOWLIST = SynchedEntityData.<CompoundTag>defineId(Sentry.class, EntityDataSerializers.COMPOUND_TAG);
	private static final EntityDataAccessor<Boolean> HAS_SPEED_MODULE = SynchedEntityData.<Boolean>defineId(Sentry.class, EntityDataSerializers.BOOLEAN);
	private static final EntityDataAccessor<Integer> MODE = SynchedEntityData.<Integer>defineId(Sentry.class, EntityDataSerializers.INT);
	public static final EntityDataAccessor<Float> HEAD_ROTATION = SynchedEntityData.<Float>defineId(Sentry.class, EntityDataSerializers.FLOAT);
	public static final float MAX_TARGET_DISTANCE = 20.0F;
	private static final float ANIMATION_STEP_SIZE = 0.025F;
	private static final float UPWARDS_ANIMATION_LIMIT = 0.025F;
	private static final float DOWNWARDS_ANIMATION_LIMIT = 0.9F;
	private float headYTranslation = 0.9F;
	private float oHeadYTranslation = 0.9F;
	private boolean shutDown = false;
	public boolean animateUpwards = false;
	public boolean animate = false;
	private long previousTargetId = Long.MIN_VALUE;
	public float headRotation;
	public float oHeadRotation;

	public Sentry(EntityType<Sentry> type, Level level) {
		super(SCContent.SENTRY_ENTITY.get(), level);
	}

	public void setupSentry(Player player) {
		entityData.set(OWNER, new Owner(player.getName().getString(), UUIDUtil.getOrCreatePlayerUUID(player.getGameProfile()).toString()));
		entityData.set(ALLOWLIST, new CompoundTag());
		entityData.set(HAS_SPEED_MODULE, false);
		entityData.set(MODE, SentryMode.CAMOUFLAGE_HP.ordinal());
		entityData.set(HEAD_ROTATION, 0.0F);
		getSentryDisguiseBlockEntity(); //here to set the disguise block and its owner
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		entityData.define(OWNER, new Owner());
		entityData.define(ALLOWLIST, new CompoundTag());
		entityData.define(HAS_SPEED_MODULE, false);
		entityData.define(MODE, SentryMode.CAMOUFLAGE_HP.ordinal());
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

			if (level.getBlockState(downPos).isAir() || level.noCollision(new AABB(downPos)))
				discard();
		}
		else {
			oHeadRotation = headRotation;
			headRotation = entityData.get(HEAD_ROTATION);
			oHeadYTranslation = headYTranslation;

			if (!shutDown && !animate && headYTranslation > 0.0F && getMode().isAggressive()) {
				animateUpwards = true;
				animate = true;
			}

			if (animate) { //no else if because animate can be changed in the above if statement
				if (animateUpwards && headYTranslation > UPWARDS_ANIMATION_LIMIT) {
					headYTranslation -= ANIMATION_STEP_SIZE;

					if (headYTranslation <= UPWARDS_ANIMATION_LIMIT) {
						animateUpwards = false;
						animate = false;
					}
				}
				else if (!animateUpwards && headYTranslation < DOWNWARDS_ANIMATION_LIMIT) {
					headYTranslation += ANIMATION_STEP_SIZE;

					if (headYTranslation >= DOWNWARDS_ANIMATION_LIMIT) {
						animateUpwards = true;
						animate = false;
					}
				}
			}
		}
	}

	@Override
	public ItemStack getPickedResult(HitResult target) {
		return new ItemStack(SCContent.SENTRY.get());
	}

	@Override
	public InteractionResult mobInteract(Player player, InteractionHand hand) {
		BlockPos pos = blockPosition();

		if (isOwnedBy(player) && hand == InteractionHand.MAIN_HAND) {
			Item item = player.getMainHandItem().getItem();

			player.closeContainer();

			if (player.isCrouching())
				kill();
			else if (item == Items.REDSTONE && isShutDown()) {
				reactivate();

				if (!player.isCreative())
					player.getMainHandItem().shrink(1);
			}
			else if (item == SCContent.UNIVERSAL_BLOCK_REMOVER.get()) {
				kill();

				if (!player.isCreative())
					player.getMainHandItem().hurtAndBreak(1, player, p -> p.broadcastBreakEvent(hand));
			}
			else if (item == SCContent.DISGUISE_MODULE.get()) {
				ItemStack module = getDisguiseModule();

				//drop the old module as to not override it with the new one
				if (!module.isEmpty())
					Block.popResource(level, pos, module);

				addDisguiseModule(player.getMainHandItem());

				if (!player.isCreative())
					player.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
			}
			else if (item == SCContent.ALLOWLIST_MODULE.get()) {
				ItemStack module = getAllowlistModule();

				if (!module.isEmpty())
					Block.popResource(level, pos, module);

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
				Block.popResource(level, pos, getDisguiseModule());
				Block.popResource(level, pos, getAllowlistModule());

				if (hasSpeedModule())
					Block.popResource(level, pos, new ItemStack(SCContent.SPEED_MODULE.get()));

				getSentryDisguiseBlockEntity().ifPresent(be -> be.removeModule(ModuleType.DISGUISE, false));
				level.setBlockAndUpdate(blockPosition(), level.getBlockState(blockPosition()).setValue(SentryDisguiseBlock.INVISIBLE, true));
				entityData.set(ALLOWLIST, new CompoundTag());
				entityData.set(HAS_SPEED_MODULE, false);
			}
			else if (item == SCContent.REMOTE_ACCESS_SENTRY.get())
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
		else if (!isOwnedBy(player) && hand == InteractionHand.MAIN_HAND && player.isCreative()) {
			if (player.isCrouching() || player.getMainHandItem().getItem() == SCContent.UNIVERSAL_BLOCK_REMOVER.get())
				kill();
		}

		return super.mobInteract(player, hand);
	}

	/**
	 * Cleanly removes this sentry from the world, dropping the module and removing the block the sentry is disguised with
	 */
	@Override
	public void remove(RemovalReason reason) {
		BlockPos pos = blockPosition();

		super.remove(reason);
		Block.popResource(level, pos, new ItemStack(SCContent.SENTRY.get()));
		Block.popResource(level, pos, getDisguiseModule()); //if there is none, nothing will drop
		Block.popResource(level, pos, getAllowlistModule()); //if there is none, nothing will drop
		level.setBlockAndUpdate(pos, level.getFluidState(pos).createLegacyBlock());

		if (hasSpeedModule())
			Block.popResource(level, pos, new ItemStack(SCContent.SPEED_MODULE.get()));
	}

	@Override
	public void kill() {
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

		if (!player.level.isClientSide)
			SecurityCraft.channel.send(PacketDistributor.TRACKING_ENTITY.with(() -> this), new InitSentryAnimation(blockPosition(), true, SentryMode.values()[mode].isAggressive(), isShutDown()));
	}

	@Override
	public void setTarget(LivingEntity target) {
		if (isShutDown()) {
			super.setTarget(null);
			return;
		}

		if (!getMode().isAggressive() && (target == null && previousTargetId != Long.MIN_VALUE || (target != null && previousTargetId != target.getId()))) {
			animateUpwards = getMode().isCamouflage() && target != null;
			animate = true;
			SecurityCraft.channel.send(PacketDistributor.TRACKING_ENTITY.with(() -> this), new InitSentryAnimation(blockPosition(), animate, animateUpwards, isShutDown()));
		}

		previousTargetId = target == null ? Long.MIN_VALUE : target.getId();
		super.setTarget(target);
	}

	@Override
	public float getEyeHeight(Pose pose) { //the sentry's eyes are higher so that it can see players even if it's inside a block when disguised - this also makes bullets spawn higher
		return 1.5F;
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

		BlockEntity blockEntity = level.getBlockEntity(blockPosition().below());
		Projectile throwableEntity = null;
		SoundEvent shootSound = SoundEvents.ARROW_SHOOT;
		AbstractProjectileDispenseBehavior pdb = null;
		LazyOptional<IItemHandler> optional = LazyOptional.empty();

		if (blockEntity instanceof ISentryBulletContainer be)
			optional = be.getHandlerForSentry(this);
		else if (blockEntity != null)
			optional = blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER, Direction.UP);

		if (optional.isPresent()) {
			IItemHandler handler = optional.orElse(null); //this is safe, because the presence was checked beforehand

			for (int i = 0; i < handler.getSlots(); i++) {
				ItemStack stack = handler.getStackInSlot(i);

				if (!stack.isEmpty()) {
					DispenseItemBehavior dispenseBehavior = ((DispenserBlock) Blocks.DISPENSER).getDispenseMethod(stack);

					if (dispenseBehavior instanceof AbstractProjectileDispenseBehavior projectileDispenseBehavior) {
						ItemStack extracted = handler.extractItem(i, 1, false);

						pdb = projectileDispenseBehavior;
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
		float yOffset = Mth.sqrt((float) (x * x + z * z)) * 0.2F;

		entityData.set(HEAD_ROTATION, (float) (Mth.atan2(x, -z) * (180D / Math.PI)));
		throwableEntity.shoot(x, y + yOffset, z, 1.6F, 0.0F); //no inaccuracy for sentries!

		if (shootSound == null) {
			if (!level.isClientSide)
				pdb.playSound(new BlockSourceImpl((ServerLevel) level, blockPosition()));
		}
		else
			playSound(shootSound, 1.0F, 1.0F / (getRandom().nextFloat() * 0.4F + 0.8F));

		level.addFreshEntity(throwableEntity);
	}

	@Override
	public void addAdditionalSaveData(CompoundTag tag) {
		tag.put("TileEntityData", getOwnerTag());
		tag.put("InstalledWhitelist", getAllowlistModule().save(new CompoundTag()));
		tag.putBoolean("HasSpeedModule", hasSpeedModule());
		tag.putInt("SentryMode", entityData.get(MODE));
		tag.putFloat("HeadRotation", entityData.get(HEAD_ROTATION));
		tag.putBoolean("ShutDown", isShutDown());
		super.addAdditionalSaveData(tag);
	}

	private CompoundTag getOwnerTag() {
		CompoundTag tag = new CompoundTag();
		Owner owner = entityData.get(OWNER);

		owner.save(tag, false);
		return tag;
	}

	@Override
	public void readAdditionalSaveData(CompoundTag tag) {
		CompoundTag teTag = tag.getCompound("TileEntityData");
		Owner owner = Owner.fromCompound(teTag);
		float headRotation = tag.getFloat("HeadRotation");

		entityData.set(OWNER, owner);
		getSentryDisguiseBlockEntity().ifPresent(be -> {
			//put the old module, if it exists, into the new disguise block
			if (tag.contains("InstalledModule")) {
				ItemStack module = ItemStack.of(tag.getCompound("InstalledModule"));

				if (!module.isEmpty() && module.getItem() instanceof ModuleItem moduleItem && moduleItem.getBlockAddon(module.getOrCreateTag()) != null) {
					be.insertModule(module, false);
					level.setBlockAndUpdate(blockPosition(), level.getBlockState(blockPosition()).setValue(SentryDisguiseBlock.INVISIBLE, false));
				}
			}
		});
		entityData.set(ALLOWLIST, tag.getCompound("InstalledWhitelist"));
		entityData.set(HAS_SPEED_MODULE, tag.getBoolean("HasSpeedModule"));
		entityData.set(MODE, tag.getInt("SentryMode"));
		entityData.set(HEAD_ROTATION, headRotation);
		oHeadRotation = headRotation;
		this.headRotation = headRotation;
		shutDown = tag.getBoolean("ShutDown");
		super.readAdditionalSaveData(tag);
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
	public void onOwnerChanged(BlockState state, Level level, BlockPos pos, Player player) {}

	/**
	 * Adds a disguise module to the sentry and places a block if possible
	 *
	 * @param module The module to set
	 */
	public void addDisguiseModule(ItemStack module) {
		if (((ModuleItem) module.getItem()).getBlockAddon(module.getTag()) != null) {
			getSentryDisguiseBlockEntity().ifPresent(be -> {
				//remove a possibly existing old disguise module
				be.removeModule(ModuleType.DISGUISE, false);
				be.insertModule(module, false);
				level.setBlockAndUpdate(blockPosition(), level.getBlockState(blockPosition()).setValue(SentryDisguiseBlock.INVISIBLE, false));
			});
		}
	}

	/**
	 * Sets the sentry's allowlist module
	 *
	 * @param module The module to set
	 */
	public void setAllowlistModule(ItemStack module) {
		entityData.set(ALLOWLIST, module.save(new CompoundTag()));
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
		CompoundTag tag = entityData.get(ALLOWLIST);

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
		return Mth.lerp(partialTicks, oHeadYTranslation, headYTranslation);
	}

	/**
	 * @return An optional containing the block entity of the block that the sentry uses to disguise itself, or an empty optional
	 *         if it doesn't exist
	 */
	public Optional<DisguisableBlockEntity> getSentryDisguiseBlockEntity() {
		BlockEntity be;
		Block blockAtSentryPos = level.getBlockState(blockPosition()).getBlock();

		if (blockAtSentryPos != SCContent.SENTRY_DISGUISE.get()) {
			level.setBlockAndUpdate(blockPosition(), SCContent.SENTRY_DISGUISE.get().defaultBlockState().setValue(SentryDisguiseBlock.WATERLOGGED, blockAtSentryPos == Blocks.WATER));
			be = level.getBlockEntity(blockPosition());

			if (be instanceof IOwnable ownable) {
				Owner owner = getOwner();

				ownable.setOwner(owner.getUUID(), owner.getName());
			}
		}
		else
			be = level.getBlockEntity(blockPosition());

		if (be instanceof DisguisableBlockEntity dbe)
			return Optional.of(dbe);
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
				if (potentialTarget.getName().getString().equalsIgnoreCase(s))
					return true;
			}
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
		return shutDown;
	}

	@Override
	public void setShutDown(boolean shutDown) {
		this.shutDown = shutDown;

		if (!level.isClientSide) {
			if (shutDown)
				SecurityCraft.channel.send(PacketDistributor.TRACKING_ENTITY.with(() -> this), new InitSentryAnimation(blockPosition(), true, false, shutDown));
			else
				SecurityCraft.channel.send(PacketDistributor.TRACKING_ENTITY.with(() -> this), new InitSentryAnimation(blockPosition(), true, getMode().isAggressive(), shutDown));
		}
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
	public boolean checkSpawnRules(LevelAccessor level, MobSpawnType reason) {
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

	@Override
	public Packet<ClientGamePacketListener> getAddEntityPacket() {
		return new ClientboundAddEntityPacket(this);
	}

	public static enum SentryMode {
		CAMOUFLAGE_HP(1, 0, 1),
		CAMOUFLAGE_H(1, 1, 3),
		CAMOUFLAGE_P(1, 2, 5),
		AGGRESSIVE_HP(0, 0, 0),
		AGGRESSIVE_H(0, 1, 2),
		AGGRESSIVE_P(0, 2, 4),
		IDLE(-1, -1, 6);

		private final int type;
		private final int attack;
		private final int descriptionKeyIndex;

		SentryMode(int type, int attack, int descriptionKeyIndex) {
			this.type = type;
			this.attack = attack;
			this.descriptionKeyIndex = descriptionKeyIndex;
		}

		public boolean isAggressive() {
			return type == 0;
		}

		public boolean isCamouflage() {
			return type == 1;
		}

		public boolean attacksHostile() {
			return attack == 0 || attack == 1;
		}

		public boolean attacksPlayers() {
			return attack == 0 || attack == 2;
		}

		public String getModeKey() {
			String key = "messages.securitycraft:sentry.mode";

			return isAggressive() ? key + "0" : (isCamouflage() ? key + "1" : key + "2");
		}

		public String getTargetKey() {
			String key = "gui.securitycraft:srat.targets";

			return attacksHostile() && attacksPlayers() ? key + "1" : (attacksHostile() ? key + "2" : (attacksPlayers() ? key + "3" : ""));
		}

		public String getDescriptionKey() {
			return "messages.securitycraft:sentry.descriptionMode" + descriptionKeyIndex;
		}
	}
}
