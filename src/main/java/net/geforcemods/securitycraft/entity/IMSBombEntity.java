package net.geforcemods.securitycraft.entity;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.EntityUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.WorldUtils;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractFireballEntity;
import net.minecraft.network.IPacket;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.world.Explosion.Mode;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class IMSBombEntity extends AbstractFireballEntity {

	private String playerName = null;
	private LivingEntity targetMob = null;

	public int ticksFlying = 0;
	private int launchHeight;
	public boolean launching = true;

	public IMSBombEntity(EntityType<IMSBombEntity> type, World world){
		super(SCContent.eTypeImsBomb, world);
	}

	public IMSBombEntity(World world, PlayerEntity targetEntity, double x, double y, double z, double targetX, double targetY, double targetZ, int height){
		super(SCContent.eTypeImsBomb, x, y, z, targetX, targetY, targetZ, world);
		playerName = targetEntity.getName().getFormattedText();
		launchHeight = height;
	}

	public IMSBombEntity(World world, LivingEntity targetEntity, double x, double y, double z, double targetX, double targetY, double targetZ, int height){
		super(SCContent.eTypeImsBomb, targetEntity, targetX, targetY, targetZ, world);
		targetMob = targetEntity;
		launchHeight = height;
	}

	@Override
	public void tick(){
		if(!launching){
			super.tick();
			return;
		}

		if(ticksFlying < launchHeight && launching){
			EntityUtils.moveY(this, 0.35F);
			ticksFlying++;
			move(MoverType.SELF, getMotion());
		}else if(ticksFlying >= launchHeight && launching)
			setTarget();
	}

	public void setTarget() {
		if(playerName != null && PlayerUtils.isPlayerOnline(playerName)){
			PlayerEntity target = PlayerUtils.getPlayerFromName(playerName);

			double targetX = target.posX - posX;
			double targetY = target.getBoundingBox().minY + target.getHeight() / 2.0F - (posY + 1.25D);
			double targetZ = target.posZ - posZ;
			IMSBombEntity imsBomb = new IMSBombEntity(world, target, posX, posY, posZ, targetX, targetY, targetZ, 0);

			imsBomb.launching = false;
			WorldUtils.addScheduledTask(world, () -> world.addEntity(imsBomb));
			remove();
		}else if(targetMob != null && !targetMob.removed){
			double targetX = targetMob.posX - posX;
			double targetY = targetMob.getBoundingBox().minY + targetMob.getHeight() / 2.0F - (posY + 1.25D);
			double targetZ = targetMob.posZ - posZ;
			IMSBombEntity imsBomb = new IMSBombEntity(world, targetMob, posX, posY, posZ, targetX, targetY, targetZ, 0);

			imsBomb.launching = false;
			WorldUtils.addScheduledTask(world, () -> world.addEntity(imsBomb));
			remove();
		}
		else
			remove();
	}

	@Override
	protected void onImpact(RayTraceResult result){
		if(!world.isRemote)
			if(result.getType() == Type.BLOCK && BlockUtils.getBlock(world, ((BlockRayTraceResult)result).getPos()) != SCContent.ims){
				world.createExplosion(this,  ((BlockRayTraceResult)result).getPos().getX(),  ((BlockRayTraceResult)result).getPos().getY() + 1D,  ((BlockRayTraceResult)result).getPos().getZ(), 7F, true, Mode.BREAK);
				remove();
			}
	}

	@Override
	protected float getMotionFactor(){
		return 1F;
	}

	@Override
	protected boolean canTriggerWalking(){
		return false;
	}

	@Override
	public boolean canBeCollidedWith(){
		return false;
	}

	@Override
	public float getCollisionBorderSize(){
		return 0.3F;
	}

	@Override
	public IPacket<?> createSpawnPacket()
	{
		return NetworkHooks.getEntitySpawningPacket(this);
	}
}
