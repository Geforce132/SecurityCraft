package net.geforcemods.securitycraft.api;

import net.geforcemods.securitycraft.screen.CheckPasswordScreen;
import net.geforcemods.securitycraft.screen.SetPasswordScreen;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Implementing this interface designates a block entity as being password-protected.
 * Implementing this allows you to use {@link SetPasswordScreen} and {@link CheckPasswordScreen} to easily set your block's password.
 *
 * @author Geforce
 */
public interface IPasswordProtected {

	/**
	 * Called whenever a player correctly enters this block's password
	 * in the password GUI.<p>
	 *
	 * @param player The player who entered the password.
	 */
	public void activate(Player player);

	/**
	 * Open the correct password GUI depending on if a password is already set or not. <p>
	 *
	 * @param player The player who the GUI should be opened to.
	 */
	public void openPasswordGUI(Player player);

	/**
	 * Can the codebreaker be used on that password-protected block?
	 *
	 * @return Return true if the codebreaker can be used on the block
	 */
	public default boolean isCodebreakable() {
		return true;
	}

	/**
	 * Called when a codebreaker is used on a password-protected block.
	 *
	 * @param state The block state of the block.
	 * @param player The player who used the codebreaker.
	 * @return Return true if the codebreaker "hack" was successful, false otherwise.
	 */
	public boolean onCodebreakerUsed(BlockState state, Player player);

	/**
	 * Return your block entity's password variable here.
	 * If the password is empty or not set yet, return null.
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

}
