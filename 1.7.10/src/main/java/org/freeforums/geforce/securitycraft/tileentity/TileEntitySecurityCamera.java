package org.freeforums.geforce.securitycraft.tileentity;

import net.minecraft.util.ChunkCoordinates;

import org.freeforums.geforce.securitycraft.lookingglass.CameraAnimatorSecurityCamera;
import org.freeforums.geforce.securitycraft.main.Utils;
import org.freeforums.geforce.securitycraft.main.mod_SecurityCraft;
import org.freeforums.geforce.securitycraft.misc.EnumCustomModules;

import com.xcompwiz.lookingglass.api.view.IWorldView;

public class TileEntitySecurityCamera extends CustomizableSCTE{
	
	public void updateEntity(){
		if(worldObj.isRemote && worldObj.checkChunksExist(xCoord, yCoord, zCoord, xCoord, yCoord, zCoord) && !mod_SecurityCraft.instance.hasViewForCoords(xCoord + " " + yCoord + " " + zCoord)){
			IWorldView lgView = mod_SecurityCraft.instance.getLGPanelRenderer().createWorldView(0, new ChunkCoordinates(xCoord, yCoord, zCoord), 192, 192); 
		
			lgView.setAnimator(new CameraAnimatorSecurityCamera(lgView.getCamera(), worldObj.getBlockMetadata(xCoord, yCoord, zCoord)));
			lgView.grab();
			if(!mod_SecurityCraft.instance.hasViewForCoords(xCoord + " " + yCoord + " " + zCoord)){
				System.out.println("Inserting new view at" + Utils.getFormattedCoordinates(xCoord, yCoord, zCoord));
				mod_SecurityCraft.instance.lgViews.put(xCoord + " " + yCoord + " " + zCoord, lgView);		
			}
		}
//		else if(worldObj.isRemote && worldObj.checkChunksExist(xCoord, yCoord, zCoord, xCoord, yCoord, zCoord) && mod_SecurityCraft.instance.hasViewForCoords(xCoord + " " + yCoord + " " + zCoord)){
//			mod_SecurityCraft.instance.getViewFromCoords(xCoord + " " + yCoord + " " + zCoord).markDirty();
//		}
	}
		
	public EnumCustomModules[] getCustomizableOptions() {
		return new EnumCustomModules[]{EnumCustomModules.REDSTONE};
	}

	public String[] getOptionDescriptions() {
		return new String[]{"Lets the camera emit a 15-block redstone signal when enabled."};
	}
   
}
