package net.geforcemods.securitycraft.tileentity;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.CustomizableSCTE;
import net.geforcemods.securitycraft.api.IPasswordProtected;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.OptionBoolean;
import net.geforcemods.securitycraft.blocks.BlockKeypad;
import net.geforcemods.securitycraft.gui.GuiHandler;
import net.geforcemods.securitycraft.misc.EnumCustomModules;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;

public class TileEntityKeypad extends CustomizableSCTE implements IPasswordProtected {

	private String passcode;

	private OptionBoolean isAlwaysActive = new OptionBoolean("isAlwaysActive", false) {
		@Override
		public void toggle() {
			super.toggle();

			if(getValue()) {
				worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, worldObj.getBlockMetadata(xCoord, yCoord, zCoord) + 5, 3);
				worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord, SCContent.keypad);
			}
			else {
				worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, worldObj.getBlockMetadata(xCoord, yCoord, zCoord) - 5, 3);
				worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord, SCContent.keypad);
			}
		}
	};

	@Override
	public void onModuleInserted(ItemStack stack, EnumCustomModules module) {
		if(!worldObj.isRemote) return;

		if(module == EnumCustomModules.DISGUISE)
			worldObj.markBlockRangeForRenderUpdate(xCoord, yCoord, zCoord, xCoord, yCoord, zCoord);
	}

	@Override
	public void onModuleRemoved(ItemStack stack, EnumCustomModules module) {
		if(!worldObj.isRemote) return;

		if(module == EnumCustomModules.DISGUISE)
			worldObj.markBlockRangeForRenderUpdate(xCoord, yCoord, zCoord, xCoord, yCoord, zCoord);
	}

	/**
	 * Writes a tile entity to NBT.
	 */
	@Override
	public void writeToNBT(NBTTagCompound tag)
	{
		super.writeToNBT(tag);

		if(passcode != null && !passcode.isEmpty())
			tag.setString("passcode", passcode);
	}

	/**
	 * Reads a tile entity from NBT.
	 */
	@Override
	public void readFromNBT(NBTTagCompound tag)
	{
		super.readFromNBT(tag);

		if (tag.hasKey("passcode"))
			if(tag.getInteger("passcode") != 0)
				passcode = String.valueOf(tag.getInteger("passcode"));
			else
				passcode = tag.getString("passcode");
	}

	@Override
	public void activate(EntityPlayer player) {
		if(!worldObj.isRemote && worldObj.getBlock(xCoord, yCoord, zCoord) instanceof BlockKeypad)
			BlockKeypad.activate(worldObj, xCoord, yCoord, zCoord);
	}

	@Override
	public void openPasswordGUI(EntityPlayer player) {
		if(getPassword() == null)
			player.openGui(SecurityCraft.instance, GuiHandler.SETUP_PASSWORD_ID, worldObj, xCoord, yCoord, zCoord);
		else
			player.openGui(SecurityCraft.instance, GuiHandler.INSERT_PASSWORD_ID, worldObj, xCoord, yCoord, zCoord);
	}

	@Override
	public boolean onCodebreakerUsed(int meta, EntityPlayer player, boolean isCodebreakerDisabled) {
		if(isCodebreakerDisabled)
			PlayerUtils.sendMessageToPlayer(player, StatCollector.translateToLocal("tile.keypad.name"), StatCollector.translateToLocal("messages.codebreakerDisabled"), EnumChatFormatting.RED);
		else if(BlockUtils.isMetadataBetween(worldObj, xCoord, yCoord, zCoord, 2, 5)) {
			activate(player);
			return true;
		}

		return false;
	}

	@Override
	public String getPassword() {
		return passcode;
	}

	@Override
	public void setPassword(String password) {
		passcode = password;
	}

	@Override
	public EnumCustomModules[] acceptedModules() {
		return new EnumCustomModules[]{EnumCustomModules.WHITELIST, EnumCustomModules.BLACKLIST, EnumCustomModules.DISGUISE};
	}

	@Override
	public Option<?>[] customOptions() {
		return new Option[]{ isAlwaysActive };
	}

}
