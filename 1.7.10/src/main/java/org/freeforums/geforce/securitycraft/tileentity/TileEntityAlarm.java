package org.freeforums.geforce.securitycraft.tileentity;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;

import org.freeforums.geforce.securitycraft.main.mod_SecurityCraft;
import org.freeforums.geforce.securitycraft.network.packets.PacketCPlaySoundAtPos;
import org.freeforums.geforce.securitycraft.sounds.SCSounds;

import cpw.mods.fml.common.FMLCommonHandler;

public class TileEntityAlarm extends TileEntityOwnable {
	
	private int cooldown = 0;
	private boolean isPowered = false;
	
	public void updateEntity(){
		if(this.worldObj.isRemote){
			return;
		}else{		
			if(cooldown > 0){
				cooldown--;
				
				if(cooldown == 0){
					mod_SecurityCraft.log("Cooldown is 0");
				}
			}
			
			
			if(isPowered && cooldown == 0){
				TileEntityAlarm TEA = (TileEntityAlarm) this.worldObj.getTileEntity(xCoord, yCoord, zCoord);
				mod_SecurityCraft.network.sendToAll(new PacketCPlaySoundAtPos(xCoord, yCoord, zCoord, SCSounds.ALARM.path, mod_SecurityCraft.configHandler.alarmSoundVolume));
				TEA.setCooldown((mod_SecurityCraft.configHandler.alarmTickDelay * 20));
				this.worldObj.setBlock(xCoord, yCoord, zCoord, mod_SecurityCraft.alarm, this.worldObj.getBlockMetadata(xCoord, yCoord, zCoord), 3);
				this.worldObj.setTileEntity(xCoord, yCoord, zCoord, TEA);
			}
			
//			else if(isPowered && cooldown == (mod_SecurityCraft.configHandler.alarmTickDelay * 20)){
//				TileEntityAlarm TEA = (TileEntityAlarm) this.worldObj.getTileEntity(xCoord, yCoord, zCoord);
//				mod_SecurityCraft.network.sendToAll(new PacketCPlaySoundAtPos(xCoord, yCoord, zCoord, SCSounds.ALARM.path)); //TODO
//				this.worldObj.setBlock(xCoord, yCoord, zCoord, mod_SecurityCraft.alarmLit, this.worldObj.getBlockMetadata(xCoord, yCoord, zCoord), 3);
//				this.worldObj.setTileEntity(xCoord, yCoord, zCoord, TEA);
//			}
				
//			if(this.worldObj.getBlockMetadata(xCoord, yCoord, zCoord) >= 2 && this.worldObj.getBlockMetadata(xCoord, yCoord, zCoord) <= 5 && isMultipleOfTwenty(cooldown)){
//				System.out.println("Running cooldown 1");
//				int tempCool = this.cooldown;
//				this.worldObj.setBlock(xCoord, yCoord, zCoord, mod_SecurityCraft.alarmLit, (this.worldObj.getBlockMetadata(xCoord, yCoord, zCoord) + 5), 3);
//				((TileEntityAlarm)this.worldObj.getTileEntity(xCoord, yCoord, zCoord)).setCooldown(20);
//				System.out.println("Setting to lit");
//			}else if(this.worldObj.getBlockMetadata(xCoord, yCoord, zCoord) >= 7 && this.worldObj.getBlockMetadata(xCoord, yCoord, zCoord) <= 10 && isMultipleOfTwenty(cooldown)){
//				System.out.println("Running cooldown 2");
//				int tempCool = this.cooldown;
//				this.worldObj.setBlock(xCoord, yCoord, zCoord, mod_SecurityCraft.alarm, (this.worldObj.getBlockMetadata(xCoord, yCoord, zCoord) - 5), 3);
//				((TileEntityAlarm)this.worldObj.getTileEntity(xCoord, yCoord, zCoord)).setCooldown(20);
//				System.out.println("Setting to unlit");
//			}
		}
	}
	
	
	private boolean getAppropriateBoolean(boolean isLit) {
		return isLit ? false : true;
	}


	private Block getAppropriateBlock(boolean isLit) {
		return isLit ? mod_SecurityCraft.alarm : mod_SecurityCraft.alarmLit;
	}


	private boolean isMultipleOfTwenty(int par1) {
		if(par1 == 1 || par1 == 20 || par1 == 40 || par1 == 60 || par1 == 80 || par1 == 100 || par1 == 120 || par1 == 140 || par1 == 160){
			return true;
		}else{
			return false;
		}
	}


	/**
     * Writes a tile entity to NBT.
     */
    public void writeToNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.writeToNBT(par1NBTTagCompound);
        par1NBTTagCompound.setInteger("cooldown", this.cooldown);
        par1NBTTagCompound.setBoolean("isPowered", this.isPowered);
    }

    /**
     * Reads a tile entity from NBT.
     */
    public void readFromNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.readFromNBT(par1NBTTagCompound);

        if (par1NBTTagCompound.hasKey("cooldown"))
        {
            this.cooldown = par1NBTTagCompound.getInteger("cooldown");
        }
        
        if (par1NBTTagCompound.hasKey("isPowered"))
        {
            this.isPowered = par1NBTTagCompound.getBoolean("isPowered");
        }
     
    }
    
    public int getCooldown(){
    	return cooldown;
    }
    
    public void setCooldown(int par1){
    	mod_SecurityCraft.log("Setting cooldown to " + par1 + " | " + FMLCommonHandler.instance().getEffectiveSide());
    	this.cooldown = par1;
    }


	public boolean isPowered() {
		return isPowered;
	}


	public void setPowered(boolean isPowered) {
		this.isPowered = isPowered;
	}

}
