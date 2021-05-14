package net.geforcemods.securitycraft.entity;

import net.geforcemods.securitycraft.api.Owner;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class EntityBullet extends EntityArrow
{
	private static final DataParameter<Owner> OWNER = EntityDataManager.<Owner>createKey(EntityBullet.class, Owner.getSerializer());

	public EntityBullet(World world)
	{
		super(world);
		setSize(0.15F, 0.1F);
	}

	public EntityBullet(World world, EntitySentry shooter)
	{
		super(world, shooter);

		Owner owner =  shooter.getOwner();

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
	protected void onHit(RayTraceResult raytraceResult)
	{
		if(raytraceResult.entityHit != null)
			raytraceResult.entityHit.attackEntityFrom(DamageSource.causeArrowDamage(this, shootingEntity == null ? this : shootingEntity), MathHelper.ceil(MathHelper.sqrt(motionX * motionX + motionY * motionY + motionZ * motionZ) * 2.0D));

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
