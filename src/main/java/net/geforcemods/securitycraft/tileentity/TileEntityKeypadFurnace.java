package net.geforcemods.securitycraft.tileentity;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.ICustomizable;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.api.IPasswordProtected;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.OptionBoolean;
import net.geforcemods.securitycraft.api.TileEntityOwnable;
import net.geforcemods.securitycraft.blocks.BlockKeypadFurnace;
import net.geforcemods.securitycraft.gui.GuiHandler;
import net.geforcemods.securitycraft.inventory.InsertOnlyInvWrapper;
import net.geforcemods.securitycraft.misc.EnumModuleType;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.SlotFurnaceFuel;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class TileEntityKeypadFurnace extends TileEntityOwnable implements ISidedInventory, IPasswordProtected, ITickable, IModuleInventory, ICustomizable {

	private IItemHandler insertOnlyHandler;
	private static final int[] slotsTop = new int[] {0};
	private static final int[] slotsBottom = new int[] {2, 1};
	private static final int[] slotsSides = new int[] {1};
	public NonNullList<ItemStack> furnaceItemStacks = NonNullList.<ItemStack>withSize(3, ItemStack.EMPTY);
	public int furnaceBurnTime;
	public int currentItemBurnTime;
	public int cookTime;
	public int totalCookTime;
	private String furnaceCustomName;
	private String passcode;
	private NonNullList<ItemStack> modules = NonNullList.<ItemStack>withSize(getMaxNumberOfModules(), ItemStack.EMPTY);
	private OptionBoolean sendMessage = new OptionBoolean("sendMessage", true);

	@Override
	public int getSizeInventory()
	{
		return furnaceItemStacks.size();
	}

	@Override
	public ItemStack getStackInSlot(int index)
	{
		return index >= 100 ? getModuleInSlot(index) : furnaceItemStacks.get(index);
	}

	@Override
	public ItemStack decrStackSize(int index, int count)
	{
		if (!furnaceItemStacks.get(index).isEmpty())
		{
			ItemStack stack;

			if (furnaceItemStacks.get(index).getCount() <= count)
			{
				stack = furnaceItemStacks.get(index);
				furnaceItemStacks.set(index, ItemStack.EMPTY);
				return stack;
			}
			else
			{
				stack = furnaceItemStacks.get(index).splitStack(count);

				if (furnaceItemStacks.get(index).getCount() == 0)
					furnaceItemStacks.set(index, ItemStack.EMPTY);

				return stack;
			}
		}
		else
			return ItemStack.EMPTY;
	}

	@Override
	public ItemStack removeStackFromSlot(int index)
	{
		if (!furnaceItemStacks.get(index).isEmpty())
		{
			ItemStack stack = furnaceItemStacks.get(index);
			furnaceItemStacks.set(index, ItemStack.EMPTY);
			return stack;
		}
		else
			return ItemStack.EMPTY;
	}

	@Override
	public void setInventorySlotContents(int index, ItemStack stack)
	{
		ItemStack furnaceStack = furnaceItemStacks.get(index);
		boolean areStacksEqual = !stack.isEmpty() && stack.isItemEqual(furnaceStack) && ItemStack.areItemStackTagsEqual(stack, furnaceStack);
		furnaceItemStacks.set(index, stack);

		if (stack.getCount() > getInventoryStackLimit())
			stack.setCount(getInventoryStackLimit());

		if (index == 0 && !areStacksEqual)
		{
			totalCookTime = getTotalCookTime();
			cookTime = 0;
			markDirty();
		}
	}

	@Override
	public String getName()
	{
		return hasCustomName() ? furnaceCustomName : "container.furnace";
	}

	@Override
	public boolean hasCustomName()
	{
		return furnaceCustomName != null && furnaceCustomName.length() > 0;
	}

	@Override
	public void readFromNBT(NBTTagCompound tag)
	{
		super.readFromNBT(tag);

		modules = readModuleInventory(tag);
		readOptions(tag);
		NBTTagList list = tag.getTagList("Items", 10);
		furnaceItemStacks = NonNullList.<ItemStack>withSize(getSizeInventory(), ItemStack.EMPTY);

		for (int i = 0; i < list.tagCount(); ++i)
		{
			NBTTagCompound stackTag = list.getCompoundTagAt(i);
			byte slot = stackTag.getByte("Slot");

			if (slot >= 0 && slot < furnaceItemStacks.size())
				furnaceItemStacks.set(slot, new ItemStack(stackTag));
		}

		furnaceBurnTime = tag.getShort("BurnTime");
		cookTime = tag.getShort("CookTime");
		totalCookTime = tag.getShort("CookTimeTotal");
		currentItemBurnTime = TileEntityFurnace.getItemBurnTime(furnaceItemStacks.get(1));
		passcode = tag.getString("passcode");
		furnaceCustomName = tag.getString("CustomName");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag)
	{
		super.writeToNBT(tag);

		writeModuleInventory(tag);
		writeOptions(tag);
		tag.setShort("BurnTime", (short)furnaceBurnTime);
		tag.setShort("CookTime", (short)cookTime);
		tag.setShort("CookTimeTotal", (short)totalCookTime);
		NBTTagList list = new NBTTagList();

		for (int i = 0; i < furnaceItemStacks.size(); ++i)
			if (!furnaceItemStacks.get(i).isEmpty())
			{
				NBTTagCompound stackTag = new NBTTagCompound();
				stackTag.setByte("Slot", (byte)i);
				furnaceItemStacks.get(i).writeToNBT(stackTag);
				list.appendTag(stackTag);
			}

		tag.setTag("Items", list);

		if(passcode != null && !passcode.isEmpty())
			tag.setString("passcode", passcode);

		if (hasCustomName())
			tag.setString("CustomName", furnaceCustomName);

		return tag;
	}

	@Override
	public int getInventoryStackLimit()
	{
		return 64;
	}

	/**
	 * Returns an integer between 0 and the passed value representing how close the current item is to being completely
	 * cooked
	 */
	@SideOnly(Side.CLIENT)
	public int getCookProgressScaled(int scaleFactor)
	{
		return cookTime * scaleFactor / 200;
	}

	/**
	 * Returns an integer between 0 and the passed value representing how much burn time is left on the current fuel
	 * item, where 0 means that the item is exhausted and the passed value means that the item is fresh
	 */
	@SideOnly(Side.CLIENT)
	public int getBurnTimeRemainingScaled(int scaleFactor)
	{
		if (currentItemBurnTime == 0)
			currentItemBurnTime = 200;

		return furnaceBurnTime * scaleFactor / currentItemBurnTime;
	}

	public boolean isBurning()
	{
		return furnaceBurnTime > 0;
	}

	@Override
	public void update()
	{
		boolean wasBurning = isBurning();
		boolean shouldMarkDirty = false;

		if(isBurning())
			--furnaceBurnTime;

		if(!world.isRemote)
		{
			ItemStack fuelStack = furnaceItemStacks.get(1);

			if(!isBurning() && (fuelStack.isEmpty() || furnaceItemStacks.get(0).isEmpty()))
			{
				if(!isBurning() && cookTime > 0)
					cookTime = MathHelper.clamp(cookTime - 2, 0, totalCookTime);
			}
			else
			{
				if(!isBurning() && canSmelt())
				{
					currentItemBurnTime = furnaceBurnTime = TileEntityFurnace.getItemBurnTime(fuelStack);

					if(isBurning())
					{
						shouldMarkDirty = true;

						if(!fuelStack.isEmpty())
						{
							fuelStack.shrink(1);

							if(fuelStack.getCount() == 0)
								furnaceItemStacks.set(1, fuelStack.getItem().getContainerItem(fuelStack));
						}
					}
				}

				if(isBurning() && canSmelt())
				{
					++cookTime;

					if(cookTime == totalCookTime)
					{
						cookTime = 0;
						totalCookTime = getTotalCookTime();
						smeltItem();
						shouldMarkDirty = true;
					}
				}
				else
					cookTime = 0;
			}

			if(wasBurning != isBurning())
				shouldMarkDirty = true;
		}

		if(shouldMarkDirty)
			markDirty();
	}

	public int getTotalCookTime()
	{
		return 200;
	}

	private boolean canSmelt()
	{
		if(furnaceItemStacks.get(0).isEmpty())
			return false;
		else
		{
			ItemStack smeltResult = FurnaceRecipes.instance().getSmeltingResult(furnaceItemStacks.get(0));

			if(smeltResult.isEmpty())
				return false;

			ItemStack outputStack = furnaceItemStacks.get(2);

			if(outputStack.isEmpty())
				return true;
			else if(!outputStack.isItemEqual(smeltResult))
				return false;

			int resultAmount = outputStack.getCount() + smeltResult.getCount();

			return resultAmount <= getInventoryStackLimit() && resultAmount <= outputStack.getMaxStackSize();
		}
	}

	public void smeltItem()
	{
		if(canSmelt())
		{
			ItemStack fuelStack = furnaceItemStacks.get(0);
			ItemStack inputStack = furnaceItemStacks.get(1);
			ItemStack smeltResult = FurnaceRecipes.instance().getSmeltingResult(fuelStack);
			ItemStack outputStack = furnaceItemStacks.get(2);

			if(outputStack.isEmpty())
				furnaceItemStacks.set(2, smeltResult.copy());
			else if(outputStack.getItem() == smeltResult.getItem())
				outputStack.grow(smeltResult.getCount());

			if(fuelStack.getItem() == Item.getItemFromBlock(Blocks.SPONGE) && fuelStack.getMetadata() == 1 && !inputStack.isEmpty() && inputStack.getItem() == Items.BUCKET)
				furnaceItemStacks.set(1, new ItemStack(Items.WATER_BUCKET));

			fuelStack.shrink(1);

			if(fuelStack.getCount() <= 0)
				furnaceItemStacks.set(0, ItemStack.EMPTY);
		}
	}

	@Override
	public boolean isUsableByPlayer(EntityPlayer player)
	{
		return world.getTileEntity(pos) != this ? false : player.getDistanceSq(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D) <= 64.0D;
	}

	@Override
	public void openInventory(EntityPlayer player) {}

	@Override
	public void closeInventory(EntityPlayer player) {}

	@Override
	public boolean isEmpty()
	{
		for(ItemStack stack : furnaceItemStacks)
			if(!stack.isEmpty())
				return false;

		return true;
	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack)
	{
		return index != 2 && (index != 1 || (TileEntityFurnace.isItemFuel(stack) || SlotFurnaceFuel.isBucket(stack) && furnaceItemStacks.get(1).getItem() != Items.BUCKET));
	}

	@Override
	public int[] getSlotsForFace(EnumFacing side)
	{
		return side == EnumFacing.DOWN ? slotsBottom : (side == EnumFacing.UP ? slotsTop : slotsSides);
	}

	@Override
	public boolean canInsertItem(int index, ItemStack stack, EnumFacing direction)
	{
		return isItemValidForSlot(index, stack);
	}

	@Override
	public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction)
	{
		if (direction == EnumFacing.DOWN && index == 1)
		{
			Item item = stack.getItem();

			if (item != Items.WATER_BUCKET && item != Items.BUCKET)
				return false;
		}

		return true;
	}

	@Override
	public int getField(int id)
	{
		switch (id)
		{
			case 0:
				return furnaceBurnTime;
			case 1:
				return currentItemBurnTime;
			case 2:
				return cookTime;
			case 3:
				return totalCookTime;
			default:
				return 0;
		}
	}

	@Override
	public void setField(int id, int value)
	{
		switch (id)
		{
			case 0:
				furnaceBurnTime = value;
				break;
			case 1:
				currentItemBurnTime = value;
				break;
			case 2:
				cookTime = value;
				break;
			case 3:
				totalCookTime = value;
		}
	}

	@Override
	public int getFieldCount()
	{
		return 4;
	}

	@Override
	public void clear()
	{
		for (int i = 0; i < furnaceItemStacks.size(); ++i)
			furnaceItemStacks.set(i, ItemStack.EMPTY);
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing)
	{
		return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
	}

	@Override
	public boolean enableHack()
	{
		return true;
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing)
	{
		if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
			return (T)BlockUtils.getProtectedCapability(facing, this, () -> super.getCapability(capability, facing), () -> getInsertOnlyHandler());
		else return super.getCapability(capability, facing);
	}

	private IItemHandler getInsertOnlyHandler()
	{
		if(insertOnlyHandler == null)
			insertOnlyHandler = new InsertOnlyInvWrapper(this);

		return insertOnlyHandler;
	}

	@Override
	public ITextComponent getDisplayName() {
		return hasCustomName() ? new TextComponentString(getName()) : new TextComponentTranslation(getName());
	}

	@Override
	public void activate(EntityPlayer player) {
		if(!world.isRemote && BlockUtils.getBlock(getWorld(), getPos()) instanceof BlockKeypadFurnace)
			BlockKeypadFurnace.activate(world, pos, player);
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
				PlayerUtils.sendMessageToPlayer(player, new TextComponentString("SecurityCraft"), ClientUtils.localize("messages.securitycraft:passwordProtected.notSetUp"), TextFormatting.DARK_RED);
		}
	}

	@Override
	public boolean onCodebreakerUsed(IBlockState blockState, EntityPlayer player) {
		activate(player);
		return true;
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
	public NonNullList<ItemStack> getInventory()
	{
		return modules;
	}

	@Override
	public EnumModuleType[] acceptedModules()
	{
		return new EnumModuleType[] {EnumModuleType.WHITELIST, EnumModuleType.BLACKLIST};
	}

	@Override
	public Option<?>[] customOptions()
	{
		return new Option[]{sendMessage};
	}

	public boolean sendsMessages()
	{
		return sendMessage.get();
	}
}
