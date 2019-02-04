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
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public class EntitySentry extends EntityCreature implements IRangedAttackMob //needs to be a creature so it can target a player, ai is also only given to living entities
{
	private static final int OWNER_NAME = 16;
	private static final int OWNER_UUID = 17;
	private static final int MODULE = 18;
	private static final int MODE = 19;
	public static final int HEAD_ROTATION = 20;
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
		initEntityAI();
	}

	public EntitySentry(World world, EntityPlayer owner)
	{
		this(world, new Owner(owner.getCommandSenderName(), EntityPlayer.getUUID(owner.getGameProfile()).toString()));
	}

	public EntitySentry(World world, Owner owner)
	{
		super(world);
		setSize(1.0F, 1.0F);
		dataWatcher.updateObject(OWNER_NAME, owner.getName());
		dataWatcher.updateObject(OWNER_UUID, owner.getUUID());
		initEntityAI();
	}

	private void initEntityAI()
	{
		tasks.addTask(1, new EntityAIAttackRangedIfEnabled(this, 0.0F, 5, 10.0F));
		targetTasks.addTask(1, new EntityAITargetNearestPlayerOrMob(this));
	}

	@Override
	protected void entityInit()
	{
		super.entityInit();
		dataWatcher.addObject(OWNER_NAME, "");
		dataWatcher.addObject(OWNER_UUID, "");
		dataWatcher.addObject(MODULE, new ItemStack(Blocks.stone));
		dataWatcher.addObject(MODE, EnumSentryMode.CAMOUFLAGE.ordinal());
		dataWatcher.addObject(HEAD_ROTATION, 0.0F);
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
	public boolean interact(EntityPlayer player)
	{
		if(getOwner().isOwner(player))
		{
			player.closeScreen();

			if(player.isSneaking())
				remove();
			else if(player.getHeldItem() != null)
			{
				ItemStack module = getModule();

				if(player.getHeldItem().getItem() == SCContent.disguiseModule)
				{
					//drop the old module as to not override it with the new one
					if(module != null && !(module.getItem() instanceof ItemBlock)) //if it's instanceof ItemBlock, the saved module is not a module!
						Block.spawnAsEntity(worldObj, getPosition(), module);

					setModule(player.getHeldItem());

					if(!player.capabilities.isCreativeMode)
						player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
				}
				else if(player.getHeldItem().getItem() == SCContent.universalBlockModifier)
				{
					worldObj.setBlockState(getPosition(), Blocks.air.getDefaultState());

					if(module != null && !(module.getItem() instanceof ItemBlock))
						Block.spawnAsEntity(worldObj, getPosition(), module);

					dataWatcher.updateObject(MODULE, new ItemStack(Blocks.stone));
				}
			}
			else
				toggleMode(player);

			player.swingItem();
			return true;
		}

		return super.interact(player);
	}

	/**
	 * Cleanly removes this sentry from the world, dropping the module and removing any block
	 */
	public void remove()
	{
		ItemStack module = getModule();

		Block.spawnAsEntity(worldObj, getPosition(), new ItemStack(SCContent.sentry));

		if(module != null && !(module.getItem() instanceof ItemBlock))
			Block.spawnAsEntity(worldObj, getPosition(), module);

		worldObj.setBlockState(getPosition(), Blocks.air.getDefaultState());
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
		int newMode = getMode().ordinal() + 1;

		if(newMode == 3)
			newMode = 0;

		dataWatcher.updateObject(MODE, newMode);

		if(player.worldObj.isRemote)
		{
			if(newMode == 0)
			{
				animateUpwards = true;
				animate = true;
			}
			else if(newMode == 1)
			{
				animateUpwards = false;
				animate = true;
			}

			PlayerUtils.sendMessageToPlayer(player, StatCollector.translateToLocal("item.securitycraft:sentry.name"), StatCollector.translateToLocal("messages.securitycraft:sentry.mode" + (newMode + 1)), EnumChatFormatting.DARK_RED);
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
		if(target instanceof EntityPlayer && (((EntityPlayer)target).isSpectator() || ((EntityPlayer)target).capabilities.isCreativeMode))
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

		dataWatcher.updateObject(HEAD_ROTATION, (float)(Math.atan2(x, -z) * (180D / Math.PI)));
		throwableEntity.setThrowableHeading(x, d2 + f, z, 1.6F, 0.0F); //no inaccuracy for sentries!
		playSound("random.bow", 1.0F, 1.0F / (getRNG().nextFloat() * 0.4F + 0.8F));
		worldObj.spawnEntityInWorld(throwableEntity);
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound tag)
	{
		ItemStack module = getModule();

		tag.setTag("TileEntityData", getOwnerTag());
		tag.setTag("InstalledModule", module == null ? new NBTTagCompound() : module.writeToNBT(new NBTTagCompound()));
		tag.setInteger("SentryMode", getMode().ordinal());
		tag.setFloat("HeadRotation", getHeadRotation());
		super.writeEntityToNBT(tag);
	}

	public float getHeadRotation()
	{
		return dataWatcher.getWatchableObjectFloat(HEAD_ROTATION);
	}

	private NBTTagCompound getOwnerTag()
	{
		NBTTagCompound tag = new NBTTagCompound();

		tag.setString("owner", dataWatcher.getWatchableObjectString(OWNER_NAME));
		tag.setString("ownerUUID", dataWatcher.getWatchableObjectString(OWNER_UUID));
		return tag;
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound tag)
	{
		NBTTagCompound teTag = tag.getCompoundTag("TileEntityData");

		dataWatcher.updateObject(OWNER_NAME, teTag.getString("owner"));
		dataWatcher.updateObject(OWNER_UUID, teTag.getString("ownerUUID"));
		dataWatcher.updateObject(MODULE, ItemStack.loadItemStackFromNBT((NBTTagCompound)tag.getTag("InstalledModule")));
		dataWatcher.updateObject(MODE, tag.getInteger("SentryMode"));
		dataWatcher.updateObject(HEAD_ROTATION, tag.getFloat("HeadRotation"));
		super.readEntityFromNBT(tag);
	}

	/**
	 * @return The owner of this sentry
	 */
	public Owner getOwner()
	{
		return new Owner(dataWatcher.getWatchableObjectString(OWNER_NAME), dataWatcher.getWatchableObjectString(OWNER_UUID));
	}

	/**
	 * Sets the sentry's disguise module and places a block if possible
	 * @param module The module to set
	 */
	public void setModule(ItemStack module)
	{
		if(module == null)
			worldObj.setBlockState(getPosition(), Blocks.air.getDefaultState());
		else
		{
			List<ItemStack> blocks = ((ItemModule)module.getItem()).getAddons(module.getTagCompound());

			if(blocks.size() > 0)
			{
				ItemStack disguiseStack = blocks.get(0);
				IBlockState state = Block.getBlockFromItem(disguiseStack.getItem()).getStateFromMeta(disguiseStack.getHasSubtypes() ? disguiseStack.getItemDamage() : 0);

				worldObj.setBlockState(getPosition(), state.getBlock().isFullBlock() ? state : Blocks.air.getDefaultState());
			}
		}

		dataWatcher.updateObject(MODULE, module != null ? module.copy() : null);
	}

	/**
	 * @return The module that is added to this sentry. ItemStack.EMPTY if none available
	 */
	public ItemStack getModule()
	{
		return dataWatcher.getWatchableObjectItemStack(MODULE);
	}

	/**
	 * @return The mode in which the sentry is currently in, CAMOUFLAGE if the saved mode is smaller than 0 or greater than 2 (there are only 3 valid modes: 0, 1, 2)
	 */
	public EnumSentryMode getMode()
	{
		int mode = dataWatcher.getWatchableObjectInt(MODE);

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
	public boolean canAttackWithItem()
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
	public boolean isPushedByWater()
	{
		return false;
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
