package net.geforcemods.securitycraft.tileentity;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.IPasswordProtected;
import net.geforcemods.securitycraft.blocks.BlockKeypadFurnace;
import net.geforcemods.securitycraft.gui.GuiHandler;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerFurnace;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.SlotFurnaceFuel;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileEntityKeypadFurnace extends TileEntityOwnable implements ISidedInventory, IPasswordProtected {

	private static final int[] slotsTop = new int[] {0};
	private static final int[] slotsBottom = new int[] {2, 1};
	private static final int[] slotsSides = new int[] {1};
	public ItemStack[] furnaceItemStacks = new ItemStack[3];
	public int furnaceBurnTime;
	public int currentItemBurnTime;
	public int cookTime;
	public int totalCookTime;
	private String furnaceCustomName;
	private String passcode;

	@Override
	public int getSizeInventory()
	{
		return furnaceItemStacks.length;
	}

	@Override
	public ItemStack getStackInSlot(int index)
	{
		return furnaceItemStacks[index];
	}

	@Override
	public ItemStack decrStackSize(int index, int count)
	{
		if (furnaceItemStacks[index] != null)
		{
			ItemStack stack;

			if (furnaceItemStacks[index].stackSize <= count)
			{
				stack = furnaceItemStacks[index];
				furnaceItemStacks[index] = null;
				return stack;
			}
			else
			{
				stack = furnaceItemStacks[index].splitStack(count);

				if (furnaceItemStacks[index].stackSize == 0)
					furnaceItemStacks[index] = null;

				return stack;
			}
		}
		else
			return null;
	}

	@Override
	public ItemStack removeStackFromSlot(int index)
	{
		if (furnaceItemStacks[index] != null)
		{
			ItemStack stack = furnaceItemStacks[index];
			furnaceItemStacks[index] = null;
			return stack;
		}
		else
			return null;
	}

	@Override
	public void setInventorySlotContents(int index, ItemStack stack)
	{
		boolean stacksEqual = stack != null && stack.isItemEqual(furnaceItemStacks[index]) && ItemStack.areItemStackTagsEqual(stack, furnaceItemStacks[index]);
		furnaceItemStacks[index] = stack;

		if (stack != null && stack.stackSize > getInventoryStackLimit())
			stack.stackSize = getInventoryStackLimit();

		if (index == 0 && !stacksEqual)
		{
			totalCookTime = getTotalCookTime(stack);
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

	public void setCustomInventoryName(String name)
	{
		furnaceCustomName = name;
	}

	@Override
	public void readFromNBT(NBTTagCompound tag)
	{
		super.readFromNBT(tag);
		NBTTagList list = tag.getTagList("Items", 10);
		furnaceItemStacks = new ItemStack[getSizeInventory()];

		for (int i = 0; i < list.tagCount(); ++i)
		{
			NBTTagCompound stackTag = list.getCompoundTagAt(i);
			byte slot = stackTag.getByte("Slot");

			if (slot >= 0 && slot < furnaceItemStacks.length)
				furnaceItemStacks[slot] = ItemStack.loadItemStackFromNBT(stackTag);
		}

		furnaceBurnTime = tag.getShort("BurnTime");
		cookTime = tag.getShort("CookTime");
		totalCookTime = tag.getShort("CookTimeTotal");
		currentItemBurnTime = getItemBurnTime(furnaceItemStacks[1]);
		passcode = tag.getString("passcode");

		if (tag.hasKey("CustomName", 8))
			furnaceCustomName = tag.getString("CustomName");
	}

	@Override
	public void writeToNBT(NBTTagCompound tag)
	{
		super.writeToNBT(tag);
		tag.setShort("BurnTime", (short)furnaceBurnTime);
		tag.setShort("CookTime", (short)cookTime);
		tag.setShort("CookTimeTotal", (short)totalCookTime);
		NBTTagList list = new NBTTagList();

		for (int i = 0; i < furnaceItemStacks.length; ++i)
			if (furnaceItemStacks[i] != null)
			{
				NBTTagCompound stackTag = new NBTTagCompound();
				stackTag.setByte("Slot", (byte)i);
				furnaceItemStacks[i].writeToNBT(stackTag);
				list.appendTag(stackTag);
			}

		tag.setTag("Items", list);

		if(passcode != null && !passcode.isEmpty())
			tag.setString("passcode", passcode);

		if (hasCustomName())
			tag.setString("CustomName", furnaceCustomName);
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

	@SideOnly(Side.CLIENT)
	public static boolean isBurning(IInventory inventory)
	{
		return inventory.getField(0) > 0;
	}

	@Override
	public void update()
	{
		boolean isBurning = this.isBurning();
		boolean shouldMarkDirty = false;

		if (this.isBurning())
			--furnaceBurnTime;

		if (!worldObj.isRemote)
		{
			if (!this.isBurning() && (furnaceItemStacks[1] == null || furnaceItemStacks[0] == null))
			{
				if (!this.isBurning() && cookTime > 0)
					cookTime = MathHelper.clamp_int(cookTime - 2, 0, totalCookTime);
			}
			else
			{
				if (!this.isBurning() && canSmelt())
				{
					currentItemBurnTime = furnaceBurnTime = getItemBurnTime(furnaceItemStacks[1]);

					if (this.isBurning())
					{
						shouldMarkDirty = true;

						if (furnaceItemStacks[1] != null)
						{
							--furnaceItemStacks[1].stackSize;

							if (furnaceItemStacks[1].stackSize == 0)
								furnaceItemStacks[1] = furnaceItemStacks[1].getItem().getContainerItem(furnaceItemStacks[1]);
						}
					}
				}

				if (this.isBurning() && canSmelt())
				{
					++cookTime;

					if (cookTime == totalCookTime)
					{
						cookTime = 0;
						totalCookTime = getTotalCookTime(furnaceItemStacks[0]);
						smeltItem();
						shouldMarkDirty = true;
					}
				}
				else
					cookTime = 0;
			}

			if (isBurning != this.isBurning())
				shouldMarkDirty = true;
		}

		if (shouldMarkDirty)
			markDirty();
	}

	public int getTotalCookTime(ItemStack stack)
	{
		return 200;
	}

	private boolean canSmelt()
	{
		if (furnaceItemStacks[0] == null)
			return false;
		else
		{
			ItemStack smeltResult = FurnaceRecipes.instance().getSmeltingResult(furnaceItemStacks[0]);
			if (smeltResult == null) return false;
			if (furnaceItemStacks[2] == null) return true;
			if (!furnaceItemStacks[2].isItemEqual(smeltResult)) return false;
			int result = furnaceItemStacks[2].stackSize + smeltResult.stackSize;
			return result <= getInventoryStackLimit() && result <= furnaceItemStacks[2].getMaxStackSize(); //Forge BugFix: Make it respect stack sizes properly.
		}
	}

	public void smeltItem()
	{
		if (canSmelt())
		{
			ItemStack smeltResult = FurnaceRecipes.instance().getSmeltingResult(furnaceItemStacks[0]);

			if (furnaceItemStacks[2] == null)
				furnaceItemStacks[2] = smeltResult.copy();
			else if (furnaceItemStacks[2].getItem() == smeltResult.getItem())
				furnaceItemStacks[2].stackSize += smeltResult.stackSize; // Forge BugFix: Results may have multiple items

			if (furnaceItemStacks[0].getItem() == Item.getItemFromBlock(Blocks.sponge) && furnaceItemStacks[0].getMetadata() == 1 && furnaceItemStacks[1] != null && furnaceItemStacks[1].getItem() == Items.bucket)
				furnaceItemStacks[1] = new ItemStack(Items.water_bucket);

			--furnaceItemStacks[0].stackSize;

			if (furnaceItemStacks[0].stackSize <= 0)
				furnaceItemStacks[0] = null;
		}
	}

	public static int getItemBurnTime(ItemStack stack)
	{
		if (stack == null)
			return 0;
		else
		{
			Item item = stack.getItem();

			if (item instanceof ItemBlock && Block.getBlockFromItem(item) != Blocks.air)
			{
				Block block = Block.getBlockFromItem(item);

				if (block == Blocks.wooden_slab)
					return 150;

				if (block.getMaterial() == Material.wood)
					return 300;

				if (block == Blocks.coal_block)
					return 16000;
			}

			if (item instanceof ItemTool && ((ItemTool)item).getToolMaterialName().equals("WOOD")) return 200;
			if (item instanceof ItemSword && ((ItemSword)item).getToolMaterialName().equals("WOOD")) return 200;
			if (item instanceof ItemHoe && ((ItemHoe)item).getMaterialName().equals("WOOD")) return 200;
			if (item == Items.stick) return 100;
			if (item == Items.coal) return 1600;
			if (item == Items.lava_bucket) return 20000;
			if (item == Item.getItemFromBlock(Blocks.sapling)) return 100;
			if (item == Items.blaze_rod) return 2400;
			return net.minecraftforge.fml.common.registry.GameRegistry.getFuelValue(stack);
		}
	}

	public static boolean isItemFuel(ItemStack stack)
	{
		return getItemBurnTime(stack) > 0;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player)
	{
		return worldObj.getTileEntity(pos) != this ? false : player.getDistanceSq(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D) <= 64.0D;
	}

	@Override
	public void openInventory(EntityPlayer player) {}

	@Override
	public void closeInventory(EntityPlayer player) {}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack)
	{
		return index == 2 ? false : (index != 1 ? true : isItemFuel(stack) || SlotFurnaceFuel.isBucket(stack));
	}

	@Override
	public int[] getSlotsForFace(EnumFacing side)
	{
		return side == EnumFacing.DOWN ? slotsBottom : (side == EnumFacing.UP ? slotsTop : slotsSides);
	}

	@Override
	public boolean canInsertItem(int index, ItemStack itemStack, EnumFacing direction)
	{
		return isItemValidForSlot(index, itemStack);
	}

	@Override
	public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction)
	{
		if (direction == EnumFacing.DOWN && index == 1)
		{
			Item item = stack.getItem();

			if (item != Items.water_bucket && item != Items.bucket)
				return false;
		}

		return true;
	}

	public String getGuiID()
	{
		return "minecraft:furnace";
	}

	public Container createContainer(InventoryPlayer playerInventory, EntityPlayer player)
	{
		return new ContainerFurnace(playerInventory, this);
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
		for (int i = 0; i < furnaceItemStacks.length; ++i)
			furnaceItemStacks[i] = null;
	}

	@Override
	public IChatComponent getDisplayName() {
		return hasCustomName() ? new ChatComponentText(getName()) : new ChatComponentTranslation(getName(), new Object[0]);
	}

	@Override
	public void activate(EntityPlayer player) {
		if(!worldObj.isRemote && BlockUtils.getBlock(getWorld(), getPos()) instanceof BlockKeypadFurnace)
			BlockKeypadFurnace.activate(worldObj, pos, player);
	}

	@Override
	public void openPasswordGUI(EntityPlayer player) {
		if(getPassword() != null)
			player.openGui(SecurityCraft.instance, GuiHandler.INSERT_PASSWORD_ID, worldObj, pos.getX(), pos.getY(), pos.getZ());
		else
		{
			if(getOwner().isOwner(player))
				player.openGui(SecurityCraft.instance, GuiHandler.SETUP_PASSWORD_ID, worldObj, pos.getX(), pos.getY(), pos.getZ());
			else
				PlayerUtils.sendMessageToPlayer(player, "SecurityCraft", StatCollector.translateToLocal("messages.securitycraft:passwordProtected.notSetUp"), EnumChatFormatting.DARK_RED);
		}
	}

	@Override
	public boolean onCodebreakerUsed(IBlockState blockState, EntityPlayer player, boolean isCodebreakerDisabled) {
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
		return (passcode != null && !passcode.isEmpty()) ? passcode : null;
	}

	@Override
	public void setPassword(String password) {
		passcode = password;
	}

}
