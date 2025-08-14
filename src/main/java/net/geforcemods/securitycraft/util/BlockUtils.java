package net.geforcemods.securitycraft.util;

import java.util.function.BiPredicate;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.api.IBlockMine;
import net.geforcemods.securitycraft.api.IDoorActivator;
import net.geforcemods.securitycraft.api.IExtractionBlock;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.IReinforcedBlock;
import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.api.SecurityCraftAPI;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
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

				if (isOwned || isBlockMine || ConfigHandler.SERVER.allowBreakingNonOwnedBlocks.get() || (allowDefault && ownable.getOwner().equals(new Owner()))) {
					float newDestroyProgress;

					state.destroySpeed = destroyTimeForOwner;
					newDestroyProgress = destroyProgress.get(state, player, level, pos) / (float) (isOwned ? 1.0F : ConfigHandler.SERVER.nonOwnedBreakingSlowdown.getAsDouble());
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
}
