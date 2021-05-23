package net.geforcemods.securitycraft.entity;

import java.util.List;
import java.util.Random;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.entity.ai.AttackRangedIfEnabledGoal;
import net.geforcemods.securitycraft.entity.ai.TargetNearestPlayerOrMobGoal;
import net.geforcemods.securitycraft.items.ModuleItem;
import net.geforcemods.securitycraft.network.client.InitSentryAnimation;
import net.geforcemods.securitycraft.util.ModuleUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.PushReaction;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.Pose;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.PlayerEntity;
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
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.fml.network.PacketDistributor;

public class SentryEntity extends CreatureEntity implements IRangedAttackMob //needs to be a creature so it can target a player, ai is also only given to living entities
{
	private static final DataParameter<Owner> OWNER = EntityDataManager.<Owner>createKey(SentryEntity.class, Owner.getSerializer());
	private static final DataParameter<CompoundNBT> DISGUISE_MODULE = EntityDataManager.<CompoundNBT>createKey(SentryEntity.class, DataSerializers.COMPOUND_NBT);
	private static final DataParameter<CompoundNBT> ALLOWLIST = EntityDataManager.<CompoundNBT>createKey(SentryEntity.class, DataSerializers.COMPOUND_NBT);
	private static final DataParameter<Boolean> HAS_SPEED_MODULE = EntityDataManager.<Boolean>createKey(SentryEntity.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Integer> MODE = EntityDataManager.<Integer>createKey(SentryEntity.class, DataSerializers.VARINT);
	public static final DataParameter<Float> HEAD_ROTATION = EntityDataManager.<Float>createKey(SentryEntity.class, DataSerializers.FLOAT);
	public static final float MAX_TARGET_DISTANCE = 20.0F;
	private static final float ANIMATION_STEP_SIZE = 0.025F;
	private static final float UPWARDS_ANIMATION_LIMIT = 0.025F;
	private static final float DOWNWARDS_ANIMATION_LIMIT = 0.9F;
	private float headYTranslation = 0.9F;
	public boolean animateUpwards = false;
	public boolean animate = false;
	private long previousTargetId = Long.MIN_VALUE;

	public SentryEntity(EntityType<SentryEntity> type, World world)
	{
		super(SCContent.eTypeSentry, world);
	}

	public void setupSentry(PlayerEntity owner)
	{
		dataManager.set(OWNER, new Owner(owner.getName().getFormattedText(), PlayerEntity.getUUID(owner.getGameProfile()).toString()));
		dataManager.set(DISGUISE_MODULE, new CompoundNBT());
		dataManager.set(ALLOWLIST, new CompoundNBT());
		dataManager.set(HAS_SPEED_MODULE, false);
		dataManager.set(MODE, SentryMode.CAMOUFLAGE_HP.ordinal());
		dataManager.set(HEAD_ROTATION, 0.0F);
	}

	@Override
	protected void registerData()
	{
		super.registerData();
		dataManager.register(OWNER, new Owner());
		dataManager.register(DISGUISE_MODULE, new CompoundNBT());
		dataManager.register(ALLOWLIST, new CompoundNBT());
		dataManager.register(HAS_SPEED_MODULE, false);
		dataManager.register(MODE, SentryMode.CAMOUFLAGE_HP.ordinal());
		dataManager.register(HEAD_ROTATION, 0.0F);
	}

	@Override
	protected void registerGoals()
	{
		goalSelector.addGoal(1, new AttackRangedIfEnabledGoal(this, this::getShootingSpeed, 10.0F));
		targetSelector.addGoal(1, new TargetNearestPlayerOrMobGoal(this));
	}

	@Override
	public void tick()
	{
		super.tick();

		if(!world.isRemote)
		{
			BlockPos downPos = getPositionUnderneath();

			if(world.getBlockState(downPos).isAir() || world.hasNoCollisions(new AxisAlignedBB(downPos)))
				remove();
		}
		else
		{
			if(!animate && headYTranslation > 0.0F && getMode().isAggressive())
			{
				animateUpwards = true;
				animate = true;
			}

			if(animate) //no else if because animate can be changed in the above if statement
			{
				if(animateUpwards && headYTranslation > UPWARDS_ANIMATION_LIMIT)
				{
					headYTranslation -= ANIMATION_STEP_SIZE;

					if(headYTranslation <= UPWARDS_ANIMATION_LIMIT)
					{
						animateUpwards = false;
						animate = false;
					}
				}
				else if(!animateUpwards && headYTranslation < DOWNWARDS_ANIMATION_LIMIT)
				{
					headYTranslation += ANIMATION_STEP_SIZE;

					if(headYTranslation >= DOWNWARDS_ANIMATION_LIMIT)
					{
						animateUpwards = true;
						animate = false;
					}
				}
			}
		}
	}

	@Override
	public ItemStack getPickedResult(RayTraceResult target)
	{
		return new ItemStack(SCContent.SENTRY.get());
	}

	@Override
	public boolean processInteract(PlayerEntity player, Hand hand)
	{
		BlockPos pos = getPosition();

		if(getOwner().isOwner(player) && hand == Hand.MAIN_HAND)
		{
			Item item = player.getHeldItemMainhand().getItem();

			player.closeScreen();

			if(player.isCrouching())
				remove();
			else if(item == SCContent.UNIVERSAL_BLOCK_REMOVER.get())
			{
				remove();

				if(!player.isCreative())
					player.getHeldItemMainhand().damageItem(1, player, p -> p.sendBreakAnimation(hand));
			}
			else if(item == SCContent.DISGUISE_MODULE.get())
			{
				ItemStack module = getDisguiseModule();

				if(!module.isEmpty()) //drop the old module as to not override it with the new one
				{
					Block.spawnAsEntity(world, pos, module);

					List<Block> blocks = ((ModuleItem)module.getItem()).getBlockAddons(module.getTag());

					if(blocks.size() > 0)
					{
						if(blocks.get(0) == world.getBlockState(pos).getBlock())
							world.setBlockState(pos, Blocks.AIR.getDefaultState());
					}
				}

				setDisguiseModule(player.getHeldItemMainhand());

				if(!player.isCreative())
					player.setItemStackToSlot(EquipmentSlotType.MAINHAND, ItemStack.EMPTY);
			}
			else if(item == SCContent.ALLOWLIST_MODULE.get())
			{
				ItemStack module = getAllowlistModule();

				if(!module.isEmpty()) //drop the old module as to not override it with the new one
					Block.spawnAsEntity(world, pos, module);

				setAllowlistModule(player.getHeldItemMainhand());

				if(!player.isCreative())
					player.setItemStackToSlot(EquipmentSlotType.MAINHAND, ItemStack.EMPTY);
			}
			else if(item == SCContent.SPEED_MODULE.get())
			{
				if(!hasSpeedModule())
				{
					setHasSpeedModule(true);

					if(!player.isCreative())
						player.setItemStackToSlot(EquipmentSlotType.MAINHAND, ItemStack.EMPTY);
				}
			}
			else if(item == SCContent.UNIVERSAL_BLOCK_MODIFIER.get())
			{
				if (!getDisguiseModule().isEmpty())
				{
					List<Block> blocks = ((ModuleItem)getDisguiseModule().getItem()).getBlockAddons(getDisguiseModule().getTag());

					if(blocks.size() > 0)
					{
						if(blocks.get(0) == world.getBlockState(pos).getBlock())
							world.setBlockState(pos, Blocks.AIR.getDefaultState());
					}
				}

				Block.spawnAsEntity(world, pos, getDisguiseModule());
				Block.spawnAsEntity(world, pos, getAllowlistModule());

				if(hasSpeedModule())
					Block.spawnAsEntity(world, pos, new ItemStack(SCContent.SPEED_MODULE.get()));

				dataManager.set(DISGUISE_MODULE, new CompoundNBT());
				dataManager.set(ALLOWLIST, new CompoundNBT());
				dataManager.set(HAS_SPEED_MODULE, false);;
			}
			else if(item == SCContent.REMOTE_ACCESS_SENTRY.get()) //bind/unbind sentry to remote control
				item.onItemUse(new ItemUseContext(player, hand, new BlockRayTraceResult(new Vec3d(0.0D, 0.0D, 0.0D), Direction.NORTH, pos, false)));
			else if(item == Items.NAME_TAG)
			{
				setCustomName(player.getHeldItemMainhand().getDisplayName());
				player.getHeldItemMainhand().shrink(1);
			}
			else if(item == SCContent.UNIVERSAL_OWNER_CHANGER.get())
			{
				String newOwner = player.getHeldItemMainhand().getDisplayName().getFormattedText();

				dataManager.set(OWNER, new Owner(newOwner, PlayerUtils.isPlayerOnline(newOwner) ? PlayerUtils.getPlayerFromName(newOwner).getUniqueID().toString() : "ownerUUID"));
				PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.UNIVERSAL_OWNER_CHANGER.get().getTranslationKey()), Utils.localize("messages.securitycraft:universalOwnerChanger.changed", newOwner), TextFormatting.GREEN);
			}
			else
				toggleMode(player);

			player.swingArm(Hand.MAIN_HAND);
			return true;
		}
		else if(!getOwner().isOwner(player) && hand == Hand.MAIN_HAND && player.isCreative())
		{
			if(player.isCrouching() || player.getHeldItemMainhand().getItem() == SCContent.UNIVERSAL_BLOCK_REMOVER.get())
				remove();
		}

		return super.processInteract(player, hand);
	}

	/**
	 * Cleanly removes this sentry from the world, dropping the module and removing the block the sentry is disguised with
	 */
	@Override
	public void remove()
	{
		BlockPos pos = getPosition();

		if (!getDisguiseModule().isEmpty())
		{
			List<Block> blocks = ((ModuleItem)getDisguiseModule().getItem()).getBlockAddons(getDisguiseModule().getTag());

			if(blocks.size() > 0)
			{
				if(blocks.get(0) == world.getBlockState(pos).getBlock())
					world.setBlockState(pos, Blocks.AIR.getDefaultState());
			}
		}

		super.remove();
		Block.spawnAsEntity(world, pos, new ItemStack(SCContent.SENTRY.get()));
		Block.spawnAsEntity(world, pos, getDisguiseModule()); //if there is none, nothing will drop
		Block.spawnAsEntity(world, pos, getAllowlistModule()); //if there is none, nothing will drop

		if(hasSpeedModule())
			Block.spawnAsEntity(world, pos, new ItemStack(SCContent.SPEED_MODULE.get()));
	}

	@Override
	public void onKillCommand()
	{
		remove();
	}

	/**
	 * Sets this sentry's mode to the next one and sends the player a message about the switch
	 * @param player The player to send the message to
	 */
	public void toggleMode(PlayerEntity player)
	{
		toggleMode(player, dataManager.get(MODE) + 1, true);
	}

	/**
	 * Sets this sentry's mode to the given mode (or 0 if the mode is not one of 0, 1, 2) and sends the player a message about the switch if wanted
	 * @param player The player to send the message to
	 * @param mode The mode (int) to switch to (instead of sequentially toggling)
	 */
	public void toggleMode(PlayerEntity player, int mode, boolean sendMessage)
	{
		if(mode < 0 || mode >= SentryMode.values().length) //bigger than the amount of possible values in case a player sets the value manually by command
			mode = 0;

		dataManager.set(MODE, mode);

		if(sendMessage)
			player.sendStatusMessage(Utils.localize(SentryMode.values()[mode].getModeKey()).appendSibling(Utils.localize(SentryMode.values()[mode].getDescriptionKey())), true);
		else if(!player.world.isRemote)
			SecurityCraft.channel.send(PacketDistributor.ALL.noArg(), new InitSentryAnimation(getPosition(), true, SentryMode.values()[mode].isAggressive()));
	}

	@Override
	public void setAttackTarget(LivingEntity target)
	{
		if(!getMode().isAggressive() && (target == null && previousTargetId != Long.MIN_VALUE || (target != null && previousTargetId != target.getEntityId())))
		{
			animateUpwards = getMode().isCamouflage() && target != null;
			animate = true;
			SecurityCraft.channel.send(PacketDistributor.ALL.noArg(), new InitSentryAnimation(getPosition(), animate, animateUpwards));
		}

		previousTargetId = target == null ? Long.MIN_VALUE : target.getEntityId();
		super.setAttackTarget(target);
	}

	@Override
	public float getEyeHeight(Pose pose) //the sentry's eyes are higher so that it can see players even if it's inside a block when disguised - this also makes bullets spawn higher
	{
		return 1.5F;
	}

	@Override
	public void attackEntityWithRangedAttack(LivingEntity target, float distanceFactor)
	{
		//don't shoot if somehow a non player is a target, or if the player is in spectator or creative mode
		if(target instanceof PlayerEntity && (((PlayerEntity)target).isSpectator() || ((PlayerEntity)target).isCreative()))
			return;

		//also don't shoot if the target is too far away
		if(getDistanceSq(target) > MAX_TARGET_DISTANCE * MAX_TARGET_DISTANCE)
			return;

		BulletEntity throwableEntity = new BulletEntity(world, this);
		double baseY = target.getPosY() + target.getEyeHeight() - 1.100000023841858D;
		double x = target.getPosX() - getPosX();
		double y = baseY - throwableEntity.getPosY();
		double z = target.getPosZ() - getPosZ();
		float yOffset = MathHelper.sqrt(x * x + z * z) * 0.2F;

		throwableEntity.setRawPosition(throwableEntity.getPosX(), throwableEntity.getPosY() - 0.1F, throwableEntity.getPosZ());
		dataManager.set(HEAD_ROTATION, (float)(MathHelper.atan2(x, -z) * (180D / Math.PI)));
		throwableEntity.shoot(x, y + yOffset, z, 1.6F, 0.0F); //no inaccuracy for sentries!
		playSound(SoundEvents.ENTITY_ARROW_SHOOT, 1.0F, 1.0F / (getRNG().nextFloat() * 0.4F + 0.8F));
		world.addEntity(throwableEntity);
	}

	@Override
	public void writeAdditional(CompoundNBT tag)
	{
		tag.put("TileEntityData", getOwnerTag());
		tag.put("InstalledModule", getDisguiseModule().write(new CompoundNBT()));
		tag.put("InstalledWhitelist", getAllowlistModule().write(new CompoundNBT()));
		tag.putBoolean("HasSpeedModule", hasSpeedModule());
		tag.putInt("SentryMode", dataManager.get(MODE));
		tag.putFloat("HeadRotation", dataManager.get(HEAD_ROTATION));
		super.writeAdditional(tag);
	}

	private CompoundNBT getOwnerTag()
	{
		CompoundNBT tag = new CompoundNBT();
		Owner owner = dataManager.get(OWNER);

		tag.putString("owner", owner.getName());
		tag.putString("ownerUUID", owner.getUUID());
		return tag;
	}

	@Override
	public void readAdditional(CompoundNBT tag)
	{
		CompoundNBT teTag = tag.getCompound("TileEntityData");
		String name = teTag.getString("owner");
		String uuid = teTag.getString("ownerUUID");

		dataManager.set(OWNER, new Owner(name, uuid));
		dataManager.set(DISGUISE_MODULE, tag.getCompound("InstalledModule"));
		dataManager.set(ALLOWLIST, tag.getCompound("InstalledWhitelist"));
		dataManager.set(HAS_SPEED_MODULE, tag.getBoolean("HasSpeedModule"));
		dataManager.set(MODE, tag.getInt("SentryMode"));
		dataManager.set(HEAD_ROTATION, tag.getFloat("HeadRotation"));
		super.readAdditional(tag);
	}

	/**
	 * @return The owner of this sentry
	 */
	public Owner getOwner()
	{
		return dataManager.get(OWNER);
	}

	/**
	 * Sets the sentry's disguise module and places a block if possible
	 * @param module The module to set
	 */
	public void setDisguiseModule(ItemStack module)
	{
		List<ItemStack> blocks = ((ModuleItem)module.getItem()).getAddons(module.getTag());

		if(blocks.size() > 0)
		{
			ItemStack disguiseStack = blocks.get(0);
			BlockState state = Block.getBlockFromItem(disguiseStack.getItem()).getDefaultState();

			if (world.getBlockState(getPosition()).isAir(world, getPosition()))
				world.setBlockState(getPosition(), state.getShape(world, getPosition()) == VoxelShapes.fullCube() ? state : Blocks.AIR.getDefaultState());
		}

		dataManager.set(DISGUISE_MODULE, module.write(new CompoundNBT()));
	}

	/**
	 * Sets the sentry's allowlist module
	 * @param module The module to set
	 */
	public void setAllowlistModule(ItemStack module)
	{
		dataManager.set(ALLOWLIST, module.write(new CompoundNBT()));
	}

	/**
	 * Sets whether this sentry has a speed module installed
	 * @param hasSpeedModule true to set that this sentry has a speed module, false otherwise
	 */
	public void setHasSpeedModule(boolean hasSpeedModule)
	{
		dataManager.set(HAS_SPEED_MODULE, hasSpeedModule);
	}

	/**
	 * @return The disguise module that is added to this sentry. ItemStack.EMPTY if none available
	 */
	public ItemStack getDisguiseModule()
	{
		CompoundNBT tag = dataManager.get(DISGUISE_MODULE);

		if(tag == null || tag.isEmpty())
			return ItemStack.EMPTY;
		else
			return ItemStack.read(tag);
	}

	/**
	 * @return The allowlist module that is added to this sentry. ItemStack.EMPTY if none available
	 */
	public ItemStack getAllowlistModule()
	{
		CompoundNBT tag = dataManager.get(ALLOWLIST);

		if(tag == null || tag.isEmpty())
			return ItemStack.EMPTY;
		else
			return ItemStack.read(tag);
	}

	public boolean hasSpeedModule()
	{
		return dataManager.get(HAS_SPEED_MODULE);
	}

	/**
	 * @return The mode in which the sentry is currently in, CAMOUFLAGE_HP as a fallback if the saved mode is not a valid mode
	 */
	public SentryMode getMode()
	{
		int mode = dataManager.get(MODE);

		return mode < 0 || mode >= SentryMode.values().length ? SentryMode.CAMOUFLAGE_HP : SentryMode.values()[mode];
	}

	/**
	 * @return The amount of y translation from the head's default position, used for animation
	 */
	public float getHeadYTranslation()
	{
		return headYTranslation;
	}

	public boolean isTargetingAllowedPlayer(LivingEntity potentialTarget)
	{
		if(potentialTarget != null)
		{
			List<String> players = ModuleUtils.getPlayersFromModule(getAllowlistModule());

			for(String s : players)
			{
				if(potentialTarget.getName().getUnformattedComponentText().equalsIgnoreCase(s))
					return true;
			}
		}

		return false;
	}

	public int getShootingSpeed()
	{
		return hasSpeedModule() ? 5 : 10;
	}

	//start: disallow sentry to take damage
	@Override
	public boolean attackEntityAsMob(Entity entity)
	{
		return false;
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float amount)
	{
		return false;
	}

	@Override
	public boolean canBeAttackedWithItem()
	{
		return false;
	}

	@Override
	public boolean attackable()
	{
		return false;
	}
	//end: disallow sentry to take damage

	@Override
	public boolean canSpawn(IWorld world, SpawnReason reason)
	{
		return false;
	}

	@Override
	public void jump() {} //sentries don't jump!

	@Override
	public boolean hasPath()
	{
		return false;
	}

	@Override
	public void checkDespawn() {} //sentries don't despawn

	@Override
	public boolean canDespawn(double distanceClosestToPlayer)
	{
		return false; //sentries don't despawn
	}

	//sentries are heavy, so don't push them around!
	@Override
	public void onCollideWithPlayer(PlayerEntity entity) {}

	@Override
	public void move(MoverType type, Vec3d vec) {} //no moving sentries!

	@Override
	protected void collideWithEntity(Entity entity) {}

	@Override
	protected void collideWithNearbyEntities() {}

	@Override
	public boolean isImmuneToExplosions()
	{
		return true; //does not get pushed around by explosions
	}

	@Override
	public boolean canBeCollidedWith()
	{
		return true; //needs to stay true so blocks can't be broken through the sentry
	}

	@Override
	public boolean canBePushed()
	{
		return false;
	}

	@Override
	public PushReaction getPushReaction()
	{
		return PushReaction.IGNORE;
	}

	@Override
	public void updateLeashedState() {} //no leashing for sentry

	//this last code is here so the ai task gets executed, which it doesn't for some weird reason
	@Override
	public Random getRNG()
	{
		return notRandom;
	}

	@Override
	public IPacket<?> createSpawnPacket()
	{
		return NetworkHooks.getEntitySpawningPacket(this);
	}

	private static Random notRandom = new NotRandom();

	private static class NotRandom extends Random
	{
		@Override
		public int nextInt(int bound)
		{
			return 0;
		}
	}

	public static enum SentryMode
	{
		CAMOUFLAGE_HP(1, 0, 1), CAMOUFLAGE_H(1, 1, 3), CAMOUFLAGE_P(1, 2, 5), AGGRESSIVE_HP(0, 0, 0), AGGRESSIVE_H(0, 1, 2), AGGRESSIVE_P(0, 2, 4), IDLE(-1, -1, 6);

		private final int type;
		private final int attack;
		private final int descriptionKeyIndex;

		SentryMode(int type, int attack, int descriptionKeyIndex)
		{
			this.type = type;
			this.attack = attack;
			this.descriptionKeyIndex = descriptionKeyIndex;
		}

		public boolean isAggressive()
		{
			return type == 0;
		}

		public boolean isCamouflage()
		{
			return type == 1;
		}

		public boolean attacksHostile()
		{
			return attack == 0 || attack == 1;
		}

		public boolean attacksPlayers()
		{
			return attack == 0 || attack == 2;
		}

		public String getModeKey()
		{
			String key = "messages.securitycraft:sentry.mode";

			return isAggressive() ? key + "0" : (isCamouflage() ? key + "1" : key + "2");
		}

		public String getTargetKey()
		{
			String key = "gui.securitycraft:srat.targets";

			return attacksHostile() && attacksPlayers() ? key + "1" : (attacksHostile() ? key + "2" : (attacksPlayers() ? key + "3" : ""));
		}

		public String getDescriptionKey()
		{
			return "messages.securitycraft:sentry.descriptionMode" + descriptionKeyIndex;
		}
	}
}
