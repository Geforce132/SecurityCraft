package net.geforcemods.securitycraft.api;

import java.util.List;

import net.geforcemods.securitycraft.blockentities.SonicSecuritySystemBlockEntity;
import net.geforcemods.securitycraft.misc.BlockEntityTracker;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

/**
 * This interface marks a BlockEntity as being lockable by SecurityCraft's Sonic Security System. You can add your own custom
 * functionality when a block is being locked. For example, not allowing players to interact with the block or disabling a
 * certain aspect of it.
 *
 * @author Geforce
 */
public interface ILockable {
	/**
	 * @return this as a BlockEntity
	 */
	public default BlockEntity getThisBlockEntity() {
		return (BlockEntity) this;
	}

	/**
	 * @return If this BlockEntity is currently being locked down by a Sonic Security System
	 */
	public default boolean isLocked() {
		BlockEntity thisBe = getThisBlockEntity();
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
	 * @param level The level that you're in.
	 * @param pos The position of the block that was clicked.
	 * @return Return true if you want the player's interaction with the block to be stopped, false otherwise.
	 */
	public default boolean disableInteractionWhenLocked(Level level, BlockPos pos, Player player) {
		return true;
	}
}
