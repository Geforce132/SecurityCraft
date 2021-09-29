package net.geforcemods.securitycraft.api;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.IDataSerializer;
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
	private String ownerName = "owner";
	private String ownerUUID = "ownerUUID";
	private boolean validated = true;

	public Owner() {}

	public Owner(String playerName, String playerUUID) {
		this.ownerName = playerName;
		this.ownerUUID = playerUUID;
	}

	public Owner(String playerName, String playerUUID, boolean validated) {
		this.ownerName = playerName;
		this.ownerUUID = playerUUID;
		this.validated = validated;
	}

	public static Owner fromCompound(CompoundNBT tag) {
		Owner owner = new Owner();

		if (tag != null){
			owner.read(tag);
		}

		return owner;
	}

	public void read(CompoundNBT tag) {
		if (tag.contains("owner"))
			ownerName = tag.getString("owner");

		if (tag.contains("ownerUUID"))
			ownerUUID = tag.getString("ownerUUID");

		if (tag.contains("ownerValidated"))
			validated = tag.getBoolean("ownerValidated");
	}

	public void write(CompoundNBT tag, boolean saveValidationStatus) {
		tag.putString("owner", ownerName);
		tag.putString("ownerUUID", ownerUUID);

		if (saveValidationStatus) {
			tag.putBoolean("ownerValidated", validated);
		}
	}

	/**
	 * @return If this user is the owner of the given blocks.
	 */
	public boolean owns(IOwnable... ownables) {
		for(IOwnable ownable : ownables) {
			if(ownable == null)
				continue;

			String uuidToCheck = ownable.getOwner().getUUID();
			String nameToCheck = ownable.getOwner().getName();

			if(ConfigHandler.SERVER.enableTeamOwnership.get() && !PlayerUtils.areOnSameTeam(ownerName, nameToCheck))
				return false;

			// Check the player's UUID first.
			if(uuidToCheck != null && !uuidToCheck.equals(ownerUUID))
				return false;

			// If the TileEntity doesn't have a UUID saved, use the player's name instead.
			if(nameToCheck != null && uuidToCheck.equals("ownerUUID") && !nameToCheck.equals("owner") && !nameToCheck.equals(ownerName))
				return false;
		}

		return true;
	}

	/**
	 * @return If this person is the same person as the given player.
	 */
	public boolean isOwner(PlayerEntity player) {
		if(player == null)
			return false;

		String uuidToCheck = player.getGameProfile().getId().toString();
		String nameToCheck = player.getName().getString();

		if(ConfigHandler.SERVER.enableTeamOwnership.get() && PlayerUtils.areOnSameTeam(ownerName, nameToCheck))
			return true;

		if(uuidToCheck != null && uuidToCheck.equals(ownerUUID))
			return true;

		return nameToCheck != null && ownerUUID.equals("ownerUUID") && nameToCheck.equals(ownerName);
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

	/**
	 * @deprecated Use {@link #owns} or {@link #isOwner} to check for ownership
	 */
	@Override
	@Deprecated
	public boolean equals(Object obj)
	{
		return obj instanceof Owner && getName().equals(((Owner)obj).getName()) && getUUID().equals(((Owner)obj).getUUID());
	}

	public static IDataSerializer<Owner> getSerializer()
	{
		return (IDataSerializer<Owner>)SERIALIZER.getSerializer();
	}
}
