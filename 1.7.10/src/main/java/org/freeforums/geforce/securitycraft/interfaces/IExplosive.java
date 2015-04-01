package org.freeforums.geforce.securitycraft.interfaces;

import net.minecraft.world.World;

/**
 * Enables a Block to be remotely detonated using SecurityCraft's mine remote access tool.
 * 
 * @author Geforce
 */
public interface IExplosive {
	
	/**
	 * Handle your explosive's explosion here.
	 * 
	 * @param world The world your block is in.
	 * @param par2 Your block's x coordinate.
	 * @param par3 Your block's y coordinate.
	 * @param par4 Your block's z coordinate.
	 */
	public void explode(World world, int par2, int par3, int par4); 
	
	/**
	 * Re-activate your defused mine.
	 * 
	 * @param world The world your block is in.
	 * @param par2 Your block's x coordinate.
	 * @param par3 Your block's y coordinate.
	 * @param par4 Your block's z coordinate.
	 */
	public void activateMine(World world, int par2, int par3, int par4);
	
	/**
	 * Defuse your active mine.
	 * 
	 * @param world The world your block is in.
	 * @param par2 Your block's x coordinate.
	 * @param par3 Your block's y coordinate.
	 * @param par4 Your block's z coordinate.
	 */
	public void defuseMine(World world, int par2, int par3, int par4);
	
	/**
	 * Is your mine currently active?
	 * 
	 * @param world
	 * @param par2 Your block's x coordinate.
	 * @param par3 Your block's y coordinate.
	 * @param par4 Your block's z coordinate.
	 */
	public boolean isActive(World world, int par2, int par3, int par4);
	
	/**
	 * @return Is your mine defusable?
	 */
	public boolean isDefusable();

}
