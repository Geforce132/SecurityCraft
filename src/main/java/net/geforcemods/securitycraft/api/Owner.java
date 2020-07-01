package net.geforcemods.securitycraft.api;

import net.geforcemods.securitycraft.SecurityCraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.datasync.IDataSerializer;
import net.minecraft.util.text.ITextComponent;
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

	public Owner() {}

	public Owner(String playerName, String playerUUID) {
		this.playerName = playerName;
		this.playerUUID = playerUUID;
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
	public boolean isOwner(PlayerEntity player) {
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
	 * Set the UUID and name of a new owner using strings.
	 */
	public void set(String uuid, ITextComponent name) {
		playerName = name.getString();
		playerUUID = uuid;
	}

	/**
	 * Set the UUID and name of a new owner using another Owner object.
	 */
	public void set(Owner newOwner) {
		playerName = newOwner.getName();
		playerUUID = newOwner.getUUID();
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

	@Override
	public String toString() {
		return "Name: " + playerName + "  UUID: " + playerUUID;
	}

	@Override
	public boolean equals(Object obj)
	{
		return obj instanceof Owner && getName().equals(((Owner)obj).getName()) && getUUID().equals(((Owner)obj).getUUID());
	}

	public static IDataSerializer<Owner> getSerializer()
	{
		return (IDataSerializer<Owner>)SERIALIZER.getSerializer();
	}
}
