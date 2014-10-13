package org.freeforums.geforce.securitycraft.tileentity;

import java.util.Iterator;
import java.util.List;

import org.freeforums.geforce.securitycraft.main.HelpfulMethods;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;

public class TileEntitySecurityCamera extends TileEntitySCTE{
	
	private int ticksTillUpdate = 0;
	
	public void updateEntity(){
		//System.out.println(this.ticksTillUpdate);

		if(this.ticksTillUpdate == 1){
			this.ticksTillUpdate--;
			HelpfulMethods.updateAndNotify(worldObj, xCoord, yCoord, zCoord, worldObj.getBlock(xCoord, yCoord, zCoord), 1, true);
			return;
		}else if(this.ticksTillUpdate >= 2){
			this.ticksTillUpdate--;
			return;
		}
				
		AxisAlignedBB axisalignedbb = AxisAlignedBB.getAABBPool().getAABB((double)this.xCoord, (double)this.yCoord, (double)this.zCoord, (double)(this.xCoord + 1), (double)(this.yCoord + 1), (double)(this.zCoord + 1)).expand(2, 2, 2);
        axisalignedbb.maxY = (double)this.worldObj.getHeight();
        List list = this.worldObj.getEntitiesWithinAABB(EntityPlayer.class, axisalignedbb);
        Iterator iterator = list.iterator();

        while (iterator.hasNext()){
            if((Entity) iterator.next() instanceof EntityPlayer){
            	this.ticksTillUpdate = 80;
            	HelpfulMethods.updateAndNotify(worldObj, xCoord, yCoord, zCoord, worldObj.getBlock(xCoord, yCoord, zCoord), 1, true);
            }
        }
	}

	public boolean isDetectingPlayer() {
		return ticksTillUpdate > 0;
	}

	public void setDetectingPlayer(int ticks) {
		this.ticksTillUpdate = ticks;
		HelpfulMethods.updateAndNotify(worldObj, xCoord, yCoord, zCoord, worldObj.getBlock(xCoord, yCoord, zCoord), 1, true);
	}

}
