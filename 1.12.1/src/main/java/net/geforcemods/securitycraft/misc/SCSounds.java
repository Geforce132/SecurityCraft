package net.geforcemods.securitycraft.misc;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;

public enum SCSounds {
	
	ALARM("securitycraft:alarm", 20),
	CAMERAZOOMIN("securitycraft:cameraZoomIn", 10),
	CAMERASNAP("securitycraft:cameraSnap", 15),
	TASERFIRED("securitycraft:taserFire", 20),
	ELECTRIFIED("securitycraft:electrified", 20);

	public final String path;
	public final ResourceLocation location;
	public final SoundEvent event;
	public final int tickLength;

	private SCSounds(String path, int tickLength){
		this.path = path;
		this.location = new ResourceLocation(path);
		this.event = new SoundEvent(new ResourceLocation(path));
		this.tickLength = tickLength;
	}
	


}
