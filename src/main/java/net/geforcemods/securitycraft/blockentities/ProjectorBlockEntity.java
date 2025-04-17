package net.geforcemods.securitycraft.blockentities;

import net.geforcemods.securitycraft.api.ILockable;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.inventory.ProjectorMenu;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.util.BlockEntityRenderDelegate;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.math.AxisAlignedBB;

public class ProjectorBlockEntity extends DisguisableBlockEntity implements IInventory, ILockable {
	public static final int MIN_WIDTH = 1; //also for height
	public static final int MAX_WIDTH = 10; //also for height
	public static final int MIN_RANGE = 1;
	public static final int MAX_RANGE = 30;
	public static final int MIN_OFFSET = -10;
	public static final int MAX_OFFSET = 10;
	public static final int RENDER_DISTANCE = 100;
	private int projectionWidth = 1;
	private int projectionHeight = 1;
	private int projectionRange = 5;
	private int projectionOffset = 0;
	private boolean activatedByRedstone = false;
	private boolean active = false;
	private boolean horizontal = false;
	private boolean overridingBlocks = false;
	private ItemStack projectedBlock = ItemStack.EMPTY;
	private IBlockState projectedState = Blocks.AIR.getDefaultState();

	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return new AxisAlignedBB(getPos()).grow(RENDER_DISTANCE);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);

		tag.setInteger("width", projectionWidth);
		tag.setInteger("height", projectionHeight);
		tag.setInteger("range", projectionRange);
		tag.setInteger("offset", projectionOffset);
		tag.setBoolean("active", active);
		tag.setBoolean("horizontal", horizontal);
		tag.setBoolean("overriding_blocks", overridingBlocks);
		tag.setTag("storedItem", projectedBlock.writeToNBT(new NBTTagCompound()));
		tag.setTag("SavedState", NBTUtil.writeBlockState(new NBTTagCompound(), projectedState));
		return tag;
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);

		projectionWidth = tag.getInteger("width");
		projectionHeight = tag.getInteger("height");
		projectionRange = tag.getInteger("range");
		projectionOffset = tag.getInteger("offset");
		activatedByRedstone = isModuleEnabled(ModuleType.REDSTONE);
		active = tag.getBoolean("active");
		horizontal = tag.getBoolean("horizontal");
		overridingBlocks = tag.getBoolean("overriding_blocks");
		projectedBlock = new ItemStack(tag.getCompoundTag("storedItem"));

		if (!tag.hasKey("SavedState"))
			resetSavedState();
		else
			setProjectedState(NBTUtil.readBlockState(tag.getCompoundTag("SavedState")));
	}

	public int getProjectionWidth() {
		return projectionWidth;
	}

	public void setProjectionWidth(int width) {
		projectionWidth = width;
		markDirty();
	}

	public int getProjectionHeight() {
		return projectionHeight;
	}

	public void setProjectionHeight(int projectionHeight) {
		this.projectionHeight = projectionHeight;
		markDirty();
	}

	public int getProjectionRange() {
		return projectionRange;
	}

	public void setProjectionRange(int range) {
		projectionRange = range;
		markDirty();
	}

	public int getProjectionOffset() {
		return projectionOffset;
	}

	public void setProjectionOffset(int offset) {
		projectionOffset = offset;
		markDirty();
	}

	public boolean isActivatedByRedstone() {
		return activatedByRedstone;
	}

	public void setActivatedByRedstone(boolean redstone) {
		activatedByRedstone = redstone;
		markDirty();
	}

	public boolean isHorizontal() {
		return horizontal;
	}

	public void setHorizontal(boolean horizontal) {
		this.horizontal = horizontal;
		markDirty();
	}

	public boolean isOverridingBlocks() {
		return overridingBlocks;
	}

	public void setOverridingBlocks(boolean overridingBlocks) {
		this.overridingBlocks = overridingBlocks;
	}

	public boolean isActive() {
		return !activatedByRedstone || active;
	}

	public void setActive(boolean isOn) {
		active = isOn;
		markDirty();
	}

	public IBlockState getProjectedState() {
		return projectedState;
	}

	@Override
	public void onModuleInserted(ItemStack stack, ModuleType module, boolean toggled) {
		super.onModuleInserted(stack, module, toggled);

		if (module == ModuleType.REDSTONE)
			setActivatedByRedstone(true);
	}

	@Override
	public void onModuleRemoved(ItemStack stack, ModuleType module, boolean toggled) {
		super.onModuleRemoved(stack, module, toggled);

		if (module == ModuleType.REDSTONE)
			setActivatedByRedstone(false);
	}

	@Override
	public ModuleType[] acceptedModules() {
		return new ModuleType[] {
				ModuleType.DISGUISE, ModuleType.REDSTONE
		};
	}

	@Override
	public Option<?>[] customOptions() {
		return new Option[0];
	}

	@Override
	public void clear() {
		projectedBlock = ItemStack.EMPTY;
		resetSavedState();
	}

	@Override
	public ItemStack decrStackSize(int index, int count) {
		ItemStack stack = projectedBlock;

		if (count >= 1) {
			projectedBlock = ItemStack.EMPTY;
			resetSavedState();
			return stack;
		}

		return ItemStack.EMPTY;
	}

	@Override
	public int getSizeInventory() {
		return ProjectorMenu.SIZE;
	}

	@Override
	public int getInventoryStackLimit() {
		return 1;
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		return slot >= 100 ? getModuleInSlot(slot) : (slot == 36 ? projectedBlock : ItemStack.EMPTY);
	}

	@Override
	public boolean isEmpty() {
		return projectedBlock.isEmpty();
	}

	@Override
	public boolean isUsableByPlayer(EntityPlayer arg0) {
		return true;
	}

	@Override
	public ItemStack removeStackFromSlot(int index) {
		ItemStack stack = projectedBlock;

		projectedBlock = ItemStack.EMPTY;
		resetSavedState();
		return stack;
	}

	@Override
	public void setInventorySlotContents(int index, ItemStack stack) {
		if (!stack.isEmpty() && stack.getCount() > getInventoryStackLimit())
			stack = new ItemStack(stack.getItem(), getInventoryStackLimit());

		ItemStack old = projectedBlock;

		projectedBlock = stack;

		if (old.getItem() != projectedBlock.getItem())
			resetSavedState();
	}

	@Override
	public boolean enableHack() {
		return true;
	}

	@Override
	public void openInventory(EntityPlayer player) {}

	@Override
	public void closeInventory(EntityPlayer player) {}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		return stack.getItem() instanceof ItemBlock;
	}

	@Override
	public int getField(int id) {
		return 0;
	}

	@Override
	public void setField(int id, int value) {}

	@Override
	public int getFieldCount() {
		return 0;
	}

	@Override
	public void onLoad() {
		super.onLoad();

		if (world.isRemote)
			BlockEntityRenderDelegate.PROJECTOR.putDelegateFor(this, projectedState, projectedBlock);
	}

	@Override
	public void invalidate() {
		super.invalidate();

		if (world.isRemote)
			BlockEntityRenderDelegate.PROJECTOR.removeDelegateOf(this);
	}

	public void setProjectedState(IBlockState projectedState) {
		if (world != null && world.isRemote) {
			if (this.projectedState.getBlock() != projectedState.getBlock())
				BlockEntityRenderDelegate.PROJECTOR.removeDelegateOf(this);

			BlockEntityRenderDelegate.PROJECTOR.putDelegateFor(this, projectedState, projectedBlock);
		}

		this.projectedState = projectedState;
		markDirty();
	}

	public void resetSavedState() {
		if (projectedBlock.getItem() instanceof ItemBlock)
			setProjectedState(((ItemBlock) projectedBlock.getItem()).getBlock().getStateFromMeta(projectedBlock.getMetadata()));
		else {
			projectedState = Blocks.AIR.getDefaultState();

			if (world != null && world.isRemote)
				BlockEntityRenderDelegate.PROJECTOR.removeDelegateOf(this);

			markDirty();
		}
	}
}
