package net.geforcemods.securitycraft.blockentities;

import net.geforcemods.securitycraft.ClientHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.ILockable;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.inventory.ProjectorMenu;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.util.StandingOrWallType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.StandingAndWallBlockItem;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class ProjectorBlockEntity extends DisguisableBlockEntity implements Container, MenuProvider, ILockable {
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
	private BlockState projectedState = Blocks.AIR.defaultBlockState();

	public ProjectorBlockEntity(BlockPos pos, BlockState state) {
		super(SCContent.PROJECTOR_BLOCK_ENTITY.get(), pos, state);
	}

	@Override
	public AABB getRenderBoundingBox() {
		return new AABB(getBlockPos()).inflate(RENDER_DISTANCE);
	}

	@Override
	public void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);

		tag.putInt("width", projectionWidth);
		tag.putInt("height", projectionHeight);
		tag.putInt("range", projectionRange);
		tag.putInt("offset", projectionOffset);
		tag.putBoolean("active", active);
		tag.putBoolean("horizontal", horizontal);
		tag.putBoolean("overriding_blocks", overridingBlocks);
		tag.put("storedItem", projectedBlock.save(new CompoundTag()));
		tag.put("SavedState", NbtUtils.writeBlockState(projectedState));
	}

	@Override
	public void load(CompoundTag tag) {
		super.load(tag);

		projectionWidth = tag.getInt("width");
		projectionHeight = tag.getInt("height");
		projectionRange = tag.getInt("range");
		projectionOffset = tag.getInt("offset");
		activatedByRedstone = isModuleEnabled(ModuleType.REDSTONE);
		active = tag.getBoolean("active");
		horizontal = tag.getBoolean("horizontal");
		overridingBlocks = tag.getBoolean("overriding_blocks");
		projectedBlock = ItemStack.of(tag.getCompound("storedItem"));

		if (!tag.contains("SavedState"))
			resetSavedState();
		else
			setProjectedState(NbtUtils.readBlockState(level != null ? level.holderLookup(Registries.BLOCK) : BuiltInRegistries.BLOCK.asLookup(), tag.getCompound("SavedState")));
	}

	public int getProjectionWidth() {
		return projectionWidth;
	}

	public void setProjectionWidth(int width) {
		projectionWidth = width;
		setChanged();
	}

	public int getProjectionHeight() {
		return projectionHeight;
	}

	public void setProjectionHeight(int projectionHeight) {
		this.projectionHeight = projectionHeight;
		setChanged();
	}

	public int getProjectionRange() {
		return projectionRange;
	}

	public void setProjectionRange(int range) {
		projectionRange = range;
		setChanged();
	}

	public int getProjectionOffset() {
		return projectionOffset;
	}

	public void setProjectionOffset(int offset) {
		projectionOffset = offset;
		setChanged();
	}

	public boolean isActivatedByRedstone() {
		return activatedByRedstone;
	}

	public void setActivatedByRedstone(boolean redstone) {
		activatedByRedstone = redstone;
		setChanged();
	}

	public boolean isHorizontal() {
		return horizontal;
	}

	public void setHorizontal(boolean horizontal) {
		this.horizontal = horizontal;
		setChanged();
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
		setChanged();
	}

	public BlockState getProjectedState() {
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
	public AbstractContainerMenu createMenu(int windowId, Inventory inv, Player player) {
		return new ProjectorMenu(windowId, level, worldPosition, inv);
	}

	@Override
	public Component getDisplayName() {
		return super.getDisplayName();
	}

	@Override
	public void clearContent() {
		projectedBlock = ItemStack.EMPTY;
		resetSavedState();
	}

	@Override
	public ItemStack removeItem(int index, int count) {
		ItemStack stack = projectedBlock;

		if (count >= 1) {
			projectedBlock = ItemStack.EMPTY;
			resetSavedState();
			return stack;
		}

		return ItemStack.EMPTY;
	}

	@Override
	public int getContainerSize() {
		return ProjectorMenu.SIZE;
	}

	@Override
	public int getMaxStackSize() {
		return 1;
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		return slot >= 100 ? getModuleInSlot(slot) : (slot == 36 ? projectedBlock : ItemStack.EMPTY);
	}

	@Override
	public ItemStack getItem(int slot) {
		return getStackInSlot(slot);
	}

	@Override
	public boolean isEmpty() {
		return projectedBlock.isEmpty();
	}

	@Override
	public boolean stillValid(Player player) {
		return true;
	}

	@Override
	public ItemStack removeItemNoUpdate(int index) {
		ItemStack stack = projectedBlock;

		projectedBlock = ItemStack.EMPTY;
		resetSavedState();
		return stack;
	}

	@Override
	public void setItem(int index, ItemStack stack) {
		if (!stack.isEmpty() && stack.getCount() > getMaxStackSize())
			stack = new ItemStack(stack.getItem(), getMaxStackSize());

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
	public void onLoad() {
		super.onLoad();

		if (level.isClientSide)
			ClientHandler.PROJECTOR_RENDER_DELEGATE.putDelegateFor(this, projectedState, projectedBlock);
	}

	@Override
	public void setRemoved() {
		super.setRemoved();

		if (level.isClientSide)
			ClientHandler.PROJECTOR_RENDER_DELEGATE.removeDelegateOf(this);
	}

	public void setProjectedState(BlockState projectedState) {
		if (level != null && level.isClientSide) {
			if (this.projectedState.getBlock() != projectedState.getBlock())
				ClientHandler.PROJECTOR_RENDER_DELEGATE.removeDelegateOf(this);

			ClientHandler.PROJECTOR_RENDER_DELEGATE.putDelegateFor(this, projectedState, projectedBlock);
		}

		this.projectedState = projectedState;
		setChanged();
	}

	public void resetSavedState() {
		if (projectedBlock.getItem() instanceof BlockItem blockItem)
			setProjectedState(blockItem.getBlock().defaultBlockState());
		else {
			projectedState = Blocks.AIR.defaultBlockState();

			if (level != null && level.isClientSide)
				ClientHandler.PROJECTOR_RENDER_DELEGATE.removeDelegateOf(this);

			setChanged();
		}
	}

	public StandingOrWallType getStandingOrWallType() {
		if (projectedState != null && projectedBlock != null && projectedBlock.getItem() instanceof StandingAndWallBlockItem sawbi) {
			if (projectedState.getBlock() == sawbi.getBlock())
				return StandingOrWallType.STANDING;
			else if (projectedState.getBlock() == sawbi.wallBlock)
				return StandingOrWallType.WALL;
		}

		return StandingOrWallType.NONE;
	}
}
