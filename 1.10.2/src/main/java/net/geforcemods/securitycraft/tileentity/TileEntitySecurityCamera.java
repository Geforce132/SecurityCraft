package net.geforcemods.securitycraft.tileentity;

import net.geforcemods.securitycraft.api.CustomizableSCTE;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.OptionFloat;
import net.geforcemods.securitycraft.misc.EnumCustomModules;

public class TileEntitySecurityCamera extends CustomizableSCTE {
	
	private final float CAMERA_SPEED = 0.0180F;
	
	public float cameraRotation = 0.0F;
	private boolean addToRotation = true;

	private OptionFloat rotationSpeedOption = new OptionFloat("rotationSpeed", CAMERA_SPEED, 0.0100F, 0.0250F, 0.001F);
	
	@Override
	public void update(){
		super.update();
		
		if(addToRotation && cameraRotation <= 1.55F){
			cameraRotation += rotationSpeedOption.asFloat();
		}else{
			addToRotation = false;
		}
		
		if(!addToRotation && cameraRotation >= -1.55F){
			cameraRotation -= rotationSpeedOption.asFloat();
		}else{
			addToRotation = true;
		}
	}
   
	@Override
	public EnumCustomModules[] acceptedModules(){
		return new EnumCustomModules[] { EnumCustomModules.REDSTONE };
	}

	@Override
	public Option<?>[] customOptions() {
		return new Option[]{ rotationSpeedOption };
	}

}
