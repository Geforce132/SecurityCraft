package net.geforcemods.securitycraft.tileentity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.ILockable;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.OptionInt;
import net.geforcemods.securitycraft.blocks.BlockBlockChangeDetector;
import net.geforcemods.securitycraft.misc.EnumModuleType;
import net.geforcemods.securitycraft.misc.TileEntityTracker;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.ModuleUtils;
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
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.util.Constants;

public class TileEntityBlockChangeDetector extends TileEntityDisguisable implements IInventory, ILockable, ITickable {
	private OptionInt signalLength = new OptionInt(this::getPos, "signalLength", 60, 5, 400, 5, true); //20 seconds max
	private OptionInt range = new OptionInt(this::getPos, "range", 5, 1, 15, 1, true);
	private EnumDetectionMode mode = EnumDetectionMode.BOTH;
	private boolean tracked = false;
	private List<ChangeEntry> entries = new ArrayList<>();
	private ItemStack filter = ItemStack.EMPTY;

	public void log(EntityPlayer player, EnumDetectionMode action, BlockPos pos, IBlockState state) {
		if (mode != EnumDetectionMode.BOTH && action != mode)
			return;

		if (getOwner().isOwner(player) || ModuleUtils.isAllowed(this, player))
			return;

		//don't detect self
		if (pos.equals(getPos()))
			return;

		if (hasModule(EnumModuleType.SMART) && (filter.getItem() instanceof ItemBlock && ((ItemBlock) filter.getItem()).getBlock() != state.getBlock()))
			return;

		IBlockState thisState = world.getBlockState(pos);

		if (hasModule(EnumModuleType.REDSTONE)) {
			world.setBlockState(this.pos, thisState.withProperty(BlockBlockChangeDetector.POWERED, true));
			BlockUtils.updateIndirectNeighbors(world, this.pos, SCContent.blockChangeDetector);
			world.scheduleUpdate(this.pos, SCContent.blockChangeDetector, signalLength.get());
		}

		entries.add(new ChangeEntry(player.getName(), player.getGameProfile().getId(), System.currentTimeMillis(), action, pos, state));
		markDirty();
		sync();
	}

	@Override
	public void update() {
		if (!tracked) {
			TileEntityTracker.BLOCK_CHANGE_DETECTOR.track(this);
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
	}

	@Override
	public void invalidate() {
		super.invalidate();
		TileEntityTracker.BLOCK_CHANGE_DETECTOR.stopTracking(this);
	}

	@Override
	public ITextComponent getDisplayName() {
		return super.getDisplayName();
	}

	public void setMode(EnumDetectionMode mode) {
		this.mode = mode;
		markDirty();
	}

	public EnumDetectionMode getMode() {
		return mode;
	}

	public int getRange() {
		return range.get();
	}

	public List<ChangeEntry> getEntries() {
		return entries;
	}

	@Override
	public EnumModuleType[] acceptedModules() {
		return new EnumModuleType[] {
				EnumModuleType.DISGUISE, EnumModuleType.ALLOWLIST, EnumModuleType.SMART, EnumModuleType.REDSTONE
		};
	}

	@Override
	public Option<?>[] customOptions() {
		return new Option[] {
				signalLength, range
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
		return hasModule(EnumModuleType.SMART) && stack.getItem() instanceof ItemBlock;
	}

	public static enum EnumDetectionMode {
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
