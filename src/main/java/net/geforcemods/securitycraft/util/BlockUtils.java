package net.geforcemods.securitycraft.util;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.api.IDoorActivator;
import net.geforcemods.securitycraft.api.IExtractionBlock;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.SecurityCraftAPI;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Explosion.BlockInteraction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.EmptyHandler;

public class BlockUtils{
	private static final LazyOptional<IItemHandler> EMPTY_INVENTORY = LazyOptional.of(() -> EmptyHandler.INSTANCE);

	public static boolean isSideSolid(LevelReader world, BlockPos pos, Direction side)
	{
		return world.getBlockState(pos).isFaceSturdy(world, pos, side);
	}

	public static void updateAndNotify(Level world, BlockPos pos, Block block, int delay, boolean shouldUpdate){
		if(shouldUpdate)
			world.getBlockTicks().scheduleTick(pos, block, delay);

		world.updateNeighborsAt(pos, block);
	}

	public static int[] posToIntArray(BlockPos pos){
		return new int[]{pos.getX(), pos.getY(), pos.getZ()};
	}

	public static BlockInteraction getExplosionMode()
	{
		return ConfigHandler.SERVER.mineExplosionsBreakBlocks.get() ? BlockInteraction.BREAK : BlockInteraction.NONE;
	}

	public static boolean hasActiveSCBlockNextTo(Level world, BlockPos pos)
	{
		return SecurityCraftAPI.getRegisteredDoorActivators().stream().anyMatch(activator -> hasActiveSCBlockNextTo(world, pos, world.getBlockEntity(pos), activator));
	}

	private static boolean hasActiveSCBlockNextTo(Level world, BlockPos pos, BlockEntity te, IDoorActivator activator)
	{
		for(Direction dir : Direction.values())
		{
			BlockPos offsetPos = pos.relative(dir);
			BlockState offsetState = world.getBlockState(offsetPos);

			if(activator.getBlocks().contains(offsetState.getBlock()))
			{
				BlockEntity offsetTe = world.getBlockEntity(offsetPos);

				if(activator.isPowering(world, offsetPos, offsetState, offsetTe, dir, 1) && (!(offsetTe instanceof IOwnable ownable) || ownable.getOwner().owns((IOwnable)te)))
					return true;
			}

			if(world.getSignal(offsetPos, dir) == 15 && !offsetState.isSignalSource())
			{
				for(Direction dirOffset : Direction.values())
				{
					if(dirOffset.getOpposite() == dir) //skip this, as it would just go back to the original position
						continue;

					BlockPos newOffsetPos = offsetPos.relative(dirOffset);

					offsetState = world.getBlockState(newOffsetPos);

					if(activator.getBlocks().contains(offsetState.getBlock()))
					{
						BlockEntity offsetTe = world.getBlockEntity(newOffsetPos);

						if(activator.isPowering(world, newOffsetPos, offsetState, offsetTe, dirOffset, 2) && (!(offsetTe instanceof IOwnable ownable) || ownable.getOwner().owns((IOwnable)te)))
							return true;
					}
				}
			}
		}

		return false;
	}

	public static LazyOptional<?> getProtectedCapability(Direction side, BlockEntity te, Supplier<LazyOptional<?>> extractionPermittedHandler, Supplier<LazyOptional<?>> insertOnlyHandler)
	{
		if(side == null)
			return EMPTY_INVENTORY;

		BlockPos offsetPos = te.getBlockPos().relative(side);
		BlockState offsetState = te.getLevel().getBlockState(offsetPos);

		for(IExtractionBlock extractionBlock : SecurityCraftAPI.getRegisteredExtractionBlocks())
		{
			if(offsetState.getBlock() == extractionBlock.getBlock())
			{
				if(!extractionBlock.canExtract((IOwnable)te, te.getLevel(), offsetPos, offsetState))
					return EMPTY_INVENTORY;
				else return extractionPermittedHandler.get();
			}
		}

		return insertOnlyHandler.get();
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
}
