package org.freeforums.geforce.securitycraft.interfaces;

/**
 * Implement this interface in your Block or Item class to add the info
 * for use with SecurityCraft's "/sc help" and "/sc recipe" commands.
 * 
 * @author Geforce
 */

public interface IHelpInfo {
	
	public String getHelpInfo();

	public String[] getRecipe();

}
