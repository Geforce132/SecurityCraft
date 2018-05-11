package net.geforcemods.securitycraft.tileentity;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.IPasswordProtected;
import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.blocks.BlockKeypadChest;
import net.geforcemods.securitycraft.gui.GuiHandler;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.text.TextFormatting;

public class TileEntityKeypadChest extends TileEntityChest implements IPasswordProtected, IOwnable {

	private String passcode;
	private Owner owner = new Owner();

	/**
	 * Writes a tile entity to NBT.
	 * @return
	 */
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound par1NBTTagCompound)
	{
		super.writeToNBT(par1NBTTagCompound);

		if(passcode != null && !passcode.isEmpty())
			par1NBTTagCompound.setString("passcode", passcode);

		if(owner != null){
			par1NBTTagCompound.setString("owner", owner.getName());
			par1NBTTagCompound.setString("ownerUUID", owner.getUUID());
		}

		return par1NBTTagCompound;
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

		if (par1NBTTagCompound.hasKey("owner"))
			owner.setOwnerName(par1NBTTagCompound.getString("owner"));

		if (par1NBTTagCompound.hasKey("ownerUUID"))
			owner.setOwnerUUID(par1NBTTagCompound.getString("ownerUUID"));
	}

	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		NBTTagCompound tag = new NBTTagCompound();
		writeToNBT(tag);
		return new SPacketUpdateTileEntity(pos, 1, tag);
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
		readFromNBT(packet.getNbtCompound());
	}

	/**
	 * Returns the name of the inventory
	 */
	@Override
	public String getName()
	{
		return "Protected chest";
	}

	@Override
	public void activate(EntityPlayer player) {
		if(!world.isRemote && BlockUtils.getBlock(getWorld(), getPos()) instanceof BlockKeypadChest && !isBlocked())
			BlockKeypadChest.activate(world, pos, player);
	}

	@Override
	public void openPasswordGUI(EntityPlayer player) {
		if(isBlocked())
			return;

		if(getPassword() != null)
			player.openGui(SecurityCraft.instance, GuiHandler.INSERT_PASSWORD_ID, world, pos.getX(), pos.getY(), pos.getZ());
		else
			player.openGui(SecurityCraft.instance, GuiHandler.SETUP_PASSWORD_ID, world, pos.getX(), pos.getY(), pos.getZ());
	}

	@Override
	public boolean onCodebreakerUsed(IBlockState blockState, EntityPlayer player, boolean isCodebreakerDisabled) {
		if(isCodebreakerDisabled)
			PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize("tile.keypadChest.name"), ClientUtils.localize("messages.codebreakerDisabled"), TextFormatting.RED);
		else {
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
		passcode = password;
	}

	@Override
	public Owner getOwner(){
		return owner;
	}

	@Override
	public void setOwner(String uuid, String name) {
		owner.set(uuid, name);
	}

	public boolean isBlocked()
	{
		Block east = BlockUtils.getBlock(getWorld(), getPos().east());
		Block south = BlockUtils.getBlock(getWorld(), getPos().south());
		Block west = BlockUtils.getBlock(getWorld(), getPos().west());
		Block north = BlockUtils.getBlock(getWorld(), getPos().north());

		if(east instanceof BlockKeypadChest && BlockKeypadChest.isBlocked(getWorld(), getPos().east()))
			return true;
		else if(south instanceof BlockKeypadChest && BlockKeypadChest.isBlocked(getWorld(), getPos().south()))
			return true;
		else if(west instanceof BlockKeypadChest && BlockKeypadChest.isBlocked(getWorld(), getPos().west()))
			return true;
		else if(north instanceof BlockKeypadChest && BlockKeypadChest.isBlocked(getWorld(), getPos().north()))
			return true;
		else return isSingleBlocked();
	}

	public boolean isSingleBlocked()
	{
		return BlockKeypadChest.isBlocked(getWorld(), getPos());
	}
}
