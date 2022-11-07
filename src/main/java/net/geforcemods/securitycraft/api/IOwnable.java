package net.geforcemods.securitycraft.api;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * This interface marks a {@link BlockEntity} as "ownable". Any block entity that implements this interface is able to be
 * destroyed by the Universal Block Remover, and can only be broken or modified by the person who placed it down. <p>
 *
 * @author Geforce
 */
public interface IOwnable {
	/**
	 * @return An Owner object containing the player's name and UUID
	 */
	public Owner getOwner();

	/**
	 * Save a new owner to your Owner object here. <p> The easiest way is to use Owner.set(UUID, name), this method is here
	 * mainly for convenience.
	 *
	 * @param uuid The UUID of the new player.
	 * @param name The name of the new player.
	 */
	public void setOwner(String uuid, String name);

	/**
	 * @return true if the owner of this IOwnable should be invalidated when changed by the Universal Owner Changer
	 */
	default boolean needsValidation() {
		return false;
	}

	/**
	 * Executes after the owner has been changed and invalidates this if it needs validation
	 *
	 * @param level The current level
	 * @param state The IOwnable's state
	 * @param pos The IOwnable's position
	 * @param player The player that changed the owner of the IOwnable
	 */
	default void onOwnerChanged(BlockState state, Level level, BlockPos pos, Player player) {
		if (needsValidation()) {
			getOwner().setValidated(false);
			PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.UNIVERSAL_OWNER_CHANGER.get().getDescriptionId()), Utils.localize("messages.securitycraft:universalOwnerChanger.ownerInvalidated"), ChatFormatting.GREEN);
		}

		BlockEntity be = (BlockEntity) this;

		if (be instanceof LinkableBlockEntity linkable)
			linkable.createLinkedBlockAction(new LinkedAction.OwnerChanged(getOwner()), linkable);

		be.setChanged();
	}
}
