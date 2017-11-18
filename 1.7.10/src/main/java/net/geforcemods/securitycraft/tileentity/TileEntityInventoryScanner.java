package net.geforcemods.securitycraft.tileentity;

import net.geforcemods.securitycraft.api.CustomizableSCTE;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.misc.EnumCustomModules;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class TileEntityInventoryScanner extends CustomizableSCTE implements IInventory {

	private ItemStack[] inventoryContents = new ItemStack[37];
	private String type = "check";
	private boolean isProvidingPower;
	private int cooldown;

	@Override
	public void updateEntity(){
		super.updateEntity();

		if(cooldown > 0)
			cooldown--;
		else if(isProvidingPower){
			isProvidingPower = false;
			BlockUtils.updateAndNotify(getWorldObj(), xCoord, yCoord, zCoord, getWorldObj().getBlock(xCoord, yCoord, zCoord), 1, true);
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound par1NBTTagCompound){
		super.readFromNBT(par1NBTTagCompound);

		NBTTagList nbttaglist = par1NBTTagCompound.getTagList("Items", 10);
		inventoryContents = new ItemStack[getSizeInventory()];

		for (int i = 0; i < nbttaglist.tagCount(); ++i)
		{
			NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
			int j = nbttagcompound1.getByte("Slot") & 255;

			if (j >= 0 && j < inventoryContents.length)
				inventoryContents[j] = ItemStack.loadItemStackFromNBT(nbttagcompound1);
		}


		if(par1NBTTagCompound.hasKey("cooldown"))
			cooldown = par1NBTTagCompound.getInteger("cooldown");

		if(par1NBTTagCompound.hasKey("type"))
			type = par1NBTTagCompound.getString("type");

	}

	@Override
	public void writeToNBT(NBTTagCompound par1NBTTagCompound){
		super.writeToNBT(par1NBTTagCompound);

		NBTTagList nbttaglist = new NBTTagList();

		for (int i = 0; i < inventoryContents.length; ++i)
			if (inventoryContents[i] != null)
			{
				NBTTagCompound nbttagcompound1 = new NBTTagCompound();
				nbttagcompound1.setByte("Slot", (byte)i);
				inventoryContents[i].writeToNBT(nbttagcompound1);
				nbttaglist.appendTag(nbttagcompound1);
			}

		par1NBTTagCompound.setTag("Items", nbttaglist);

		par1NBTTagCompound.setInteger("cooldown", cooldown);

		par1NBTTagCompound.setString("type", type);

	}

	@Override
	public int getSizeInventory() {
		return 37;
	}

	@Override
	public ItemStack decrStackSize(int par1, int par2)
	{
		if (inventoryContents[par1] != null)
		{
			ItemStack itemstack;

			if (inventoryContents[par1].stackSize <= par2)
			{
				itemstack = inventoryContents[par1];
				inventoryContents[par1] = null;
				markDirty();
				return itemstack;
			}
			else
			{
				itemstack = inventoryContents[par1].splitStack(par2);

				if (inventoryContents[par1].stackSize == 0)
					inventoryContents[par1] = null;

				markDirty();
				return itemstack;
			}
		}
		else
			return null;
	}

	/**
	 * When some containers are closed they call this on each slot, then drop whatever it returns as an EntityItem -
	 * like when you close a workbench GUI.
	 */
	@Override
	public ItemStack getStackInSlotOnClosing(int par1)
	{
		if (inventoryContents[par1] != null)
		{
			ItemStack itemstack = inventoryContents[par1];
			inventoryContents[par1] = null;
			return itemstack;
		}
		else
			return null;
	}

	@Override
	public ItemStack getStackInSlot(int var1) {
		return inventoryContents[var1];
	}

	/**
	 * Copy of getStackInSlot which doesn't get overrided by CustomizableSCTE.
	 */

	public ItemStack getStackInSlotCopy(int var1) {
		return inventoryContents[var1];
	}

	@Override
	public void setInventorySlotContents(int par1, ItemStack par2ItemStack) {
		inventoryContents[par1] = par2ItemStack;

		if (par2ItemStack != null && par2ItemStack.stackSize > getInventoryStackLimit())
			par2ItemStack.stackSize = getInventoryStackLimit();

		markDirty();
	}

	public void addItemToStorage(ItemStack par1ItemStack) {
		for(int i = 10; i < inventoryContents.length; i++)
			if(inventoryContents[i] == null){
				inventoryContents[i] = par1ItemStack;
				break;
			}else if(inventoryContents[i] != null && inventoryContents[i].getItem() != null && par1ItemStack.getItem() != null && inventoryContents[i].getItem() == par1ItemStack.getItem())
				if(inventoryContents[i].stackSize + par1ItemStack.stackSize <= getInventoryStackLimit()){
					inventoryContents[i].stackSize += par1ItemStack.stackSize;
					break;
				}
				else
					inventoryContents[i].stackSize = getInventoryStackLimit();

		markDirty();
	}

	public void clearStorage() {
		for(int i = 10; i < inventoryContents.length; i++)
			if(inventoryContents[i] != null){
				inventoryContents[i] = null;
				break;
			}

		markDirty();
	}

	@Override
	public boolean hasCustomInventoryName() {
		return true;
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer var1) {
		return true;
	}

	@Override
	public void openInventory() {}

	@Override
	public void closeInventory() {}

	@Override
	public boolean isItemValidForSlot(int var1, ItemStack var2) {
		return true;
	}

	public String getType(){
		return type;
	}

	public void setType(String type){
		this.type = type;
	}

	public boolean shouldProvidePower() {
		return (type.matches("redstone") && isProvidingPower) ? true : false;
	}

	public void setShouldProvidePower(boolean isProvidingPower) {
		this.isProvidingPower = isProvidingPower;
	}

	public int getCooldown() {
		return cooldown;
	}

	public void setCooldown(int cooldown) {
		this.cooldown = cooldown;
	}

	public ItemStack[] getContents(){
		return inventoryContents;
	}

	public void setContents(ItemStack[] contents){
		inventoryContents = contents;
	}

	@Override
	public void onModuleInserted(ItemStack stack, EnumCustomModules module){
		if(getWorldObj().getTileEntity(xCoord + 2, yCoord, zCoord) != null && getWorldObj().getTileEntity(xCoord + 2, yCoord, zCoord) instanceof TileEntityInventoryScanner){
			if(!((CustomizableSCTE) getWorldObj().getTileEntity(xCoord + 2, yCoord, zCoord)).hasModule(module))
				((CustomizableSCTE) getWorldObj().getTileEntity(xCoord + 2, yCoord, zCoord)).insertModule(stack);
		}else if(getWorldObj().getTileEntity(xCoord - 2, yCoord, zCoord) != null && getWorldObj().getTileEntity(xCoord - 2, yCoord, zCoord) instanceof TileEntityInventoryScanner){
			if(!((CustomizableSCTE) getWorldObj().getTileEntity(xCoord - 2, yCoord, zCoord)).hasModule(module))
				((CustomizableSCTE) getWorldObj().getTileEntity(xCoord - 2, yCoord, zCoord)).insertModule(stack);
		}else if(getWorldObj().getTileEntity(xCoord, yCoord, zCoord + 2) != null && getWorldObj().getTileEntity(xCoord, yCoord, zCoord + 2) instanceof TileEntityInventoryScanner){
			if(!((CustomizableSCTE) getWorldObj().getTileEntity(xCoord, yCoord, zCoord + 2)).hasModule(module))
				((CustomizableSCTE) getWorldObj().getTileEntity(xCoord, yCoord, zCoord + 2)).insertModule(stack);
		}else if(getWorldObj().getTileEntity(xCoord, yCoord, zCoord - 2) != null && getWorldObj().getTileEntity(xCoord, yCoord, zCoord - 2) instanceof TileEntityInventoryScanner)
			if(!((CustomizableSCTE) getWorldObj().getTileEntity(xCoord, yCoord, zCoord - 2)).hasModule(module))
				((CustomizableSCTE) getWorldObj().getTileEntity(xCoord, yCoord, zCoord - 2)).insertModule(stack);
	}

	@Override
	public void onModuleRemoved(ItemStack stack, EnumCustomModules module){
		if(getWorldObj().getTileEntity(xCoord + 2, yCoord, zCoord) != null && getWorldObj().getTileEntity(xCoord + 2, yCoord, zCoord) instanceof TileEntityInventoryScanner){
			if(((CustomizableSCTE) getWorldObj().getTileEntity(xCoord + 2, yCoord, zCoord)).hasModule(module))
				((CustomizableSCTE) getWorldObj().getTileEntity(xCoord + 2, yCoord, zCoord)).removeModule(module);
		}else if(getWorldObj().getTileEntity(xCoord - 2, yCoord, zCoord) != null && getWorldObj().getTileEntity(xCoord - 2, yCoord, zCoord) instanceof TileEntityInventoryScanner){
			if(((CustomizableSCTE) getWorldObj().getTileEntity(xCoord - 2, yCoord, zCoord)).hasModule(module))
				((CustomizableSCTE) getWorldObj().getTileEntity(xCoord - 2, yCoord, zCoord)).removeModule(module);
		}else if(getWorldObj().getTileEntity(xCoord, yCoord, zCoord + 2) != null && getWorldObj().getTileEntity(xCoord, yCoord, zCoord + 2) instanceof TileEntityInventoryScanner){
			if(((CustomizableSCTE) getWorldObj().getTileEntity(xCoord, yCoord, zCoord + 2)).hasModule(module))
				((CustomizableSCTE) getWorldObj().getTileEntity(xCoord, yCoord, zCoord + 2)).removeModule(module);
		}else if(getWorldObj().getTileEntity(xCoord, yCoord, zCoord - 2) != null && getWorldObj().getTileEntity(xCoord, yCoord, zCoord - 2) instanceof TileEntityInventoryScanner)
			if(((CustomizableSCTE) getWorldObj().getTileEntity(xCoord, yCoord, zCoord - 2)).hasModule(module))
				((CustomizableSCTE) getWorldObj().getTileEntity(xCoord, yCoord, zCoord - 2)).removeModule(module);
	}

	@Override
	public EnumCustomModules[] acceptedModules() {
		return new EnumCustomModules[]{EnumCustomModules.WHITELIST, EnumCustomModules.SMART, EnumCustomModules.STORAGE};
	}

	@Override
	public Option<?>[] customOptions() {
		return null;
	}

}
