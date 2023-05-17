package net.geforcemods.securitycraft.api;

import java.util.Arrays;
import java.util.UUID;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.misc.CustomDamageSources;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.misc.SaltData;
import net.geforcemods.securitycraft.network.client.OpenScreen;
import net.geforcemods.securitycraft.network.client.OpenScreen.DataType;
import net.geforcemods.securitycraft.screen.CheckPasscodeScreen;
import net.geforcemods.securitycraft.screen.SetPasscodeScreen;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.network.PacketDistributor;

/**
 * Implementing this interface designates a block entity as being passcode-protected. Implementing this allows you to use
 * {@link SetPasscodeScreen} and {@link CheckPasscodeScreen} to easily set your block's passcode. Extends
 * {@link ICodebreakable} as most passcode-protected blocks are likely able to be hacked using the Codebreaker by default.
 *
 * @author Geforce
 */
public interface IPasscodeProtected extends ICodebreakable {
	/**
	 * Open the check passcode GUI if a passcode is set. <p>
	 *
	 * @param level The level of this block entity
	 * @param pos The position of this block entity
	 * @param player The player who the GUI should be opened to.
	 */
	public default void openPasscodeGUI(Level level, BlockPos pos, Player player) {
		if (!level.isClientSide) {
			if (getPasscode() != null)
				SecurityCraft.channel.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player), new OpenScreen(DataType.CHECK_PASSCODE, pos));
		}
	}

	/**
	 * Check that a passcode has been set, and if not, opens the set passcode screen or sends a warning message. <p>
	 *
	 * @param level The level of this block entity
	 * @param pos The position of this block entity
	 * @param ownable This block entity
	 * @param player The player who interacted with this block entity
	 * @return true if a passcode has been set, false otherwise
	 */
	default boolean verifyPasscodeSet(Level level, BlockPos pos, IOwnable ownable, Player player) {
		if (!level.isClientSide) {
			if (getPasscode() != null)
				return true;

			if (ownable.isOwnedBy(player))
				SecurityCraft.channel.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player), new OpenScreen(DataType.SET_PASSCODE, pos));
			else
				PlayerUtils.sendMessageToPlayer(player, Component.literal("SecurityCraft"), Utils.localize("messages.securitycraft:passcodeProtected.notSetUp"), ChatFormatting.DARK_RED);
		}

		return false;
	}

	@Override
	default boolean shouldAttemptCodebreak(BlockState state, Player player) {
		if (getPasscode() == null) {
			PlayerUtils.sendMessageToPlayer(player, Component.literal("SecurityCraft"), Utils.localize("messages.securitycraft:passcodeProtected.notSetUp"), ChatFormatting.DARK_RED);
			return false;
		}

		return true;
	}

	@Override
	public default void useCodebreaker(BlockState state, Player player) {
		activate(player);
	}

	/**
	 * Called whenever a player correctly enters this block's passcode in the passcode GUI.
	 *
	 * @param player The player who entered the passcode.
	 */
	public void activate(Player player);

	/**
	 * Returns the block entity's passcode.
	 *
	 * @return The passcode, null if the passcode is not set yet
	 */
	public byte[] getPasscode();

	/**
	 * Hashes and stores the given passcode using a new, randomly generated salt.
	 *
	 * @param passcode The passcode
	 */
	default void hashAndSetPasscode(String passcode) {
		hashAndSetPasscode(passcode, Utils.generateSalt());
	}

	/**
	 * Hashes and stores the given passcode using the given salt.
	 *
	 * @param passcode The passcode
	 * @param salt The salt used for hashing
	 */
	default void hashAndSetPasscode(String passcode, byte[] salt) {
		SaltData.removeKey(getSaltKey());
		setSaltKey(SaltData.putSalt(salt));
		setPasscode(Utils.hashPasscode(passcode, salt));
	}

	/**
	 * Sets a new passcode. Note that this should not hash the passcode. Prefer calling {@link #hashAndSetPasscode}
	 * instead, if you are calling this without a salted and hashed passcode. The passcode should always be set alongside
	 * the salt.
	 *
	 * @param passcode The new passcode to be saved
	 */
	public void setPasscode(byte[] passcode);

	/**
	 * Check whether the given passcode matches the stored one after it has been hashed with the salt.
	 *
	 * @param passcode the passcode prior to hashing with the salt
	 * @return Whether the given passcode matches the stored one after hashing with the salt
	 */
	default boolean checkPasscode(String passcode) {
		return Arrays.equals(getPasscode(), Utils.hashPasscode(passcode, getSalt()));
	}

	/**
	 * Sets the salt key from the information stored in the given CompoundTag.
	 *
	 * @param tag The tag that the salt key information is stored in
	 */
	default void loadSaltKey(CompoundTag tag) {
		UUID saltKey = tag.contains("saltKey") ? tag.getUUID("saltKey") : null;

		if (!SaltData.containsKey(saltKey)) //If no salt key or no salt associated with the given key can be found, a new password needs to be set
			Utils.filterPasscodeAndSaltFromTag(tag);
		else
			setSaltKey(saltKey);
	}

	/**
	 * Sets the passcode from the information stored in the given CompoundTag.
	 *
	 * @param tag The tag that the passcode information is stored in
	 */
	default void loadPasscode(CompoundTag tag) {
		String passcode = tag.getString(tag.contains("Passcode", Tag.TAG_STRING) ? "Passcode" : "passcode"); //"Passcode" is also checked in order to support old versions where both spellings were used to store passcode information

		//SecurityCraft's passcode-protected blocks do not support passcodes longer than 20 characters, so if such a short passcode is encountered instead of a hash, store the properly hashed version inside the block
		if (!passcode.isEmpty()) {
			if (passcode.length() <= 20)
				hashAndSetPasscode(Utils.hashPasscodeWithoutSalt(passcode));
			else
				setPasscode(Utils.stringToBytes(passcode));
		}
	}

	/**
	 * Returns the block entity's salt, which is used for hashing incoming passcodes.
	 *
	 * @return The salt, null if the passcode and salt are not set yet
	 */
	default byte[] getSalt() {
		return SaltData.getSalt(getSaltKey());
	}

	/**
	 * Returns the block entity's salt key, which is used for retrieving the salt from the external salt list.
	 *
	 * @return The stored salt key, null if the passcode and salt are not set yet
	 */
	public UUID getSaltKey();

	/**
	 * Sets the block entity's salt key, which is used for retrieving the salt from the external salt list. The salt
	 * key should always be set alongside the passcode.
	 *
	 * @param saltKey The new key associated with the salt
	 */
	public void setSaltKey(UUID saltKey);

	/**
	 * Sets this block to be on cooldown and starts the cooldown
	 */
	public void startCooldown();

	/**
	 * Checks whether this block is on cooldown, meaning a new code cannot be entered.
	 *
	 * @return true if this block is on cooldown, false otherwise
	 */
	public boolean isOnCooldown();

	/**
	 * Returns the time at which the cooldown ends
	 *
	 * @return A UNIX timestamp representing the cooldown's end time
	 */
	public long getCooldownEnd();

	/**
	 * Gets called when an incorrect passcode has been inserted.
	 *
	 * @param player The player who entered the incorrect code
	 * @param incorrectCode The incorrect code that was entered
	 */
	public default void onIncorrectPasscodeEntered(Player player, String incorrectCode) {
		if (this instanceof IModuleInventory moduleInv) {
			if (moduleInv.isModuleEnabled(ModuleType.SMART))
				startCooldown();

			if (moduleInv.isModuleEnabled(ModuleType.HARMING)) {
				if (player.hurt(CustomDamageSources.incorrectPasscode(player.level.registryAccess()), ConfigHandler.SERVER.incorrectPasscodeDamage.get()))
					player.closeContainer();
			}
		}
	}
}
