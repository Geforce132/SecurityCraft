package net.geforcemods.securitycraft.util;

import java.util.function.BiPredicate;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.api.IDoorActivator;
import net.geforcemods.securitycraft.api.IExtractionBlock;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.IReinforcedBlock;
import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.api.SecurityCraftAPI;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.Explosion.Mode;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

public class BlockUtils {
	private BlockUtils() {}

	public static boolean isSideSolid(IWorldReader level, BlockPos pos, Direction side) {
		return level.getBlockState(pos).isFaceSturdy(level, pos, side);
	}

	public static Mode getExplosionMode() {
		return ConfigHandler.SERVER.mineExplosionsBreakBlocks.get() ? Mode.BREAK : Mode.NONE;
	}

	public static boolean hasActiveSCBlockNextTo(World level, BlockPos pos) {
		TileEntity be = level.getBlockEntity(pos);

		return SecurityCraftAPI.getRegisteredDoorActivators().stream().anyMatch(activator -> hasActiveSCBlockNextTo(level, pos, be, activator));
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

	public static <T extends TileEntity & IOwnable> boolean isAllowedToExtractFromProtectedObject(Direction side, T be) {
		return isAllowedToExtractFromProtectedObject(side, be, be.getLevel(), be.getBlockPos());
	}

	public static boolean isAllowedToExtractFromProtectedObject(Direction side, IOwnable ownable, World level, BlockPos pos) {
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

	public static boolean isInsideUnownedReinforcedBlocks(World level, PlayerEntity player, Vector3d pos) {
		float width = player.getBbWidth() * 0.8F;
		AxisAlignedBB inWallArea = AxisAlignedBB.ofSize(width, 0.1F, width).move(pos.x, pos.y, pos.z);

		return level.getBlockCollisions(null, inWallArea, (wallState, testPos) -> {
			if (wallState.getBlock() instanceof IReinforcedBlock && wallState.isSuffocating(level, testPos)) {
				TileEntity be = level.getBlockEntity(testPos);

				return !(be instanceof IOwnable) || !((IOwnable) be).isOwnedBy(player);
			}

			return false;
		}).findAny().isPresent();
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

	public static float getDestroyProgress(DestroyProgress destroyProgress, BlockState state, PlayerEntity player, IBlockReader level, BlockPos pos) {
		return getDestroyProgress(destroyProgress, state, player, level, pos, false);
	}

	public static float getDestroyProgress(DestroyProgress destroyProgress, BlockState state, PlayerEntity player, IBlockReader level, BlockPos pos, boolean allowDefault) {
		if (state.getBlock() instanceof IBlockMine)
			return destroyProgress.get(state, player, level, pos);

		if (ConfigHandler.SERVER.vanillaToolBlockBreaking.get()) {
			TileEntity be = level.getBlockEntity(pos);

			if (be instanceof IOwnable && ((IOwnable) be).isOwnedBy(player))
				return destroyProgress.get(state, player, level, pos);
			else if (ConfigHandler.SERVER.allowBreakingNonOwnedBlocks.get() || (allowDefault && be instanceof IOwnable && ((IOwnable) be).getOwner().equals(new Owner())))
				return (float) (destroyProgress.get(state, player, level, pos) / ConfigHandler.SERVER.nonOwnedBreakingSlowdown.get());
		}

		return 0.0F;
	}

	@FunctionalInterface
	public static interface DestroyProgress {
		float get(BlockState state, PlayerEntity player, IBlockReader level, BlockPos pos);
	}
}
