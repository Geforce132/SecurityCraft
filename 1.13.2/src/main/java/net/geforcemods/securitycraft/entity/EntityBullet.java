package net.geforcemods.securitycraft.entity;

import net.geforcemods.securitycraft.SCContent;
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
		super(SCContent.eTypeBullet, world);
		setSize(0.15F, 0.1F);
	}

	public EntityBullet(World world, EntityLivingBase shooter)
	{
		super(SCContent.eTypeBullet, shooter, world);
		setSize(0.15F, 0.1F);
	}

	@Override
	protected void onHit(RayTraceResult raytraceResult)
	{
		if(raytraceResult.entity!= null)
			raytraceResult.entity.attackEntityFrom(DamageSource.causeArrowDamage(this, shootingEntity == null ? this : shootingEntity), MathHelper.ceil(MathHelper.sqrt(motionX * motionX + motionY * motionY + motionZ * motionZ) * 2.0D));

		remove();
	}

	@Override
	protected void arrowHit(EntityLivingBase entity)
	{
		remove();
	}

	@Override
	protected ItemStack getArrowStack()
	{
		return ItemStack.EMPTY;
	}
}
