package net.geforcemods.securitycraft.api;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

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
	public void explode(Level world, BlockPos pos);

	/**
	 * Re-activate your defused mine.
	 *
	 * @param world The world your block is in.
	 * @param pos Your block's position.
	 * @return true if the mine was activated, false otherwise
	 */
	public boolean activateMine(Level world, BlockPos pos);

	/**
	 * Defuse your active mine.
	 *
	 * @param world The world your block is in.
	 * @param pos Your block's position.
	 * @return true if the mine was defused, false otherwise
	 */
	public boolean defuseMine(Level world, BlockPos pos);

	/**
	 * Is your mine currently active?
	 *
	 * @param world
	 * @param pos
	 */
	public boolean isActive(Level world, BlockPos pos);

	/**
	 * @return Is your mine defusable?
	 */
	public boolean isDefusable();

}
