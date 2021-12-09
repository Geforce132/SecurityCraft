package net.geforcemods.securitycraft.api;

import java.util.List;

import net.geforcemods.securitycraft.misc.SonicSecuritySystemTracker;
import net.geforcemods.securitycraft.tileentity.SonicSecuritySystemTileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

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
	 * @return this as a TileEntity
	 */
	public TileEntity getTileEntity();

	/**
	 * @return If this TileEntity is currently being locked down by a Sonic Security System
	 */
	public default boolean isLocked()
	{
		TileEntity thisTe = getTileEntity();
		List<SonicSecuritySystemTileEntity> sonicSecuritySystems = SonicSecuritySystemTracker.getSonicSecuritySystemsInRange(thisTe.getWorld(), thisTe.getPos());

		for(SonicSecuritySystemTileEntity te : sonicSecuritySystems) {
			if(te.isActive() && te.isLinkedToBlock(thisTe.getPos())) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Called when a locked block is right-clicked by a player.
	 * @param world The world that you're in.
	 * @param pos The position of the block that was clicked.
	 * @return Return true if you want the player's interaction with the block to be stopped, false otherwise.
	 */
	public default boolean onRightClickWhenLocked(World world, BlockPos pos, PlayerEntity player)
	{
		return true;
	}
}
