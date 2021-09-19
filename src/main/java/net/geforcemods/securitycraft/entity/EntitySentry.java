package net.geforcemods.securitycraft.entity;

import java.util.List;
import java.util.Random;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.entity.ai.EntityAIAttackRangedIfEnabled;
import net.geforcemods.securitycraft.entity.ai.EntityAITargetNearestPlayerOrMob;
import net.geforcemods.securitycraft.items.ItemModule;
import net.geforcemods.securitycraft.network.client.InitSentryAnimation;
import net.geforcemods.securitycraft.tileentity.TileEntityKeypadChest;
import net.geforcemods.securitycraft.util.ModuleUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.geforcemods.securitycraft.util.WorldUtils;
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

public class EntitySentry extends EntityCreature implements IRangedAttackMob //needs to be a creature so it can target a player, ai is also only given to living entities
{
	private static final DataParameter<Owner> OWNER = EntityDataManager.<Owner>createKey(EntitySentry.class, Owner.getSerializer());
	private static final DataParameter<NBTTagCompound> DISGUISE_MODULE = EntityDataManager.<NBTTagCompound>createKey(EntitySentry.class, DataSerializers.COMPOUND_TAG);
	private static final DataParameter<NBTTagCompound> ALLOWLIST = EntityDataManager.<NBTTagCompound>createKey(EntitySentry.class, DataSerializers.COMPOUND_TAG);
	private static final DataParameter<Boolean> HAS_SPEED_MODULE = EntityDataManager.<Boolean>createKey(EntitySentry.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Integer> MODE = EntityDataManager.<Integer>createKey(EntitySentry.class, DataSerializers.VARINT);
	public static final DataParameter<Float> HEAD_ROTATION = EntityDataManager.<Float>createKey(EntitySentry.class, DataSerializers.FLOAT);
	public static final float MAX_TARGET_DISTANCE = 20.0F;
	private static final float ANIMATION_STEP_SIZE = 0.025F;
	private static final float UPWARDS_ANIMATION_LIMIT = 0.025F;
	private static final float DOWNWARDS_ANIMATION_LIMIT = 0.9F;
	private float headYTranslation = 0.9F;
	public boolean animateUpwards = true;
	public boolean animate = false;
	private long previousTargetId = Long.MIN_VALUE;

	public EntitySentry(World world)
	{
		super(world);
		setSize(1.0F, 1.0F);
	}

	public EntitySentry(World world, EntityPlayer owner)
	{
		this(world, new Owner(owner.getName(), EntityPlayer.getUUID(owner.getGameProfile()).toString()));
	}

	public EntitySentry(World world, Owner owner)
	{
		super(world);
		setSize(1.0F, 1.0F);
		dataManager.set(OWNER, owner);
		dataManager.set(DISGUISE_MODULE, new NBTTagCompound());
		dataManager.set(ALLOWLIST, new NBTTagCompound());
		dataManager.set(HAS_SPEED_MODULE, false);
		dataManager.set(MODE, EnumSentryMode.CAMOUFLAGE_HP.ordinal());
		dataManager.set(HEAD_ROTATION, 0.0F);
	}

	@Override
	protected void entityInit()
	{
		super.entityInit();
		dataManager.register(OWNER, new Owner());
		dataManager.register(DISGUISE_MODULE, new NBTTagCompound());
		dataManager.register(ALLOWLIST, new NBTTagCompound());
		dataManager.register(HAS_SPEED_MODULE, false);
		dataManager.register(MODE, EnumSentryMode.CAMOUFLAGE_HP.ordinal());
		dataManager.register(HEAD_ROTATION, 0.0F);
	}

	@Override
	protected void initEntityAI()
	{
		tasks.addTask(1, new EntityAIAttackRangedIfEnabled(this, this::getShootingSpeed, 10.0F));
		targetTasks.addTask(1, new EntityAITargetNearestPlayerOrMob(this));
	}

	@Override
	public void onEntityUpdate()
	{
		super.onEntityUpdate();

		if(!world.isRemote)
		{
			BlockPos downPos = getPosition().down();
			IBlockState state = world.getBlockState(downPos);

			if(state.getBlock().isAir(state, world, downPos) || world.getCollisionBoxes(null, new AxisAlignedBB(downPos)).isEmpty())
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
		return new ItemStack(SCContent.sentry);
	}

	@Override
	public boolean processInteract(EntityPlayer player, EnumHand hand)
	{
		BlockPos pos = getPosition();

		if(getOwner().isOwner(player) && hand == EnumHand.MAIN_HAND)
		{
			Item item = player.getHeldItemMainhand().getItem();

			player.closeScreen();

			if(player.isSneaking())
				remove();
			else if(item == SCContent.universalBlockRemover)
			{
				remove();

				if(!player.isCreative())
					player.getHeldItemMainhand().damageItem(1, player);
			}
			else if(item == SCContent.disguiseModule)
			{
				ItemStack module = getDisguiseModule();

				if(!module.isEmpty()) //drop the old module as to not override it with the new one
				{
					Block.spawnAsEntity(world, pos, module);

					List<Block> blocks = ((ItemModule)module.getItem()).getBlockAddons(module.getTagCompound());

					if(blocks.size() > 0)
					{
						if(blocks.get(0) == world.getBlockState(pos).getBlock())
							world.setBlockState(pos, Blocks.AIR.getDefaultState());
					}
				}

				setDisguiseModule(player.getHeldItemMainhand());

				if(!player.isCreative())
					player.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, ItemStack.EMPTY);
			}
			else if(item == SCContent.allowlistModule)
			{
				ItemStack module = getAllowlistModule();

				if(!module.isEmpty()) //drop the old module as to not override it with the new one
					Block.spawnAsEntity(world, pos, module);

				setAllowlistModule(player.getHeldItemMainhand());

				if(!player.isCreative())
					player.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, ItemStack.EMPTY);
			}
			else if(item == SCContent.speedModule)
			{
				if(!hasSpeedModule())
				{
					setHasSpeedModule(true);

					if(!player.isCreative())
						player.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, ItemStack.EMPTY);
				}
			}
			else if(item == SCContent.universalBlockModifier)
			{
				if (!getDisguiseModule().isEmpty())
				{
					List<Block> blocks = ((ItemModule)getDisguiseModule().getItem()).getBlockAddons(getDisguiseModule().getTagCompound());

					if(blocks.size() > 0)
					{
						if(blocks.get(0) == world.getBlockState(pos).getBlock())
							world.setBlockState(pos, Blocks.AIR.getDefaultState());
					}
				}

				Block.spawnAsEntity(world, pos, getDisguiseModule());
				Block.spawnAsEntity(world, pos, getAllowlistModule());

				if(hasSpeedModule())
					Block.spawnAsEntity(world, pos, new ItemStack(SCContent.speedModule));

				dataManager.set(DISGUISE_MODULE, new NBTTagCompound());
				dataManager.set(ALLOWLIST, new NBTTagCompound());
				dataManager.set(HAS_SPEED_MODULE, false);
			}
			else if(item == SCContent.remoteAccessSentry) //bind/unbind sentry to remote control
				item.onItemUse(player, world, pos, hand, EnumFacing.NORTH, 0.0F, 0.0F, 0.0F);
			else if(item == Items.NAME_TAG)
			{
				setCustomNameTag(player.getHeldItemMainhand().getDisplayName());
				player.getHeldItemMainhand().shrink(1);
			}
			else if(item == SCContent.universalOwnerChanger)
			{
				String newOwner = player.getHeldItemMainhand().getDisplayName();

				dataManager.set(OWNER, new Owner(newOwner, PlayerUtils.isPlayerOnline(newOwner) ? PlayerUtils.getPlayerFromName(newOwner).getUniqueID().toString() : "ownerUUID"));
				PlayerUtils.sendMessageToPlayer(player, Utils.localize("item.securitycraft:universalOwnerChanger.name"), Utils.localize("messages.securitycraft:universalOwnerChanger.changed", newOwner), TextFormatting.GREEN);
			}
			else
				toggleMode(player);

			player.swingArm(EnumHand.MAIN_HAND);
			return true;
		}
		else if(!getOwner().isOwner(player) && hand == EnumHand.MAIN_HAND && player.isCreative())
		{
			if(player.isSneaking() || player.getHeldItemMainhand().getItem() == SCContent.universalBlockRemover)
				remove();
		}

		return super.processInteract(player, hand);
	}

	/**
	 * Cleanly removes this sentry from the world, dropping the module and removing the block the sentry is disguised with
	 */
	public void remove()
	{
		BlockPos pos = getPosition();

		if (!getDisguiseModule().isEmpty())
		{
			List<Block> blocks = ((ItemModule)getDisguiseModule().getItem()).getBlockAddons(getDisguiseModule().getTagCompound());

			if(blocks.size() > 0)
			{
				if(blocks.get(0) == world.getBlockState(pos).getBlock())
					world.setBlockState(pos, Blocks.AIR.getDefaultState());
			}
		}

		Block.spawnAsEntity(world, pos, new ItemStack(SCContent.sentry));
		Block.spawnAsEntity(world, pos, getDisguiseModule()); //if there is none, nothing will drop
		Block.spawnAsEntity(world, pos, getAllowlistModule()); //if there is none, nothing will drop

		if(hasSpeedModule())
			Block.spawnAsEntity(world, pos, new ItemStack(SCContent.speedModule));

		setDead();
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
	public void toggleMode(EntityPlayer player)
	{
		toggleMode(player, dataManager.get(MODE) + 1, true);
	}

	/**
	 * Sets this sentry's mode to the given mode (or 0 if the mode is not one of 0, 1, 2) and sends the player a message about the switch if wanted
	 * @param player The player to send the message to
	 * @param mode The mode (int) to switch to (instead of sequentially toggling)
	 * @param sendMessage Whether or not to send a message to the player
	 */
	public void toggleMode(EntityPlayer player, int mode, boolean sendMessage)
	{
		if(mode < 0 || mode >= EnumSentryMode.values().length) //bigger than the amount of possible values in case a player sets the value manually by command
			mode = 0;

		dataManager.set(MODE, mode);

		if(sendMessage)
			player.sendStatusMessage(Utils.localize(EnumSentryMode.values()[mode].getModeKey()).appendSibling(Utils.localize(EnumSentryMode.values()[mode].getDescriptionKey())), true);

		if(!player.world.isRemote)
			SecurityCraft.network.sendToAll(new InitSentryAnimation(getPosition(), true, EnumSentryMode.values()[mode].isAggressive()));
	}

	@Override
	public void setAttackTarget(EntityLivingBase target)
	{
		if(!getMode().isAggressive() && (target == null && previousTargetId != Long.MIN_VALUE || (target != null && previousTargetId != target.getEntityId())))
		{
			animateUpwards = getMode().isCamouflage() && target != null;
			animate = true;
			SecurityCraft.network.sendToAll(new InitSentryAnimation(getPosition(), animate, animateUpwards));
		}

		previousTargetId = target == null ? Long.MIN_VALUE : target.getEntityId();
		super.setAttackTarget(target);
	}

	@Override
	public float getEyeHeight() //the sentry's eyes are higher so that it can see players even if it's inside a block when disguised - this also makes bullets spawn higher
	{
		return 1.5F;
	}

	@Override
	public void attackEntityWithRangedAttack(EntityLivingBase target, float distanceFactor)
	{
		//don't shoot if somehow a non player is a target, or if the player is in spectator or creative mode
		if(target instanceof EntityPlayer && (((EntityPlayer)target).isSpectator() || ((EntityPlayer)target).isCreative()))
			return;

		//also don't shoot if the target is too far away
		if(getDistanceSq(target) > MAX_TARGET_DISTANCE * MAX_TARGET_DISTANCE)
			return;

		TileEntity te = world.getTileEntity(getPosition().down());
		IProjectile throwableEntity = null;
		SoundEvent shootSound = SoundEvents.ENTITY_ARROW_SHOOT;
		BehaviorProjectileDispense pdb = null;
		IItemHandler handler = null;

		if(te instanceof TileEntityKeypadChest)
			handler = ((TileEntityKeypadChest)te).getHandlerForSentry(this);
		else if(te != null)
			handler = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.UP);

		if(handler != null)
		{
			for(int i = 0; i < handler.getSlots(); i++)
			{
				ItemStack stack = handler.getStackInSlot(i);

				if(!stack.isEmpty())
				{
					IBehaviorDispenseItem dispenseBehavior = ((BlockDispenser)Blocks.DISPENSER).getBehavior(stack);

					if(dispenseBehavior instanceof BehaviorProjectileDispense)
					{
						ItemStack extracted = handler.extractItem(i, 1, false);
						Vec3d vec = getPositionVector();

						pdb = ((BehaviorProjectileDispense)dispenseBehavior);
						throwableEntity = pdb.getProjectileEntity(world, new PositionImpl(vec.x, vec.y + 1.6D, vec.z), extracted);

						if(throwableEntity instanceof EntityArrow)
							((EntityArrow)throwableEntity).shootingEntity = this;
						else if(throwableEntity instanceof EntityThrowable)
							((EntityThrowable)throwableEntity).thrower = this;

						shootSound = null;
						break;
					}
				}
			}
		}

		if(throwableEntity == null)
			throwableEntity = new EntityBullet(world, this);

		double baseY = target.posY + target.getEyeHeight() - 1.100000023841858D;
		double x = target.posX - posX;
		double y = baseY - ((Entity)throwableEntity).posY;
		double z = target.posZ - posZ;
		float yOffset = MathHelper.sqrt(x * x + z * z) * 0.2F;

		dataManager.set(HEAD_ROTATION, (float)(MathHelper.atan2(x, -z) * (180D / Math.PI)));
		throwableEntity.shoot(x, y + yOffset, z, 1.6F, 0.0F); //no inaccuracy for sentries!

		if(shootSound == null)
			pdb.playDispenseSound(new BlockSourceImpl(world, getPosition()));
		else
			playSound(shootSound, 1.0F, 1.0F / (getRNG().nextFloat() * 0.4F + 0.8F));

		final Entity entity = (Entity)throwableEntity; //reee

		WorldUtils.addScheduledTask(world, () -> world.spawnEntity(entity));
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound tag)
	{
		tag.setTag("TileEntityData", getOwnerTag());
		tag.setTag("InstalledModule", getDisguiseModule().writeToNBT(new NBTTagCompound()));
		tag.setTag("InstalledWhitelist", getAllowlistModule().writeToNBT(new NBTTagCompound()));
		tag.setBoolean("HasSpeedModule", hasSpeedModule());
		tag.setInteger("SentryMode", dataManager.get(MODE));
		tag.setFloat("HeadRotation", dataManager.get(HEAD_ROTATION));
		super.writeEntityToNBT(tag);
	}

	private NBTTagCompound getOwnerTag()
	{
		NBTTagCompound tag = new NBTTagCompound();
		Owner owner = dataManager.get(OWNER);

		owner.writeToNBT(tag, false);
		return tag;
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound tag)
	{
		NBTTagCompound teTag = tag.getCompoundTag("TileEntityData");
		Owner owner = Owner.fromCompound(teTag);

		dataManager.set(OWNER, owner);
		dataManager.set(DISGUISE_MODULE, tag.getCompoundTag("InstalledModule"));
		dataManager.set(ALLOWLIST, tag.getCompoundTag("InstalledWhitelist"));
		dataManager.set(HAS_SPEED_MODULE, tag.getBoolean("HasSpeedModule"));
		dataManager.set(MODE, tag.getInteger("SentryMode"));
		dataManager.set(HEAD_ROTATION, tag.getFloat("HeadRotation"));
		super.readEntityFromNBT(tag);
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
		List<ItemStack> blocks = ((ItemModule)module.getItem()).getAddons(module.getTagCompound());

		if(blocks.size() > 0)
		{
			ItemStack disguiseStack = blocks.get(0);
			IBlockState state = Block.getBlockFromItem(disguiseStack.getItem()).getStateFromMeta(disguiseStack.getHasSubtypes() ? disguiseStack.getItemDamage() : 0);

			if (world.isAirBlock(getPosition()))
				world.setBlockState(getPosition(), state.isFullBlock() ? state : Blocks.AIR.getDefaultState());
		}

		dataManager.set(DISGUISE_MODULE, module.writeToNBT(new NBTTagCompound()));
	}

	/**
	 * Sets the sentry's allowlist module
	 * @param module The module to set
	 */
	public void setAllowlistModule(ItemStack module)
	{
		dataManager.set(ALLOWLIST, module.writeToNBT(new NBTTagCompound()));
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
		NBTTagCompound tag = dataManager.get(DISGUISE_MODULE);

		if(tag == null || tag.isEmpty())
			return ItemStack.EMPTY;
		else
			return new ItemStack(tag);
	}

	/**
	 * @return The allowlist module that is added to this sentry. ItemStack.EMPTY if none available
	 */
	public ItemStack getAllowlistModule()
	{
		NBTTagCompound tag = dataManager.get(ALLOWLIST);

		if(tag == null || tag.isEmpty())
			return ItemStack.EMPTY;
		else
			return new ItemStack(tag);
	}

	public boolean hasSpeedModule()
	{
		return dataManager.get(HAS_SPEED_MODULE);
	}

	/**
	 * @return The mode in which the sentry is currently in, CAMOUFLAGE_HP as a fallback if the saved mode is not a valid mode
	 */
	public EnumSentryMode getMode()
	{
		int mode = dataManager.get(MODE);

		return mode < 0 || mode >= EnumSentryMode.values().length ? EnumSentryMode.CAMOUFLAGE_HP : EnumSentryMode.values()[mode];
	}

	/**
	 * @return The amount of y translation from the head's default position, used for animation
	 */
	public float getHeadYTranslation()
	{
		return headYTranslation;
	}

	public boolean isTargetingAllowedPlayer(EntityLivingBase potentialTarget)
	{
		if(potentialTarget != null)
		{
			List<String> players = ModuleUtils.getPlayersFromModule(getAllowlistModule());

			for(String s : players)
			{
				if(potentialTarget.getName().equalsIgnoreCase(s))
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
	public boolean getCanSpawnHere()
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
	public EnumPushReaction getPushReaction()
	{
		return EnumPushReaction.IGNORE;
	}

	@Override
	public void updateLeashedState() {} //no leashing for sentry

	@Override
	public void setSwingingArms(boolean swingingArms) {} //sentrys don't have arms, do they?

	//this last code is here so the ai task gets executed, which it doesn't for some weird reason
	@Override
	public Random getRNG()
	{
		return notRandom;
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

	public static enum EnumSentryMode
	{
		CAMOUFLAGE_HP(1, 0, 1), CAMOUFLAGE_H(1, 1, 3), CAMOUFLAGE_P(1, 2, 5), AGGRESSIVE_HP(0, 0, 0), AGGRESSIVE_H(0, 1, 2), AGGRESSIVE_P(0, 2, 4), IDLE(-1, -1, 6);

		private final int type;
		private final int attack;
		private final int descriptionKeyIndex;

		EnumSentryMode(int type, int attack, int descriptionKeyIndex)
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
