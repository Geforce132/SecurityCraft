package net.geforcemods.securitycraft.tileentity;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.ILockable;
import net.geforcemods.securitycraft.api.IPasswordProtected;
import net.geforcemods.securitycraft.blocks.BlockKeypad;
import net.geforcemods.securitycraft.blocks.BlockKeypadDoor;
import net.geforcemods.securitycraft.gui.GuiHandler;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockDoor.EnumDoorHalf;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

public class TileEntityKeypadDoor extends TileEntitySpecialDoor implements IPasswordProtected, ILockable
{
	private String passcode;

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag)
	{
		super.writeToNBT(tag);

		if(passcode != null && !passcode.isEmpty())
			tag.setString("passcode", passcode);

		return tag;
	}

	@Override
	public void readFromNBT(NBTTagCompound tag)
	{
		super.readFromNBT(tag);
		passcode = tag.getString("passcode");
	}

	@Override
	public void activate(EntityPlayer player) {
		if(!world.isRemote)
		{
			IBlockState state = world.getBlockState(pos);

			if(state.getBlock() instanceof BlockKeypadDoor)
			{
				//for some reason calling BlockKeypadDoor#activate if the block is the upper half does not work, so delegate opening to the lower half
				if(state.getValue(BlockDoor.HALF) == EnumDoorHalf.UPPER)
				{
					pos = pos.down();
					state = world.getBlockState(pos);
				}

				((BlockKeypadDoor)state.getBlock()).activate(state, world, pos, getSignalLength());
			}
		}
	}

	@Override
	public void openPasswordGUI(EntityPlayer player) {
		if(getPassword() != null)
			player.openGui(SecurityCraft.instance, GuiHandler.INSERT_PASSWORD_ID, world, pos.getX(), pos.getY(), pos.getZ());
		else
		{
			if(getOwner().isOwner(player))
				player.openGui(SecurityCraft.instance, GuiHandler.SETUP_PASSWORD_ID, world, pos.getX(), pos.getY(), pos.getZ());
			else
				PlayerUtils.sendMessageToPlayer(player, new TextComponentString("SecurityCraft"), Utils.localize("messages.securitycraft:passwordProtected.notSetUp"), TextFormatting.DARK_RED);
		}
	}

	@Override
	public boolean onCodebreakerUsed(IBlockState state, EntityPlayer player) {
		if(!state.getValue(BlockKeypad.POWERED)) {
			activate(player);
			return true;
		}

		return false;
	}

	@Override
	public String getPassword() {
		return (passcode != null && !passcode.isEmpty()) ? passcode : null;
	}

	@Override
	public void setPassword(String password) {
		TileEntity te = null;
		IBlockState state = world.getBlockState(pos);

		passcode = password;

		if(state.getValue(BlockDoor.HALF) == EnumDoorHalf.LOWER)
			te = world.getTileEntity(pos.up());
		else if(state.getValue(BlockDoor.HALF) == EnumDoorHalf.UPPER)
			te = world.getTileEntity(pos.down());

		if(te instanceof TileEntityKeypadDoor)
			((TileEntityKeypadDoor)te).setPasswordExclusively(password);
	}

	//only set the password for this door half
	public void setPasswordExclusively(String password)
	{
		passcode = password;
	}

	@Override
	public int defaultSignalLength()
	{
		return 60;
	}
}
