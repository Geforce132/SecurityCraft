package net.geforcemods.securitycraft.api;

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
	 * @return An Owner object containing the player's name and UUID.
	 *         You are responsible for reading and writing the name and UUID variables
	 *         to your TileEntity's NBTTagCompound in writeToNBT() and readFromNBT().
	 */
	public Owner getOwner();

	/**
	 * Save a new owner to your Owner object here. <p>
	 * The easiest way is to use Owner.set(UUID, name), this method is
	 * here mainly for convenience.
	 *
	 * @param uuid The UUID of the new player.
	 * @param name The name of the new player.
	 */
	public void setOwner(String uuid, String name);

}
