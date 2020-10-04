package net.geforcemods.securitycraft.entity;

import net.geforcemods.securitycraft.SCContent;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.IPacket;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class BulletEntity extends AbstractArrowEntity
{
	public BulletEntity(EntityType<BulletEntity> type, World world)
	{
		super(SCContent.eTypeBullet, world);
	}

	public BulletEntity(World world, LivingEntity shooter)
	{
		super(SCContent.eTypeBullet, shooter, world);
	}

	@Override
	protected void onEntityHit(EntityRayTraceResult raytraceResult)
	{
		if(!(raytraceResult.getEntity() instanceof SentryEntity))
		{
			raytraceResult.getEntity().attackEntityFrom(DamageSource.causeArrowDamage(this, func_234616_v_()), MathHelper.ceil(getMotion().length()));
			remove();
		}
	}

	@Override
	protected void func_230299_a_(BlockRayTraceResult raytraceResult) //onBlockHit
	{
		remove();
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
