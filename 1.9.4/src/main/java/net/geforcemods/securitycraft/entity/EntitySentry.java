package net.geforcemods.securitycraft.entity;

import java.util.List;
import java.util.Random;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.entity.ai.EntityAIAttackRangedIfEnabled;
import net.geforcemods.securitycraft.entity.ai.EntityAITargetNearestPlayerOrMob;
import net.geforcemods.securitycraft.items.ItemModule;
import net.geforcemods.securitycraft.network.packets.PacketCInitSentryAnimation;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.block.Block;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

public class EntitySentry extends EntityCreature implements IRangedAttackMob //needs to be a creature so it can target a player, ai is also only given to living entities
{
	private static final DataParameter<Owner> OWNER = EntityDataManager.<Owner>createKey(EntitySentry.class, Owner.SERIALIZER);
	private static final DataParameter<NBTTagCompound> MODULE = EntityDataManager.<NBTTagCompound>createKey(EntitySentry.class, SecurityCraft.COMPOUND_TAG);
	private static final DataParameter<Integer> MODE = EntityDataManager.<Integer>createKey(EntitySentry.class, DataSerializers.VARINT);
	public static final DataParameter<Float> HEAD_ROTATION = EntityDataManager.<Float>createKey(EntitySentry.class, DataSerializers.FLOAT);
	public static final float MAX_TARGET_DISTANCE = 20.0F;
	private float headYTranslation = 0.9F;
	private final float animationStepSize = 0.025F;
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
		dataManager.set(MODULE, new NBTTagCompound());
		dataManager.set(MODE, EnumSentryMode.CAMOUFLAGE.ordinal());
		dataManager.set(HEAD_ROTATION, 0.0F);
	}

	@Override
	protected void entityInit()
	{
		super.entityInit();
		dataManager.register(OWNER, new Owner());
		dataManager.register(MODULE, new NBTTagCompound());
		dataManager.register(MODE, EnumSentryMode.CAMOUFLAGE.ordinal());
		dataManager.register(HEAD_ROTATION, 0.0F);
	}

	@Override
	protected void initEntityAI()
	{
		tasks.addTask(1, new EntityAIAttackRangedIfEnabled(this, 0.0F, 5, 10.0F));
		targetTasks.addTask(1, new EntityAITargetNearestPlayerOrMob(this));
	}

	@Override
	public void onEntityUpdate()
	{
		super.onEntityUpdate();

		if(worldObj.isRemote && animate)
		{
			if(animateUpwards && headYTranslation > 0.0F)
			{
				headYTranslation -= animationStepSize;

				if(headYTranslation <= 0.0F)
				{
					animateUpwards = false;
					animate = false;
				}
			}
			else if(!animateUpwards && headYTranslation < 0.9F)
			{
				headYTranslation += animationStepSize;

				if(headYTranslation >= 0.9F)
				{
					animateUpwards = true;
					animate = false;
				}
			}
		}
	}

	@Override
	public boolean processInteract(EntityPlayer player, EnumHand hand, ItemStack stack)
	{
		if(getOwner().isOwner(player) && hand == EnumHand.MAIN_HAND)
		{
			player.closeScreen();

			if(player.isSneaking())
				remove();
			else if(player.getHeldItemMainhand() != null)
			{
				if(player.getHeldItemMainhand().getItem() == SCContent.disguiseModule)
				{
					ItemStack module = getModule();

					if(module != null) //drop the old module as to not override it with the new one
						Block.spawnAsEntity(worldObj, getPosition(), module);

					setModule(player.getHeldItemMainhand());

					if(!player.isCreative())
						player.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, null);
				}
				else if(player.getHeldItemMainhand().getItem() == SCContent.universalBlockModifier)
				{
					worldObj.setBlockState(getPosition(), Blocks.AIR.getDefaultState());
					Block.spawnAsEntity(worldObj, getPosition(), getModule());
					dataManager.set(MODULE, new NBTTagCompound());
				}
			}
			else
				toggleMode(player);

			player.swingArm(EnumHand.MAIN_HAND);
			return true;
		}

		return super.processInteract(player, hand, stack);
	}

	/**
	 * Cleanly removes this sentry from the world, dropping the module and removing any block
	 */
	public void remove()
	{
		ItemStack module = getModule();

		Block.spawnAsEntity(worldObj, getPosition(), new ItemStack(SCContent.sentry));

		if(module != null)
			Block.spawnAsEntity(worldObj, getPosition(), module);

		worldObj.setBlockState(getPosition(), Blocks.AIR.getDefaultState());
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
		int mode = dataManager.get(MODE) + 1;

		if(mode == 3)
			mode = 0;

		dataManager.set(MODE, mode);

		if(player.worldObj.isRemote)
		{
			if(mode == 0)
			{
				animateUpwards = true;
				animate = true;
			}
			else if(mode == 1)
			{
				animateUpwards = false;
				animate = true;
			}

			PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize("item.securitycraft:sentry.name"), ClientUtils.localize("messages.securitycraft:sentry.mode" + (mode + 1)), TextFormatting.DARK_RED);
		}
	}

	@Override
	public void setAttackTarget(EntityLivingBase target)
	{
		if(getMode() != EnumSentryMode.AGGRESSIVE && (target == null && previousTargetId != Long.MIN_VALUE || (target != null && previousTargetId != target.getEntityId())))
		{
			animateUpwards = getMode() == EnumSentryMode.CAMOUFLAGE && target != null;
			animate = true;
			SecurityCraft.network.sendToAll(new PacketCInitSentryAnimation(getPosition(), animate, animateUpwards));
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
		if(getDistanceSqToEntity(target) > MAX_TARGET_DISTANCE * MAX_TARGET_DISTANCE)
			return;

		EntityBullet throwableEntity = new EntityBullet(worldObj, this);
		double y = target.posY + target.getEyeHeight() - 1.100000023841858D;
		double x = target.posX - posX;
		double d2 = y - throwableEntity.posY;
		double z = target.posZ - posZ;
		float f = MathHelper.sqrt_double(x * x + z * z) * 0.2F;

		dataManager.set(HEAD_ROTATION, (float)(MathHelper.atan2(x, -z) * (180D / Math.PI)));
		throwableEntity.setThrowableHeading(x, d2 + f, z, 1.6F, 0.0F); //no inaccuracy for sentries!
		playSound(SoundEvents.ENTITY_ARROW_SHOOT, 1.0F, 1.0F / (getRNG().nextFloat() * 0.4F + 0.8F));
		worldObj.spawnEntityInWorld(throwableEntity);
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound tag)
	{
		ItemStack module = getModule();

		tag.setTag("TileEntityData", getOwnerTag());
		tag.setTag("InstalledModule", module == null ? new NBTTagCompound() : module.writeToNBT(new NBTTagCompound()));
		tag.setInteger("SentryMode", dataManager.get(MODE));
		tag.setFloat("HeadRotation", dataManager.get(HEAD_ROTATION));
		super.writeEntityToNBT(tag);
	}

	private NBTTagCompound getOwnerTag()
	{
		NBTTagCompound tag = new NBTTagCompound();
		Owner owner = dataManager.get(OWNER);

		tag.setString("owner", owner.getName());
		tag.setString("ownerUUID", owner.getUUID());
		return tag;
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound tag)
	{
		NBTTagCompound teTag = tag.getCompoundTag("TileEntityData");
		String name = teTag.getString("owner");
		String uuid = teTag.getString("ownerUUID");

		dataManager.set(OWNER, new Owner(name, uuid));
		dataManager.set(MODULE, (NBTTagCompound)tag.getTag("InstalledModule"));
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
	public void setModule(ItemStack module)
	{
		NBTTagCompound tag = new NBTTagCompound();

		if(module == null)
			worldObj.setBlockState(getPosition(), Blocks.AIR.getDefaultState());
		else
		{
			List<ItemStack> blocks = ((ItemModule)module.getItem()).getAddons(module.getTagCompound());

			if(blocks.size() > 0)
			{
				ItemStack disguiseStack = blocks.get(0);
				IBlockState state = Block.getBlockFromItem(disguiseStack.getItem()).getStateFromMeta(disguiseStack.getHasSubtypes() ? disguiseStack.getItemDamage() : 0);

				worldObj.setBlockState(getPosition(), state.isFullBlock() ? state : Blocks.AIR.getDefaultState());
			}

			module.writeToNBT(tag);
		}

		dataManager.set(MODULE, tag);
	}

	/**
	 * @return The module that is added to this sentry. ItemStack.EMPTY if none available
	 */
	public ItemStack getModule()
	{
		NBTTagCompound tag = dataManager.get(MODULE);

		if(tag == null || tag.hasNoTags())
			return null;
		else
			return ItemStack.loadItemStackFromNBT(tag);
	}

	/**
	 * @return The mode in which the sentry is currently in, CAMOUFLAGE if the saved mode is smaller than 0 or greater than 2 (there are only 3 valid modes: 0, 1, 2)
	 */
	public EnumSentryMode getMode()
	{
		int mode = dataManager.get(MODE);

		return mode < 0 || mode > 2 ? EnumSentryMode.CAMOUFLAGE : EnumSentryMode.values()[mode];
	}

	/**
	 * @return The amount of y translation from the head's default position, used for animation
	 */
	public float getHeadYTranslation()
	{
		return headYTranslation;
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
	public void moveEntity(double x, double y, double z) {} //no moving sentries!

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
		AGGRESSIVE, CAMOUFLAGE, IDLE;
	}
}
