package net.geforcemods.securitycraft.api;

/**
 * This interface marks a TileEntity as being lockable
 * by SecurityCraft's Sonic Security System. You can add
 * your own custom functionality when a block is being 
 * locked. For example, not allowing players to interact
 * with the block or disabling a certain aspect of it.
 *
 * @author Geforce
 */
public interface ILockable {
	
	/**
	 * @return Can this TileEntity be locked by a Sonic Security System?
	 */
	public boolean canBeLocked();
	
	/**
	 * @return If this TileEntity is currently being locked down by a Sonic Security System
	 */
	public boolean isLocked();

}
