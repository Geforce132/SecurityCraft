package net.geforcemods.securitycraft.tileentity;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blocks.BlockAlarm;
import net.geforcemods.securitycraft.misc.SCSounds;
import net.geforcemods.securitycraft.network.packets.PacketCPlaySoundAtPos;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class TileEntityAlarm extends TileEntityOwnable {

	private int cooldown = 0;
	private boolean isPowered = false;

	@Override
	public void update(){
		if(worldObj.isRemote)
			return;
		else{
			if(cooldown > 0){
				cooldown--;

				if(cooldown == 0)
					SecurityCraft.log("Cooldown is 0");
			}

			if(isPowered && cooldown == 0){
				TileEntityAlarm alarm = (TileEntityAlarm) worldObj.getTileEntity(pos);
				SecurityCraft.network.sendToAll(new PacketCPlaySoundAtPos(pos.getX(), pos.getY(), pos.getZ(), SCSounds.ALARM.path, SecurityCraft.config.alarmSoundVolume));
				alarm.setCooldown((SecurityCraft.config.alarmTickDelay * 20));
				worldObj.setBlockState(pos, worldObj.getBlockState(pos).withProperty(BlockAlarm.FACING, worldObj.getBlockState(pos).getValue(BlockAlarm.FACING)), 2);
				worldObj.setTileEntity(pos, alarm);
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
		tag.setBoolean("isPowered", isPowered);
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

		if (tag.hasKey("isPowered"))
			isPowered = tag.getBoolean("isPowered");

	}

	public int getCooldown(){
		return cooldown;
	}

	public void setCooldown(int value){
		SecurityCraft.log("Setting cooldown to " + value + " | " + FMLCommonHandler.instance().getEffectiveSide());
		cooldown = value;
	}

	public boolean isPowered() {
		return isPowered;
	}

	public void setPowered(boolean isPowered) {
		this.isPowered = isPowered;
	}

}
