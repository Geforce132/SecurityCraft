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
import net.geforcemods.securitycraft.blockentities.KeypadChestBlockEntity;
import net.geforcemods.securitycraft.blocks.SometimesVisibleBlock;
import net.geforcemods.securitycraft.items.ModuleItem;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.misc.TargetingMode;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDispenser;
import net.minecraft.block.BlockSourceImpl;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.state.IBlockState;
import net.minecraft.dispenser.BehaviorProjectileDispense;
import net.minecraft.dispenser.IBehaviorDispenseItem;
import net.minecraft.dispenser.PositionImpl;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class Sentry extends EntityCreature implements IRangedAttackMob, IEMPAffected, IOwnable { //needs to be a creature so it can target a player, ai is also only given to living entities
	private static final DataParameter<Owner> OWNER = EntityDataManager.<Owner>createKey(Sentry.class, Owner.getSerializer());
	private static final DataParameter<NBTTagCompound> ALLOWLIST = EntityDataManager.<NBTTagCompound>createKey(Sentry.class, DataSerializers.COMPOUND_TAG);
	private static final DataParameter<Boolean> HAS_SPEED_MODULE = EntityDataManager.<Boolean>createKey(Sentry.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Integer> MODE = EntityDataManager.<Integer>createKey(Sentry.class, DataSerializers.VARINT);
	private static final DataParameter<Boolean> HAS_TARGET = EntityDataManager.<Boolean>createKey(Sentry.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Boolean> SHUT_DOWN = EntityDataManager.<Boolean>createKey(Sentry.class, DataSerializers.BOOLEAN);
	public static final DataParameter<Float> HEAD_ROTATION = EntityDataManager.<Float>createKey(Sentry.class, DataSerializers.FLOAT);
	public static final float MAX_TARGET_DISTANCE = 20.0F;
	private static final float ANIMATION_STEP_SIZE = 0.025F;
	private static final float UPWARDS_ANIMATION_LIMIT = 0.025F;
	private static final float DOWNWARDS_ANIMATION_LIMIT = 0.9F;
	private float headYTranslation = 0.9F;
	private float oHeadYTranslation = 0.9F;
	private boolean animateUpwards = true;
	private boolean animate = false;
	private float headRotation;
	private float oHeadRotation;
	private boolean hasReceivedEntityData = false;
	/**
	 * @deprecated Only used for upgrading old sentries
	 */
	@Deprecated
	private ItemStack oldModule = ItemStack.EMPTY;

	public Sentry(World world) {
		super(world);
		setSize(1.0F, 1.0001F);
	}

	public Sentry(World world, double x, double y, double z, EntityPlayer owner) {
		this(world, x, y, z, new Owner(owner.getName(), EntityPlayer.getUUID(owner.getGameProfile()).toString()));
		dataManager.set(HEAD_ROTATION, (float) (MathHelper.atan2(owner.posX - posX, -(owner.posZ - posZ)) * (180D / Math.PI)));
	}

	public Sentry(World world, double x, double y, double z, Owner owner) {
		this(world);
		dataManager.set(OWNER, owner);
		dataManager.set(ALLOWLIST, new NBTTagCompound());
		dataManager.set(HAS_SPEED_MODULE, false);
		dataManager.set(MODE, SentryMode.CAMOUFLAGE_HP.ordinal());
		dataManager.set(HAS_TARGET, false);
		dataManager.set(SHUT_DOWN, false);
		dataManager.set(HEAD_ROTATION, 0.0F);
		setPosition(x, y, z);
		getSentryDisguiseBlockEntity(); //here to set the disguise block and its owner
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		dataManager.register(OWNER, new Owner());
		dataManager.register(ALLOWLIST, new NBTTagCompound());
		dataManager.register(HAS_SPEED_MODULE, false);
		dataManager.register(MODE, SentryMode.CAMOUFLAGE_HP.ordinal());
		dataManager.register(HAS_TARGET, false);
		dataManager.register(SHUT_DOWN, false);
		dataManager.register(HEAD_ROTATION, 0.0F);
	}

	@Override
	protected void initEntityAI() {
		tasks.addTask(1, new AttackRangedIfEnabledGoal(this, this::getShootingSpeed, 10.0F));
		targetTasks.addTask(1, new TargetNearestPlayerOrMobGoal(this));
	}

	@Override
	public void onEntityUpdate() {
		super.onEntityUpdate();

		if (!world.isRemote) {
			BlockPos downPos = getPosition().down();
			IBlockState state = world.getBlockState(downPos);

			if (state.getBlock().isAir(state, world, downPos) || world.getCollisionBoxes(null, new AxisAlignedBB(downPos)).isEmpty())
				remove();

			if (!oldModule.isEmpty()) {
				getSentryDisguiseBlockEntity().ifPresent(be -> {
					//put the old module, if it exists, into the new disguise block
					if (!oldModule.isEmpty() && oldModule.getItem() instanceof ModuleItem && ModuleItem.getBlockAddon(oldModule) != null) {
						be.insertModule(oldModule, false);
						world.setBlockState(getPosition(), world.getBlockState(getPosition()).withProperty(SometimesVisibleBlock.INVISIBLE, false));
					}

					oldModule = ItemStack.EMPTY;
				});
			}
		}
		else {
			oHeadRotation = getHeadRotation();
			headRotation = dataManager.get(HEAD_ROTATION);
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

			if (shouldAnimate()) { //no else if because animate can be changed in the above if statement
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
	public ItemStack getPickedResult(RayTraceResult target) {
		return new ItemStack(SCContent.sentry);
	}

	@Override
	public boolean processInteract(EntityPlayer player, EnumHand hand) {
		BlockPos pos = getPosition();

		if (isOwnedBy(player) && hand == EnumHand.MAIN_HAND) {
			Item item = player.getHeldItemMainhand().getItem();

			player.closeScreen();

			if (player.isSneaking())
				remove();
			else if (item == Items.REDSTONE && isShutDown()) {
				reactivate();

				if (!player.isCreative())
					player.getHeldItemMainhand().shrink(1);
			}
			else if (item == SCContent.universalBlockRemover && !ConfigHandler.vanillaToolBlockBreaking) {
				remove();

				if (!player.isCreative())
					player.getHeldItemMainhand().damageItem(1, player);
			}
			else if (item == SCContent.disguiseModule) {
				ItemStack module = getDisguiseModule();

				if (!module.isEmpty()) //drop the old module as to not override it with the new one
					Block.spawnAsEntity(world, pos, module);

				addDisguiseModule(player.getHeldItemMainhand());

				if (!player.isCreative())
					player.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, ItemStack.EMPTY);
			}
			else if (item == SCContent.allowlistModule) {
				ItemStack module = getAllowlistModule();

				if (!module.isEmpty())
					Block.spawnAsEntity(world, pos, module);

				setAllowlistModule(player.getHeldItemMainhand());

				if (!player.isCreative())
					player.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, ItemStack.EMPTY);
			}
			else if (item == SCContent.speedModule) {
				if (!hasSpeedModule()) {
					setHasSpeedModule(true);

					if (!player.isCreative())
						player.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, ItemStack.EMPTY);
				}
			}
			else if (item == SCContent.universalBlockModifier) {
				if (!getDisguiseModule().isEmpty()) {
					Block block = ModuleItem.getBlockAddon(getDisguiseModule());

					if (block == world.getBlockState(pos).getBlock())
						world.setBlockState(pos, Blocks.AIR.getDefaultState());
				}

				Block.spawnAsEntity(world, pos, getDisguiseModule());
				Block.spawnAsEntity(world, pos, getAllowlistModule());

				if (hasSpeedModule())
					Block.spawnAsEntity(world, pos, new ItemStack(SCContent.speedModule));

				getSentryDisguiseBlockEntity().ifPresent(be -> be.removeModule(ModuleType.DISGUISE, false));
				world.setBlockState(getPosition(), world.getBlockState(getPosition()).withProperty(SometimesVisibleBlock.INVISIBLE, true));
				dataManager.set(ALLOWLIST, new NBTTagCompound());
				dataManager.set(HAS_SPEED_MODULE, false);
			}
			else if (item == SCContent.sentryRemoteAccessTool)
				item.onItemUse(player, world, pos, hand, EnumFacing.NORTH, 0.0F, 0.0F, 0.0F);
			else if (item == SCContent.universalOwnerChanger) {
				String newOwner = player.getHeldItemMainhand().getDisplayName();

				dataManager.set(OWNER, new Owner(newOwner, PlayerUtils.isPlayerOnline(newOwner) ? PlayerUtils.getPlayerFromName(newOwner).getUniqueID().toString() : "ownerUUID"));
				PlayerUtils.sendMessageToPlayer(player, Utils.localize("item.securitycraft:universalOwnerChanger.name"), Utils.localize("messages.securitycraft:universalOwnerChanger.changed", newOwner), TextFormatting.GREEN);
			}
			else if (!world.isRemote)
				toggleMode(player);

			player.swingArm(EnumHand.MAIN_HAND);
			return true;
		}
		else if (!isOwnedBy(player) && hand == EnumHand.MAIN_HAND && player.isCreative() && (player.isSneaking() || player.getHeldItemMainhand().getItem() == SCContent.universalBlockRemover))
			remove();

		return super.processInteract(player, hand);
	}

	/**
	 * Cleanly removes this sentry from the world, dropping the module and removing the block the sentry is disguised with
	 */
	public void remove() {
		BlockPos pos = getPosition();
		ItemStack sentryStack = new ItemStack(SCContent.sentry);

		if (hasCustomName())
			sentryStack.setStackDisplayName(getCustomNameTag());

		Block.spawnAsEntity(world, pos, sentryStack);
		Block.spawnAsEntity(world, pos, getDisguiseModule()); //if there is none, nothing will drop
		Block.spawnAsEntity(world, pos, getAllowlistModule()); //if there is none, nothing will drop
		world.setBlockState(pos, Blocks.AIR.getDefaultState());

		if (hasSpeedModule())
			Block.spawnAsEntity(world, pos, new ItemStack(SCContent.speedModule));

		setDead();
	}

	@Override
	public void onKillCommand() {
		remove();
	}

	/**
	 * Sets this sentry's mode to the next one and sends the player a message about the switch
	 *
	 * @param player The player to send the message to
	 */
	public void toggleMode(EntityPlayer player) {
		toggleMode(player, dataManager.get(MODE) + 1, true);
	}

	/**
	 * Sets this sentry's mode to the given mode (or 0 if the mode is not one of 0, 1, 2) and sends the player a message about
	 * the switch if wanted
	 *
	 * @param player The player to send the message to
	 * @param mode The mode (int) to switch to (instead of sequentially toggling)
	 * @param sendMessage Whether or not to send a message to the player
	 */
	public void toggleMode(EntityPlayer player, int mode, boolean sendMessage) {
		if (mode < 0 || mode >= SentryMode.values().length)
			mode = 0;

		dataManager.set(MODE, mode);

		if (sendMessage)
			player.sendStatusMessage(Utils.localize(SentryMode.values()[mode].getModeKey()).appendSibling(Utils.localize(SentryMode.values()[mode].getDescriptionKey())), true);
	}

	@Override
	public void setAttackTarget(EntityLivingBase target) {
		if (isShutDown()) {
			super.setAttackTarget(null);
			return;
		}

		dataManager.set(HAS_TARGET, target != null);
		super.setAttackTarget(target);
	}

	public boolean hasTarget() {
		return dataManager.get(HAS_TARGET);
	}

	@Override
	public float getEyeHeight() { //the sentry's eyes are higher so that it can see players even if it's inside a block when disguised - this also makes bullets spawn higher
		return 1.6F;
	}

	@Override
	public void attackEntityWithRangedAttack(EntityLivingBase target, float distanceFactor) {
		//don't shoot if somehow a non player is a target, or if the player is in spectator or creative mode
		if (target instanceof EntityPlayer && (((EntityPlayer) target).isSpectator() || ((EntityPlayer) target).isCreative()))
			return;

		//also don't shoot if the target is too far away
		if (getDistanceSq(target) > MAX_TARGET_DISTANCE * MAX_TARGET_DISTANCE)
			return;

		if (isShutDown())
			return;

		TileEntity te = world.getTileEntity(getPosition().down());
		IProjectile throwableEntity = null;
		SoundEvent shootSound = SoundEvents.ENTITY_ARROW_SHOOT;
		BehaviorProjectileDispense pdb = null;
		IItemHandler handler = null;

		if (te instanceof KeypadChestBlockEntity)
			handler = ((KeypadChestBlockEntity) te).getHandlerForSentry(this);
		else if (te != null)
			handler = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.UP);

		if (handler != null) {
			for (int i = 0; i < handler.getSlots(); i++) {
				ItemStack stack = handler.getStackInSlot(i);

				if (!stack.isEmpty()) {
					IBehaviorDispenseItem dispenseBehavior = ((BlockDispenser) Blocks.DISPENSER).getBehavior(stack);

					if (dispenseBehavior instanceof BehaviorProjectileDispense) {
						ItemStack extracted = handler.extractItem(i, 1, false);
						Vec3d vec = getPositionVector();

						pdb = ((BehaviorProjectileDispense) dispenseBehavior);
						throwableEntity = pdb.getProjectileEntity(world, new PositionImpl(vec.x, vec.y + 1.6D, vec.z), extracted);

						if (throwableEntity instanceof EntityArrow)
							((EntityArrow) throwableEntity).shootingEntity = this;
						else if (throwableEntity instanceof EntityThrowable)
							((EntityThrowable) throwableEntity).thrower = this;

						shootSound = null;
						break;
					}
				}
			}
		}

		if (throwableEntity == null)
			throwableEntity = new Bullet(world, this);

		double baseY = target.posY + target.getEyeHeight() - 1.100000023841858D;
		double x = target.posX - posX;
		double y = baseY - ((Entity) throwableEntity).posY;
		double z = target.posZ - posZ;
		float yOffset = MathHelper.sqrt(x * x + z * z) * 0.2F;

		dataManager.set(HEAD_ROTATION, (float) (MathHelper.atan2(x, -z) * (180D / Math.PI)));
		throwableEntity.shoot(x, y + yOffset, z, 1.6F, 0.0F); //no inaccuracy for sentries!

		if (shootSound == null) {
			if (pdb != null)
				pdb.playDispenseSound(new BlockSourceImpl(world, getPosition()));
		}
		else
			playSound(shootSound, 1.0F, 1.0F / (getRNG().nextFloat() * 0.4F + 0.8F));

		final Entity entity = (Entity) throwableEntity; //reee

		Utils.addScheduledTask(world, () -> world.spawnEntity(entity));
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound tag) {
		tag.setTag("TileEntityData", getOwnerTag());
		tag.setTag("InstalledWhitelist", getAllowlistModule().writeToNBT(new NBTTagCompound()));
		tag.setBoolean("HasSpeedModule", hasSpeedModule());
		tag.setInteger("SentryMode", dataManager.get(MODE));
		tag.setBoolean("HasTarget", hasTarget());
		tag.setFloat("HeadRotation", dataManager.get(HEAD_ROTATION));
		tag.setBoolean("ShutDown", isShutDown());
		super.writeEntityToNBT(tag);
	}

	private NBTTagCompound getOwnerTag() {
		NBTTagCompound tag = new NBTTagCompound();
		Owner owner = dataManager.get(OWNER);

		owner.save(tag, needsValidation());
		return tag;
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound tag) {
		NBTTagCompound teTag = tag.getCompoundTag("TileEntityData");
		Owner owner = Owner.fromCompound(teTag);
		float savedHeadRotation = tag.getFloat("HeadRotation");

		dataManager.set(OWNER, owner);

		if (tag.hasKey("InstalledModule"))
			oldModule = new ItemStack(tag.getCompoundTag("InstalledModule"));
		else
			oldModule = ItemStack.EMPTY;

		dataManager.set(ALLOWLIST, tag.getCompoundTag("InstalledWhitelist"));
		dataManager.set(HAS_SPEED_MODULE, tag.getBoolean("HasSpeedModule"));
		dataManager.set(MODE, tag.getInteger("SentryMode"));
		dataManager.set(HAS_TARGET, tag.getBoolean("HasTarget"));
		dataManager.set(SHUT_DOWN, tag.getBoolean("ShutDown"));
		dataManager.set(HEAD_ROTATION, savedHeadRotation);
		oHeadRotation = savedHeadRotation;
		headRotation = savedHeadRotation;
		super.readEntityFromNBT(tag);
	}

	@Override
	public void notifyDataManagerChange(DataParameter<?> data) {
		super.notifyDataManagerChange(data);

		if (world.isRemote && data.equals(HAS_TARGET) && !hasReceivedEntityData) {
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
		dataManager.set(OWNER, new Owner(name, uuid));
	}

	@Override
	public Owner getOwner() {
		return dataManager.get(OWNER);
	}

	@Override
	public void onOwnerChanged(IBlockState state, World level, BlockPos pos, EntityPlayer player, Owner oldOwner, Owner newOwner) {}

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
				world.setBlockState(getPosition(), world.getBlockState(getPosition()).withProperty(SometimesVisibleBlock.INVISIBLE, false));
			});
		}
	}

	/**
	 * Sets the sentry's allowlist module
	 *
	 * @param module The module to set
	 */
	public void setAllowlistModule(ItemStack module) {
		dataManager.set(ALLOWLIST, module.writeToNBT(new NBTTagCompound()));
	}

	/**
	 * Sets whether this sentry has a speed module installed
	 *
	 * @param hasSpeedModule true to set that this sentry has a speed module, false otherwise
	 */
	public void setHasSpeedModule(boolean hasSpeedModule) {
		dataManager.set(HAS_SPEED_MODULE, hasSpeedModule);
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
		NBTTagCompound tag = dataManager.get(ALLOWLIST);

		if (tag == null || tag.isEmpty())
			return ItemStack.EMPTY;
		else
			return new ItemStack(tag);
	}

	public boolean hasSpeedModule() {
		return dataManager.get(HAS_SPEED_MODULE);
	}

	/**
	 * @return The mode in which the sentry is currently in, CAMOUFLAGE_HP as a fallback if the saved mode is not a valid mode
	 */
	public SentryMode getMode() {
		int mode = dataManager.get(MODE);

		return mode < 0 || mode >= SentryMode.values().length ? SentryMode.CAMOUFLAGE_HP : SentryMode.values()[mode];
	}

	/**
	 * @param partialTicks Partial ticks
	 * @return The amount of y translation from the head's default position, used for animation
	 */
	public float getHeadYTranslation(float partialTicks) {
		return Utils.lerp(partialTicks, oHeadYTranslation, headYTranslation);
	}

	/**
	 * @return An optional containing the block entity of the block that the sentry uses to disguise itself, or an empty optional
	 *         if it doesn't exist
	 */
	public Optional<DisguisableBlockEntity> getSentryDisguiseBlockEntity() {
		TileEntity be;

		if (world.getBlockState(getPosition()).getBlock() != SCContent.sentryDisguise) {
			world.setBlockState(getPosition(), SCContent.sentryDisguise.getDefaultState());
			be = world.getTileEntity(getPosition());

			if (be instanceof IOwnable) {
				Owner owner = getOwner();

				((IOwnable) be).setOwner(owner.getUUID(), owner.getName());
			}
		}
		else
			be = world.getTileEntity(getPosition());

		if (be instanceof DisguisableBlockEntity)
			return Optional.of((DisguisableBlockEntity) be);
		else
			return Optional.empty();
	}

	public boolean isTargetingAllowedPlayer(EntityLivingBase potentialTarget) {
		if (potentialTarget != null) {
			ItemStack allowlistModule = getAllowlistModule();

			if (allowlistModule.hasTagCompound() && allowlistModule.getTagCompound().getBoolean("affectEveryone"))
				return true;

			List<String> players = ModuleItem.getPlayersFromModule(allowlistModule);

			for (String s : players) {
				if (potentialTarget.getName().equalsIgnoreCase(s))
					return true;
			}

			return ModuleItem.doesModuleHaveTeamOf(allowlistModule, potentialTarget.getName(), world);
		}

		return false;
	}

	public int getShootingSpeed() {
		return hasSpeedModule() ? 10 : 20;
	}

	@Override
	public void shutDown() {
		IEMPAffected.super.shutDown();
		setAttackTarget(null);
	}

	@Override
	public boolean isShutDown() {
		return dataManager.get(SHUT_DOWN);
	}

	@Override
	public void setShutDown(boolean shutDown) {
		dataManager.set(SHUT_DOWN, shutDown);
	}

	//start: disallow sentry to take damage
	@Override
	public boolean attackEntityAsMob(Entity entity) {
		return false;
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float amount) {
		return false;
	}

	@Override
	public boolean canBeAttackedWithItem() {
		return false;
	}

	@Override
	public boolean attackable() {
		return false;
	}
	//end: disallow sentry to take damage

	@Override
	public boolean getCanSpawnHere() {
		return false;
	}

	@Override
	public void jump() {} //sentries don't jump!

	@Override
	public boolean hasPath() {
		return false;
	}

	@Override
	protected void despawnEntity() {} //sentries don't despawn

	//sentries are heavy, so don't push them around!
	@Override
	public void onCollideWithPlayer(EntityPlayer entity) {}

	@Override
	public void move(MoverType type, double x, double y, double z) {} //no moving sentries!

	@Override
	protected void collideWithEntity(Entity entity) {}

	@Override
	protected void collideWithNearbyEntities() {}

	@Override
	public boolean isImmuneToExplosions() {
		return true; //does not get pushed around by explosions
	}

	@Override
	public boolean canBeCollidedWith() {
		return true; //needs to stay true so blocks can't be broken through the sentry
	}

	@Override
	public boolean canBePushed() {
		return false;
	}

	@Override
	public EnumPushReaction getPushReaction() {
		return EnumPushReaction.IGNORE;
	}

	@Override
	public void updateLeashedState() {} //no leashing for sentry

	@Override
	public void setSwingingArms(boolean swingingArms) {} //sentrys don't have arms, do they?

	//this last code is here so the ai task gets executed, which it doesn't for some weird reason
	@Override
	public Random getRNG() {
		return notRandom;
	}

	public boolean animatesUpwards() {
		return animateUpwards;
	}

	public void setAnimateUpwards(boolean animateUpwards) {
		this.animateUpwards = animateUpwards;
	}

	public boolean shouldAnimate() {
		return animate;
	}

	public void setAnimate(boolean animate) {
		this.animate = animate;
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
