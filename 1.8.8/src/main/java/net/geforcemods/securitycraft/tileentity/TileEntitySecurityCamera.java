package net.geforcemods.securitycraft.tileentity;

import net.geforcemods.securitycraft.api.CustomizableSCTE;
import net.geforcemods.securitycraft.misc.EnumCustomModules;

public class TileEntitySecurityCamera extends CustomizableSCTE {
	
	private final float CAMERA_SPEED = 0.0180F;
	
	public float cameraRotation = 0.0F;
	private boolean addToRotation = true;

	public void update(){
		super.update();
		
		if(addToRotation && cameraRotation <= 1.55F){
			cameraRotation += CAMERA_SPEED;
		}else{
			addToRotation = false;
		}
		
		if(!addToRotation && cameraRotation >= -1.55F){
			cameraRotation -= CAMERA_SPEED;
		}else{
			addToRotation = true;
		}
	}
   
	public EnumCustomModules[] acceptedModules(){
		return new EnumCustomModules[] { EnumCustomModules.REDSTONE };
	}

}
