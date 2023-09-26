package net.geforcemods.securitycraft.api;

import java.util.List;

import net.geforcemods.securitycraft.blockentities.SonicSecuritySystemBlockEntity;
import net.geforcemods.securitycraft.misc.BlockEntityTracker;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * This interface marks a TileEntity as being lockable by SecurityCraft's Sonic Security System. You can add your own custom
 * functionality when a block is being locked. For example, not allowing players to interact with the block or disabling a
 * certain aspect of it.
 *
 * @author Geforce
 */
public interface ILockable {
	/**
	 * @return this as a TileEntity
	 */
	public default TileEntity getThisBlockEntity() {
		return (TileEntity) this;
	}

	/**
	 * @return If this TileEntity is currently being locked down by a Sonic Security System
	 */
	public default boolean isLocked() {
		TileEntity thisBe = getThisBlockEntity();
		List<SonicSecuritySystemBlockEntity> sonicSecuritySystems = BlockEntityTracker.SONIC_SECURITY_SYSTEM.getBlockEntitiesInRange(thisBe.getLevel(), thisBe.getBlockPos());

		for (SonicSecuritySystemBlockEntity be : sonicSecuritySystems) {
			if (be.isActive() && be.isLinkedToBlock(thisBe.getBlockPos()))
				return be.wasCorrectTunePlayed() == be.disablesBlocksWhenTuneIsPlayed();
		}

		return false;
	}

	/**
	 * Called when a locked block is right-clicked by a player.
	 *
	 * @param level The world that you're in.
	 * @param pos The position of the block that was clicked.
	 * @return Return true if you want the player's interaction with the block to be stopped, false otherwise.
	 */
	public default boolean disableInteractionWhenLocked(World level, BlockPos pos, PlayerEntity player) {
		return true;
	}
}
