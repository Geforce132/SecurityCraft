package net.geforcemods.securitycraft.imc.lookingglass;

import com.xcompwiz.lookingglass.api.animator.ICameraAnimator;
import com.xcompwiz.lookingglass.api.view.IViewCamera;

import net.geforcemods.securitycraft.main.mod_SecurityCraft;
import net.geforcemods.securitycraft.tileentity.TileEntitySecurityCamera;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChunkCoordinates;

/**
 * The IWorldView animator for the security cameras. <p>
 * 
 * Sets the location of the camera, and rotates the view.
 * 
 * @author Geforce
 */
public class CameraAnimatorSecurityCamera implements ICameraAnimator {
	
	private final double cameraYOffset = 2.425D;
	
	private IViewCamera camera;
	private int cameraMeta = 0;
	private int xCoord, yCoord, zCoord;
		
	public CameraAnimatorSecurityCamera(IViewCamera camera, int xCoord, int yCoord, int zCoord, int securityCameraMeta){
		this.camera = camera;
		this.cameraMeta = securityCameraMeta;
		this.xCoord = xCoord;
		this.yCoord = yCoord;
		this.zCoord = zCoord;

		if(securityCameraMeta == 1){
			this.camera.setLocation(camera.getX() + 0.5D, camera.getY() - cameraYOffset, camera.getZ() + 0.5D);
		}else if(securityCameraMeta == 2){
			this.camera.setLocation(camera.getX() + 0.5D, camera.getY() - cameraYOffset, camera.getZ() + 0.5D);
		}else if(securityCameraMeta == 3){
			this.camera.setLocation(camera.getX() + 0.5D, camera.getY() - cameraYOffset, camera.getZ() + 0.5D);
		}else if(securityCameraMeta == 4){
			this.camera.setLocation(camera.getX() + 0.5D, camera.getY() - cameraYOffset, camera.getZ() + 0.5D);
		}
		
		if(securityCameraMeta == 1){
			this.camera.setYaw(180F);
		}else if(securityCameraMeta == 2){
			this.camera.setYaw(90F);
		}else if(securityCameraMeta == 3){
			this.camera.setYaw(0F);
		}else if(securityCameraMeta == 4){
			this.camera.setYaw(270F);
		}
	}
	

	public void setTarget(ChunkCoordinates target){}

	public void refresh(){}

	public void update(float arg0) {
		if(camera == null || cameraMeta == 0){ return; }
		if(Minecraft.getMinecraft().theWorld.getBlock(xCoord, yCoord, zCoord) != mod_SecurityCraft.securityCamera){ return; }
		
		float yaw = this.camera.getYaw();
		float cameraRotation = ((TileEntitySecurityCamera) Minecraft.getMinecraft().theWorld.getTileEntity(xCoord, yCoord, zCoord)).cameraRotation * 60;

		if(cameraMeta == 4){ 
			this.camera.setYaw(180 + cameraRotation);
		}else if(cameraMeta == 2){
			this.camera.setYaw(90 + cameraRotation);
		}else if(cameraMeta == 3){ 
			this.camera.setYaw(0 + cameraRotation);
		}else if(cameraMeta == 1){ 
			this.camera.setYaw(270 + cameraRotation);
		}	
	}
}
