package org.freeforums.geforce.securitycraft.tileentity;

import net.minecraft.nbt.NBTTagCompound;


public class TileEntityFrame extends TileEntityOwnable {

	private int[] boundCameraLocation = new int[3];

	public void writeToNBT(NBTTagCompound par1NBTTagCompound){
		super.writeToNBT(par1NBTTagCompound);

		if(this.boundCameraLocation != null && this.hasCameraLocation()){
			par1NBTTagCompound.setString("cameraLoc", this.boundCameraLocation[0] + " " + this.boundCameraLocation[1] + " " + this.boundCameraLocation[2]);
		}       
	}

	public void readFromNBT(NBTTagCompound par1NBTTagCompound){
		super.readFromNBT(par1NBTTagCompound);

		if (par1NBTTagCompound.hasKey("cameraLoc")){
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

	private boolean hasCameraLocation(){
		if(this.boundCameraLocation[0] != 0 || this.boundCameraLocation[1] != 0 || this.boundCameraLocation[2] != 0){
			return true;
		}else{
			return false;
		}
	}

}
