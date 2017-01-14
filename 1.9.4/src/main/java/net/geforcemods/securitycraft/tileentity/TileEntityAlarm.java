package net.geforcemods.securitycraft.tileentity;

import net.geforcemods.securitycraft.blocks.BlockAlarm;
import net.geforcemods.securitycraft.main.mod_SecurityCraft;
import net.geforcemods.securitycraft.misc.SCSounds;
import net.geforcemods.securitycraft.network.packets.PacketCPlaySoundAtPos;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class TileEntityAlarm extends TileEntityOwnable {
	
	private int cooldown = 0;
	private boolean isPowered = false;
	
	public void update(){
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
				TileEntityAlarm TEA = (TileEntityAlarm) this.worldObj.getTileEntity(pos);
				mod_SecurityCraft.network.sendToAll(new PacketCPlaySoundAtPos(pos.getX(), pos.getY(), pos.getZ(), SCSounds.ALARM.path, mod_SecurityCraft.configHandler.alarmSoundVolume));
				TEA.setCooldown((mod_SecurityCraft.configHandler.alarmTickDelay * 20));
				this.worldObj.setBlockState(pos, this.worldObj.getBlockState(pos).withProperty(BlockAlarm.FACING, this.worldObj.getBlockState(pos).getValue(BlockAlarm.FACING)), 2); //TODO
				this.worldObj.setTileEntity(pos, TEA);
			}
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
