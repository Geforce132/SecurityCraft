package org.freeforums.geforce.securitycraft.interfaces;

/**
 * This interface is only used for the admin tool. Any password-protected
 * block's TileEntity will implement this interface.
 * 
 * @author Geforce
 */

public interface IPasswordProtected {
	
	public String getPassword();

}
