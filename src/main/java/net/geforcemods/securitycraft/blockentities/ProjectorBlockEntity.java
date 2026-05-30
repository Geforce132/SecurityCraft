package net.geforcemods.securitycraft.blockentities;

import net.geforcemods.securitycraft.ClientHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.ILockable;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.blocks.ProjectorBlock;
import net.geforcemods.securitycraft.inventory.ProjectorMenu;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.util.StandingOrWallType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.StandingAndWallBlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;

public class ProjectorBlockEntity extends DisguisableBlockEntity implements Container, MenuProvider, ILockable {
	public static final int MIN_WIDTH = 1; //also for height
	public static final int MAX_WIDTH = 10; //also for height
	public static final int MIN_RANGE = 1;
	public static final int MAX_RANGE = 30;
	public static final int MIN_OFFSET = -10;
	public static final int MAX_OFFSET = 10;
	private AABB projectedBlocksArea;
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
	public void saveAdditional(ValueOutput tag) {
		super.saveAdditional(tag);

		tag.putInt("width", projectionWidth);
		tag.putInt("height", projectionHeight);
		tag.putInt("range", projectionRange);
		tag.putInt("offset", projectionOffset);
		tag.putBoolean("active", active);
		tag.putBoolean("horizontal", horizontal);
		tag.putBoolean("overriding_blocks", overridingBlocks);

		if (!projectedBlock.isEmpty())
			tag.store("storedItem", ItemStack.CODEC, projectedBlock);

		tag.store("SavedState", BlockState.CODEC, projectedState);
	}

	@Override
	public void loadAdditional(ValueInput tag) {
		super.loadAdditional(tag);

		projectionWidth = tag.getIntOr("width", 1);
		projectionHeight = tag.getIntOr("height", 1);
		projectionRange = tag.getIntOr("range", 5);
		projectionOffset = tag.getIntOr("offset", 0);
		activatedByRedstone = isModuleEnabled(ModuleType.REDSTONE);
		active = tag.getBooleanOr("active", false);
		horizontal = tag.getBooleanOr("horizontal", false);
		overridingBlocks = tag.getBooleanOr("overriding_blocks", false);
		projectedBlock = tag.read("storedItem", ItemStack.CODEC).orElse(ItemStack.EMPTY);
		tag.read("SavedState", BlockState.CODEC).ifPresentOrElse(this::setProjectedState, this::resetSavedState);
	}

	@Override
	public void preRemoveSideEffects(BlockPos pos, BlockState state) {
		if (level != null)
			Block.popResource(level, pos, projectedBlock);

		super.preRemoveSideEffects(pos, state);
	}

	@Override
	public void setChanged() {
		super.setChanged();
		projectedBlocksArea = calculateProjectedBlockArea();
	}

	public AABB calculateProjectedBlockArea() {
		BlockState state = getBlockState();
		boolean hanging = state.getValue(ProjectorBlock.HANGING);
		Direction direction = state.getValue(ProjectorBlock.FACING);
		int projectionHeight = getProjectionHeight();
		int projectionOffset = getProjectionOffset();
		int projectionWidth = projectionOffset + getProjectionWidth();
		int minRange = getProjectionRange();
		int maxRange = minRange + 1;
		int minHeight = hanging ? 1 : 0;
		int maxHeight = minHeight + (hanging ? -projectionHeight : projectionHeight);

		if (isHorizontal()) {
			int oldMinHeight = minHeight;

			maxRange = maxHeight + 1;
			minHeight = minRange - 16;
			maxHeight = minHeight + 1;
			minRange = oldMinHeight + 1;
		}

		return switch (direction) {
			case NORTH -> new AABB(projectionOffset, minHeight, minRange, projectionWidth, maxHeight, maxRange);
			case SOUTH -> new AABB(projectionOffset, minHeight, -minRange + 1, projectionWidth, maxHeight, -maxRange + 1);
			case WEST -> new AABB(minRange, minHeight, projectionOffset, maxRange, maxHeight, projectionWidth);
			case EAST -> new AABB(-minRange + 1, minHeight, projectionOffset, -maxRange + 1, maxHeight, projectionWidth);
			default -> null;
		};
	}

	public AABB getProjectedBlocksArea() {
		if (projectedBlocksArea == null && !isEmpty())
			projectedBlocksArea = calculateProjectedBlockArea();

		return projectedBlocksArea;
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
	public void writeClientSideData(AbstractContainerMenu menu, RegistryFriendlyByteBuf buffer) {
		MenuProvider.super.writeClientSideData(menu, buffer);
		buffer.writeBlockPos(worldPosition);
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
	public ItemStack removeItemNoUpdate(int index) {
		ItemStack stack = projectedBlock;

		projectedBlock = ItemStack.EMPTY;
		resetSavedState();
		return stack;
	}

	@Override
	public void clearContent() {
		projectedBlock = ItemStack.EMPTY;
		resetSavedState();
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
	public ItemStack getItem(int slot) {
		return slot == 36 ? projectedBlock : ItemStack.EMPTY;
	}

	@Override
	public void setItem(int index, ItemStack stack) {
		if (!stack.isEmpty() && stack.getCount() > getMaxStackSize())
			stack = new ItemStack(stack.getItem(), getMaxStackSize());

		ItemStack old = projectedBlock;

		projectedBlock = stack;

		if (old.getItem() != projectedBlock.getItem())
			resetSavedState();

		if (level != null && !level.isClientSide())
			level.sendBlockUpdated(worldPosition, blockState, blockState, 2);
	}

	@Override
	public void onLoad() {
		super.onLoad();

		if (level.isClientSide())
			ClientHandler.PROJECTOR_RENDER_DELEGATE.putDelegateFor(this, projectedState, projectedBlock);
	}

	@Override
	public void setRemoved() {
		super.setRemoved();

		if (level.isClientSide())
			ClientHandler.PROJECTOR_RENDER_DELEGATE.removeDelegateOf(this);
	}

	public void setProjectedState(BlockState projectedState) {
		if (level != null && level.isClientSide()) {
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

			if (level != null && level.isClientSide())
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

	@Override
	public boolean stillValid(Player player) {
		return true;
	}

	@Override
	public boolean isEmpty() {
		return projectedBlock.isEmpty();
	}
}
