package net.geforcemods.securitycraft.api;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.gui.GuiCheckPassword;
import net.geforcemods.securitycraft.gui.GuiHandler;
import net.geforcemods.securitycraft.gui.GuiSetPassword;
import net.geforcemods.securitycraft.misc.CustomDamageSources;
import net.geforcemods.securitycraft.misc.EnumModuleType;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

/**
 * Implementing this interface designates a block entity as being password-protected. Implementing this allows you to use
 * {@link GuiSetPassword} and {@link GuiCheckPassword} to easily set your block's password. Extends {@link ICodebreakable} as
 * most password-protected blocks are likely able to be hacked using the Codebreaker by default.
 *
 * @author Geforce
 */
public interface IPasswordProtected extends ICodebreakable {
	/**
	 * Open the check password GUI if a password is set. <p>
	 *
	 * @param world The level of this block entity
	 * @param pos The position of this block entity
	 * @param player The player who the GUI should be opened to.
	 */
	public default void openPasswordGUI(World world, BlockPos pos, EntityPlayer player) {
		if (!world.isRemote) {
			if (getPassword() != null)
				player.openGui(SecurityCraft.instance, GuiHandler.INSERT_PASSWORD_ID, world, pos.getX(), pos.getY(), pos.getZ());
		}
	}

	/**
	 * Check that a password has been set, and if not, opens the set password GUI or sends a warning message. <p>
	 *
	 * @param world The level of this block entity
	 * @param pos The position of this block entity
	 * @param ownable This block entity
	 * @param player The player who interacted with this block entity
	 * @return true if a password has been set, false otherwise
	 */
	default boolean verifyPasswordSet(World world, BlockPos pos, IOwnable ownable, EntityPlayer player) {
		if (!world.isRemote) {
			if (getPassword() != null)
				return true;

			if (ownable.isOwnedBy(player))
				player.openGui(SecurityCraft.instance, GuiHandler.SETUP_PASSWORD_ID, world, pos.getX(), pos.getY(), pos.getZ());
			else
				PlayerUtils.sendMessageToPlayer(player, new TextComponentString("SecurityCraft"), Utils.localize("messages.securitycraft:passwordProtected.notSetUp"), TextFormatting.DARK_RED);
		}

		return false;
	}

	@Override
	default boolean shouldAttemptCodebreak(IBlockState state, EntityPlayer player) {
		if (getPassword() == null) {
			PlayerUtils.sendMessageToPlayer(player, new TextComponentString("SecurityCraft"), Utils.localize("messages.securitycraft:passwordProtected.notSetUp"), TextFormatting.DARK_RED);
			return false;
		}

		return true;
	}

	@Override
	public default void useCodebreaker(IBlockState state, EntityPlayer player) {
		activate(player);
	}

	/**
	 * Called whenever a player correctly enters this block's password in the password GUI.<p>
	 *
	 * @param player The player who entered the password.
	 */
	public void activate(EntityPlayer player);

	/**
	 * Return your block entity's password variable here. If the password is empty or not set yet, return null.
	 *
	 * @return The password.
	 */
	public String getPassword();

	/**
	 * Save newly created passwords to your block entity here.
	 *
	 * @param password The new password to be saved.
	 */
	public void setPassword(String password);

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

			if (moduleInv.isModuleEnabled(EnumModuleType.SMART))
				startCooldown();

			if (moduleInv.isModuleEnabled(EnumModuleType.HARMING)) {
				if (player.attackEntityFrom(CustomDamageSources.INCORRECT_PASSCODE, ConfigHandler.incorrectPasscodeDamage))
					player.closeScreen();
			}
		}
	}
}
