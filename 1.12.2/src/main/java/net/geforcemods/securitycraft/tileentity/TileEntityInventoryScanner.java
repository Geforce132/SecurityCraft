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
import net.minecraft.util.NonNullList;

public class TileEntityInventoryScanner extends CustomizableSCTE implements IInventory{

	private NonNullList<ItemStack> inventoryContents = NonNullList.<ItemStack>withSize(37, ItemStack.EMPTY);
	private String type = "check";
	private boolean isProvidingPower;
	private int cooldown;

	@Override
	public void update(){
		if(cooldown > 0)
			cooldown--;
		else if(isProvidingPower){
			isProvidingPower = false;
			BlockUtils.updateAndNotify(getWorld(), pos, getWorld().getBlockState(pos).getBlock(), 1, true);
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound par1NBTTagCompound){
		super.readFromNBT(par1NBTTagCompound);

		NBTTagList nbttaglist = par1NBTTagCompound.getTagList("Items", 10);
		inventoryContents = NonNullList.<ItemStack>withSize(getSizeInventory(), ItemStack.EMPTY);

		for (int i = 0; i < nbttaglist.tagCount(); ++i)
		{
			NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
			int j = nbttagcompound1.getByte("Slot") & 255;

			if (j >= 0 && j < inventoryContents.size())
				inventoryContents.set(j, new ItemStack(nbttagcompound1));
		}


		if(par1NBTTagCompound.hasKey("cooldown"))
			cooldown = par1NBTTagCompound.getInteger("cooldown");

		if(par1NBTTagCompound.hasKey("type"))
			type = par1NBTTagCompound.getString("type");

	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound par1NBTTagCompound){
		super.writeToNBT(par1NBTTagCompound);

		NBTTagList nbttaglist = new NBTTagList();

		for (int i = 0; i < inventoryContents.size(); ++i)
			if (!inventoryContents.get(i).isEmpty())
			{
				NBTTagCompound nbttagcompound1 = new NBTTagCompound();
				nbttagcompound1.setByte("Slot", (byte)i);
				inventoryContents.get(i).writeToNBT(nbttagcompound1);
				nbttaglist.appendTag(nbttagcompound1);
			}

		par1NBTTagCompound.setTag("Items", nbttaglist);
		par1NBTTagCompound.setInteger("cooldown", cooldown);
		par1NBTTagCompound.setString("type", type);
		return par1NBTTagCompound;
	}

	@Override
	public int getSizeInventory() {
		return 37;
	}

	@Override
	public ItemStack decrStackSize(int par1, int par2)
	{
		if (!inventoryContents.get(par1).isEmpty())
		{
			ItemStack itemstack;

			if (inventoryContents.get(par1).getCount() <= par2)
			{
				itemstack = inventoryContents.get(par1);
				inventoryContents.set(par1, ItemStack.EMPTY);
				markDirty();
				return itemstack;
			}
			else
			{
				itemstack = inventoryContents.get(par1).splitStack(par2);

				if (inventoryContents.get(par1).getCount() == 0)
					inventoryContents.set(par1, ItemStack.EMPTY);

				markDirty();
				return itemstack;
			}
		}
		else
			return ItemStack.EMPTY;
	}

	/**
	 * When some containers are closed they call this on each slot, then drop whatever it returns as an EntityItem -
	 * like when you close a workbench GUI.
	 */
	public ItemStack getStackInSlotOnClosing(int par1)
	{
		if (!inventoryContents.get(par1).isEmpty())
		{
			ItemStack itemstack = inventoryContents.get(par1);
			inventoryContents.set(par1, ItemStack.EMPTY);
			return itemstack;
		}
		else
			return ItemStack.EMPTY;
	}

	@Override
	public ItemStack getStackInSlot(int var1) {
		return inventoryContents.get(var1);
	}

	/**
	 * Copy of getStackInSlot which doesn't get overrided by CustomizableSCTE.
	 */

	public ItemStack getStackInSlotCopy(int var1) {
		return inventoryContents.get(var1);
	}

	@Override
	public void setInventorySlotContents(int par1, ItemStack par2ItemStack) {
		inventoryContents.set(par1, par2ItemStack);

		if (!par2ItemStack.isEmpty() && par2ItemStack.getCount() > getInventoryStackLimit())
			par2ItemStack.setCount(getInventoryStackLimit());

		markDirty();
	}

	public void addItemToStorage(ItemStack par1ItemStack) {
		for(int i = 10; i < inventoryContents.size(); i++)
			if(inventoryContents.get(i).isEmpty()){
				inventoryContents.set(i, par1ItemStack);
				break;
			}else if(!inventoryContents.get(i).isEmpty() && inventoryContents.get(i).getItem() != null && par1ItemStack.getItem() != null && inventoryContents.get(i).getItem() == par1ItemStack.getItem())
				if(inventoryContents.get(i).getCount() + par1ItemStack.getCount() <= getInventoryStackLimit()){
					inventoryContents.get(i).grow(par1ItemStack.getCount());
					break;
				}
				else
					inventoryContents.get(i).setCount(getInventoryStackLimit());

		markDirty();
	}

	public void clearStorage() {
		for(int i = 10; i < inventoryContents.size(); i++)
			if(!inventoryContents.get(i).isEmpty()){
				inventoryContents.set(i, ItemStack.EMPTY);
				break;
			}

		markDirty();
	}

	@Override
	public boolean hasCustomName() {
		return true;
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public boolean isUsableByPlayer(EntityPlayer var1) {
		return true;
	}

	@Override
	public void openInventory(EntityPlayer player) {}

	@Override
	public void closeInventory(EntityPlayer player) {}

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

	public NonNullList<ItemStack> getContents(){
		return inventoryContents;
	}

	public void setContents(NonNullList<ItemStack> contents){
		inventoryContents = contents;
	}

	@Override
	public void onModuleInserted(ItemStack stack, EnumCustomModules module){
		if(!getWorld().isRemote)
			if(getWorld().getTileEntity(pos.east(2)) != null && getWorld().getTileEntity(pos.east(2)) instanceof TileEntityInventoryScanner){
				if(!((CustomizableSCTE) getWorld().getTileEntity(pos.east(2))).hasModule(module))
					((CustomizableSCTE) getWorld().getTileEntity(pos.east(2))).insertModule(stack);
			}else if(getWorld().getTileEntity(pos.west(2)) != null && getWorld().getTileEntity(pos.west(2)) instanceof TileEntityInventoryScanner){
				if(!((CustomizableSCTE) getWorld().getTileEntity(pos.west(2))).hasModule(module))
					((CustomizableSCTE) getWorld().getTileEntity(pos.west(2))).insertModule(stack);
			}else if(getWorld().getTileEntity(pos.south(2)) != null && getWorld().getTileEntity(pos.south(2)) instanceof TileEntityInventoryScanner){
				if(!((CustomizableSCTE) getWorld().getTileEntity(pos.south(2))).hasModule(module))
					((CustomizableSCTE) getWorld().getTileEntity(pos.south(2))).insertModule(stack);
			}else if(getWorld().getTileEntity(pos.north(2)) != null && getWorld().getTileEntity(pos.north(2)) instanceof TileEntityInventoryScanner)
				if(!((CustomizableSCTE) getWorld().getTileEntity(pos.north(2))).hasModule(module))
					((CustomizableSCTE) getWorld().getTileEntity(pos.north(2))).insertModule(stack);
	}

	@Override
	public void onModuleRemoved(ItemStack stack, EnumCustomModules module){
		if(!getWorld().isRemote)
			if(getWorld().getTileEntity(pos.east(2)) != null && getWorld().getTileEntity(pos.east(2)) instanceof TileEntityInventoryScanner){
				if(((CustomizableSCTE) getWorld().getTileEntity(pos.east(2))).hasModule(module))
					((CustomizableSCTE) getWorld().getTileEntity(pos.east(2))).removeModule(module);
			}else if(getWorld().getTileEntity(pos.west(2)) != null && getWorld().getTileEntity(pos.west(2)) instanceof TileEntityInventoryScanner){
				if(((CustomizableSCTE) getWorld().getTileEntity(pos.west(2))).hasModule(module))
					((CustomizableSCTE) getWorld().getTileEntity(pos.west(2))).removeModule(module);
			}else if(getWorld().getTileEntity(pos.south(2)) != null && getWorld().getTileEntity(pos.south(2)) instanceof TileEntityInventoryScanner){
				if(((CustomizableSCTE) getWorld().getTileEntity(pos.south(2))).hasModule(module))
					((CustomizableSCTE) getWorld().getTileEntity(pos.south(2))).removeModule(module);
			}else if(getWorld().getTileEntity(pos.north(2)) != null && getWorld().getTileEntity(pos.north(2)) instanceof TileEntityInventoryScanner)
				if(((CustomizableSCTE) getWorld().getTileEntity(pos.north(2))).hasModule(module))
					((CustomizableSCTE) getWorld().getTileEntity(pos.north(2))).removeModule(module);
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
