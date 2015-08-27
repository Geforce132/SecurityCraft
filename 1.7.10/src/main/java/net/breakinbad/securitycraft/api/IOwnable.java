package net.breakinbad.securitycraft.api;

import net.minecraft.tileentity.TileEntity;

/**
 * This interface marks a {@link TileEntity} as "ownable". Any TileEntity
 * that implements this interface is able to be destroyed by the
 * Universal Block Remover, and can only be broken or modified by
 * the person who placed it down. <p>
 * 
 * Use this to set the owner, preferably in Block.onBlockPlacedBy(): <pre> 
 * ((IOwnable) world.getTileEntity(x, y, z)).setOwner(player.getGameProfile().getId().toString(), player.getCommandSenderName());
 * </pre>
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
	 * your TileEntity. You are responsible for reading and writing the variables
	 * to your NBTTagCompound in writeToNBT() and readFromNBT().
	 */
	public void setOwner(String uuid, String name);

}
