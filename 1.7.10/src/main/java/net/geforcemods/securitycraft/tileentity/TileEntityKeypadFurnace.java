package net.geforcemods.securitycraft.tileentity;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.IPasswordProtected;
import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.blocks.BlockKeypadFurnace;
import net.geforcemods.securitycraft.gui.GuiHandler;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;

public class TileEntityKeypadFurnace extends TileEntityFurnace implements IOwnable, ISidedInventory, IPasswordProtected {

	private String passcode;
	private Owner owner = new Owner();

	@Override
	public void updateEntity(){
		boolean hasTimeLeft = furnaceBurnTime > 0;
		boolean smelting = false;

		if (hasTimeLeft)
			furnaceBurnTime--;

		if(!worldObj.isRemote){
			if(furnaceBurnTime != 0 || getStackInSlot(1) != null && getStackInSlot(0) != null){
				if(furnaceBurnTime == 0 && canSmelt()){
					currentItemBurnTime = furnaceBurnTime = getItemBurnTime(getStackInSlot(1));

					if(furnaceBurnTime > 0){
						smelting = true;

						if(getStackInSlot(1) != null){
							getStackInSlot(1).stackSize--;

							if(getStackInSlot(1).stackSize == 0)
								setInventorySlotContents(1, getStackInSlot(1).getItem().getContainerItem(getStackInSlot(1)));
						}
					}
				}

				if(isBurning() && canSmelt()){
					furnaceCookTime++;

					if(furnaceCookTime == 200){
						furnaceCookTime = 0;
						smeltItem();
						smelting = true;
					}
				}
				else
					furnaceCookTime = 0;
			}

			if(hasTimeLeft != furnaceBurnTime > 0)
				smelting = true;
		}

		if(smelting)
			markDirty();
	}


	private boolean canSmelt(){
		if (getStackInSlot(0) == null)
			return false;
		else{
			ItemStack resultStack = FurnaceRecipes.instance().getSmeltingResult(getStackInSlot(0));
			if (resultStack == null) return false;
			if (getStackInSlot(2) == null) return true;
			if (!getStackInSlot(2).isItemEqual(resultStack)) return false;
			int result = getStackInSlot(2).stackSize + resultStack.stackSize;
			return result <= getInventoryStackLimit() && result <= getStackInSlot(2).getMaxStackSize();
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound tag){
		super.writeToNBT(tag);

		if(passcode != null && !passcode.isEmpty())
			tag.setString("passcode", passcode);

		if(owner != null){
			tag.setString("owner", owner.getName());
			tag.setString("ownerUUID", owner.getUUID());
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound tag){
		super.readFromNBT(tag);

		if (tag.hasKey("passcode"))
			if(tag.getInteger("passcode") != 0)
				passcode = String.valueOf(tag.getInteger("passcode"));
			else
				passcode = tag.getString("passcode");

		if (tag.hasKey("owner"))
			owner.setOwnerName(tag.getString("owner"));

		if (tag.hasKey("ownerUUID"))
			owner.setOwnerUUID(tag.getString("ownerUUID"));
	}

	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound tag = new NBTTagCompound();
		writeToNBT(tag);
		return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 1, tag);
	}

	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity packet) {
		readFromNBT(packet.getNbtCompound());
	}

	@Override
	public Owner getOwner(){
		return owner;
	}

	@Override
	public void setOwner(String uuid, String name) {
		owner.set(uuid, name);
	}

	@Override
	public void activate(EntityPlayer player) {
		if(!worldObj.isRemote && worldObj.getBlock(xCoord, yCoord, zCoord) instanceof BlockKeypadFurnace)
			BlockKeypadFurnace.activate(worldObj, xCoord, yCoord, zCoord, player);
	}

	@Override
	public void openPasswordGUI(EntityPlayer player) {
		if(getPassword() != null)
			player.openGui(SecurityCraft.instance, GuiHandler.INSERT_PASSWORD_ID, worldObj, xCoord, yCoord, zCoord);
		else
		{
			if(getOwner().isOwner(player))
				player.openGui(SecurityCraft.instance, GuiHandler.SETUP_PASSWORD_ID, worldObj, xCoord, yCoord, zCoord);
			else
				PlayerUtils.sendMessageToPlayer(player, "SecurityCraft", StatCollector.translateToLocal("messages.securitycraft:passwordProtected.notSetUp"), EnumChatFormatting.DARK_RED);
		}
	}

	@Override
	public boolean onCodebreakerUsed(int meta, EntityPlayer player, boolean isCodebreakerDisabled) {
		if(isCodebreakerDisabled)
			PlayerUtils.sendMessageToPlayer(player, StatCollector.translateToLocal("tile.securitycraft:keypadFurnace.name"), StatCollector.translateToLocal("messages.securitycraft:codebreakerDisabled"), EnumChatFormatting.RED);
		else {
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

}