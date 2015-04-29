package org.freeforums.geforce.securitycraft.misc;

public enum SCSounds {
	
	ALARM("securitycraft:alarm", 20),
	CAMERAZOOMIN("securitycraft:cameraZoomIn", 10),
	CAMERASNAP("securitycraft:cameraSnap", 15);

	
	public final String path;
	public final int tickLength;

	private SCSounds(String path, int tickLength){
		this.path = path;
		this.tickLength = tickLength;
	}
	


}
