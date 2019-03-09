package net.geforcemods.securitycraft.tileentity;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IPasswordProtected;
import net.geforcemods.securitycraft.blocks.BlockKeypadFurnace;
import net.geforcemods.securitycraft.gui.GuiHandler;
import net.geforcemods.securitycraft.misc.BaseInteractionObject;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerFurnace;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.SlotFurnaceFuel;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.crafting.VanillaRecipeTypes;
import net.minecraftforge.fml.network.NetworkHooks;

public class TileEntityKeypadFurnace extends TileEntityOwnable implements ISidedInventory, IPasswordProtected {

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

	public TileEntityKeypadFurnace()
	{
		super(SCContent.teTypeKeypadFurnace);
	}

	@Override
	public int getSizeInventory()
	{
		return furnaceItemStacks.size();
	}

	@Override
	public ItemStack getStackInSlot(int index)
	{
		return furnaceItemStacks.get(index);
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
				stack = furnaceItemStacks.get(index).split(count);

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
		boolean areStacksEqual = !stack.isEmpty() && stack.isItemEqual(furnaceItemStacks.get(index)) && ItemStack.areItemStackTagsEqual(stack, furnaceItemStacks.get(index));
		furnaceItemStacks.set(index, stack);

		if (!stack.isEmpty() && stack.getCount() > getInventoryStackLimit())
			stack.setCount(getInventoryStackLimit());

		if (index == 0 && !areStacksEqual)
		{
			totalCookTime = getTotalCookTime(stack);
			cookTime = 0;
			markDirty();
		}
	}

	@Override
	public ITextComponent getName()
	{
		return new TextComponentString(hasCustomName() ? furnaceCustomName : "container.furnace");
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
	public void read(NBTTagCompound tag)
	{
		super.read(tag);
		NBTTagList list = tag.getList("Items", 10);
		furnaceItemStacks = NonNullList.<ItemStack>withSize(getSizeInventory(), ItemStack.EMPTY);

		for (int i = 0; i < list.size(); ++i)
		{
			NBTTagCompound stackTag = list.getCompound(i);
			byte slot = stackTag.getByte("Slot");

			if (slot >= 0 && slot < furnaceItemStacks.size())
				furnaceItemStacks.set(slot, ItemStack.read(stackTag));
		}

		furnaceBurnTime = tag.getShort("BurnTime");
		cookTime = tag.getShort("CookTime");
		totalCookTime = tag.getShort("CookTimeTotal");
		currentItemBurnTime = getItemBurnTime(furnaceItemStacks.get(1));
		passcode = tag.getString("passcode");

		if (tag.contains("CustomName", 8))
			furnaceCustomName = tag.getString("CustomName");
	}

	@Override
	public NBTTagCompound write(NBTTagCompound tag)
	{
		super.write(tag);
		tag.putShort("BurnTime", (short)furnaceBurnTime);
		tag.putShort("CookTime", (short)cookTime);
		tag.putShort("CookTimeTotal", (short)totalCookTime);
		NBTTagList list = new NBTTagList();

		for (int i = 0; i < furnaceItemStacks.size(); ++i)
			if (!furnaceItemStacks.get(i).isEmpty())
			{
				NBTTagCompound stackTag = new NBTTagCompound();
				stackTag.putByte("Slot", (byte)i);
				furnaceItemStacks.get(i).write(stackTag);
				list.add(stackTag);
			}

		tag.put("Items", list);

		if(passcode != null && !passcode.isEmpty())
			tag.putString("passcode", passcode);

		if (hasCustomName())
			tag.putString("CustomName", furnaceCustomName);

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
	public int getCookProgressScaled(int scaleFactor)
	{
		return cookTime * scaleFactor / 200;
	}

	/**
	 * Returns an integer between 0 and the passed value representing how much burn time is left on the current fuel
	 * item, where 0 means that the item is exhausted and the passed value means that the item is fresh
	 */
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

	public static boolean isBurning(IInventory inventory)
	{
		return inventory.getField(0) > 0;
	}

	@Override
	public void tick()
	{
		boolean isBurning = this.isBurning();
		boolean shouldMarkDirty = false;

		if (this.isBurning())
			--furnaceBurnTime;

		if (!world.isRemote)
		{
			if (!this.isBurning() && (furnaceItemStacks.get(1).isEmpty() || furnaceItemStacks.get(0).isEmpty()))
			{
				if (!this.isBurning() && cookTime > 0)
					cookTime = MathHelper.clamp(cookTime - 2, 0, totalCookTime);
			}
			else
			{
				if (!this.isBurning() && canSmelt())
				{
					currentItemBurnTime = furnaceBurnTime = getItemBurnTime(furnaceItemStacks.get(1));

					if (this.isBurning())
					{
						shouldMarkDirty = true;

						if (!furnaceItemStacks.get(1).isEmpty())
						{
							furnaceItemStacks.get(1).shrink(1);

							if (furnaceItemStacks.get(1).getCount() == 0)
								furnaceItemStacks.set(1, furnaceItemStacks.get(1).getItem().getContainerItem(furnaceItemStacks.get(1)));
						}
					}
				}

				if (this.isBurning() && canSmelt())
				{
					++cookTime;

					if (cookTime == totalCookTime)
					{
						cookTime = 0;
						totalCookTime = getTotalCookTime(furnaceItemStacks.get(0));
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
		if (furnaceItemStacks.get(0).isEmpty())
			return false;
		else
		{
			ItemStack smeltResult = world.getRecipeManager().getResult(this, world, VanillaRecipeTypes.SMELTING);
			if (smeltResult.isEmpty()) return false;
			if (furnaceItemStacks.get(2).isEmpty()) return true;
			if (!furnaceItemStacks.get(2).isItemEqual(smeltResult)) return false;
			int result = furnaceItemStacks.get(2).getCount() + smeltResult.getCount();
			return result <= getInventoryStackLimit() && result <= furnaceItemStacks.get(2).getMaxStackSize(); //Forge BugFix: Make it respect stack sizes properly.
		}
	}

	public void smeltItem()
	{
		if (canSmelt())
		{
			ItemStack smeltResult = world.getRecipeManager().getResult(this, world, VanillaRecipeTypes.SMELTING);

			if (furnaceItemStacks.get(2).isEmpty())
				furnaceItemStacks.set(2, smeltResult.copy());
			else if (furnaceItemStacks.get(2).getItem() == smeltResult.getItem())
				furnaceItemStacks.get(2).grow(smeltResult.getCount()); // Forge BugFix: Results may have multiple items

			if (furnaceItemStacks.get(0).getItem() == Blocks.WET_SPONGE.asItem() && !furnaceItemStacks.get(1).isEmpty() && furnaceItemStacks.get(1).getItem() == Items.BUCKET)
				furnaceItemStacks.set(1, new ItemStack(Items.WATER_BUCKET));

			furnaceItemStacks.get(0).shrink(1);

			if (furnaceItemStacks.get(0).getCount() <= 0)
				furnaceItemStacks.set(0, ItemStack.EMPTY);
		}
	}

	public static int getItemBurnTime(ItemStack stack)
	{
		if (stack.isEmpty()) {
			return 0;
		} else {
			Item item = stack.getItem();
			int ret = stack.getBurnTime();
			return net.minecraftforge.event.ForgeEventFactory.getItemBurnTime(stack, ret == -1 ? TileEntityFurnace.getBurnTimes().getOrDefault(item, 0) : ret);
		}
	}

	public static boolean isItemFuel(ItemStack stack)
	{
		return getItemBurnTime(stack) > 0;
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
		return index == 2 ? false : (index != 1 ? true : isItemFuel(stack) || SlotFurnaceFuel.isBucket(stack));
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
		for (int i = 0; i < furnaceItemStacks.size(); ++i)
			furnaceItemStacks.set(i, ItemStack.EMPTY);
	}

	@Override
	public ITextComponent getDisplayName() {
		return hasCustomName() ? getName() : new TextComponentTranslation(getName().getFormattedText(), new Object[0]);
	}

	@Override
	public void activate(EntityPlayer player) {
		if(!world.isRemote && BlockUtils.getBlock(getWorld(), getPos()) instanceof BlockKeypadFurnace)
			BlockKeypadFurnace.activate(world, pos, player);
	}

	@Override
	public void openPasswordGUI(EntityPlayer player) {
		if(getPassword() != null)
		{
			if(player instanceof EntityPlayerMP)
				NetworkHooks.openGui((EntityPlayerMP)player, new BaseInteractionObject(GuiHandler.INSERT_PASSWORD), pos);
		}
		else
		{
			if(getOwner().isOwner(player))
			{
				if(player instanceof EntityPlayerMP)
					NetworkHooks.openGui((EntityPlayerMP)player, new BaseInteractionObject(GuiHandler.SETUP_PASSWORD), pos);
			}
			else
				PlayerUtils.sendMessageToPlayer(player, "SecurityCraft", ClientUtils.localize("messages.securitycraft:passwordProtected.notSetUp"), TextFormatting.DARK_RED);
		}
	}

	@Override
	public boolean onCodebreakerUsed(IBlockState blockState, EntityPlayer player, boolean isCodebreakerDisabled) {
		if(isCodebreakerDisabled)
			PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize("tile.securitycraft:keypadFurnace.name"), ClientUtils.localize("messages.securitycraft:codebreakerDisabled"), TextFormatting.RED);
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
