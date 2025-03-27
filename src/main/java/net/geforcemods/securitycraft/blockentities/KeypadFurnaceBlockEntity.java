package net.geforcemods.securitycraft.blockentities;

import java.util.UUID;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.ILockable;
import net.geforcemods.securitycraft.api.INameSetter;
import net.geforcemods.securitycraft.api.IPasscodeProtected;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.BooleanOption;
import net.geforcemods.securitycraft.api.Option.DisabledOption;
import net.geforcemods.securitycraft.api.Option.SendAllowlistMessageOption;
import net.geforcemods.securitycraft.api.Option.SendDenylistMessageOption;
import net.geforcemods.securitycraft.api.Option.SmartModuleCooldownOption;
import net.geforcemods.securitycraft.blocks.KeypadFurnaceBlock;
import net.geforcemods.securitycraft.inventory.InsertOnlySidedInvWrapper;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.PasscodeUtils;
import net.geforcemods.securitycraft.util.Utils;
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
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class KeypadFurnaceBlockEntity extends DisguisableBlockEntity implements ISidedInventory, IPasscodeProtected, ITickable, INameSetter, ILockable {
	private IItemHandler insertOnlyHandlerTop, insertOnlyHandlerBottom, insertOnlyHandlerSide;
	private static final int[] slotsTop = {
			0
	};
	private static final int[] slotsBottom = {
			2, 1
	};
	private static final int[] slotsSides = {
			1
	};
	private NonNullList<ItemStack> furnaceItemStacks = NonNullList.<ItemStack>withSize(3, ItemStack.EMPTY);
	private int furnaceBurnTime;
	private int currentItemBurnTime;
	private int cookTime;
	private int totalCookTime;
	private String furnaceCustomName;
	private byte[] passcode;
	private UUID saltKey;
	private NonNullList<ItemStack> modules = NonNullList.<ItemStack>withSize(getMaxNumberOfModules(), ItemStack.EMPTY);
	private BooleanOption sendAllowlistMessage = new SendAllowlistMessageOption(false);
	private BooleanOption sendDenylistMessage = new SendDenylistMessageOption(true);
	private DisabledOption disabled = new DisabledOption(false);
	private SmartModuleCooldownOption smartModuleCooldown = new SmartModuleCooldownOption(this::getPos);
	private long cooldownEnd = 0;

	@Override
	public int getSizeInventory() {
		return furnaceItemStacks.size();
	}

	@Override
	public ItemStack getStackInSlot(int index) {
		return index >= 100 ? getModuleInSlot(index) : furnaceItemStacks.get(index);
	}

	@Override
	public ItemStack decrStackSize(int index, int count) {
		if (!furnaceItemStacks.get(index).isEmpty()) {
			ItemStack stack;

			if (furnaceItemStacks.get(index).getCount() <= count) {
				stack = furnaceItemStacks.get(index);
				furnaceItemStacks.set(index, ItemStack.EMPTY);
				return stack;
			}
			else {
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
	public ItemStack removeStackFromSlot(int index) {
		if (!furnaceItemStacks.get(index).isEmpty()) {
			ItemStack stack = furnaceItemStacks.get(index);
			furnaceItemStacks.set(index, ItemStack.EMPTY);
			return stack;
		}
		else
			return ItemStack.EMPTY;
	}

	@Override
	public void setInventorySlotContents(int index, ItemStack stack) {
		ItemStack furnaceStack = furnaceItemStacks.get(index);
		boolean areStacksEqual = !stack.isEmpty() && stack.isItemEqual(furnaceStack) && ItemStack.areItemStackTagsEqual(stack, furnaceStack);
		furnaceItemStacks.set(index, stack);

		if (stack.getCount() > getInventoryStackLimit())
			stack.setCount(getInventoryStackLimit());

		if (index == 0 && !areStacksEqual) {
			totalCookTime = getTotalCookTime();
			cookTime = 0;
			markDirty();
		}
	}

	@Override
	public String getName() {
		return furnaceCustomName;
	}

	@Override
	public boolean hasCustomName() {
		return furnaceCustomName != null && !furnaceCustomName.isEmpty() && !furnaceCustomName.equals(getDefaultName().getFormattedText());
	}

	@Override
	public ITextComponent getDisplayName() {
		return hasCustomName() ? new TextComponentString(getName()) : getDefaultName();
	}

	@Override
	public ITextComponent getDefaultName() {
		return Utils.localize(SCContent.keypadFurnace);
	}

	@Override
	public void setCustomName(String customName) {
		furnaceCustomName = customName;
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);

		long cooldownLeft;
		NBTTagList list = tag.getTagList("Items", 10);

		furnaceItemStacks = NonNullList.<ItemStack>withSize(getSizeInventory(), ItemStack.EMPTY);

		for (int i = 0; i < list.tagCount(); ++i) {
			NBTTagCompound stackTag = list.getCompoundTagAt(i);
			byte slot = stackTag.getByte("Slot");

			if (slot >= 0 && slot < furnaceItemStacks.size())
				furnaceItemStacks.set(slot, new ItemStack(stackTag));
		}

		furnaceBurnTime = tag.getShort("BurnTime");
		cookTime = tag.getShort("CookTime");
		totalCookTime = tag.getShort("CookTimeTotal");
		currentItemBurnTime = TileEntityFurnace.getItemBurnTime(furnaceItemStacks.get(1));
		loadSaltKey(tag);
		loadPasscode(tag);
		cooldownLeft = getCooldownEnd() - System.currentTimeMillis();
		tag.setLong("cooldownLeft", cooldownLeft <= 0 ? -1 : cooldownLeft);
		furnaceCustomName = tag.getString("CustomName");

		if (tag.hasKey("sendMessage") && !tag.getBoolean("sendMessage")) {
			sendAllowlistMessage.setValue(false);
			sendDenylistMessage.setValue(false);
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);

		tag.setShort("BurnTime", (short) furnaceBurnTime);
		tag.setShort("CookTime", (short) cookTime);
		tag.setShort("CookTimeTotal", (short) totalCookTime);
		NBTTagList list = new NBTTagList();

		for (int i = 0; i < furnaceItemStacks.size(); ++i) {
			if (!furnaceItemStacks.get(i).isEmpty()) {
				NBTTagCompound stackTag = new NBTTagCompound();
				stackTag.setByte("Slot", (byte) i);
				furnaceItemStacks.get(i).writeToNBT(stackTag);
				list.appendTag(stackTag);
			}
		}

		tag.setTag("Items", list);
		savePasscodeAndSalt(tag);
		tag.setLong("cooldownLeft", getCooldownEnd() - System.currentTimeMillis());

		if (hasCustomName())
			tag.setString("CustomName", furnaceCustomName);

		return tag;
	}

	@Override
	public NBTTagCompound getUpdateTag() {
		return PasscodeUtils.filterPasscodeAndSaltFromTag(writeToNBT(new NBTTagCompound()));
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@SideOnly(Side.CLIENT)
	public int getCookProgressScaled(int scaleFactor) {
		return cookTime * scaleFactor / 200;
	}

	@SideOnly(Side.CLIENT)
	public int getBurnTimeRemainingScaled(int scaleFactor) {
		if (currentItemBurnTime == 0)
			currentItemBurnTime = 200;

		return furnaceBurnTime * scaleFactor / currentItemBurnTime;
	}

	public boolean isBurning() {
		return furnaceBurnTime > 0;
	}

	@Override
	public void update() {
		if (isDisabled())
			return;

		boolean wasBurning = isBurning();
		boolean shouldMarkDirty = false;

		if (isBurning())
			--furnaceBurnTime;

		if (!world.isRemote) {
			ItemStack fuelStack = furnaceItemStacks.get(1);

			if (!isBurning() && (fuelStack.isEmpty() || furnaceItemStacks.get(0).isEmpty())) {
				if (!isBurning() && cookTime > 0)
					cookTime = MathHelper.clamp(cookTime - 2, 0, totalCookTime);
			}
			else {
				if (!isBurning() && canSmelt()) {
					currentItemBurnTime = furnaceBurnTime = TileEntityFurnace.getItemBurnTime(fuelStack);

					if (isBurning()) {
						shouldMarkDirty = true;

						if (!fuelStack.isEmpty()) {
							fuelStack.shrink(1);

							if (fuelStack.getCount() == 0)
								furnaceItemStacks.set(1, fuelStack.getItem().getContainerItem(fuelStack));
						}
					}
				}

				if (isBurning() && canSmelt()) {
					++cookTime;

					if (cookTime == totalCookTime) {
						cookTime = 0;
						totalCookTime = getTotalCookTime();
						smeltItem();
						shouldMarkDirty = true;
					}
				}
				else
					cookTime = 0;
			}

			if (wasBurning != isBurning()) {
				shouldMarkDirty = true;
				world.setBlockState(pos, world.getBlockState(pos).withProperty(KeypadFurnaceBlock.LIT, isBurning()));
			}
		}

		if (shouldMarkDirty)
			markDirty();
	}

	public int getTotalCookTime() {
		return 200;
	}

	private boolean canSmelt() {
		if (furnaceItemStacks.get(0).isEmpty())
			return false;
		else {
			ItemStack smeltResult = FurnaceRecipes.instance().getSmeltingResult(furnaceItemStacks.get(0));

			if (smeltResult.isEmpty())
				return false;

			ItemStack outputStack = furnaceItemStacks.get(2);

			if (outputStack.isEmpty())
				return true;
			else if (!outputStack.isItemEqual(smeltResult))
				return false;

			int resultAmount = outputStack.getCount() + smeltResult.getCount();

			return resultAmount <= getInventoryStackLimit() && resultAmount <= outputStack.getMaxStackSize();
		}
	}

	public void smeltItem() {
		if (canSmelt()) {
			ItemStack fuelStack = furnaceItemStacks.get(0);
			ItemStack inputStack = furnaceItemStacks.get(1);
			ItemStack smeltResult = FurnaceRecipes.instance().getSmeltingResult(fuelStack);
			ItemStack outputStack = furnaceItemStacks.get(2);

			if (outputStack.isEmpty())
				furnaceItemStacks.set(2, smeltResult.copy());
			else if (outputStack.getItem() == smeltResult.getItem())
				outputStack.grow(smeltResult.getCount());

			if (fuelStack.getItem() == Item.getItemFromBlock(Blocks.SPONGE) && fuelStack.getMetadata() == 1 && !inputStack.isEmpty() && inputStack.getItem() == Items.BUCKET)
				furnaceItemStacks.set(1, new ItemStack(Items.WATER_BUCKET));

			fuelStack.shrink(1);

			if (fuelStack.getCount() <= 0)
				furnaceItemStacks.set(0, ItemStack.EMPTY);
		}
	}

	@Override
	public boolean isUsableByPlayer(EntityPlayer player) {
		return world.getTileEntity(pos) == this && player.getDistanceSq(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D) <= 64.0D;
	}

	@Override
	public void openInventory(EntityPlayer player) {}

	@Override
	public void closeInventory(EntityPlayer player) {}

	@Override
	public boolean isEmpty() {
		for (ItemStack stack : furnaceItemStacks) {
			if (!stack.isEmpty())
				return false;
		}

		return true;
	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		return index != 2 && (index != 1 || (TileEntityFurnace.isItemFuel(stack) || SlotFurnaceFuel.isBucket(stack) && furnaceItemStacks.get(1).getItem() != Items.BUCKET));
	}

	@Override
	public int[] getSlotsForFace(EnumFacing side) {
		return side == EnumFacing.DOWN ? slotsBottom : (side == EnumFacing.UP ? slotsTop : slotsSides);
	}

	@Override
	public boolean canInsertItem(int index, ItemStack stack, EnumFacing direction) {
		return isItemValidForSlot(index, stack);
	}

	@Override
	public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
		if (direction == EnumFacing.DOWN && index == 1) {
			Item item = stack.getItem();

			if (item != Items.WATER_BUCKET && item != Items.BUCKET)
				return false;
		}

		return true;
	}

	@Override
	public int getField(int id) {
		switch (id) {
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
	public void setField(int id, int value) {
		switch (id) {
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
				break;
			default:
				throw new IllegalArgumentException(String.format("Unknown field id passed to KeypadFurnaceBlock#setField %s", id));
		}
	}

	@Override
	public int getFieldCount() {
		return 4;
	}

	@Override
	public void clear() {
		for (int i = 0; i < furnaceItemStacks.size(); ++i) {
			furnaceItemStacks.set(i, ItemStack.EMPTY);
		}
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
	}

	@Override
	public boolean enableHack() {
		return true;
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
			return BlockUtils.isAllowedToExtractFromProtectedObject(facing, this) ? (T) super.getCapability(capability, facing) : (T) getInsertOnlyHandler(facing);
		else
			return super.getCapability(capability, facing);
	}

	private IItemHandler getInsertOnlyHandler(EnumFacing facing) {
		if (facing == EnumFacing.DOWN) {
			if (insertOnlyHandlerBottom == null)
				insertOnlyHandlerBottom = new InsertOnlySidedInvWrapper(this, EnumFacing.DOWN);

			return insertOnlyHandlerBottom;
		}
		else if (facing == EnumFacing.UP) {
			if (insertOnlyHandlerTop == null)
				insertOnlyHandlerTop = new InsertOnlySidedInvWrapper(this, EnumFacing.UP);

			return insertOnlyHandlerTop;
		}
		else {
			if (insertOnlyHandlerSide == null)
				insertOnlyHandlerSide = new InsertOnlySidedInvWrapper(this, EnumFacing.WEST);

			return insertOnlyHandlerSide;
		}
	}

	@Override
	public void activate(EntityPlayer player) {
		if (!world.isRemote)
			((KeypadFurnaceBlock) getBlockType()).activate(world.getBlockState(pos), world, pos, player);
	}

	@Override
	public byte[] getPasscode() {
		return passcode == null || passcode.length == 0 ? null : passcode;
	}

	@Override
	public void setPasscode(byte[] passcode) {
		this.passcode = passcode;
	}

	@Override
	public UUID getSaltKey() {
		return saltKey;
	}

	@Override
	public void setSaltKey(UUID saltKey) {
		this.saltKey = saltKey;
	}

	@Override
	public NonNullList<ItemStack> getInventory() {
		return modules;
	}

	@Override
	public void startCooldown() {
		if (!isOnCooldown()) {
			IBlockState state = world.getBlockState(pos);

			cooldownEnd = System.currentTimeMillis() + smartModuleCooldown.get() * 50;
			world.notifyBlockUpdate(pos, state, state, 3);
			markDirty();
		}
	}

	@Override
	public long getCooldownEnd() {
		return cooldownEnd;
	}

	@Override
	public boolean isOnCooldown() {
		return System.currentTimeMillis() < getCooldownEnd();
	}

	@Override
	public ModuleType[] acceptedModules() {
		return new ModuleType[] {
				ModuleType.ALLOWLIST, ModuleType.DENYLIST, ModuleType.DISGUISE, ModuleType.SMART, ModuleType.HARMING
		};
	}

	@Override
	public Option<?>[] customOptions() {
		return new Option[] {
				sendAllowlistMessage, sendDenylistMessage, disabled, smartModuleCooldown
		};
	}

	public boolean sendsAllowlistMessage() {
		return sendAllowlistMessage.get();
	}

	public boolean sendsDenylistMessage() {
		return sendDenylistMessage.get();
	}

	public boolean isDisabled() {
		return disabled.get();
	}
}
