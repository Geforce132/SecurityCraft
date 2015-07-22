package org.freeforums.geforce.securitycraft.entity;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class EntityIMSBomb extends EntityThrowable {

	private int ticksFlying = 0;

	public EntityIMSBomb(World worldIn){
		super(worldIn);
		this.setSize(0.375F, 0.5F);
	}

	public EntityIMSBomb(World worldIn, double x, double y, double z){
		this(worldIn);
		this.setSize(0.375F, 0.5F);
		this.setPosition(x, y, z);
	}

	public void onUpdate(){
		//Copied code from EntityThrowable to check if this entity is colliding with a block.
		Vec3 vec3 = Vec3.createVectorHelper(this.posX, this.posY, this.posZ);
		Vec3 vec31 = Vec3.createVectorHelper(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
		MovingObjectPosition movingobjectposition = this.worldObj.rayTraceBlocks(vec3, vec31);
		vec3 = Vec3.createVectorHelper(this.posX, this.posY, this.posZ);
		vec31 = Vec3.createVectorHelper(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);

		if(movingobjectposition != null){
			vec31 = Vec3.createVectorHelper(movingobjectposition.hitVec.xCoord, movingobjectposition.hitVec.yCoord, movingobjectposition.hitVec.zCoord);
		}

		if(!this.worldObj.isRemote){
			Entity entity = null;
			List list = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.boundingBox.addCoord(this.motionX, this.motionY, this.motionZ).expand(1.0D, 1.0D, 1.0D));
			double d0 = 0.0D;
			EntityLivingBase entitylivingbase = this.getThrower();

			for(int j = 0; j < list.size(); ++j){
				Entity entity1 = (Entity)list.get(j);

				if(entity1.canBeCollidedWith() && (entity1 != entitylivingbase)){
					float f = 0.3F;
					AxisAlignedBB axisalignedbb = entity1.boundingBox.expand((double)f, (double)f, (double)f);
					MovingObjectPosition movingobjectposition1 = axisalignedbb.calculateIntercept(vec3, vec31);

					if(movingobjectposition1 != null){
						double d1 = vec3.distanceTo(movingobjectposition1.hitVec);

						if(d1 < d0 || d0 == 0.0D){
							entity = entity1;
							d0 = d1;
						}
					}
				}
			}

			if(entity != null){
				movingobjectposition = new MovingObjectPosition(entity);
			}
		}

		if(movingobjectposition != null){
			if(movingobjectposition.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK){
				this.onImpact(movingobjectposition);
			}
		}
        //-----
		
		if(ticksFlying <= 40){
			this.motionY = 0.25F;
			this.ticksFlying++;
			this.moveEntity(this.motionX, this.motionY, this.motionZ);
		}else{
			this.motionY = -3F;
			this.moveEntity(this.motionX, this.motionY, this.motionZ);
		}
	}

	protected void onImpact(MovingObjectPosition par1MovingObjectPosition){
		System.out.println("Impacting");
		if(!this.worldObj.isRemote){
			if(par1MovingObjectPosition.typeOfHit == MovingObjectType.BLOCK){
				this.worldObj.createExplosion(this, par1MovingObjectPosition.blockX, par1MovingObjectPosition.blockY + 1D, par1MovingObjectPosition.blockZ, 10F, true);
				this.setDead();
			}
		}
	}

	protected boolean canTriggerWalking(){
		return false;
	}

}
