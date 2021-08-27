package net.geforcemods.securitycraft.api;

import net.geforcemods.securitycraft.SecurityCraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.registries.DataSerializerEntry;
import net.minecraftforge.registries.ObjectHolder;

/**
 * This class is used with {@link IOwnable} to get the player of the block.
 * Allows for easy access to the player's IGN and UUID, with a few helpful methods as well.
 *
 * @author Geforce
 */
public class Owner {
	@ObjectHolder(SecurityCraft.MODID + ":owner")
	public static final DataSerializerEntry SERIALIZER = null;
	private String playerName = "owner";
	private String playerUUID = "ownerUUID";
	private boolean validated = true;

	public Owner() {
	}

	public Owner(String playerName, String playerUUID) {
		this.playerName = playerName;
		this.playerUUID = playerUUID;
	}

	public Owner(String playerName, String playerUUID, boolean validated) {
		this.playerName = playerName;
		this.playerUUID = playerUUID;
		this.validated = validated;
	}

	public static Owner fromCompound(CompoundTag tag) {
		Owner owner = new Owner();

		if (tag != null){
			owner.load(tag);
		}

		return owner;
	}

	public void load(CompoundTag tag) {
		if (tag.contains("owner"))
			playerName = tag.getString("owner");

		if (tag.contains("ownerUUID"))
			playerUUID = tag.getString("ownerUUID");

		if (tag.contains("ownerValidated"))
			validated = tag.getBoolean("ownerValidated");
	}

	public void save(CompoundTag tag) {
		tag.putString("owner", playerName);
		tag.putString("ownerUUID", playerUUID);
		tag.putBoolean("ownerValidated", validated);
	}

	/**
	 * @return If this user is the owner of the given blocks.
	 */
	public boolean owns(IOwnable... ownables) {
		for(IOwnable ownable : ownables) {
			if(ownable == null) continue;

			String uuid = ownable.getOwner().getUUID();
			String owner = ownable.getOwner().getName();

			// Check the player's UUID first.
			if(uuid != null && !uuid.equals(playerUUID))
				return false;

			// If the TileEntity doesn't have a UUID saved, use the player's name instead.
			if(owner != null && uuid.equals("ownerUUID") && !owner.equals("owner") && !owner.equals(playerName))
				return false;
		}

		return true;
	}

	/**
	 * @return If this person is the same person as the given player.
	 */
	public boolean isOwner(Player player) {
		if(player == null) return false;
		String uuid = player.getGameProfile().getId().toString();
		String owner = player.getName().getString();

		if(uuid != null && uuid.equals(playerUUID))
			return true;

		return owner != null && playerUUID.equals("ownerUUID") && owner.equals(playerName);
	}

	/**
	 * Set the UUID and name of a new owner using strings.
	 */
	public void set(String uuid, String name) {
		playerName = name;
		playerUUID = uuid;
	}

	/**
	 * Set the owner's new name.
	 *
	 * @param name The new owner's name
	 */
	public void setOwnerName(String name) {
		playerName = name;
	}

	/**
	 * Set the owner's new UUID.
	 *
	 * @param uuid The new owner's UUID
	 */
	public void setOwnerUUID(String uuid) {
		playerUUID = uuid;
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
		return playerName;
	}

	/**
	 * @return The owner's UUID.
	 */
	public String getUUID() {
		return playerUUID;
	}

	/**
	 * @return true if this owner is validated by the owning player
	 */
	public boolean isValidated() {
		return validated;
	}

	@Override
	public String toString() {
		return "Name: " + playerName + "  UUID: " + playerUUID;
	}

	@Override
	public boolean equals(Object obj)
	{
		return obj instanceof Owner owner && getName().equals(owner.getName()) && getUUID().equals(owner.getUUID());
	}

	public static EntityDataSerializer<Owner> getSerializer()
	{
		return (EntityDataSerializer<Owner>)SERIALIZER.getSerializer();
	}
}
