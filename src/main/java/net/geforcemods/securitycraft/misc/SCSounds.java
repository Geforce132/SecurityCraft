package net.geforcemods.securitycraft.misc;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;

public enum SCSounds {

	ALARM("securitycraft:alarm"),
	CAMERAZOOMIN("securitycraft:cameraZoomIn"),
	CAMERASNAP("securitycraft:cameraSnap"),
	TASERFIRED("securitycraft:taserFire"),
	ELECTRIFIED("securitycraft:electrified"),
	LOCK("securitycraft:lock"),
	GET_ITEM("securitycraft:get_item");

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
