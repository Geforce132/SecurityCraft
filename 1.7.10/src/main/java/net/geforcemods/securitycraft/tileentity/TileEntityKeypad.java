package net.geforcemods.securitycraft.tileentity;

import net.geforcemods.securitycraft.api.CustomizableSCTE;
import net.geforcemods.securitycraft.api.IPasswordProtected;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.OptionBoolean;
import net.geforcemods.securitycraft.blocks.BlockKeypad;
import net.geforcemods.securitycraft.gui.GuiHandler;
import net.geforcemods.securitycraft.main.mod_SecurityCraft;
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
				worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord, mod_SecurityCraft.keypad);
			}
			else {
				worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, worldObj.getBlockMetadata(xCoord, yCoord, zCoord) - 5, 3);
				worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord, mod_SecurityCraft.keypad);
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
	public void writeToNBT(NBTTagCompound par1NBTTagCompound)
	{
		super.writeToNBT(par1NBTTagCompound);

		if(passcode != null && !passcode.isEmpty())
			par1NBTTagCompound.setString("passcode", passcode);
	}

	/**
	 * Reads a tile entity from NBT.
	 */
	@Override
	public void readFromNBT(NBTTagCompound par1NBTTagCompound)
	{
		super.readFromNBT(par1NBTTagCompound);

		if (par1NBTTagCompound.hasKey("passcode"))
			if(par1NBTTagCompound.getInteger("passcode") != 0)
				passcode = String.valueOf(par1NBTTagCompound.getInteger("passcode"));
			else
				passcode = par1NBTTagCompound.getString("passcode");
	}

	@Override
	public void activate(EntityPlayer player) {
		if(!worldObj.isRemote && worldObj.getBlock(xCoord, yCoord, zCoord) instanceof BlockKeypad)
			BlockKeypad.activate(worldObj, xCoord, yCoord, zCoord);
	}

	@Override
	public void openPasswordGUI(EntityPlayer player) {
		if(getPassword() == null)
			player.openGui(mod_SecurityCraft.instance, GuiHandler.SETUP_PASSWORD_ID, worldObj, xCoord, yCoord, zCoord);
		else
			player.openGui(mod_SecurityCraft.instance, GuiHandler.INSERT_PASSWORD_ID, worldObj, xCoord, yCoord, zCoord);
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
