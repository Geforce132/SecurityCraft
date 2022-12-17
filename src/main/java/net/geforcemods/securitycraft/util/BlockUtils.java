package net.geforcemods.securitycraft.util;

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

	public static boolean isSideSolid(IWorldReader world, BlockPos pos, Direction side) {
		return world.getBlockState(pos).isFaceSturdy(world, pos, side);
	}

	public static void updateAndNotify(World world, BlockPos pos, Block block, int delay, boolean shouldUpdate) {
		if (shouldUpdate)
			world.getBlockTicks().scheduleTick(pos, block, delay);

		world.updateNeighborsAt(pos, block);
	}

	public static int[] posToIntArray(BlockPos pos) {
		return new int[] {
				pos.getX(), pos.getY(), pos.getZ()
		};
	}

	public static Mode getExplosionMode() {
		return ConfigHandler.SERVER.mineExplosionsBreakBlocks.get() ? Mode.BREAK : Mode.NONE;
	}

	public static boolean hasActiveSCBlockNextTo(World world, BlockPos pos) {
		return SecurityCraftAPI.getRegisteredDoorActivators().stream().anyMatch(activator -> hasActiveSCBlockNextTo(world, pos, world.getBlockEntity(pos), activator));
	}

	private static boolean hasActiveSCBlockNextTo(World world, BlockPos pos, TileEntity te, IDoorActivator activator) {
		for (Direction dir : Direction.values()) {
			BlockPos offsetPos = pos.relative(dir);
			BlockState offsetState = world.getBlockState(offsetPos);

			if (activator.getBlocks().contains(offsetState.getBlock())) {
				TileEntity offsetTe = world.getBlockEntity(offsetPos);

				if (activator.isPowering(world, offsetPos, offsetState, offsetTe, dir, 1) && (!(offsetTe instanceof IOwnable) || ((IOwnable) offsetTe).getOwner().owns((IOwnable) te)))
					return true;
			}

			if (world.getSignal(offsetPos, dir) == 15 && !offsetState.isSignalSource()) {
				for (Direction dirOffset : Direction.values()) {
					if (dirOffset.getOpposite() == dir)
						continue;

					BlockPos newOffsetPos = offsetPos.relative(dirOffset);

					offsetState = world.getBlockState(newOffsetPos);

					if (activator.getBlocks().contains(offsetState.getBlock())) {
						TileEntity offsetTe = world.getBlockEntity(newOffsetPos);

						if (activator.isPowering(world, newOffsetPos, offsetState, offsetTe, dirOffset, 2) && (!(offsetTe instanceof IOwnable) || ((IOwnable) offsetTe).getOwner().owns((IOwnable) te)))
							return true;
					}
				}
			}
		}

		return false;
	}

	public static LazyOptional<?> getProtectedCapability(Direction side, TileEntity te, Supplier<LazyOptional<?>> extractionPermittedHandler, Supplier<LazyOptional<?>> insertOnlyHandler) {
		if (side == null)
			return EMPTY_INVENTORY;

		BlockPos offsetPos = te.getBlockPos().relative(side);
		BlockState offsetState = te.getLevel().getBlockState(offsetPos);

		for (IExtractionBlock extractionBlock : SecurityCraftAPI.getRegisteredExtractionBlocks()) {
			if (offsetState.getBlock() == extractionBlock.getBlock()) {
				if (!extractionBlock.canExtract((IOwnable) te, te.getLevel(), offsetPos, offsetState))
					return EMPTY_INVENTORY;
				else
					return extractionPermittedHandler.get();
			}
		}

		return insertOnlyHandler.get();
	}

	public static void updateIndirectNeighbors(World world, BlockPos pos, Block block) {
		updateIndirectNeighbors(world, pos, block, Direction.values());
	}

	public static void updateIndirectNeighbors(World world, BlockPos pos, Block block, Direction... directions) {
		world.updateNeighborsAt(pos, block);

		for (Direction dir : directions) {
			world.updateNeighborsAt(pos.relative(dir), block);
		}
	}

	public static void destroyInSequence(Block blockToDestroy, IWorld level, BlockPos pos, Direction... directions) {
		for (Direction direction : directions) {
			int i = 1;
			BlockPos modifiedPos = pos.relative(direction, i);

			while (level.getBlockState(modifiedPos).getBlock() == blockToDestroy) {
				level.destroyBlock(modifiedPos, false);
				modifiedPos = pos.relative(direction, ++i);
			}
		}
	}
}
