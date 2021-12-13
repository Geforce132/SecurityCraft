package net.geforcemods.securitycraft.api;

import java.util.List;

import net.geforcemods.securitycraft.misc.SonicSecuritySystemTracker;
import net.geforcemods.securitycraft.tileentity.TileEntitySonicSecuritySystem;
import net.minecraft.entity.player.EntityPlayer;
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
	public default TileEntity getThisTileEntity() {
		return (TileEntity)this;
	}

	/**
	 * @return If this TileEntity is currently being locked down by a Sonic Security System
	 */
	public default boolean isLocked()
	{
		TileEntity thisTe = getThisTileEntity();
		List<TileEntitySonicSecuritySystem> sonicSecuritySystems = SonicSecuritySystemTracker.getSonicSecuritySystemsInRange(thisTe.getWorld(), thisTe.getPos());

		for(TileEntitySonicSecuritySystem te : sonicSecuritySystems) {
			if(te.isActive() && te.isLinkedToBlock(thisTe.getPos())) {
				return !te.correctTuneWasPlayed; //if the correct tune was recently played, the block should not be locked
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
	public default boolean disableInteractionWhenLocked(World world, BlockPos pos, EntityPlayer player)
	{
		return true;
	}
}
