package net.geforcemods.securitycraft.misc;

import net.geforcemods.securitycraft.SecurityCraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

public enum SCSounds {
	ALARM("alarm"),
	CAMERAZOOMIN("camerazoomin"),
	CAMERASNAP("camerasnap"),
	TASERFIRED("taserfire"),
	ELECTRIFIED("electrified"),
	LOCK("lock"),
	PING("ping"),
	GET_ITEM("get_item"),
	DISPLAY_CASE_OPEN("display_case_open"),
	DISPLAY_CASE_CLOSE("display_case_close");

	public final String path;
	public final ResourceLocation location;
	public final SoundEvent event;

	private SCSounds(String path) {
		this.path = path;
		location = SecurityCraft.resLoc(path);
		event = SoundEvent.createVariableRangeEvent(location);
	}
}
