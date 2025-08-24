package net.geforcemods.securitycraft.api;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.TeamUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.OwnableEntity;
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
	 * Called when this is validated
	 */
	default void onValidate() {}

	/**
	 * Executes after the owner has been changed and invalidates this if it needs validation
	 *
	 * @param state The IOwnable's state
	 * @param level The current level
	 * @param pos The IOwnable's position
	 * @param player The player who changed the owner of the IOwnable
	 * @param oldOwner The previous owner of this IOwnable
	 * @param newOwner The new owner of this IOwnable
	 */
	default void onOwnerChanged(BlockState state, Level level, BlockPos pos, Player player, Owner oldOwner, Owner newOwner) {
		if (needsValidation()) {
			getOwner().setValidated(false);

			if (player != null)
				PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.UNIVERSAL_OWNER_CHANGER.get().getDescriptionId()), Utils.localize("messages.securitycraft:universalOwnerChanger.ownerInvalidated"), ChatFormatting.GREEN);
		}

		BlockEntity be = (BlockEntity) this;

		if (be instanceof LinkableBlockEntity linkable)
			linkable.propagate(new ILinkedAction.OwnerChanged(getOwner()), linkable);

		be.setChanged();
	}

	/**
	 * Checks whether the given entity owns this IOwnable.
	 *
	 * @param entity The entity to check ownership of
	 * @return true if the given entity owns this IOwnable, false otherwise
	 */
	public default boolean isOwnedBy(Entity entity) {
		return entity instanceof Player player && isOwnedBy(player, false);
	}

	/**
	 * Checks whether the given player owns this IOwnable.
	 *
	 * @param player The player to check ownership of
	 * @param ignoreMask Whether the incognito mask, if worn by the player, should be taken into account
	 * @return true if the player, or the player represented by the incognito mask, owns this IOwnable, false otherwise
	 */
	public default boolean isOwnedBy(Player player, boolean ignoreMask) {
		return isOwnedBy(new Owner(player)) && (ignoreMask || isOwnedBy(PlayerUtils.getOwnerFromPlayerOrMask(player)));
	}

	/**
	 * Checks whether the given owner owns this IOwnable.
	 *
	 * @param otherOwner The owner to check ownership of
	 * @return true if the given owner owns this IOwnable, false otherwise
	 */
	public default boolean isOwnedBy(Owner otherOwner) {
		Owner self = getOwner();

		if (TeamUtils.areOnSameTeam(self, otherOwner))
			return true;

		String selfUUID = self.getUUID();
		String otherUUID = otherOwner.getUUID();
		String otherName = otherOwner.getName();

		// Check the player's UUID first.
		if (otherUUID != null && otherUUID.equals(selfUUID))
			return true;

		// If the BlockEntity doesn't have a UUID saved, use the player's name instead.
		return otherName != null && (selfUUID.equals("ownerUUID") || otherUUID.equals("ownerUUID")) && otherName.equals(self.getName());
	}

	/**
	 * Checks whether this and ownable entity's owner owns this block entity
	 *
	 * @param entity The entity to check
	 * @return true if the entity's owner owns this block entity, false otherwise
	 */
	public default boolean allowsOwnableEntity(OwnableEntity entity) {
		Owner beOwner = getOwner();

		return entity.getOwnerUUID() != null && (entity.getOwnerUUID().toString().equals(beOwner.getUUID()) || TeamUtils.areOnSameTeam(beOwner, new Owner(entity.getOwner())));
	}

	/**
	 * Checks if this block entity should ignore its owner. Note that this is not used in {@link #isOwnedBy(Player)}, so there
	 * are cases where SecurityCraft does not use this method in conjunction with owner checks (e.g. breaking reinforced blocks).
	 *
	 * @return true if the owner is ignored, false otherwise
	 */
	public default boolean ignoresOwner() {
		return false;
	}
}
