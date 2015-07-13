package org.freeforums.geforce.securitycraft.imc.lookingglass;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ChunkCoordinates;

import com.xcompwiz.lookingglass.api.animator.ICameraAnimator;
import com.xcompwiz.lookingglass.api.view.IViewCamera;

/**
 * Not used at the moment.
 * 
 * @author Geforce
 */
@Deprecated
public class CameraAnimatorCompactCamera implements ICameraAnimator {
	
	private IViewCamera camera;
	private int x = 0, y = 0, z = 0;
	
	public CameraAnimatorCompactCamera(IViewCamera camera, int x, int y, int z, int meta){
		this.x = x;
		this.y = y;
		this.z = z;
		
		double camX, camY, camZ, camYOffset;
		camX = camera.getX();
		camY = camera.getY();
		camZ = camera.getZ();
		camYOffset = 2.67D;
		
		if(meta == 1){
			//Leave yaw at 0F.
			camera.setLocation(camX + 0.5D, camY - camYOffset, camZ + 0.5D);
		}else if(meta == 2){
			camera.setYaw(270F);
			camera.setLocation(camX + 0.5D, camY - camYOffset, camZ + 0.5D);
		}else if(meta == 3){
			camera.setYaw(180F);
			camera.setLocation(camX + 0.5D, camY - camYOffset, camZ + 0.5D);
		}else if(meta == 4){
			camera.setYaw(90F);
			camera.setLocation(camX, camY - camYOffset, camZ + 0.5D);
		}
		
		this.camera = camera;
	}

	public void refresh() {
		Minecraft.getMinecraft().theWorld.markBlockRangeForRenderUpdate(x, y, z, x, y, z);
	}

	public void setTarget(ChunkCoordinates arg0) {

	}

	public void update(float arg0) {

	}

}
