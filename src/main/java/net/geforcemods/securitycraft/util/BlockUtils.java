package net.geforcemods.securitycraft.util;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.api.IDoorActivator;
import net.geforcemods.securitycraft.api.IExtractionBlock;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.SecurityCraftAPI;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.BlockLever;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.wrapper.EmptyHandler;

public class BlockUtils{
	public static void updateAndNotify(World world, BlockPos pos, Block block, int delay, boolean shouldUpdate){
		if(shouldUpdate)
			world.scheduleUpdate(pos, block, delay);

		world.notifyNeighborsOfStateChange(pos, block, false);
	}

	public static AxisAlignedBB fromBounds(double x1, double y1, double z1, double x2, double y2, double z2)
	{
		double d6 = Math.min(x1, x2);
		double d7 = Math.min(y1, y2);
		double d8 = Math.min(z1, z2);
		double d9 = Math.max(x1, x2);
		double d10 = Math.max(y1, y2);
		double d11 = Math.max(z1, z2);
		return new AxisAlignedBB(d6, d7, d8, d9, d10, d11);
	}

	public static int[] posToIntArray(BlockPos pos){
		return new int[]{pos.getX(), pos.getY(), pos.getZ()};
	}

	public static boolean hasActiveSCBlockNextTo(World world, BlockPos pos)
	{
		return SecurityCraftAPI.getRegisteredDoorActivators().stream().anyMatch(activator -> hasActiveSCBlockNextTo(world, pos, world.getTileEntity(pos), activator));
	}

	private static boolean hasActiveSCBlockNextTo(World world, BlockPos pos, TileEntity te, IDoorActivator activator)
	{
		for(EnumFacing facing : EnumFacing.values())
		{
			BlockPos offsetPos = pos.offset(facing);
			IBlockState offsetState = world.getBlockState(offsetPos);

			if(activator.getBlocks().contains(offsetState.getBlock()))
			{
				TileEntity offsetTe = world.getTileEntity(offsetPos);

				if(activator.isPowering(world, offsetPos, offsetState, offsetTe) && (!(offsetTe instanceof IOwnable) || ((IOwnable)offsetTe).getOwner().owns((IOwnable)te)))
					return true;
			}

			if(world.getRedstonePower(offsetPos, facing) == 15 && !offsetState.canProvidePower())
			{
				for(EnumFacing dirOffset : EnumFacing.values())
				{
					if(dirOffset.getOpposite() == facing) //skip this, as it would just go back to the original position
						continue;

					BlockPos newOffsetPos = offsetPos.offset(dirOffset);

					offsetState = world.getBlockState(newOffsetPos);

					if(activator.getBlocks().contains(offsetState.getBlock()))
					{
						if(offsetState.getPropertyKeys().contains(BlockLever.FACING))
						{
							if(dirOffset != offsetState.getValue(BlockLever.FACING).getFacing())
								continue;
						}
						else if(offsetState.getPropertyKeys().contains(BlockDirectional.FACING))
						{
							if(dirOffset != offsetState.getValue(BlockDirectional.FACING))
								continue;
						}

						TileEntity offsetTe = world.getTileEntity(newOffsetPos);

						if(activator.isPowering(world, newOffsetPos, offsetState, offsetTe) && (!(offsetTe instanceof IOwnable) || ((IOwnable)offsetTe).getOwner().owns((IOwnable)te)))
							return true;
					}
				}
			}
		}

		return false;
	}

	public static <T> T getProtectedCapability(EnumFacing side, TileEntity te, Supplier<T> extractionPermittedHandler, Supplier<T> insertOnlyHandler)
	{
		if(side == null)
			return (T)EmptyHandler.INSTANCE;

		BlockPos offsetPos = te.getPos().offset(side);
		IBlockState offsetState = te.getWorld().getBlockState(offsetPos);

		for(IExtractionBlock extractionBlock : SecurityCraftAPI.getRegisteredExtractionBlocks())
		{
			if(offsetState.getBlock() == extractionBlock.getBlock())
			{
				if(!extractionBlock.canExtract((IOwnable)te, te.getWorld(), offsetPos, offsetState))
					return (T)EmptyHandler.INSTANCE;
				else return extractionPermittedHandler.get();
			}
		}

		return insertOnlyHandler.get();
	}

	public static boolean isWithinUsableDistance(World world, BlockPos pos, EntityPlayer player, Block block)
	{
		return world.getBlockState(pos).getBlock() == block && player.getDistanceSq(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D) <= 64.0D;
	}
}
