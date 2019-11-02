package net.geforcemods.securitycraft.tileentity;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.CustomizableSCTE;
import net.geforcemods.securitycraft.api.IPasswordProtected;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.blocks.BlockKeycardReader;
import net.geforcemods.securitycraft.gui.GuiHandler;
import net.geforcemods.securitycraft.misc.EnumCustomModules;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

public class TileEntityKeycardReader extends CustomizableSCTE implements IPasswordProtected {

	private int passLV = 0;
	private boolean requiresExactKeycard = false;


	/**
	 * Writes a tile entity to NBT.
	 */
	@Override
	public void writeToNBT(NBTTagCompound tag){
		super.writeToNBT(tag);
		tag.setInteger("passLV", passLV);
		tag.setBoolean("requiresExactKeycard", requiresExactKeycard);
	}

	/**
	 * Reads a tile entity from NBT.
	 */
	@Override
	public void readFromNBT(NBTTagCompound tag){
		super.readFromNBT(tag);

		if (tag.hasKey("passLV"))
			passLV = tag.getInteger("passLV");

		if (tag.hasKey("requiresExactKeycard"))
			requiresExactKeycard = tag.getBoolean("requiresExactKeycard");

	}

	public void setRequiresExactKeycard(boolean exact) {
		requiresExactKeycard = exact;
	}

	public boolean doesRequireExactKeycard() {
		return requiresExactKeycard;
	}

	@Override
	public void activate(EntityPlayer player) {
		if(!worldObj.isRemote && worldObj.getBlock(xCoord, yCoord, zCoord) instanceof BlockKeycardReader)
			BlockKeycardReader.activate(worldObj, xCoord, yCoord, zCoord);
	}

	@Override
	public void openPasswordGUI(EntityPlayer player) {
		if(getPassword() == null)
			player.openGui(SecurityCraft.instance, GuiHandler.SETUP_KEYCARD_READER_ID, worldObj, xCoord, yCoord, zCoord);
	}

	@Override
	public boolean onCodebreakerUsed(int meta, EntityPlayer player, boolean isCodebreakerDisabled) {
		return false;
	}

	@Override
	public String getPassword() {
		return passLV == 0 ? null : String.valueOf(passLV);
	}

	@Override
	public void setPassword(String password) {
		passLV = Integer.parseInt(password);
	}

	@Override
	public EnumCustomModules[] acceptedModules() {
		return new EnumCustomModules[]{EnumCustomModules.WHITELIST, EnumCustomModules.BLACKLIST};
	}

	@Override
	public Option<?>[] customOptions() {
		return null;
	}

}
