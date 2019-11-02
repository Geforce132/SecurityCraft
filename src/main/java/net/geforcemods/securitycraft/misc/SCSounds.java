package net.geforcemods.securitycraft.misc;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;

public enum SCSounds {

	ALARM("securitycraft:alarm", 20),
	CAMERAZOOMIN("securitycraft:cameraZoomIn", 10),
	CAMERASNAP("securitycraft:cameraSnap", 15),
	TASERFIRED("securitycraft:taserFire", 20),
	ELECTRIFIED("securitycraft:electrified", 20),
	LOCK("securitycraft:lock", 20);

	public final String path;
	public final ResourceLocation location;
	public final SoundEvent event;
	public final int tickLength;

	private SCSounds(String path, int tickLength){
		this.path = path;
		location = new ResourceLocation(path);
		event = new SoundEvent(new ResourceLocation(path));
		event.setRegistryName(path);
		this.tickLength = tickLength;
	}
}
