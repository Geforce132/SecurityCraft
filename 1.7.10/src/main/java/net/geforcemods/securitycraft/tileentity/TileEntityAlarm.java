package net.geforcemods.securitycraft.tileentity;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.misc.SCSounds;
import net.geforcemods.securitycraft.network.packets.PacketCPlaySoundAtPos;
import net.minecraft.nbt.NBTTagCompound;

public class TileEntityAlarm extends TileEntityOwnable {

	private int cooldown = 0;
	private boolean isPowered = false;

	@Override
	public void updateEntity(){
		super.updateEntity();

		if(worldObj.isRemote)
			return;
		else{
			if(cooldown > 0)
				cooldown--;

			if(isPowered && cooldown == 0){
				SecurityCraft.network.sendToAll(new PacketCPlaySoundAtPos(xCoord, yCoord, zCoord, SCSounds.ALARM.path, SecurityCraft.config.alarmSoundVolume));
				setCooldown((SecurityCraft.config.alarmTickDelay * 20));
			}
		}
	}

	/**
	 * Writes a tile entity to NBT.
	 */
	@Override
	public void writeToNBT(NBTTagCompound par1NBTTagCompound){
		super.writeToNBT(par1NBTTagCompound);

		par1NBTTagCompound.setInteger("cooldown", cooldown);
		par1NBTTagCompound.setBoolean("isPowered", isPowered);
	}

	/**
	 * Reads a tile entity from NBT.
	 */
	@Override
	public void readFromNBT(NBTTagCompound par1NBTTagCompound){
		super.readFromNBT(par1NBTTagCompound);

		if (par1NBTTagCompound.hasKey("cooldown"))
			cooldown = par1NBTTagCompound.getInteger("cooldown");

		if (par1NBTTagCompound.hasKey("isPowered"))
			isPowered = par1NBTTagCompound.getBoolean("isPowered");

	}

	public int getCooldown(){
		return cooldown;
	}

	public void setCooldown(int par1){
		cooldown = par1;
	}

	public boolean isPowered() {
		return isPowered;
	}

	public void setPowered(boolean isPowered) {
		this.isPowered = isPowered;
	}

}
