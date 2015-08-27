package net.breakinbad.securitycraft.api;

import net.breakinbad.securitycraft.gui.GuiCheckPassword;
import net.breakinbad.securitycraft.gui.GuiSetPassword;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;

/**
 * Implementing this interface designates a TileEntity as being password-protected.
 * Implementing this allows you to use {@link GuiSetPassword} and {@link GuiCheckPassword} to easily set your block's password. <p>
 * 
 * The following code can be used in your Block.onBlockActivated() method to correctly set and insert passwords:
 * <pre>
 * if(((IPasswordProtected) yourTileEntity).getPassword() == null){
       entityPlayer.openGui(mod_SecurityCraft.instance, GuiHandler.SETUP_PASSWORD_ID, world, x, y, z);
  }else{
	   entityPlayer.openGui(mod_SecurityCraft.instance, GuiHandler.INSERT_PASSWORD_ID, world, x, y, z);
  }
 * </pre>
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
	public void activate(EntityPlayer player);
	
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
	 * to your NBTTagCompound in writeToNBT() and readFromNBT().
	 * 
	 * @param password The new password to be saved.
	 */	
	public void setPassword(String password);
		
}
