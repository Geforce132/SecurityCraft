package net.geforcemods.securitycraft.api;

import net.minecraft.world.World;

/**
 * Enables a Block to be remotely detonated using SecurityCraft's mine remote access tool.
 *
 * @author Geforce
 */
public interface IExplosive {

	/**
	 * Called whenever someone uses the "detonate" option
	 * on a Mine Remote Access Tool button linked to this block. <p>
	 *
	 * You can handle your explosive's explosion here.
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
	 * Check to see if your block is in its active state. If so, return true.
	 *
	 * @param world
	 * @param par2 Your block's x coordinate.
	 * @param par3 Your block's y coordinate.
	 * @param par4 Your block's z coordinate.
	 *
	 */
	public boolean isActive(World world, int par2, int par3, int par4);

	/**
	 * @return Is your mine defusable?
	 */
	public boolean isDefusable();

}
