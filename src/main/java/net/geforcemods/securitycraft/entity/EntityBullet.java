package net.geforcemods.securitycraft.entity;

import java.util.Collection;

import com.google.common.collect.Sets;

import net.geforcemods.securitycraft.api.Owner;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class EntityBullet extends EntityArrow
{
	private static final DataParameter<Owner> OWNER = EntityDataManager.<Owner>createKey(EntityBullet.class, Owner.getSerializer());
	private Collection<PotionEffect> potionEffects = Sets.newHashSet();

	public EntityBullet(World world)
	{
		super(world);
		setSize(0.15F, 0.1F);
	}

	public EntityBullet(World world, EntitySentry shooter)
	{
		super(world, shooter);

		Owner owner =  shooter.getOwner();

		this.potionEffects = shooter.getActivePotionEffects();
		dataManager.set(OWNER, new Owner(owner.getName(), owner.getUUID()));
		setSize(0.15F, 0.1F);
	}

	/**
	 * @return The owner of the sentry which shot this bullet
	 */
	public Owner getOwner()
	{
		return dataManager.get(OWNER);
	}

	@Override
	protected void entityInit()
	{
		super.entityInit();
		dataManager.register(OWNER, new Owner());
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound compound) {
		super.writeEntityToNBT(compound);

		if (!potionEffects.isEmpty()) {
			NBTTagList list = new NBTTagList();

			for(PotionEffect effect : potionEffects) {
				list.appendTag(effect.writeCustomPotionEffectToNBT(new NBTTagCompound()));
			}

			compound.setTag("PotionEffects", list);
		}
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound compound) {
		super.readEntityFromNBT(compound);

		if (compound.hasKey("PotionEffects", 9)) {
			NBTTagList potionList = compound.getTagList("PotionEffects", 10);

			if (!potionList.isEmpty()) {
				for (int i = 0; i < potionList.tagCount(); ++i) {
					PotionEffect effect = PotionEffect.readCustomPotionEffectFromNBT(potionList.getCompoundTagAt(i));

					if (effect != null) {
						potionEffects.add(effect);
					}
				}
			}
		}
	}

	@Override
	protected void onHit(RayTraceResult raytraceResult)
	{
		Entity target = raytraceResult.entityHit;

		if(target != null && !(target instanceof EntitySentry)) {
			target.attackEntityFrom(DamageSource.causeArrowDamage(this, shootingEntity == null ? this : shootingEntity), MathHelper.ceil(MathHelper.sqrt(motionX * motionX + motionY * motionY + motionZ * motionZ)));

			if (target instanceof EntityLivingBase && !potionEffects.isEmpty()) {
				for (PotionEffect effect : potionEffects) {
					((EntityLivingBase)target).addPotionEffect(effect);
				}
			}
		}

		setDead();
	}

	@Override
	protected void arrowHit(EntityLivingBase entity)
	{
		setDead();
	}

	@Override
	protected ItemStack getArrowStack()
	{
		return ItemStack.EMPTY;
	}
}
