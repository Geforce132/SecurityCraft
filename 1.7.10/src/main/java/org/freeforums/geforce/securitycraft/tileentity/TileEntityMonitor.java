package org.freeforums.geforce.securitycraft.tileentity;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChunkCoordinates;

import org.freeforums.geforce.securitycraft.blocks.BlockSecurityCamera;
import org.freeforums.geforce.securitycraft.lookingglass.CameraAnimatorSecurityCamera;
import org.freeforums.geforce.securitycraft.main.Utils;
import org.freeforums.geforce.securitycraft.main.mod_SecurityCraft;

import com.xcompwiz.lookingglass.api.view.IWorldView;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityMonitor extends TileEntityOwnable {
		
	private IWorldView lgView = null;

	private int[] boundCameraLocation = new int[3];
	
	public TileEntityMonitor(){
		System.out.println("New TileEntityMonitor being created!");
	}
	
	public void updateEntity(){
//		if(this.hasCameraLocation() && !(this.worldObj.getBlock(this.boundCameraLocation[0], this.boundCameraLocation[1], this.boundCameraLocation[2]) instanceof BlockSecurityCamera)){
//			this.setCameraLocation(0, 0, 0);
//			lgView = null;
//			return;
//		}
//		
//		if(this.worldObj.isRemote && this.hasCameraLocation() && this.worldObj.getBlock(this.boundCameraLocation[0], this.boundCameraLocation[1], this.boundCameraLocation[2]) instanceof BlockSecurityCamera){
//			if(lgView == null){
//				if(mod_SecurityCraft.instance.hasViewForCoords(this.boundCameraLocation[0] + " " + this.boundCameraLocation[1] + " " + this.boundCameraLocation[2])){
//					System.out.println("Using old view for " + Utils.getFormattedCoordinates(this.boundCameraLocation[0], this.boundCameraLocation[1], this.boundCameraLocation[2]));
//					lgView = mod_SecurityCraft.instance.getViewFromCoords(this.boundCameraLocation[0] + " " + this.boundCameraLocation[1] + " " + this.boundCameraLocation[2]);
//					lgView.setAnimator(new CameraAnimatorSecurityCamera(lgView.getCamera(), this.worldObj.getBlockMetadata(this.boundCameraLocation[0], this.boundCameraLocation[1], this.boundCameraLocation[2])));
//					lgView.grab();
//					lgView.markDirty();
//					//this.worldObj.markBlockRangeForRenderUpdate(xCoord, yCoord, zCoord, xCoord, yCoord, zCoord);
//					return;
//				}
//				
//				lgView = mod_SecurityCraft.instance.getLGPanelRenderer().createWorldView(0, new ChunkCoordinates(this.boundCameraLocation[0], this.boundCameraLocation[1], this.boundCameraLocation[2]), 192, 192);
//				lgView.setAnimator(new CameraAnimatorSecurityCamera(lgView.getCamera(), this.worldObj.getBlockMetadata(this.boundCameraLocation[0], this.boundCameraLocation[1], this.boundCameraLocation[2])));
//				if(!mod_SecurityCraft.instance.hasViewForCoords(this.boundCameraLocation[0] + " " + this.boundCameraLocation[1] + " " + this.boundCameraLocation[2])){
//					System.out.println("Inserting new view at" + Utils.getFormattedCoordinates(this.boundCameraLocation[0], this.boundCameraLocation[1], this.boundCameraLocation[2]));
//					mod_SecurityCraft.instance.lgViews.put(this.boundCameraLocation[0] + " " + this.boundCameraLocation[1] + " " + this.boundCameraLocation[2], lgView);
//				}
//				
//				lgView.grab();
//				//this.worldObj.markBlockRangeForRenderUpdate(xCoord, yCoord, zCoord, xCoord, yCoord, zCoord);
//			}
//		}
	}
	
	public void writeToNBT(NBTTagCompound par1NBTTagCompound){
        super.writeToNBT(par1NBTTagCompound);
        
        if(this.boundCameraLocation != null && this.hasCameraLocation()){
        	par1NBTTagCompound.setString("cameraLoc", this.boundCameraLocation[0] + " " + this.boundCameraLocation[1] + " " + this.boundCameraLocation[2]);
        }       
    }

    public void readFromNBT(NBTTagCompound par1NBTTagCompound){
        super.readFromNBT(par1NBTTagCompound);

        if (par1NBTTagCompound.hasKey("cameraLoc"))
        {
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
	
	@SideOnly(Side.CLIENT)
	public IWorldView getLGRenderer(){
		return this.lgView;
	}

}
