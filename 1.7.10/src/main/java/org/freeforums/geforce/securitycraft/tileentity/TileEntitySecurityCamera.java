package org.freeforums.geforce.securitycraft.tileentity;

import org.freeforums.geforce.securitycraft.imc.lookingglass.CameraAnimatorSecurityCamera;
import org.freeforums.geforce.securitycraft.imc.lookingglass.IWorldViewHelper;
import org.freeforums.geforce.securitycraft.main.Utils;
import org.freeforums.geforce.securitycraft.main.mod_SecurityCraft;
import org.freeforums.geforce.securitycraft.network.ClientProxy;

import com.xcompwiz.lookingglass.api.view.IWorldView;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChunkCoordinates;

public class TileEntitySecurityCamera extends TileEntityOwnable {
	
	private boolean createdView = false;
	
	public void updateEntity(){
		if(worldObj.isRemote && worldObj.checkChunksExist(xCoord, yCoord, zCoord, xCoord, yCoord, zCoord) && !mod_SecurityCraft.instance.hasViewForCoords(xCoord + " " + yCoord + " " + zCoord) && !createdView){
			if(worldObj.getBlockMetadata(xCoord, yCoord, zCoord) == 0 || this.createdView) return; 
			
			IWorldView lgView = mod_SecurityCraft.instance.getLGPanelRenderer().createWorldView(0, new ChunkCoordinates(xCoord, yCoord, zCoord), 192, 192); 
					
			lgView.setAnimator(new CameraAnimatorSecurityCamera(lgView.getCamera(), worldObj.getBlockMetadata(xCoord, yCoord, zCoord)));

			if(!mod_SecurityCraft.instance.hasViewForCoords(xCoord + " " + yCoord + " " + zCoord)){
				mod_SecurityCraft.log("Inserting new view at" + Utils.getFormattedCoordinates(xCoord, yCoord, zCoord));
				((ClientProxy) mod_SecurityCraft.instance.serverProxy).worldViews.put(xCoord + " " + yCoord + " " + zCoord, new IWorldViewHelper(lgView));		
			}
			
			this.createdView = true;
		}
	}
	
	public void readFromNBT(NBTTagCompound par1NBTTagCompound){
		super.readFromNBT(par1NBTTagCompound);
		
		//if(par1NBTTagCompound.hasKey("createdView")){
		//	this.createdView = par1NBTTagCompound.getBoolean("createdView");
		//}
    }
	
	public void writeToNBT(NBTTagCompound par1NBTTagCompound){
		super.writeToNBT(par1NBTTagCompound);
		
		//par1NBTTagCompound.setBoolean("createdView", this.createdView);
	}
   
}
