package org.freeforums.geforce.securitycraft.api;

/**
 * Implement this interface in your Block or Item class to add the info
 * for use with SecurityCraft's "/sc recipe" commands.
 * 
 * @author Geforce
 */

public interface IHelpInfo {
	
	public String[] getRecipe();

}
