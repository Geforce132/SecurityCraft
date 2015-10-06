package net.breakinbad.securitycraft.tileentity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.breakinbad.securitycraft.imc.lookingglass.LookingGlassAPIProvider;
import net.breakinbad.securitycraft.main.mod_SecurityCraft;
import net.breakinbad.securitycraft.misc.CameraShutoffTimer;
import net.minecraft.nbt.NBTTagCompound;


public class TileEntityFrame extends TileEntityOwnable {

	private int[] boundCameraLocation = new int[3];
	private boolean shouldShowView = false;
	private boolean createdView = false;
	
	public void updateEntity(){
		if(worldObj.isRemote && worldObj.checkChunksExist(xCoord, yCoord, zCoord, xCoord, yCoord, zCoord) && this.hasCameraLocation() && !mod_SecurityCraft.instance.hasViewForCoords(boundCameraLocation[0] + " " + boundCameraLocation[1] + " " + boundCameraLocation[2]) && !createdView){
			if(worldObj.getBlockMetadata(xCoord, yCoord, zCoord) == 0 || this.createdView) return; 
			
			LookingGlassAPIProvider.createLookingGlassView(worldObj, 0, xCoord, yCoord, zCoord, 192, 192);
			
			this.createdView = true;
		}
	}
	
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
				LookingGlassAPIProvider.createLookingGlassView(worldObj, 0, boundCameraLocation[0], boundCameraLocation[1], boundCameraLocation[2], 192, 192);
//				IWorldView lgView = mod_SecurityCraft.instance.getLGPanelRenderer().createWorldView(0, new ChunkCoordinates(boundCameraLocation[0], boundCameraLocation[1], boundCameraLocation[2]), 192, 192); 
//				
//				lgView.setAnimator(new CameraAnimatorSecurityCamera(lgView.getCamera(), worldObj.getBlockMetadata(boundCameraLocation[0], boundCameraLocation[1], boundCameraLocation[2])));
//	
//				mod_SecurityCraft.log("Inserting new view at" + Utils.getFormattedCoordinates(boundCameraLocation[0], boundCameraLocation[1], boundCameraLocation[2]));
//				((ClientProxy) mod_SecurityCraft.instance.serverProxy).worldViews.put(boundCameraLocation[0] + " " + boundCameraLocation[1] + " " + boundCameraLocation[2], new IWorldViewHelper(lgView));		
			}
			
			new CameraShutoffTimer(this);
		}
	}
	
	@SideOnly(Side.CLIENT)
	public void disableView(){
		if(mod_SecurityCraft.instance.configHandler.fiveMinAutoShutoff && mod_SecurityCraft.instance.hasViewForCoords(boundCameraLocation[0] + " " + boundCameraLocation[1] + " " + boundCameraLocation[2])){
			mod_SecurityCraft.instance.getLGPanelRenderer().getApi().cleanupWorldView(mod_SecurityCraft.instance.getViewFromCoords(boundCameraLocation[0] + " " + boundCameraLocation[1] + " " + boundCameraLocation[2]).getView());
			mod_SecurityCraft.instance.removeViewForCoords(boundCameraLocation[0] + " " + boundCameraLocation[1] + " " + boundCameraLocation[2]);
		}
		
		shouldShowView = false;
	}

}
