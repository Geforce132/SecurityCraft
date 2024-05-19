package net.geforcemods.securitycraft.api;

import java.util.UUID;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.misc.CustomDamageSources;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.misc.SaltData;
import net.geforcemods.securitycraft.screen.CheckPasscodeScreen;
import net.geforcemods.securitycraft.screen.ScreenHandler.Screens;
import net.geforcemods.securitycraft.screen.SetPasscodeScreen;
import net.geforcemods.securitycraft.util.PasscodeUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

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
	 * @param world The level of this block entity
	 * @param pos The position of this block entity
	 * @param player The player who the GUI should be opened to.
	 */
	public default void openPasscodeGUI(World world, BlockPos pos, EntityPlayer player) {
		if (!world.isRemote && getPasscode() != null)
			player.openGui(SecurityCraft.instance, Screens.INSERT_PASSCODE.ordinal(), world, pos.getX(), pos.getY(), pos.getZ());
	}

	/**
	 * Check that a passcode has been set, and if not, opens the set passcode GUI or sends a warning message. <p>
	 *
	 * @param world The level of this block entity
	 * @param pos The position of this block entity
	 * @param ownable This block entity
	 * @param player The player who interacted with this block entity
	 * @return true if a passcode has been set, false otherwise
	 */
	default boolean verifyPasscodeSet(World world, BlockPos pos, IOwnable ownable, EntityPlayer player) {
		if (!world.isRemote) {
			if (getPasscode() != null)
				return true;

			if (ownable.isOwnedBy(player))
				player.openGui(SecurityCraft.instance, Screens.SETUP_PASSCODE.ordinal(), world, pos.getX(), pos.getY(), pos.getZ());
			else
				PlayerUtils.sendMessageToPlayer(player, new TextComponentString("SecurityCraft"), Utils.localize("messages.securitycraft:passcodeProtected.notSetUp"), TextFormatting.DARK_RED);
		}

		return false;
	}

	@Override
	default boolean shouldAttemptCodebreak(IBlockState state, EntityPlayer player) {
		if (getPasscode() == null) {
			PlayerUtils.sendMessageToPlayer(player, new TextComponentString("SecurityCraft"), Utils.localize("messages.securitycraft:passcodeProtected.notSetUp"), TextFormatting.DARK_RED);
			return false;
		}

		return true;
	}

	@Override
	public default void useCodebreaker(IBlockState state, EntityPlayer player) {
		activate(player);
	}

	/**
	 * Called whenever a player correctly enters this block's passcode in the passcode GUI.
	 *
	 * @param player The player who entered the passcode.
	 */
	public void activate(EntityPlayer player);

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
		hashAndSetPasscode(passcode, PasscodeUtils.generateSalt());
	}

	/**
	 * Hashes and stores the given passcode using the given salt.
	 *
	 * @param passcode The passcode
	 * @param salt The salt used for hashing
	 */
	default void hashAndSetPasscode(String passcode, byte[] salt) {
		SaltData.removeSalt(getSaltKey());
		setSaltKey(SaltData.putSalt(salt));
		PasscodeUtils.hashPasscode(passcode, salt, this::setPasscode);
	}

	/**
	 * Sets a new passcode. Note that this should not hash the passcode. Prefer calling {@link #hashAndSetPasscode} instead, if
	 * you are calling this without a salted and hashed passcode. The passcode should always be set alongside the salt.
	 *
	 * @param passcode The new passcode to be saved
	 */
	public void setPasscode(byte[] passcode);

	/**
	 * Sets the salt key from the information stored in the given CompoundNBT.
	 *
	 * @param tag The tag that the salt key information is stored in
	 */
	default void loadSaltKey(NBTTagCompound tag) {
		UUID saltKey = tag.hasUniqueId("saltKey") ? tag.getUniqueId("saltKey") : null;
		String passcode = tag.getString(tag.hasKey("Passcode", Constants.NBT.TAG_STRING) ? "Passcode" : "passcode"); //"Passcode" is also checked in order to support old versions where both spellings were used to store passcode information

		if (passcode.length() == 32) {
			if (!SaltData.containsKey(saltKey)) { //If the passcode hash is set correctly, but no salt key or no salt associated with the given key can be found, a new passcode needs to be set
				PasscodeUtils.filterPasscodeAndSaltFromTag(tag);
				return;
			}
			else if (SaltData.isKeyInUse(saltKey))
				saltKey = SaltData.copySaltToNewKey(saltKey);

			setSaltKey(saltKey);
			SaltData.setKeyInUse(saltKey);
		}
	}

	/**
	 * Sets the passcode from the information stored in the given CompoundNBT.
	 *
	 * @param tag The tag that the passcode information is stored in
	 */
	default void loadPasscode(NBTTagCompound tag) {
		String passcode = tag.getString(tag.hasKey("Passcode", Constants.NBT.TAG_STRING) ? "Passcode" : "passcode"); //"Passcode" is also checked in order to support old versions where both spellings were used to store passcode information

		//SecurityCraft's passcode-protected blocks do not support passcodes longer than 20 characters, so if such a short passcode is encountered instead of a hash, store the properly hashed version inside the block
		if (!passcode.isEmpty()) {
			if (passcode.length() <= 20)
				hashAndSetPasscode(PasscodeUtils.hashPasscodeWithoutSalt(passcode));
			else
				setPasscode(PasscodeUtils.stringToBytes(passcode));
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
	 * Sets the block entity's salt key, which is used for retrieving the salt from the external salt list. The salt key should
	 * always be set alongside the passcode.
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
	public default void onIncorrectPasscodeEntered(EntityPlayer player, String incorrectCode) {
		if (this instanceof IModuleInventory) {
			IModuleInventory moduleInv = (IModuleInventory) this;

			if (moduleInv.isModuleEnabled(ModuleType.SMART))
				startCooldown();

			if (moduleInv.isModuleEnabled(ModuleType.HARMING) && player.attackEntityFrom(CustomDamageSources.INCORRECT_PASSCODE, ConfigHandler.incorrectPasscodeDamage))
				player.closeScreen();
		}
	}
}
