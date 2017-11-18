package net.geforcemods.securitycraft.tileentity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.geforcemods.securitycraft.imc.lookingglass.LookingGlassAPIProvider;
import net.geforcemods.securitycraft.main.mod_SecurityCraft;
import net.geforcemods.securitycraft.misc.CameraShutoffTimer;
import net.geforcemods.securitycraft.misc.CameraView;
import net.minecraft.nbt.NBTTagCompound;


public class TileEntityFrame extends TileEntityOwnable {

	//private int[] boundCameraLocation = new int[3];
	private CameraView cameraView;
	private boolean shouldShowView = false;
	private boolean createdView = false;

	@Override
	public void updateEntity(){
		if(worldObj.isRemote && worldObj.checkChunksExist(xCoord, yCoord, zCoord, xCoord, yCoord, zCoord) && hasCameraLocation() && !mod_SecurityCraft.instance.hasViewForCoords(cameraView.toNBTString()) && !createdView){
			if(worldObj.getBlockMetadata(xCoord, yCoord, zCoord) == 0 || createdView) return;

			LookingGlassAPIProvider.createLookingGlassView(worldObj, cameraView.dimension, xCoord, yCoord, zCoord, 192, 192);

			createdView = true;
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound par1NBTTagCompound){
		super.writeToNBT(par1NBTTagCompound);

		if(hasCameraLocation())
			par1NBTTagCompound.setString("cameraLoc", cameraView.toNBTString());
	}

	@Override
	public void readFromNBT(NBTTagCompound par1NBTTagCompound){
		super.readFromNBT(par1NBTTagCompound);

		if(par1NBTTagCompound.hasKey("cameraLoc")){
			String[] coords = par1NBTTagCompound.getString("cameraLoc").split(" ");

			setCameraLocation(Integer.parseInt(coords[0]), Integer.parseInt(coords[1]), Integer.parseInt(coords[2]), coords.length == 4 ? Integer.parseInt(coords[3]) : 0);
		}
	}

	public void setCameraLocation(int x, int y, int z, int dimension){
		if(cameraView == null) {
			cameraView = new CameraView(x, y, z, dimension);
			return;
		}

		cameraView.setLocation(x, y, z, dimension);
	}

	public CameraView getCameraView(){
		return cameraView;
	}

	public int getCamDimension(){
		return cameraView.dimension;
	}

	public boolean hasCameraLocation(){
		return cameraView != null;
	}

	@SideOnly(Side.CLIENT)
	public boolean shouldShowView(){
		return mod_SecurityCraft.configHandler.fiveMinAutoShutoff ? shouldShowView : true;
	}

	@SideOnly(Side.CLIENT)
	public void enableView(){
		shouldShowView = true;

		if(mod_SecurityCraft.configHandler.fiveMinAutoShutoff){
			if(!mod_SecurityCraft.instance.hasViewForCoords(cameraView.toNBTString()))
				LookingGlassAPIProvider.createLookingGlassView(worldObj, cameraView.dimension, cameraView.x, cameraView.y, cameraView.z, 192, 192);

			new CameraShutoffTimer(this);
		}
	}

	@SideOnly(Side.CLIENT)
	public void disableView(){
		if(mod_SecurityCraft.configHandler.fiveMinAutoShutoff && mod_SecurityCraft.instance.hasViewForCoords(cameraView.toNBTString())){
			mod_SecurityCraft.instance.getLGPanelRenderer().getApi().cleanupWorldView(mod_SecurityCraft.instance.getViewFromCoords(cameraView.toNBTString()).getView());
			mod_SecurityCraft.instance.removeViewForCoords(cameraView.toNBTString());
		}

		shouldShowView = false;
	}

}
