package net.geforcemods.securitycraft.misc;

import net.minecraft.util.ResourceLocation;

public enum SCSounds {
	
	ALARM("securitycraft:alarm", 20),
	CAMERAZOOMIN("securitycraft:cameraZoomIn", 10),
	CAMERASNAP("securitycraft:cameraSnap", 15),
	TASERFIRED("securitycraft:taserFire", 20),
	ELECTRIFIED("securitycraft:electrified", 20);

	public final ResourceLocation path;
	public final int tickLength;

	private SCSounds(String path, int tickLength){
		this.path = new ResourceLocation(path);
		this.tickLength = tickLength;
	}
	


}
