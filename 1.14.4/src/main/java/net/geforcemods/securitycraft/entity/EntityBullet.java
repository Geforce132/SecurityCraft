package net.geforcemods.securitycraft.entity;

import net.geforcemods.securitycraft.SCContent;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.IPacket;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class EntityBullet extends AbstractArrowEntity
{
	public EntityBullet(EntityType<EntityBullet> type, World world)
	{
		super(SCContent.eTypeBullet, world);
	}

	public EntityBullet(World world, LivingEntity shooter)
	{
		super(SCContent.eTypeBullet, shooter, world);
	}

	@Override
	protected void onHit(RayTraceResult raytraceResult)
	{
		if(raytraceResult.getType() == Type.ENTITY && !(((EntityRayTraceResult)raytraceResult).getEntity() instanceof EntitySentry))
		{
			((EntityRayTraceResult)raytraceResult).getEntity().attackEntityFrom(DamageSource.causeArrowDamage(this, getShooter()), MathHelper.ceil(getMotion().length()));
			remove();
		}
	}

	@Override
	protected void arrowHit(LivingEntity entity)
	{
		remove();
	}

	@Override
	protected ItemStack getArrowStack()
	{
		return ItemStack.EMPTY;
	}

	@Override
	public IPacket<?> createSpawnPacket()
	{
		return NetworkHooks.getEntitySpawningPacket(this);
	}
}
