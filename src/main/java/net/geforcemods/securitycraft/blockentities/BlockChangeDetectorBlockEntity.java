package net.geforcemods.securitycraft.blockentities;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.ILockable;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.DisabledOption;
import net.geforcemods.securitycraft.api.Option.IgnoreOwnerOption;
import net.geforcemods.securitycraft.api.Option.IntOption;
import net.geforcemods.securitycraft.blocks.BlockChangeDetectorBlock;
import net.geforcemods.securitycraft.inventory.BlockChangeDetectorMenu;
import net.geforcemods.securitycraft.misc.BlockEntityTracker;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.util.Constants;

public class BlockChangeDetectorBlockEntity extends DisguisableBlockEntity implements IInventory, INamedContainerProvider, ILockable, ITickableTileEntity {
	private IntOption signalLength = new IntOption(this::getBlockPos, "signalLength", 60, 0, 400, 5); //20 seconds max
	private IntOption range = new IntOption(this::getBlockPos, "range", 5, 1, 15, 1);
	private DisabledOption disabled = new DisabledOption(false);
	private IgnoreOwnerOption ignoreOwner = new IgnoreOwnerOption(true);
	private DetectionMode mode = DetectionMode.BOTH;
	private boolean tracked = false;
	private List<ChangeEntry> entries = new ArrayList<>();
	private final List<ChangeEntry> filteredEntries = new ArrayList<>();
	private ItemStack filter = ItemStack.EMPTY;
	private boolean showHighlights = false;
	private int color = 0xFF0000FF;

	public BlockChangeDetectorBlockEntity() {
		super(SCContent.BLOCK_CHANGE_DETECTOR_BLOCK_ENTITY.get());
	}

	public void log(PlayerEntity player, DetectionMode action, BlockPos pos, BlockState state) {
		if (isDisabled())
			return;

		if (mode != DetectionMode.BOTH && action != mode)
			return;

		if ((isOwnedBy(player) && ignoresOwner()) || isAllowed(player))
			return;

		//don't detect self
		if (pos.equals(getBlockPos()))
			return;

		if (isModuleEnabled(ModuleType.SMART) && (filter.getItem() instanceof BlockItem && ((BlockItem) filter.getItem()).getBlock() != state.getBlock()))
			return;

		if (isModuleEnabled(ModuleType.REDSTONE)) {
			int signalLength = getSignalLength();

			level.setBlockAndUpdate(worldPosition, getBlockState().cycle(BlockChangeDetectorBlock.POWERED));
			BlockUtils.updateIndirectNeighbors(level, worldPosition, SCContent.BLOCK_CHANGE_DETECTOR.get());

			if (signalLength > 0)
				level.getBlockTicks().scheduleTick(worldPosition, SCContent.BLOCK_CHANGE_DETECTOR.get(), signalLength);
		}

		entries.add(new ChangeEntry(player.getDisplayName().getString(), player.getUUID(), System.currentTimeMillis(), action, pos, state));
		setChanged();
		level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 2);
	}

	@Override
	public void tick() {
		if (!tracked) {
			BlockEntityTracker.BLOCK_CHANGE_DETECTOR.track(this);
			tracked = true;
		}
	}

	@Override
	public CompoundNBT save(CompoundNBT tag) {
		super.save(tag);

		ListNBT entryList = new ListNBT();

		entries.stream().map(ChangeEntry::save).forEach(entryList::add);
		tag.putInt("mode", mode.ordinal());
		tag.put("entries", entryList);
		tag.put("filter", filter.save(new CompoundNBT()));
		tag.putBoolean("ShowHighlights", showHighlights);
		tag.putInt("Color", color);
		return tag;
	}

	@Override
	public void load(BlockState state, CompoundNBT tag) {
		super.load(state, tag);

		int modeOrdinal = tag.getInt("mode");

		if (modeOrdinal < 0 || modeOrdinal >= DetectionMode.values().length)
			modeOrdinal = 0;

		mode = DetectionMode.values()[modeOrdinal];
		entries = new ArrayList<>();
		tag.getList("entries", Constants.NBT.TAG_COMPOUND).stream().map(element -> ChangeEntry.load((CompoundNBT) element)).forEach(entries::add);
		filter = ItemStack.of(tag.getCompound("filter"));
		showHighlights = tag.getBoolean("ShowHighlights");
		setColor(tag.getInt("Color"));
		updateFilteredEntries();
	}

	@Override
	public <T> void onOptionChanged(Option<T> option) {
		if (option == signalLength) {
			level.setBlockAndUpdate(worldPosition, getBlockState().setValue(BlockStateProperties.POWERED, false));
			BlockUtils.updateIndirectNeighbors(level, worldPosition, getBlockState().getBlock());
		}
	}

	@Override
	public void setRemoved() {
		super.setRemoved();
		BlockEntityTracker.BLOCK_CHANGE_DETECTOR.stopTracking(this);
	}

	@Override
	public Container createMenu(int id, PlayerInventory inventory, PlayerEntity player) {
		return new BlockChangeDetectorMenu(id, level, worldPosition, inventory);
	}

	@Override
	public ITextComponent getDisplayName() {
		return super.getDisplayName();
	}

	public void setMode(DetectionMode mode) {
		this.mode = mode;

		if (!level.isClientSide)
			setChanged();
	}

	public DetectionMode getMode() {
		return mode;
	}

	public int getSignalLength() {
		return signalLength.get();
	}

	public int getRange() {
		return range.get();
	}

	public boolean isDisabled() {
		return disabled.get();
	}

	@Override
	public boolean ignoresOwner() {
		return ignoreOwner.get();
	}

	public List<ChangeEntry> getEntries() {
		return entries;
	}

	public List<ChangeEntry> getFilteredEntries() {
		return filteredEntries;
	}

	public void updateFilteredEntries() {
		filteredEntries.clear();
		entries.stream().filter(this::isEntryShown).forEach(filteredEntries::add);
	}

	public boolean isEntryShown(ChangeEntry entry) {
		DetectionMode currentMode = getMode();

		return (currentMode == DetectionMode.BOTH || currentMode == entry.action) && (filter.isEmpty() || ((BlockItem) filter.getItem()).getBlock() == entry.state.getBlock());
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
	public void clearContent() {
		filter = ItemStack.EMPTY;
		setChanged();
	}

	@Override
	public int getContainerSize() {
		return 1;
	}

	@Override
	public int getMaxStackSize() {
		return 1;
	}

	@Override
	public boolean isEmpty() {
		return filter.isEmpty();
	}

	@Override
	public ItemStack getItem(int index) {
		return getStackInSlot(index);
	}

	@Override
	public ItemStack removeItem(int index, int count) {
		ItemStack stack = filter;

		if (count >= 1) {
			filter = ItemStack.EMPTY;
			setChanged();
			return stack;
		}

		return ItemStack.EMPTY;
	}

	@Override
	public ItemStack removeItemNoUpdate(int index) {
		ItemStack stack = filter;

		filter = ItemStack.EMPTY;
		setChanged();
		return stack;
	}

	@Override
	public void setItem(int index, ItemStack stack) {
		if (stack.getItem() instanceof BlockItem) {
			if (!stack.isEmpty() && stack.getCount() > getMaxStackSize())
				stack = new ItemStack(stack.getItem(), getMaxStackSize());

			filter = stack;
			setChanged();
		}
	}

	@Override
	public boolean stillValid(PlayerEntity player) {
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

	public enum DetectionMode {
		BREAK("gui.securitycraft:block_change_detector.mode.break"),
		PLACE("gui.securitycraft:block_change_detector.mode.place"),
		BOTH("gui.securitycraft:block_change_detector.mode.both");

		private String descriptionId;

		private DetectionMode(String desciptionId) {
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
		public final DetectionMode action;
		public final BlockPos pos;
		public final BlockState state;

		public ChangeEntry(String player, UUID uuid, long timestamp, DetectionMode action, BlockPos pos, BlockState state) {
			this.player = player;
			this.uuid = uuid;
			this.timestamp = timestamp;
			this.action = action;
			this.pos = pos;
			this.state = state;
		}

		public CompoundNBT save() {
			CompoundNBT tag = new CompoundNBT();

			tag.putString("player", player);
			tag.putUUID("uuid", uuid);
			tag.putLong("timestamp", timestamp);
			tag.putInt("action", action.ordinal());
			tag.putLong("pos", pos.asLong());
			tag.put("state", NBTUtil.writeBlockState(state));
			return tag;
		}

		public static ChangeEntry load(CompoundNBT tag) {
			int actionOrdinal = tag.getInt("action");

			if (actionOrdinal < 0 || actionOrdinal >= DetectionMode.values().length)
				actionOrdinal = 0;

			//@formatter:off
			return new ChangeEntry(
					tag.getString("player"),
					tag.getUUID("uuid"),
					tag.getLong("timestamp"),
					DetectionMode.values()[actionOrdinal],
					BlockPos.of(tag.getLong("pos")),
					NBTUtil.readBlockState(tag.getCompound("state")));
			//@formatter:on
		}
	}
}
