package org.freeforums.geforce.securitycraft.tileentity;

import org.freeforums.geforce.securitycraft.imc.lookingglass.CameraAnimatorSecurityCamera;
import org.freeforums.geforce.securitycraft.imc.lookingglass.IWorldViewHelper;
import org.freeforums.geforce.securitycraft.main.Utils;
import org.freeforums.geforce.securitycraft.main.mod_SecurityCraft;
import org.freeforums.geforce.securitycraft.misc.CameraShutoffTimer;
import org.freeforums.geforce.securitycraft.network.ClientProxy;

import com.xcompwiz.lookingglass.api.view.IWorldView;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChunkCoordinates;


public class TileEntityFrame extends TileEntityOwnable {

	private int[] boundCameraLocation = new int[3];
	private boolean shouldShowView = false;

	public void writeToNBT(NBTTagCompound par1NBTTagCompound){
		super.writeToNBT(par1NBTTagCompound);

		if(this.boundCameraLocation != null && this.hasCameraLocation()){
			par1NBTTagCompound.setString("cameraLoc", this.boundCameraLocation[0] + " " + this.boundCameraLocation[1] + " " + this.boundCameraLocation[2]);
		}    	
	}

	public void readFromNBT(NBTTagCompound par1NBTTagCompound){
		super.readFromNBT(par1NBTTagCompound);

		if(par1NBTTagCompound.hasKey("cameraLoc")){
			this.boundCameraLocation[0] = Integer.parseInt(par1NBTTagCompound.getString("cameraLoc").split(" ")[0]);
			this.boundCameraLocation[1] = Integer.parseInt(par1NBTTagCompound.getString("cameraLoc").split(" ")[1]);
			this.boundCameraLocation[2] = Integer.parseInt(par1NBTTagCompound.getString("cameraLoc").split(" ")[2]);
		}   
	}

	public void setCameraLocation(int x, int y, int z){
		this.boundCameraLocation[0] = x;
		this.boundCameraLocation[1] = y;
		this.boundCameraLocation[2] = z;
	}

	public int getCamX(){
		return this.boundCameraLocation[0];
	}

	public int getCamY(){
		return this.boundCameraLocation[1];
	}

	public int getCamZ(){
		return this.boundCameraLocation[2];
	}

	public boolean hasCameraLocation(){
		if(this.boundCameraLocation[0] != 0 || this.boundCameraLocation[1] != 0 || this.boundCameraLocation[2] != 0){
			return true;
		}else{
			return false;
		}
	}
	
	@SideOnly(Side.CLIENT)
	public boolean shouldShowView(){
		return mod_SecurityCraft.instance.configHandler.fiveMinAutoShutoff ? shouldShowView : true;
	}
	
	@SideOnly(Side.CLIENT)
	public void enableView(){
		shouldShowView = true;
		
		if(mod_SecurityCraft.instance.configHandler.fiveMinAutoShutoff){
			if(!mod_SecurityCraft.instance.hasViewForCoords(boundCameraLocation[0] + " " + boundCameraLocation[1] + " " + boundCameraLocation[2])){
				IWorldView lgView = mod_SecurityCraft.instance.getLGPanelRenderer().createWorldView(0, new ChunkCoordinates(boundCameraLocation[0], boundCameraLocation[1], boundCameraLocation[2]), 192, 192); 
				
				lgView.setAnimator(new CameraAnimatorSecurityCamera(lgView.getCamera(), worldObj.getBlockMetadata(boundCameraLocation[0], boundCameraLocation[1], boundCameraLocation[2])));
	
				mod_SecurityCraft.log("Inserting new view at" + Utils.getFormattedCoordinates(boundCameraLocation[0], boundCameraLocation[1], boundCameraLocation[2]));
				((ClientProxy) mod_SecurityCraft.instance.serverProxy).worldViews.put(boundCameraLocation[0] + " " + boundCameraLocation[1] + " " + boundCameraLocation[2], new IWorldViewHelper(lgView));		
			}
			
			new CameraShutoffTimer(this);
		}
	}
	
	@SideOnly(Side.CLIENT)
	public void disableView(){
		if(mod_SecurityCraft.instance.configHandler.fiveMinAutoShutoff && mod_SecurityCraft.instance.hasViewForCoords(boundCameraLocation[0] + " " + boundCameraLocation[1] + " " + boundCameraLocation[2])){
			mod_SecurityCraft.instance.getLGPanelRenderer().cleanupWorldView(mod_SecurityCraft.instance.getViewFromCoords(boundCameraLocation[0] + " " + boundCameraLocation[1] + " " + boundCameraLocation[2]).getView());
			mod_SecurityCraft.instance.removeViewForCoords(boundCameraLocation[0] + " " + boundCameraLocation[1] + " " + boundCameraLocation[2]);
		}
		
		shouldShowView = false;
	}

}
