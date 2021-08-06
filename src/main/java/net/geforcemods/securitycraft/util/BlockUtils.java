package net.geforcemods.securitycraft.util;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.api.IDoorActivator;
import net.geforcemods.securitycraft.api.IExtractionBlock;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.SecurityCraftAPI;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.properties.AttachFace;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion.Mode;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.EmptyHandler;

public class BlockUtils{
	private static final LazyOptional<IItemHandler> EMPTY_INVENTORY = LazyOptional.of(() -> new EmptyHandler());

	public static boolean isSideSolid(IWorldReader world, BlockPos pos, Direction side)
	{
		return world.getBlockState(pos).isSolidSide(world, pos, side);
	}

	public static void updateAndNotify(World world, BlockPos pos, Block block, int delay, boolean shouldUpdate){
		if(shouldUpdate)
			world.getPendingBlockTicks().scheduleTick(pos, block, delay);

		world.notifyNeighborsOfStateChange(pos, block);
	}

	public static int[] posToIntArray(BlockPos pos){
		return new int[]{pos.getX(), pos.getY(), pos.getZ()};
	}

	public static Mode getExplosionMode()
	{
		return ConfigHandler.SERVER.mineExplosionsBreakBlocks.get() ? Mode.BREAK : Mode.NONE;
	}

	public static boolean hasActiveSCBlockNextTo(World world, BlockPos pos)
	{
		return SecurityCraftAPI.getRegisteredDoorActivators().stream().anyMatch(activator -> hasActiveSCBlockNextTo(world, pos, world.getTileEntity(pos), activator));
	}

	private static boolean hasActiveSCBlockNextTo(World world, BlockPos pos, TileEntity te, IDoorActivator activator)
	{
		for(Direction dir : Direction.values())
		{
			BlockPos offsetPos = pos.offset(dir);
			BlockState offsetState = world.getBlockState(offsetPos);

			if(activator.getBlocks().contains(offsetState.getBlock()))
			{
				TileEntity offsetTe = world.getTileEntity(offsetPos);

				return activator.isPowering(world, offsetPos, offsetState, offsetTe) && (!(offsetTe instanceof IOwnable) || ((IOwnable)offsetTe).getOwner().owns((IOwnable)te));
			}

			if(world.getRedstonePower(offsetPos, dir) == 15 && !offsetState.canProvidePower())
			{
				for(Direction dirOffset : Direction.values())
				{
					if(dirOffset.getOpposite() == dir) //skip this, as it would just go back to the original position
						continue;

					BlockPos newOffsetPos = offsetPos.offset(dirOffset);

					offsetState = world.getBlockState(newOffsetPos);

					if(activator.getBlocks().contains(offsetState.getBlock()))
					{
						//checking that e.g. a lever/button is correctly attached to the block
						if(offsetState.hasProperty(BlockStateProperties.FACE) && offsetState.hasProperty(BlockStateProperties.HORIZONTAL_FACING))
						{
							Axis offsetAxis = dirOffset.getAxis();
							Direction offsetFacing = offsetState.get(BlockStateProperties.HORIZONTAL_FACING);
							AttachFace offsetAttachFace = offsetState.get(BlockStateProperties.FACE);

							switch(offsetAxis)
							{
								case X: case Z:
									if(offsetAttachFace != AttachFace.WALL || dirOffset != offsetFacing)
										return false;
									break;
								case Y:
									if((dirOffset == Direction.UP && offsetAttachFace != AttachFace.FLOOR) || (dirOffset == Direction.DOWN && offsetAttachFace != AttachFace.CEILING))
										return false;
									break;
							}
						}

						TileEntity offsetTe = world.getTileEntity(newOffsetPos);

						return activator.isPowering(world, newOffsetPos, offsetState, offsetTe) && (!(offsetTe instanceof IOwnable) || ((IOwnable)offsetTe).getOwner().owns((IOwnable)te));
					}
				}
			}
		}

		return false;
	}

	public static LazyOptional<?> getProtectedCapability(Direction side, TileEntity te, Supplier<LazyOptional<?>> extractionPermittedHandler, Supplier<LazyOptional<?>> insertOnlyHandler)
	{
		if(side == null)
			return EMPTY_INVENTORY;

		BlockPos offsetPos = te.getPos().offset(side);
		BlockState offsetState = te.getWorld().getBlockState(offsetPos);

		for(IExtractionBlock extractionBlock : SecurityCraftAPI.getRegisteredExtractionBlocks())
		{
			if(offsetState.getBlock() == extractionBlock.getBlock())
			{
				if(!extractionBlock.canExtract((IOwnable)te, te.getWorld(), offsetPos, offsetState))
					return EMPTY_INVENTORY;
				else return extractionPermittedHandler.get();
			}
		}

		return insertOnlyHandler.get();
	}
}
