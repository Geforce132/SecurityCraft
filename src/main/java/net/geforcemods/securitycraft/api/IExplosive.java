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
	 * @param level The level your block is in.
	 * @param pos Your block's position.
	 */
	public void explode(Level level, BlockPos pos);

	/**
	 * Re-activate your defused mine.
	 *
	 * @param level The level your block is in.
	 * @param pos Your block's position.
	 * @return true if the mine was activated, false otherwise
	 */
	public boolean activateMine(Level level, BlockPos pos);

	/**
	 * Defuse your active mine.
	 *
	 * @param level The level your block is in.
	 * @param pos Your block's position.
	 * @return true if the mine was defused, false otherwise
	 */
	public boolean defuseMine(Level level, BlockPos pos);

	/**
	 * Whether the mine is currently active, aka if it can explode
	 *
	 * @param level The level the block is in
	 * @param pos true if the mine is active, false otherwise
	 */
	public boolean isActive(Level level, BlockPos pos);

	/**
	 * @return Is your mine defusable?
	 */
	public boolean isDefusable();

}
