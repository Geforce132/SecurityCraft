package org.freeforums.geforce.securitycraft.interfaces;

/**
 * This interface marks a {@link TileEntity} as "ownable". Any TileEntity
 * that implements this interface is able to be destroyed by the
 * Universal Block Remover.
 * 
 * @author Geforce
 */
public interface IOwnable {
	
	/**
	 * @return The owner's UUID tag.
	 */
	public String getOwnerUUID();
	
	/**
	 * @return The owner's username.
	 */	
	public String getOwnerName();
	
	/**
	 * Save the UUID and name of the player who placed the Block down to
	 * your TileEntity's NBTTagCompound here.
	 */
	public void setOwner(String uuid, String name);

}
