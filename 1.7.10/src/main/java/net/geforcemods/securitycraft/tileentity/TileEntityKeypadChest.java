package net.geforcemods.securitycraft.tileentity;

import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.IPasswordProtected;
import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.blocks.BlockKeypadChest;
import net.geforcemods.securitycraft.gui.GuiHandler;
import net.geforcemods.securitycraft.main.mod_SecurityCraft;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;

public class TileEntityKeypadChest extends TileEntityChest implements IOwnable, IPasswordProtected {

	private String passcode;
	private Owner owner = new Owner();

	public TileEntityKeypadChest adjacentChestZNeg;
	public TileEntityKeypadChest adjacentChestXPos;
	public TileEntityKeypadChest adjacentChestXNeg;
	public TileEntityKeypadChest adjacentChestZPos;


	/**
	 * Writes a tile entity to NBT.
	 */
	@Override
	public void writeToNBT(NBTTagCompound par1NBTTagCompound)
	{
		super.writeToNBT(par1NBTTagCompound);

		if(passcode != null && !passcode.isEmpty())
			par1NBTTagCompound.setString("passcode", passcode);

		if(owner != null){
			par1NBTTagCompound.setString("owner", owner.getName());
			par1NBTTagCompound.setString("ownerUUID", owner.getUUID());
		}
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
	public Packet getDescriptionPacket() {
		NBTTagCompound tag = new NBTTagCompound();
		writeToNBT(tag);
		return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 1, tag);
	}

	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity packet) {
		readFromNBT(packet.func_148857_g());
	}

	@Override
	public String getInventoryName(){
		return "Protected chest";
	}

	@Override
	public Owner getOwner(){
		return owner;
	}

	@Override
	public void setOwner(String uuid, String name){
		owner.setOwnerName(name);
		owner.setOwnerUUID(uuid);
	}

	@Override
	public void activate(EntityPlayer player) {
		if(!worldObj.isRemote && worldObj.getBlock(xCoord, yCoord, zCoord) instanceof BlockKeypadChest)
			BlockKeypadChest.activate(worldObj, xCoord, yCoord, zCoord, player);
	}

	@Override
	public void openPasswordGUI(EntityPlayer player) {
		if(getPassword() != null)
			player.openGui(mod_SecurityCraft.instance, GuiHandler.INSERT_PASSWORD_ID, worldObj, xCoord, yCoord, zCoord);
		else
			player.openGui(mod_SecurityCraft.instance, GuiHandler.SETUP_PASSWORD_ID, worldObj, xCoord, yCoord, zCoord);
	}

	@Override
	public boolean onCodebreakerUsed(int meta, EntityPlayer player, boolean isCodebreakerDisabled) {
		if(isCodebreakerDisabled)
			PlayerUtils.sendMessageToPlayer(player, StatCollector.translateToLocal("tile.keypadChest.name"), StatCollector.translateToLocal("messages.codebreakerDisabled"), EnumChatFormatting.RED);
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

}
