package net.geforcemods.securitycraft.api;

import net.geforcemods.securitycraft.screen.CheckPasswordScreen;
import net.geforcemods.securitycraft.screen.SetPasswordScreen;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.TileEntity;

/**
 * Implementing this interface designates a TileEntity as being password-protected.
 * Implementing this allows you to use {@link SetPasswordScreen} and {@link CheckPasswordScreen} to easily set your block's password.
 *
 * @author Geforce
 */
public interface IPasswordProtected {

	/**
	 * Called whenever a player correctly enters this block's password
	 * in the password GUI.<p>
	 *
	 * World, and x, y, and z variables are not given, as they are already
	 * provided in {@link TileEntity}. This runs on both the CLIENT and
	 * SERVER sides, be sure to check for World.isRemote.<p>
	 *
	 * The cleanest way to use this is to check if the block at xCoord, yCoord,
	 * and zCoord is an instance of your block, if so, call a static method
	 * to do your block's function.
	 *
	 * @param player The player who entered the password.
	 */
	public void activate(PlayerEntity player);

	/**
	 * Open the correct password GUI depending on if a password is already set or not. <p>
	 *
	 * To open the "password setup" GUI:
	 * <pre>player.openGui(mod_SecurityCraft.instance, GuiHandler.SETUP_PASSWORD_ID, worldObj, pos.getX(), pos.getY(), pos.getZ());</pre>
	 *
	 * To open the "insert password" GUI:
	 * <pre>player.openGui(mod_SecurityCraft.instance, GuiHandler.INSERT_PASSWORD_ID, worldObj, pos.getX(), pos.getY(), pos.getZ());</pre>
	 *
	 * @param player The player who the GUI should be opened to.
	 */
	public void openPasswordGUI(PlayerEntity player);

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
	 * @param blockState The BlockState of the block.
	 * @param player The player who used the codebreaker.
	 * @return Return true if the codebreaker "hack" was successful, false otherwise.
	 */
	public boolean onCodebreakerUsed(BlockState blockState, PlayerEntity player);

	/**
	 * Return your TileEntity's password variable here.
	 * If the password is empty or not set yet, return null.
	 *
	 * @return The password.
	 */
	public String getPassword();

	/**
	 * Save newly created passwords to your TileEntity here.
	 * You are responsible for reading and writing the password
	 * to your CompoundNBT in writeToNBT() and readFromNBT().
	 *
	 * @param password The new password to be saved.
	 */
	public void setPassword(String password);

}
