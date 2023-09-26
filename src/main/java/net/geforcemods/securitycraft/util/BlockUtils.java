package net.geforcemods.securitycraft.util;

import java.util.function.BiPredicate;
import java.util.function.Supplier;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.api.IDoorActivator;
import net.geforcemods.securitycraft.api.IExtractionBlock;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.SecurityCraftAPI;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion.Mode;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.EmptyHandler;

public class BlockUtils {
	private static final LazyOptional<IItemHandler> EMPTY_INVENTORY = LazyOptional.of(() -> EmptyHandler.INSTANCE);

	private BlockUtils() {}

	public static boolean isSideSolid(IWorldReader level, BlockPos pos, Direction side) {
		return level.getBlockState(pos).isFaceSturdy(level, pos, side);
	}

	public static void updateAndNotify(World level, BlockPos pos, Block block, int delay, boolean shouldUpdate) {
		if (shouldUpdate)
			level.getBlockTicks().scheduleTick(pos, block, delay);

		level.updateNeighborsAt(pos, block);
	}

	public static int[] posToIntArray(BlockPos pos) {
		return new int[] {
				pos.getX(), pos.getY(), pos.getZ()
		};
	}

	public static Mode getExplosionMode() {
		return ConfigHandler.SERVER.mineExplosionsBreakBlocks.get() ? Mode.BREAK : Mode.NONE;
	}

	public static boolean hasActiveSCBlockNextTo(World level, BlockPos pos) {
		return SecurityCraftAPI.getRegisteredDoorActivators().stream().anyMatch(activator -> hasActiveSCBlockNextTo(level, pos, level.getBlockEntity(pos), activator));
	}

	private static boolean hasActiveSCBlockNextTo(World level, BlockPos pos, TileEntity be, IDoorActivator activator) {
		for (Direction dir : Direction.values()) {
			BlockPos offsetPos = pos.relative(dir);
			BlockState offsetState = level.getBlockState(offsetPos);

			if (activator.getBlocks().contains(offsetState.getBlock())) {
				TileEntity offsetBe = level.getBlockEntity(offsetPos);

				if (activator.isPowering(level, offsetPos, offsetState, offsetBe, dir, 1) && (!(offsetBe instanceof IOwnable) || ((IOwnable) offsetBe).getOwner().owns((IOwnable) be)))
					return true;
			}

			if (level.getSignal(offsetPos, dir) == 15 && !offsetState.isSignalSource()) {
				for (Direction dirOffset : Direction.values()) {
					if (dirOffset.getOpposite() == dir)
						continue;

					BlockPos newOffsetPos = offsetPos.relative(dirOffset);

					offsetState = level.getBlockState(newOffsetPos);

					if (activator.getBlocks().contains(offsetState.getBlock())) {
						TileEntity offsetBe = level.getBlockEntity(newOffsetPos);

						if (activator.isPowering(level, newOffsetPos, offsetState, offsetBe, dirOffset, 2) && (!(offsetBe instanceof IOwnable) || ((IOwnable) offsetBe).getOwner().owns((IOwnable) be)))
							return true;
					}
				}
			}
		}

		return false;
	}

	public static LazyOptional<?> getProtectedCapability(Direction side, TileEntity be, Supplier<LazyOptional<?>> extractionPermittedHandler, Supplier<LazyOptional<?>> insertOnlyHandler) {
		if (side == null)
			return EMPTY_INVENTORY;

		BlockPos offsetPos = be.getBlockPos().relative(side);
		BlockState offsetState = be.getLevel().getBlockState(offsetPos);

		for (IExtractionBlock extractionBlock : SecurityCraftAPI.getRegisteredExtractionBlocks()) {
			if (offsetState.getBlock() == extractionBlock.getBlock()) {
				if (!extractionBlock.canExtract((IOwnable) be, be.getLevel(), offsetPos, offsetState))
					return EMPTY_INVENTORY;
				else
					return extractionPermittedHandler.get();
			}
		}

		return insertOnlyHandler.get();
	}

	public static void updateIndirectNeighbors(World level, BlockPos pos, Block block) {
		updateIndirectNeighbors(level, pos, block, Direction.values());
	}

	public static void updateIndirectNeighbors(World level, BlockPos pos, Block block, Direction... directions) {
		level.updateNeighborsAt(pos, block);

		for (Direction dir : directions) {
			level.updateNeighborsAt(pos.relative(dir), block);
		}
	}

	public static void removeInSequence(BiPredicate<Direction, BlockState> stateMatcher, IWorld level, BlockPos pos, Direction... directions) {
		for (Direction direction : directions) {
			int i = 1;
			BlockPos modifiedPos = pos.relative(direction, i);

			while (stateMatcher.test(direction, level.getBlockState(modifiedPos))) {
				level.removeBlock(modifiedPos, false);
				modifiedPos = pos.relative(direction, ++i);
			}
		}
	}
}
