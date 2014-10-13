package org.freeforums.geforce.securitycraft.tileentity;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;

public class TileEntityEmpedWire extends TileEntitySCTE {
	
	public int ticksRemaining;
	
	public void updateTick(){
		if(FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER){
			MinecraftServer.getServer().getEntityWorld().markBlockForUpdate(xCoord, yCoord, zCoord);
		}
	}
	
	/**
     * Writes a tile entity to NBT.
     */
    public void writeToNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.writeToNBT(par1NBTTagCompound);
        par1NBTTagCompound.setInteger("ticksRemaining", this.ticksRemaining);
    }

    /**
     * Reads a tile entity from NBT.
     */
    public void readFromNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.readFromNBT(par1NBTTagCompound);

        if (par1NBTTagCompound.hasKey("ticksRemaining"))
        {
            this.ticksRemaining = par1NBTTagCompound.getInteger("ticksRemaining");
        }
    }

}
