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
	 * @param level The level your block is in.
	 * @param pos Your block's position.
	 */
	public void explode(World level, BlockPos pos);

	/**
	 * Re-activate your defused mine.
	 *
	 * @param level The level your block is in.
	 * @param pos Your block's position.
	 * @return true if the mine was activated, false otherwise
	 */
	public boolean activateMine(World level, BlockPos pos);

	/**
	 * Defuse your active mine.
	 *
	 * @param level The level your block is in.
	 * @param pos Your block's position.
	 * @return true if the mine was defused, false otherwise
	 */
	public boolean defuseMine(World level, BlockPos pos);

	/**
	 * Whether the mine is currently active, aka if it can explode
	 *
	 * @param level The level the block is in
	 * @param pos true if the mine is active, false otherwise
	 */
	public boolean isActive(World level, BlockPos pos);

	/**
	 * @return Is your mine defusable?
	 */
	public boolean isDefusable();
}
