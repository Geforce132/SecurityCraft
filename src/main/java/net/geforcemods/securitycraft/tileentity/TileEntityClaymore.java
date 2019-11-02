package net.geforcemods.securitycraft.tileentity;

import java.util.Iterator;
import java.util.List;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.TileEntitySCTE;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;

public class TileEntityClaymore extends TileEntitySCTE{

	private double entityX = -1D;
	private double entityY = -1D;
	private double entityZ = -1D;
	private int cooldown = -1;

	@Override
	public void updateEntity() {
		if(getWorld().isRemote)
			return;
		else{
			if(getWorld().getBlock(xCoord, yCoord, zCoord) == SCContent.claymoreDefused)
				return;

			if(cooldown > 0){
				cooldown--;
				return;
			}

			if(cooldown == 0){
				BlockUtils.destroyBlock(getWorld(), xCoord, yCoord, zCoord, false);
				getWorld().createExplosion((Entity) null, entityX, entityY + 0.5F, entityZ, 3.5F, true);
				return;
			}

			int meta = getWorld().getBlockMetadata(xCoord, yCoord, zCoord);
			AxisAlignedBB area = AxisAlignedBB.getBoundingBox(xCoord, yCoord, zCoord, xCoord + 1, yCoord + 1, zCoord + 1);

			if(meta == 3)
				area = area.addCoord(0, 0, -SecurityCraft.config.claymoreRange);
			else if(meta == 1)
				area = area.addCoord(0, 0, SecurityCraft.config.claymoreRange);
			else if(meta == 2)
				area = area.addCoord(SecurityCraft.config.claymoreRange, 0, 0);
			else if(meta == 4)
				area = area.addCoord(-SecurityCraft.config.claymoreRange, 0, 0);

			List<?> entities = getWorld().getEntitiesWithinAABB(EntityLivingBase.class, area);
			Iterator<?> iterator = entities.iterator();
			EntityLivingBase entity;

			while(iterator.hasNext()){
				entity = (EntityLivingBase) iterator.next();

				if(PlayerUtils.isPlayerMountedOnCamera(entity))
					continue;

				entityX = entity.posX;
				entityY = entity.posY;
				entityZ = entity.posZ;
				cooldown = 20;

				getWorld().playSoundEffect(xCoord + 0.5D, yCoord + 0.5D, zCoord + 0.5D, "random.click", 0.3F, 0.6F);
				break;
			}
		}

	}

	/**
	 * Writes a tile entity to NBT.
	 */
	@Override
	public void writeToNBT(NBTTagCompound tag)
	{
		super.writeToNBT(tag);
		tag.setInteger("cooldown", cooldown);
		tag.setDouble("entityX", entityX);
		tag.setDouble("entityY", entityY);
		tag.setDouble("entityZ", entityZ);

	}

	/**
	 * Reads a tile entity from NBT.
	 */
	@Override
	public void readFromNBT(NBTTagCompound tag)
	{
		super.readFromNBT(tag);

		if (tag.hasKey("cooldown"))
			cooldown = tag.getInteger("cooldown");

		if (tag.hasKey("entityX"))
			entityX = tag.getDouble("entityX");

		if (tag.hasKey("entityY"))
			entityY = tag.getDouble("entityY");

		if (tag.hasKey("entityZ"))
			entityZ = tag.getDouble("entityZ");
	}

}
