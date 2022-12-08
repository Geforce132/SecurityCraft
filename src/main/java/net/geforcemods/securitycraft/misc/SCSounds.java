package net.geforcemods.securitycraft.misc;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

public enum SCSounds {
	ALARM("securitycraft:alarm"),
	CAMERAZOOMIN("securitycraft:camerazoomin"),
	CAMERASNAP("securitycraft:camerasnap"),
	TASERFIRED("securitycraft:taserfire"),
	ELECTRIFIED("securitycraft:electrified"),
	LOCK("securitycraft:lock"),
	PING("securitycraft:ping"),
	GET_ITEM("securitycraft:get_item"),
	DISPLAY_CASE_OPEN("securitycraft:display_case_open"),
	DISPLAY_CASE_CLOSE("securitycraft:display_case_close");

	public final String path;
	public final ResourceLocation location;
	public final SoundEvent event;

	private SCSounds(String path) {
		this.path = path;
		location = new ResourceLocation(path);
		event = SoundEvent.createVariableRangeEvent(new ResourceLocation(path));
	}
}
