package net.geforcemods.securitycraft.tileentity;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IPasswordProtected;
import net.geforcemods.securitycraft.blocks.BlockKeypadFurnace;
import net.geforcemods.securitycraft.gui.GuiHandler;
import net.geforcemods.securitycraft.misc.BaseInteractionObject;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.container.FurnaceContainer;
import net.minecraft.inventory.container.FurnaceFuelSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tileentity.FurnaceTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.MathHelper;
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
	public boolean hasCustomSCName()
	{
		return furnaceCustomName != null && furnaceCustomName.length() > 0;
	}

	public void setCustomInventoryName(String name)
	{
		furnaceCustomName = name;
	}

	@Override
	public void read(CompoundNBT tag)
	{
		super.read(tag);
		ListNBT list = tag.getList("Items", 10);
		furnaceItemStacks = NonNullList.<ItemStack>withSize(getSizeInventory(), ItemStack.EMPTY);

		for (int i = 0; i < list.size(); ++i)
		{
			CompoundNBT stackTag = list.getCompound(i);
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
	public CompoundNBT write(CompoundNBT tag)
	{
		super.write(tag);
		tag.putShort("BurnTime", (short)furnaceBurnTime);
		tag.putShort("CookTime", (short)cookTime);
		tag.putShort("CookTimeTotal", (short)totalCookTime);
		ListNBT list = new ListNBT();

		for (int i = 0; i < furnaceItemStacks.size(); ++i)
			if (!furnaceItemStacks.get(i).isEmpty())
			{
				CompoundNBT stackTag = new CompoundNBT();
				stackTag.putByte("Slot", (byte)i);
				furnaceItemStacks.get(i).write(stackTag);
				list.add(stackTag);
			}

		tag.put("Items", list);

		if(passcode != null && !passcode.isEmpty())
			tag.putString("passcode", passcode);

		if (hasCustomSCName())
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
			return net.minecraftforge.event.ForgeEventFactory.getItemBurnTime(stack, ret == -1 ? FurnaceTileEntity.getBurnTimes().getOrDefault(item, 0) : ret);
		}
	}

	public static boolean isItemFuel(ItemStack stack)
	{
		return getItemBurnTime(stack) > 0;
	}

	@Override
	public boolean isUsableByPlayer(PlayerEntity player)
	{
		return world.getTileEntity(pos) != this ? false : player.getDistanceSq(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D) <= 64.0D;
	}

	@Override
	public void openInventory(PlayerEntity player) {}

	@Override
	public void closeInventory(PlayerEntity player) {}

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
		return index == 2 ? false : (index != 1 ? true : isItemFuel(stack) || FurnaceFuelSlot.isBucket(stack));
	}

	@Override
	public int[] getSlotsForFace(Direction side)
	{
		return side == Direction.DOWN ? slotsBottom : (side == Direction.UP ? slotsTop : slotsSides);
	}

	@Override
	public boolean canInsertItem(int index, ItemStack stack, Direction direction)
	{
		return isItemValidForSlot(index, stack);
	}

	@Override
	public boolean canExtractItem(int index, ItemStack stack, Direction direction)
	{
		if (direction == Direction.DOWN && index == 1)
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

	public Container createContainer(PlayerInventory playerInventory, PlayerEntity player)
	{
		return new FurnaceContainer(playerInventory, this);
	}

	@Override
	public void clear()
	{
		for (int i = 0; i < furnaceItemStacks.size(); ++i)
			furnaceItemStacks.set(i, ItemStack.EMPTY);
	}

	@Override
	public void activate(PlayerEntity player) {
		if(!world.isRemote && BlockUtils.getBlock(getWorld(), getPos()) instanceof BlockKeypadFurnace)
			BlockKeypadFurnace.activate(world, pos, player);
	}

	@Override
	public void openPasswordGUI(PlayerEntity player) {
		if(getPassword() != null)
		{
			if(player instanceof ServerPlayerEntity)
				NetworkHooks.openGui((ServerPlayerEntity)player, new BaseInteractionObject(GuiHandler.INSERT_PASSWORD), pos);
		}
		else
		{
			if(getOwner().isOwner(player))
			{
				if(player instanceof ServerPlayerEntity)
					NetworkHooks.openGui((ServerPlayerEntity)player, new BaseInteractionObject(GuiHandler.SETUP_PASSWORD), pos);
			}
			else
				PlayerUtils.sendMessageToPlayer(player, "SecurityCraft", ClientUtils.localize("messages.securitycraft:passwordProtected.notSetUp"), TextFormatting.DARK_RED);
		}
	}

	@Override
	public boolean onCodebreakerUsed(BlockState blockState, PlayerEntity player, boolean isCodebreakerDisabled) {
		if(isCodebreakerDisabled)
			PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize(SCContent.keypadFurnace.getTranslationKey()), ClientUtils.localize("messages.securitycraft:codebreakerDisabled"), TextFormatting.RED);
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
