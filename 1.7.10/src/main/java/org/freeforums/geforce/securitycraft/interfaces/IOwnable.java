package org.freeforums.geforce.securitycraft.interfaces;

public interface IOwnable {
	
	public String getOwnerUUID();
	
	public String getOwnerName();
	
	public void setOwner(String uuid, String name);

}
