package net.geforcemods.securitycraft.misc;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;

public enum SCSounds {

	ALARM("securitycraft:alarm"),
	CAMERAZOOMIN("securitycraft:camerazoomin"),
	CAMERASNAP("securitycraft:camerasnap"),
	TASERFIRED("securitycraft:taserfire"),
	ELECTRIFIED("securitycraft:electrified"),
	LOCK("securitycraft:lock");

	public final String path;
	public final ResourceLocation location;
	public final SoundEvent event;

	private SCSounds(String path){
		this.path = path;
		location = new ResourceLocation(path);
		event = new SoundEvent(new ResourceLocation(path));
		event.setRegistryName(path);
	}
}
