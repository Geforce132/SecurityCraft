package net.geforcemods.securitycraft.api;

import net.geforcemods.securitycraft.gui.GuiCheckPassword;
import net.geforcemods.securitycraft.gui.GuiSetPassword;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;

/**
 * Implementing this interface designates a block entity as being password-protected. Implementing this allows you to use
 * {@link GuiSetPassword} and {@link GuiCheckPassword} to easily set your block's password. Extends
 * {@link ICodebreakable} as most password-protected blocks are likely able to be hacked using the Codebreaker by default.
 *
 * @author Geforce
 */
public interface IPasswordProtected extends ICodebreakable {
	/**
	 * Called whenever a player correctly enters this block's password in the password GUI.<p> World, and x, y, and z variables
	 * are not given, as they are already provided in {@link TileEntity}. This runs on both the CLIENT and SERVER sides, be sure
	 * to check for World.isRemote.<p> The cleanest way to use this is to check if the block at xCoord, yCoord, and zCoord is an
	 * instance of your block, if so, call a static method to do your block's function.
	 *
	 * @param player The player who entered the password.
	 */
	public void activate(EntityPlayer player);

	/**
	 * Open the correct password GUI depending on if a password is already set or not. <p> To open the "password setup" GUI:
	 *
	 * <pre>
	 * player.openGui(mod_SecurityCraft.instance, GuiHandler.SETUP_PASSWORD_ID, worldObj, pos.getX(), pos.getY(), pos.getZ());
	 * </pre>
	 *
	 * To open the "insert password" GUI:
	 *
	 * <pre>
	 * player.openGui(mod_SecurityCraft.instance, GuiHandler.INSERT_PASSWORD_ID, worldObj, pos.getX(), pos.getY(), pos.getZ());
	 * </pre>
	 *
	 * @param player The player who the GUI should be opened to.
	 */
	public void openPasswordGUI(EntityPlayer player);

	/**
	 * Return your TileEntity's password variable here. If the password is empty or not set yet, return null.
	 *
	 * @return The password.
	 */
	public String getPassword();

	/**
	 * Save newly created passwords to your TileEntity here. You are responsible for reading and writing the password to your
	 * NBTTagCompound in writeToNBT() and readFromNBT().
	 *
	 * @param password The new password to be saved.
	 */
	public void setPassword(String password);
}
