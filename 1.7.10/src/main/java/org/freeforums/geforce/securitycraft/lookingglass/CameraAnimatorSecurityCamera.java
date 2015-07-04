package org.freeforums.geforce.securitycraft.lookingglass;

import net.minecraft.util.ChunkCoordinates;

import com.xcompwiz.lookingglass.api.animator.ICameraAnimator;
import com.xcompwiz.lookingglass.api.view.IViewCamera;

/**
 * The IWorldView animator for the security cameras. <p>
 * 
 * Sets the location of the camera, and rotates the view.
 * 
 * @author Geforce
 */
public class CameraAnimatorSecurityCamera implements ICameraAnimator {
	
	private final float cameraRotationSpeed = 0.5F;
	private final double cameraYOffset = 2.425D;
	
	private IViewCamera camera;
	private int cameraMeta = 0;
	private ChunkCoordinates target;
	
	private boolean reverseRotation = false;
	
	public CameraAnimatorSecurityCamera(IViewCamera camera, int securityCameraMeta){
		this.camera = camera;
		this.cameraMeta = securityCameraMeta;
		
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
	

	public void setTarget(ChunkCoordinates target) {
		this.target = target;
	}

	public void refresh() {
		
	}

	public void update(float arg0) {
		if(camera == null || cameraMeta == 0){ return; }
		
		float yaw = this.camera.getYaw();
		
		if(cameraMeta == 1){
			if(!reverseRotation && yaw >= 270F){
				reverseRotation = true;
			}else if(reverseRotation && yaw <= 90F){
				reverseRotation = false;
			}
			
			if(reverseRotation){
				this.camera.setYaw(yaw - cameraRotationSpeed);
			}else{
				this.camera.setYaw(yaw + cameraRotationSpeed);
			}
		}else if(cameraMeta == 2){
			if(!reverseRotation && yaw >= 180F){
				reverseRotation = true;
			}else if(reverseRotation && yaw <= 0F){
				reverseRotation = false;
			}
			
			if(reverseRotation){
				this.camera.setYaw(yaw - cameraRotationSpeed);
			}else{
				this.camera.setYaw(yaw + cameraRotationSpeed);
			}
		}else if(cameraMeta == 3){
			if(!reverseRotation && yaw >= 90F){
				reverseRotation = true;
			}else if(reverseRotation && yaw <= -90F){
				reverseRotation = false;
			}
			
			if(reverseRotation){
				this.camera.setYaw(yaw - cameraRotationSpeed);
			}else{
				this.camera.setYaw(yaw + cameraRotationSpeed);
			}
		}else if(cameraMeta == 4){
			if(!reverseRotation && yaw >= 360F){
				reverseRotation = true;
			}else if(reverseRotation && yaw <= 180F){
				reverseRotation = false;
			}
			
			if(reverseRotation){
				this.camera.setYaw(yaw - cameraRotationSpeed);
			}else{
				this.camera.setYaw(yaw + cameraRotationSpeed);
			}
		}		
	}

}
