package net.geforcemods.securitycraft.api;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.util.TeamUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

/**
 * This class is used with {@link IOwnable} to get the player of the block. Allows for easy access to the player's IGN and
 * UUID, with a few helpful methods as well.
 *
 * @author Geforce
 */
public class Owner {
	//@formatter:off
	public static final StreamCodec<RegistryFriendlyByteBuf, Owner> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.STRING_UTF8, Owner::getName,
			ByteBufCodecs.STRING_UTF8, Owner::getUUID,
			Owner::new);
	//@formatter:on
	private String ownerName = "owner";
	private String ownerUUID = "ownerUUID";
	private boolean validated = true;

	public Owner() {}

	public Owner(Entity entity) {
		if (entity instanceof Player player) {
			ownerName = player.getName().getString();
			ownerUUID = player.getGameProfile().getId().toString();
		}
	}

	public Owner(Player player) {
		if (player != null) {
			ownerName = player.getName().getString();
			ownerUUID = player.getGameProfile().getId().toString();
		}
	}

	public Owner(String playerName, String playerUUID) {
		this.ownerName = playerName;
		this.ownerUUID = playerUUID;
	}

	public Owner(String playerName, String playerUUID, boolean validated) {
		this.ownerName = playerName;
		this.ownerUUID = playerUUID;
		this.validated = validated;
	}

	public static Owner fromCompound(CompoundTag tag) {
		Owner owner = new Owner();

		if (tag != null)
			owner.load(tag);

		return owner;
	}

	public void load(CompoundTag tag) {
		ownerName = tag.getStringOr("owner", ownerName);
		ownerUUID = tag.getStringOr("ownerUUID", ownerName);
		validated = tag.getBooleanOr("ownerValidated", validated);
	}

	public void save(CompoundTag tag, boolean saveValidationStatus) {
		tag.putString("owner", ownerName);
		tag.putString("ownerUUID", ownerUUID);

		if (saveValidationStatus)
			tag.putBoolean("ownerValidated", validated);
	}

	/**
	 * @return If this user is the owner of the given blocks.
	 */
	public boolean owns(IOwnable... ownables) {
		for (IOwnable ownable : ownables) {
			if (ownable == null)
				continue;

			Owner ownableOwner = ownable.getOwner();
			String uuidToCheck = ownableOwner.getUUID();
			String nameToCheck = ownableOwner.getName();

			if (TeamUtils.areOnSameTeam(this, ownableOwner))
				continue;

			// Check the player's UUID first.
			if (uuidToCheck == null || !uuidToCheck.equals(ownerUUID))
				return false;

			// If the BlockEntity doesn't have a UUID saved, use the player's name instead.
			if (nameToCheck != null && uuidToCheck.equals("ownerUUID") && !nameToCheck.equals("owner") && !nameToCheck.equals(ownerName))
				return false;
		}

		return true;
	}

	/**
	 * Set the UUID and name of a new owner using strings.
	 */
	public void set(String uuid, String name) {
		ownerName = name;
		ownerUUID = uuid;
	}

	/**
	 * Set the owner's new name.
	 *
	 * @param name The new owner's name
	 */
	public void setOwnerName(String name) {
		ownerName = name;
	}

	/**
	 * Set the owner's new UUID.
	 *
	 * @param uuid The new owner's UUID
	 */
	public void setOwnerUUID(String uuid) {
		ownerUUID = uuid;
	}

	/**
	 * Sets the validation status of the owner
	 *
	 * @param validated The owner's new validation status
	 */
	public void setValidated(boolean validated) {
		this.validated = validated;
	}

	/**
	 * @return The owner's name.
	 */
	public String getName() {
		return ownerName;
	}

	/**
	 * @return The owner's UUID.
	 */
	public String getUUID() {
		return ownerUUID;
	}

	/**
	 * @return true if this owner is validated by the owning player
	 */
	public boolean isValidated() {
		return validated;
	}

	@Override
	public String toString() {
		return "Name: " + ownerName + "  UUID: " + ownerUUID;
	}

	public Owner copy() {
		return new Owner(ownerName, ownerUUID);
	}

	/**
	 * @deprecated Use {@link #owns} or {@link IOwnable#isOwnedBy} to check for ownership
	 */
	@Override
	@Deprecated
	public boolean equals(Object obj) {
		return obj instanceof Owner owner && getName().equals(owner.getName()) && getUUID().equals(owner.getUUID());
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(ownerName).append(ownerUUID).build();
	}

	public static EntityDataSerializer<Owner> getSerializer() {
		return SCContent.OWNER_SERIALIZER.get();
	}
}
