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
		boolean flag = furnaceBurnTime > 0;
		boolean flag1 = false;

		if (furnaceBurnTime > 0)
			furnaceBurnTime--;

		if(!worldObj.isRemote){
			if(furnaceBurnTime != 0 || getStackInSlot(1) != null && getStackInSlot(0) != null){
				if(furnaceBurnTime == 0 && canSmelt()){
					currentItemBurnTime = furnaceBurnTime = getItemBurnTime(getStackInSlot(1));

					if(furnaceBurnTime > 0){
						flag1 = true;

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
						flag1 = true;
					}
				}
				else
					furnaceCookTime = 0;
			}

			if(flag != furnaceBurnTime > 0)
				flag1 = true;
		}

		if(flag1)
			markDirty();
	}


	private boolean canSmelt(){
		if (getStackInSlot(0) == null)
			return false;
		else{
			ItemStack itemstack = FurnaceRecipes.instance().getSmeltingResult(getStackInSlot(0));
			if (itemstack == null) return false;
			if (getStackInSlot(2) == null) return true;
			if (!getStackInSlot(2).isItemEqual(itemstack)) return false;
			int result = getStackInSlot(2).stackSize + itemstack.stackSize;
			return result <= getInventoryStackLimit() && result <= getStackInSlot(2).getMaxStackSize();
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound par1NBTTagCompound){
		super.writeToNBT(par1NBTTagCompound);

		if(passcode != null && !passcode.isEmpty())
			par1NBTTagCompound.setString("passcode", passcode);

		if(owner != null){
			par1NBTTagCompound.setString("owner", owner.getName());
			par1NBTTagCompound.setString("ownerUUID", owner.getUUID());
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound par1NBTTagCompound){
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
			player.openGui(SecurityCraft.instance, GuiHandler.SETUP_PASSWORD_ID, worldObj, xCoord, yCoord, zCoord);
	}

	@Override
	public boolean onCodebreakerUsed(int meta, EntityPlayer player, boolean isCodebreakerDisabled) {
		if(isCodebreakerDisabled)
			PlayerUtils.sendMessageToPlayer(player, StatCollector.translateToLocal("tile.keypadFurnace.name"), StatCollector.translateToLocal("messages.codebreakerDisabled"), EnumChatFormatting.RED);
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