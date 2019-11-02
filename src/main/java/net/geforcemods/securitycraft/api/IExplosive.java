package net.geforcemods.securitycraft.api;

import net.minecraft.util.math.BlockPos;
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
	 * @param pos Your block's position.
	 */
	public void explode(World world, BlockPos pos);

	/**
	 * Re-activate your defused mine.
	 *
	 * @param world The world your block is in.
	 * @param pos Your block's position.
	 */
	public void activateMine(World world, BlockPos pos);

	/**
	 * Defuse your active mine.
	 *
	 * @param world The world your block is in.
	 * @param pos Your block's position.
	 */
	public void defuseMine(World world, BlockPos pos);

	/**
	 * Is your mine currently active?
	 *
	 * @param world
	 * @param pos
	 */
	public boolean isActive(World world, BlockPos pos);

	/**
	 * @return Is your mine defusable?
	 */
	public boolean isDefusable();

}
