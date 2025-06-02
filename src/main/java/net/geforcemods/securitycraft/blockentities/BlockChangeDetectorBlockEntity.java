package net.geforcemods.securitycraft.blockentities;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.ILockable;
import net.geforcemods.securitycraft.api.IModuleInventoryWithContainer;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.DisabledOption;
import net.geforcemods.securitycraft.api.Option.IgnoreOwnerOption;
import net.geforcemods.securitycraft.api.Option.IntOption;
import net.geforcemods.securitycraft.blocks.AbstractPanelBlock;
import net.geforcemods.securitycraft.blocks.BlockChangeDetectorBlock;
import net.geforcemods.securitycraft.inventory.BlockChangeDetectorMenu;
import net.geforcemods.securitycraft.misc.BlockEntityTracker;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.ITickingBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.level.storage.ValueOutput.TypedOutputList;

public class BlockChangeDetectorBlockEntity extends DisguisableBlockEntity implements IModuleInventoryWithContainer, MenuProvider, ILockable, ITickingBlockEntity {
	private IntOption signalLength = new IntOption("signalLength", 60, 0, 400, 5); //20 seconds max
	private IntOption range = new IntOption("range", 5, 1, 15, 1);
	private DisabledOption disabled = new DisabledOption(false);
	private IgnoreOwnerOption ignoreOwner = new IgnoreOwnerOption(true);
	private DetectionMode mode = DetectionMode.BOTH;
	private boolean tracked = false;
	private List<ChangeEntry> entries = new ArrayList<>();
	private final List<ChangeEntry> filteredEntries = new ArrayList<>();
	private ItemStack filter = ItemStack.EMPTY;
	private boolean showHighlights = false;
	private int color = 0xFF0000FF;

	public BlockChangeDetectorBlockEntity(BlockPos pos, BlockState state) {
		super(SCContent.BLOCK_CHANGE_DETECTOR_BLOCK_ENTITY.get(), pos, state);
	}

	public void log(Player player, DetectionMode action, BlockPos pos, BlockState state) {
		if (isDisabled())
			return;

		if (mode != DetectionMode.BOTH && action != mode)
			return;

		if ((isOwnedBy(player) && ignoresOwner()) || isAllowed(player))
			return;

		//don't detect self
		if (pos.equals(getBlockPos()))
			return;

		if (isModuleEnabled(ModuleType.SMART) && (filter.getItem() instanceof BlockItem item && item.getBlock() != state.getBlock()))
			return;

		if (isModuleEnabled(ModuleType.REDSTONE)) {
			int signalLength = getSignalLength();

			level.setBlockAndUpdate(worldPosition, getBlockState().cycle(BlockChangeDetectorBlock.POWERED));
			BlockUtils.updateIndirectNeighbors(level, worldPosition, SCContent.BLOCK_CHANGE_DETECTOR.get(), AbstractPanelBlock.getConnectedDirection(getBlockState()).getOpposite());

			if (signalLength > 0)
				level.scheduleTick(worldPosition, SCContent.BLOCK_CHANGE_DETECTOR.get(), signalLength);
		}

		entries.add(new ChangeEntry(player.getDisplayName().getString(), player.getUUID(), System.currentTimeMillis(), action, pos, state));
		setChanged();
		level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 2);
	}

	@Override
	public void tick(Level level, BlockPos pos, BlockState state) {
		if (!tracked) {
			BlockEntityTracker.BLOCK_CHANGE_DETECTOR.track(this);
			tracked = true;
		}
	}

	@Override
	public void saveAdditional(ValueOutput tag) {
		super.saveAdditional(tag);

		//TODO: does list saving and loading work with and the same as old data?
		TypedOutputList<ChangeEntry> entryList = tag.list("entries", ChangeEntry.CODEC);

		entries.forEach(entryList::add);
		tag.putInt("mode", mode.ordinal());

		if (!filter.isEmpty())
			tag.store("filter", ItemStack.CODEC, filter);

		tag.putBoolean("ShowHighlights", showHighlights);
		tag.putInt("Color", color);
	}

	@Override
	public void loadAdditional(ValueInput tag) {
		super.loadAdditional(tag);

		int modeOrdinal = tag.getIntOr("mode", 2);

		if (modeOrdinal < 0 || modeOrdinal >= DetectionMode.values().length)
			modeOrdinal = 0;

		mode = DetectionMode.values()[modeOrdinal];
		entries = new ArrayList<>();
		tag.listOrEmpty("entries", ChangeEntry.CODEC).forEach(entries::add);
		tag.read("filter", ItemStack.CODEC).orElse(ItemStack.EMPTY);
		showHighlights = tag.getBooleanOr("ShowHighlights", false);
		setColor(tag.getIntOr("Color", 0xFF0000FF));
		updateFilteredEntries();
	}

	@Override
	public void preRemoveSideEffects(BlockPos pos, BlockState state) {
		if (level != null)
			Block.popResource(level, pos, getStackInSlot(36));

		super.preRemoveSideEffects(pos, state);
	}

	@Override
	public <T> void onOptionChanged(Option<T> option) {
		if (option == signalLength) {
			level.setBlockAndUpdate(worldPosition, getBlockState().setValue(BlockStateProperties.POWERED, false));
			BlockUtils.updateIndirectNeighbors(level, worldPosition, getBlockState().getBlock());
		}

		super.onOptionChanged(option);
	}

	@Override
	public void setRemoved() {
		super.setRemoved();
		BlockEntityTracker.BLOCK_CHANGE_DETECTOR.stopTracking(this);
	}

	@Override
	public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
		return new BlockChangeDetectorMenu(id, level, worldPosition, inventory);
	}

	@Override
	public Component getDisplayName() {
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

		return (currentMode == DetectionMode.BOTH || currentMode == entry.action()) && (filter.isEmpty() || ((BlockItem) filter.getItem()).getBlock() == entry.state.getBlock());
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
	public int getContainerSize() {
		return 1;
	}

	@Override
	public int getContainerStackSize() {
		return 1;
	}

	@Override
	public boolean isContainerEmpty() {
		return filter.isEmpty();
	}

	@Override
	public ItemStack removeContainerItem(int index, int count, boolean simulate) {
		ItemStack stack = filter;

		if (count >= 1) {
			if (simulate) {
				filter = ItemStack.EMPTY;
				setChanged();
			}

			return stack.copy();
		}

		return ItemStack.EMPTY;
	}

	@Override
	public void setContainerItem(int index, ItemStack stack) {
		if (stack.getItem() instanceof BlockItem) {
			if (!stack.isEmpty() && stack.getCount() > getContainerStackSize())
				stack = new ItemStack(stack.getItem(), getContainerStackSize());

			filter = stack;
			setChanged();
		}
	}

	@Override
	public void writeClientSideData(AbstractContainerMenu menu, RegistryFriendlyByteBuf buffer) {
		MenuProvider.super.writeClientSideData(menu, buffer);
		buffer.writeBlockPos(worldPosition);
	}

	@Override
	public boolean enableHack() {
		return true;
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		if (!isContainer(slot))
			return getModuleInSlot(slot);
		else
			return slot == 36 ? filter : ItemStack.EMPTY;
	}

	public void showHighlights(boolean showHighlights) {
		this.showHighlights = showHighlights;
	}

	public boolean isShowingHighlights() {
		return showHighlights;
	}

	public void setColor(int color) {
		this.color = Mth.clamp(color, 0xFF000000, 0xFFFFFFFF);
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

	public static record ChangeEntry(String player, UUID uuid, long timestamp, DetectionMode action, BlockPos pos, BlockState state) {
		//@formatter:off
		public static final Codec<ChangeEntry> CODEC = RecordCodecBuilder.create(i -> i.group(
						Codec.STRING.fieldOf("player").forGetter(ChangeEntry::player),
						UUIDUtil.CODEC.fieldOf("uuid").forGetter(ChangeEntry::uuid),
						Codec.LONG.fieldOf("timestamp").forGetter(ChangeEntry::timestamp),
						Codec.INT.fieldOf("action").xmap(ordinal -> DetectionMode.values()[ordinal], DetectionMode::ordinal).forGetter(ChangeEntry::action),
						Codec.LONG.fieldOf("pos").xmap(BlockPos::of, BlockPos::asLong).forGetter(ChangeEntry::pos),
						BlockState.CODEC.fieldOf("state").forGetter(ChangeEntry::state)) //TODO: is it correct to use the codec instead of NbtUtils#write/readBlockState?
			.apply(i, ChangeEntry::new));
		//@formatter:on
	}

	@Override
	public boolean isItemValidForContainer(int slot, ItemStack stack) {
		return true;
	}

	@Override
	public ItemStack getStackInContainer(int slot) {
		return filter;
	}
}
