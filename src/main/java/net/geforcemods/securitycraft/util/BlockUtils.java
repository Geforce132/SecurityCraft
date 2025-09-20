package net.geforcemods.securitycraft.util;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.api.IDoorActivator;
import net.geforcemods.securitycraft.api.IExtractionBlock;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.IReinforcedBlock;
import net.geforcemods.securitycraft.api.SecurityCraftAPI;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BundleContents;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.Level.ExplosionInteraction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;

public class BlockUtils {
	private BlockUtils() {}

	public static boolean isSideSolid(LevelReader level, BlockPos pos, Direction side) {
		return level.getBlockState(pos).isFaceSturdy(level, pos, side);
	}

	public static ExplosionInteraction getExplosionInteraction() {
		return ConfigHandler.SERVER.mineExplosionsBreakBlocks.get() ? ExplosionInteraction.BLOCK : ExplosionInteraction.NONE;
	}

	public static boolean hasActiveSCBlockNextTo(Level level, BlockPos pos) {
		BlockEntity be = level.getBlockEntity(pos);

		return SecurityCraftAPI.getRegisteredDoorActivators().stream().anyMatch(activator -> hasActiveSCBlockNextTo(level, pos, be, activator));
	}

	private static boolean hasActiveSCBlockNextTo(Level level, BlockPos pos, BlockEntity be, IDoorActivator activator) {
		for (Direction dir : Direction.values()) {
			BlockPos offsetPos = pos.relative(dir);
			BlockState offsetState = level.getBlockState(offsetPos);

			if (activator.getBlocks().contains(offsetState.getBlock())) {
				BlockEntity offsetBe = level.getBlockEntity(offsetPos);

				if (activator.isPowering(level, offsetPos, offsetState, offsetBe, dir, 1) && (!(offsetBe instanceof IOwnable ownable) || ownable.getOwner().owns((IOwnable) be)))
					return true;
			}

			if (level.getSignal(offsetPos, dir) == 15 && !offsetState.isSignalSource()) {
				for (Direction dirOffset : Direction.values()) {
					//skip this, as it would just go back to the original position
					if (dirOffset.getOpposite() == dir)
						continue;

					BlockPos newOffsetPos = offsetPos.relative(dirOffset);

					offsetState = level.getBlockState(newOffsetPos);

					if (activator.getBlocks().contains(offsetState.getBlock())) {
						BlockEntity offsetBe = level.getBlockEntity(newOffsetPos);

						if (activator.isPowering(level, newOffsetPos, offsetState, offsetBe, dirOffset, 2) && (!(offsetBe instanceof IOwnable ownable) || ownable.getOwner().owns((IOwnable) be)))
							return true;
					}
				}
			}
		}

		return false;
	}

	public static <T extends BlockEntity & IOwnable> boolean isAllowedToExtractFromProtectedObject(Direction side, T be) {
		return isAllowedToExtractFromProtectedObject(side, be, be.getLevel(), be.getBlockPos());
	}

	public static boolean isAllowedToExtractFromProtectedObject(Direction side, IOwnable ownable, Level level, BlockPos pos) {
		if (side != null && level != null) {
			BlockPos offsetPos = pos.relative(side);
			BlockState offsetState = level.getBlockState(offsetPos);

			for (IExtractionBlock extractionBlock : SecurityCraftAPI.getRegisteredExtractionBlocks()) {
				if (offsetState.getBlock() == extractionBlock.getBlock())
					return extractionBlock.canExtract(ownable, level, offsetPos, offsetState);
			}
		}

		return false;
	}

	public static boolean isInsideUnownedReinforcedBlocks(Level level, Player player, Vec3 pos, float entityWidth) {
		float width = entityWidth * 0.8F;
		AABB inWallArea = AABB.ofSize(pos, width, 1.0E-6, width);

		return BlockPos.betweenClosedStream(inWallArea).anyMatch(testPos -> {
			BlockState wallState = level.getBlockState(testPos);

			return wallState.getBlock() instanceof IReinforcedBlock && wallState.isSuffocating(level, testPos) && (!(level.getBlockEntity(testPos) instanceof IOwnable ownable) || !ownable.isOwnedBy(player)) && Shapes.joinIsNotEmpty(wallState.getCollisionShape(level, testPos).move(testPos.getX(), testPos.getY(), testPos.getZ()), Shapes.create(inWallArea), BooleanOp.AND);
		});
	}

	public static void updateIndirectNeighbors(Level level, BlockPos pos, Block block) {
		updateIndirectNeighbors(level, pos, block, Direction.values());
	}

	public static void updateIndirectNeighbors(Level level, BlockPos pos, Block block, Direction... directions) {
		level.updateNeighborsAt(pos, block);

		for (Direction dir : directions) {
			level.updateNeighborsAt(pos.relative(dir), block);
		}
	}

	public static void removeInSequence(BiPredicate<Direction, BlockState> stateMatcher, LevelAccessor level, BlockPos pos, Direction... directions) {
		for (Direction direction : directions) {
			int i = 1;
			BlockPos modifiedPos = pos.relative(direction, i);

			while (stateMatcher.test(direction, level.getBlockState(modifiedPos))) {
				level.removeBlock(modifiedPos, false);
				modifiedPos = pos.relative(direction, ++i);
			}
		}
	}

	public static float getDestroyProgress(DestroyProgress destroyProgress, float destroyTimeForOwner, BlockState state, Player player, BlockGetter level, BlockPos pos) {
		return getDestroyProgress(destroyProgress, destroyTimeForOwner, state, player, level, pos, false);
	}

	public static float getDestroyProgress(DestroyProgress destroyProgress, float destroyTimeForOwner, BlockState state, Player player, BlockGetter level, BlockPos pos, boolean allowDefault) {
		boolean isBlockMine = state.getBlock() instanceof IBlockMine;

		if (ConfigHandler.SERVER.vanillaToolBlockBreaking.get() || isBlockMine) {
			BlockEntity be = level.getBlockEntity(pos);

			if (be instanceof IOwnable ownable && state.destroySpeed == -1.0F) {
				boolean isOwned = ownable.isOwnedBy(player);

				if (isOwned || isBlockMine || ConfigHandler.SERVER.allowBreakingNonOwnedBlocks.get() || (allowDefault && ownable.getOwner().isDefaultOwner())) {
					float newDestroyProgress;

					state.destroySpeed = destroyTimeForOwner;
					newDestroyProgress = destroyProgress.get(state, player, level, pos) / (float) (isOwned || isBlockMine ? 1.0F : ConfigHandler.SERVER.nonOwnedBreakingSlowdown.getAsDouble());
					state.destroySpeed = -1.0F;
					return newDestroyProgress;
				}
			}
		}

		return destroyProgress.get(state, player, level, pos);
	}

	@FunctionalInterface
	public static interface DestroyProgress {
		float get(BlockState state, Player player, BlockGetter level, BlockPos pos);
	}

	//TODO maybe move these helper methods to different class?
	public static int checkInventoryForItem(List<ItemStack> inventory, ItemStack stackReference, int removeAmount, boolean exactStackCheck, boolean shouldRemoveItems, Consumer<ItemStack> handleRemovedItem, BiConsumer<Integer, ItemStack> handleRemainingItemInSlot) {
		return checkInventoryForItem(inventory, 0, inventory.size() - 1, stackReference, removeAmount, exactStackCheck, shouldRemoveItems, handleRemovedItem, handleRemainingItemInSlot);
	}

	public static int checkInventoryForItem(List<ItemStack> inventory, int startSlot, int endSlot, ItemStack stackReference, int removeAmount, boolean exactStackCheck, boolean shouldRemoveItems, Consumer<ItemStack> handleRemovedItem, BiConsumer<Integer, ItemStack> handleRemainingItemInSlot) {
		for (int i = startSlot; i <= endSlot; i++) {
			removeAmount = checkItemsInItem(inventory.get(i), i, stackReference, removeAmount, exactStackCheck, shouldRemoveItems, handleRemovedItem, handleRemainingItemInSlot);

			if (removeAmount == 0)
				break;
		}

		return removeAmount;
	}

	public static int checkItemsInItem(ItemStack stackToCheck, int positionInInv, ItemStack stackReference, int removeAmount, boolean exactStackCheck, boolean shouldRemoveItems, Consumer<ItemStack> handleRemovedItem, BiConsumer<Integer, ItemStack> handleRemainingItemInSlot) {
		if (areItemsEqual(stackToCheck, stackReference, exactStackCheck)) {
			if (shouldRemoveItems) {
				ItemStack splitStack = stackToCheck.split(removeAmount);

				removeAmount -= splitStack.getCount();
				handleRemovedItem.accept(splitStack);
				handleRemainingItemInSlot.accept(positionInInv, stackToCheck);
			}
			else
				removeAmount = Math.max(removeAmount - stackToCheck.getCount(), 0);

			if (removeAmount == 0)
				return removeAmount;
		}

		int containerRemoveAmount = checkItemsInItemContainer(stackToCheck, stackReference, removeAmount, exactStackCheck, shouldRemoveItems, handleRemovedItem);

		if (containerRemoveAmount != removeAmount) {
			removeAmount = containerRemoveAmount;

			if (removeAmount == 0)
				return removeAmount;
		}

		int bundleRemoveAmount = checkItemsInBundle(stackToCheck, stackReference, removeAmount, exactStackCheck, shouldRemoveItems, handleRemovedItem);

		if (bundleRemoveAmount != removeAmount) {
			removeAmount = bundleRemoveAmount;

			if (removeAmount == 0)
				return removeAmount;
		}

		return removeAmount;
	}

	//TODO maybe move to a better place?
	public static int checkItemsInItemContainer(ItemStack item, ItemStack stackToCheck, int removeAmount, boolean exactStackCheck, boolean shouldRemoveItems, Consumer<ItemStack> handleRemovedItem) {
		if (item != null && item.has(DataComponents.CONTAINER)) {
			ItemContainerContents contents = item.get(DataComponents.CONTAINER);
			NonNullList<ItemStack> containerItems = NonNullList.withSize(contents.getSlots(), ItemStack.EMPTY);

			contents.copyInto(containerItems);
			removeAmount = checkInventoryForItem(containerItems, stackToCheck, removeAmount, exactStackCheck, shouldRemoveItems, handleRemovedItem, containerItems::set);

			if (shouldRemoveItems)
				item.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(containerItems)); //TODO check if shulker box content tag is updated appropriately
		}

		return removeAmount;
	}

	public static int checkItemsInBundle(ItemStack item, ItemStack stackToCheck, int removeAmount, boolean exactStackCheck, boolean shouldRemoveItems, Consumer<ItemStack> handleRemovedItem) {
		if (item != null && item.has(DataComponents.BUNDLE_CONTENTS)) {
			List<ItemStack> bundleItems = item.get(DataComponents.BUNDLE_CONTENTS).itemCopyStream().collect(Collectors.toList());

			removeAmount = checkInventoryForItem(bundleItems, stackToCheck, removeAmount, exactStackCheck, shouldRemoveItems, handleRemovedItem, (i, stack) -> {
				if (stack.isEmpty())
					bundleItems.remove((int) i);
				else
					bundleItems.set(i, stack);
			});

			if (shouldRemoveItems)
				item.set(DataComponents.BUNDLE_CONTENTS, new BundleContents(bundleItems));
		}

		return removeAmount;
	}

	public static int countItemsBetween(Container container, ItemStack stackReference, int start, int endInclusive, boolean hasSmartModule) {
		int itemsToRemove = Integer.MAX_VALUE;

		for (int i = start; i <= endInclusive; i++) {
			itemsToRemove = checkItemsInItem(container.getItem(i), i, stackReference, itemsToRemove, hasSmartModule, false, stack -> {}, (slot, stack) -> {});
		}

		return Integer.MAX_VALUE - itemsToRemove;
	}

	public static boolean areItemsEqual(ItemStack firstItemStack, ItemStack secondItemStack, boolean exactComponentCheck) {
		return exactComponentCheck ? ItemStack.isSameItemSameComponents(firstItemStack, secondItemStack) : firstItemStack.is(secondItemStack.getItem());
	}
}
