package net.geforcemods.securitycraft.blockentities;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.geforcemods.securitycraft.api.ILockable;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.DisabledOption;
import net.geforcemods.securitycraft.api.Option.IgnoreOwnerOption;
import net.geforcemods.securitycraft.api.Option.IntOption;
import net.geforcemods.securitycraft.blocks.BlockChangeDetectorBlock;
import net.geforcemods.securitycraft.misc.BlockEntityTracker;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.util.Constants;

public class BlockChangeDetectorBlockEntity extends DisguisableBlockEntity implements IInventory, ILockable, ITickable {
	private IntOption signalLength = new IntOption(this::getPos, "signalLength", 60, 0, 400, 5); //20 seconds max
	private IntOption range = new IntOption(this::getPos, "range", 5, 1, 15, 1);
	private DisabledOption disabled = new DisabledOption(false);
	private IgnoreOwnerOption ignoreOwner = new IgnoreOwnerOption(true);
	private EnumDetectionMode mode = EnumDetectionMode.BOTH;
	private boolean tracked = false;
	private List<ChangeEntry> entries = new ArrayList<>();
	private final List<ChangeEntry> filteredEntries = new ArrayList<>();
	private ItemStack filter = ItemStack.EMPTY;
	private boolean showHighlights = false;
	private int color = 0xFF0000FF;

	public void log(EntityPlayer player, EnumDetectionMode action, BlockPos changedPos, IBlockState state) {
		if (isDisabled())
			return;

		if (mode != EnumDetectionMode.BOTH && action != mode)
			return;

		if ((isOwnedBy(player) && ignoresOwner()) || isAllowed(player))
			return;

		BlockPos thisPos = getPos();

		//don't detect self
		if (changedPos.equals(thisPos))
			return;

		if (isModuleEnabled(ModuleType.SMART) && filter.getItem() instanceof ItemBlock) {
			if (((ItemBlock) filter.getItem()).getBlock() == state.getBlock()) {
				//only return if the filter block's metadata is different from the metadata of the block that was placed
				if (filter.getMetadata() != state.getBlock().getMetaFromState(state))
					return;
			}
			else
				return;
		}

		IBlockState thisState = world.getBlockState(thisPos);

		if (isModuleEnabled(ModuleType.REDSTONE)) {
			int signalLength = getSignalLength();

			world.setBlockState(thisPos, thisState.cycleProperty(BlockChangeDetectorBlock.POWERED));
			BlockUtils.updateIndirectNeighbors(world, thisPos, getBlockType());

			if (signalLength > 0)
				world.scheduleUpdate(thisPos, getBlockType(), signalLength);
		}

		entries.add(new ChangeEntry(player.getName(), player.getGameProfile().getId(), System.currentTimeMillis(), action, changedPos, state));
		markDirty();
		sync();
	}

	@Override
	public void update() {
		if (!tracked) {
			BlockEntityTracker.BLOCK_CHANGE_DETECTOR.track(this);
			tracked = true;
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);

		NBTTagList entryList = new NBTTagList();

		entries.stream().map(ChangeEntry::save).forEach(entryList::appendTag);
		tag.setInteger("mode", mode.ordinal());
		tag.setTag("entries", entryList);
		tag.setTag("filter", filter.writeToNBT(new NBTTagCompound()));
		tag.setBoolean("ShowHighlights", showHighlights);
		tag.setInteger("Color", color);
		return tag;
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);

		int modeOrdinal = tag.getInteger("mode");

		if (modeOrdinal < 0 || modeOrdinal >= EnumDetectionMode.values().length)
			modeOrdinal = 0;

		mode = EnumDetectionMode.values()[modeOrdinal];
		entries = new ArrayList<>();
		tag.getTagList("entries", Constants.NBT.TAG_COMPOUND).forEach(element -> entries.add(ChangeEntry.load((NBTTagCompound) element)));
		filter = new ItemStack(tag.getCompoundTag("filter"));
		showHighlights = tag.getBoolean("ShowHighlights");
		setColor(tag.getInteger("Color"));
		updateFilteredEntries();
	}

	@Override
	public <T> void onOptionChanged(Option<T> option) {
		if (option == signalLength) {
			world.setBlockState(pos, world.getBlockState(pos).withProperty(BlockChangeDetectorBlock.POWERED, false));
			BlockUtils.updateIndirectNeighbors(world, pos, getBlockType(), ((BlockChangeDetectorBlock) getBlockType()).getConnectedDirection(world.getBlockState(pos)).getOpposite());
		}
	}

	@Override
	public void invalidate() {
		super.invalidate();
		BlockEntityTracker.BLOCK_CHANGE_DETECTOR.stopTracking(this);
	}

	@Override
	public ITextComponent getDisplayName() {
		return super.getDisplayName();
	}

	public void setMode(EnumDetectionMode mode) {
		this.mode = mode;

		if (!world.isRemote)
			markDirty();
	}

	public EnumDetectionMode getMode() {
		return mode;
	}

	public int getSignalLength() {
		return signalLength.get();
	}

	public int getRange() {
		return range.get();
	}

	public List<ChangeEntry> getEntries() {
		return entries;
	}

	public boolean isDisabled() {
		return disabled.get();
	}

	@Override
	public boolean ignoresOwner() {
		return ignoreOwner.get();
	}

	public List<ChangeEntry> getFilteredEntries() {
		return filteredEntries;
	}

	public void updateFilteredEntries() {
		filteredEntries.clear();
		entries.stream().filter(this::isEntryShown).forEach(filteredEntries::add);
	}

	public boolean isEntryShown(ChangeEntry entry) {
		EnumDetectionMode currentMode = getMode();

		if (currentMode == EnumDetectionMode.BOTH || currentMode == entry.action) {
			if (filter.getItem() instanceof ItemBlock) {
				Block block = entry.state.getBlock();

				return ((ItemBlock) filter.getItem()).getBlock() == block && block.getMetaFromState(entry.state) == filter.getMetadata();
			}

			return true;
		}

		return false;
	}

	@Override
	public ModuleType[] acceptedModules() {
		return new ModuleType[] {
				ModuleType.DISGUISE, ModuleType.ALLOWLIST, ModuleType.SMART, ModuleType.REDSTONE
		};
	}

	@Override
	public Option<?>[] customOptions() {
		return new Option[] {
				signalLength, range, disabled, ignoreOwner
		};
	}

	@Override
	public void clear() {
		filter = ItemStack.EMPTY;
		markDirty();
	}

	@Override
	public int getSizeInventory() {
		return 1;
	}

	@Override
	public int getInventoryStackLimit() {
		return 1;
	}

	@Override
	public boolean isEmpty() {
		return filter.isEmpty();
	}

	@Override
	public ItemStack decrStackSize(int index, int count) {
		ItemStack stack = filter;

		if (count >= 1) {
			filter = ItemStack.EMPTY;
			markDirty();
			return stack;
		}

		return ItemStack.EMPTY;
	}

	@Override
	public ItemStack removeStackFromSlot(int index) {
		ItemStack stack = filter;

		filter = ItemStack.EMPTY;
		markDirty();
		return stack;
	}

	@Override
	public void setInventorySlotContents(int index, ItemStack stack) {
		if (stack.getItem() instanceof ItemBlock) {
			if (!stack.isEmpty() && stack.getCount() > getInventoryStackLimit())
				stack = new ItemStack(stack.getItem(), getInventoryStackLimit());

			filter = stack;
			markDirty();
		}
	}

	@Override
	public boolean isUsableByPlayer(EntityPlayer player) {
		return true;
	}

	@Override
	public boolean enableHack() {
		return true;
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		return slot >= 100 ? getModuleInSlot(slot) : (slot == 36 ? filter : ItemStack.EMPTY);
	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		return isModuleEnabled(ModuleType.SMART) && stack.getItem() instanceof ItemBlock;
	}

	public void showHighlights(boolean showHighlights) {
		this.showHighlights = showHighlights;
	}

	public boolean isShowingHighlights() {
		return showHighlights;
	}

	public void setColor(int color) {
		this.color = MathHelper.clamp(color, 0xFF000000, 0xFFFFFFFF);
	}

	public int getColor() {
		return color;
	}

	public enum EnumDetectionMode {
		BREAK("gui.securitycraft:block_change_detector.mode.break"),
		PLACE("gui.securitycraft:block_change_detector.mode.place"),
		BOTH("gui.securitycraft:block_change_detector.mode.both");

		private String descriptionId;

		private EnumDetectionMode(String desciptionId) {
			this.descriptionId = desciptionId;
		}

		public String getDescriptionId() {
			return descriptionId;
		}
	}

	public static class ChangeEntry {
		public final String player;
		public final UUID uuid;
		public final long timestamp;
		public final EnumDetectionMode action;
		public final BlockPos pos;
		public final IBlockState state;

		public ChangeEntry(String player, UUID uuid, long timestamp, EnumDetectionMode action, BlockPos pos, IBlockState state) {
			this.player = player;
			this.uuid = uuid;
			this.timestamp = timestamp;
			this.action = action;
			this.pos = pos;
			this.state = state;
		}

		public NBTTagCompound save() {
			NBTTagCompound tag = new NBTTagCompound();

			tag.setString("player", player);
			tag.setUniqueId("uuid", uuid);
			tag.setLong("timestamp", timestamp);
			tag.setInteger("action", action.ordinal());
			tag.setLong("pos", pos.toLong());
			tag.setTag("state", NBTUtil.writeBlockState(new NBTTagCompound(), state));
			return tag;
		}

		public static ChangeEntry load(NBTTagCompound tag) {
			int actionOrdinal = tag.getInteger("action");

			if (actionOrdinal < 0 || actionOrdinal >= EnumDetectionMode.values().length)
				actionOrdinal = 0;

			//@formatter:off
			return new ChangeEntry(
					tag.getString("player"),
					tag.getUniqueId("uuid"),
					tag.getLong("timestamp"),
					EnumDetectionMode.values()[actionOrdinal],
					BlockPos.fromLong(tag.getLong("pos")),
					NBTUtil.readBlockState(tag.getCompoundTag("state")));
			//@formatter:on
		}
	}

	@Override
	public void openInventory(EntityPlayer player) {}

	@Override
	public void closeInventory(EntityPlayer player) {}

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
}
