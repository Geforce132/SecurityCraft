package net.geforcemods.securitycraft.entity;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class EntityBullet extends EntityArrow
{
	public EntityBullet(World world)
	{
		super(world);
		setSize(0.15F, 0.1F);
	}

	public EntityBullet(World world, EntityLivingBase shooter)
	{
		super(world, shooter);
		setSize(0.15F, 0.1F);
	}

	@Override
	protected void onHit(RayTraceResult raytraceResult)
	{
		if(raytraceResult.entityHit != null)
			raytraceResult.entityHit.attackEntityFrom(DamageSource.causeArrowDamage(this, shootingEntity == null ? this : shootingEntity), MathHelper.ceiling_double_int(MathHelper.sqrt_double(motionX * motionX + motionY * motionY + motionZ * motionZ) * 2.0D));

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
		return null;
	}
}
