package net.geforcemods.securitycraft.util;

import java.util.function.BiPredicate;

import net.geforcemods.securitycraft.api.IDoorActivator;
import net.geforcemods.securitycraft.api.IExtractionBlock;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.IReinforcedBlock;
import net.geforcemods.securitycraft.api.SecurityCraftAPI;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class BlockUtils {
	private BlockUtils() {}

	public static void updateAndNotify(World world, BlockPos pos, Block block, int delay, boolean shouldUpdate) {
		if (shouldUpdate)
			world.scheduleUpdate(pos, block, delay);

		world.notifyNeighborsOfStateChange(pos, block, false);
	}

	public static AxisAlignedBB fromBounds(double x1, double y1, double z1, double x2, double y2, double z2) {
		double d6 = Math.min(x1, x2);
		double d7 = Math.min(y1, y2);
		double d8 = Math.min(z1, z2);
		double d9 = Math.max(x1, x2);
		double d10 = Math.max(y1, y2);
		double d11 = Math.max(z1, z2);
		return new AxisAlignedBB(d6, d7, d8, d9, d10, d11);
	}

	public static int[] posToIntArray(BlockPos pos) {
		return new int[] {
				pos.getX(), pos.getY(), pos.getZ()
		};
	}

	public static boolean hasActiveSCBlockNextTo(World world, BlockPos pos) {
		return SecurityCraftAPI.getRegisteredDoorActivators().stream().anyMatch(activator -> hasActiveSCBlockNextTo(world, pos, world.getTileEntity(pos), activator));
	}

	private static boolean hasActiveSCBlockNextTo(World world, BlockPos pos, TileEntity te, IDoorActivator activator) {
		for (EnumFacing facing : EnumFacing.values()) {
			BlockPos offsetPos = pos.offset(facing);
			IBlockState offsetState = world.getBlockState(offsetPos);

			if (activator.getBlocks().contains(offsetState.getBlock())) {
				TileEntity offsetTe = world.getTileEntity(offsetPos);

				if (activator.isPowering(world, offsetPos, offsetState, offsetTe, facing, 1) && (!(offsetTe instanceof IOwnable) || ((IOwnable) offsetTe).getOwner().owns((IOwnable) te)))
					return true;
			}

			if (world.getRedstonePower(offsetPos, facing) == 15 && !offsetState.canProvidePower()) {
				for (EnumFacing dirOffset : EnumFacing.values()) {
					if (dirOffset.getOpposite() == facing)
						continue;

					BlockPos newOffsetPos = offsetPos.offset(dirOffset);

					offsetState = world.getBlockState(newOffsetPos);

					if (activator.getBlocks().contains(offsetState.getBlock())) {
						TileEntity offsetTe = world.getTileEntity(newOffsetPos);

						if (activator.isPowering(world, newOffsetPos, offsetState, offsetTe, dirOffset, 2) && (!(offsetTe instanceof IOwnable) || ((IOwnable) offsetTe).getOwner().owns((IOwnable) te)))
							return true;
					}
				}
			}
		}

		return false;
	}

	public static boolean isAllowedToExtractFromProtectedBlock(EnumFacing side, TileEntity te) {
		if (side != null) {
			BlockPos offsetPos = te.getPos().offset(side);
			IBlockState offsetState = te.getWorld().getBlockState(offsetPos);

			for (IExtractionBlock extractionBlock : SecurityCraftAPI.getRegisteredExtractionBlocks()) {
				if (offsetState.getBlock() == extractionBlock.getBlock())
					return extractionBlock.canExtract((IOwnable) te, te.getWorld(), offsetPos, offsetState);
			}
		}

		return false;
	}

	public static boolean isInsideReinforcedBlocks(World level, Entity entity, double yHeight) {
		BlockPos.PooledMutableBlockPos testPos = BlockPos.PooledMutableBlockPos.retain();

		for (int i = 0; i < 8; ++i) {
			int x = MathHelper.floor(entity.posX + ((i >> 1) % 2 - 0.5F) * entity.width * 0.8F);
			int y = MathHelper.floor(entity.posY + ((i % 2 - 0.5F) * 0.1F) + yHeight);
			int z = MathHelper.floor(entity.posZ + ((i >> 2) % 2 - 0.5F) * entity.width * 0.8F);

			if (testPos.getX() != x || testPos.getY() != y || testPos.getZ() != z) {
				testPos.setPos(x, y, z);

				IBlockState state = level.getBlockState(testPos);

				if (state.getBlock() instanceof IReinforcedBlock && state.causesSuffocation()) {
					testPos.release();
					return true;
				}
			}
		}

		testPos.release();
		return false;
	}

	public static boolean isWithinUsableDistance(World world, BlockPos pos, EntityPlayer player, Block block) {
		return world.getBlockState(pos).getBlock() == block && player.getDistanceSq(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D) <= 64.0D;
	}

	public static void updateIndirectNeighbors(World world, BlockPos pos, Block block) {
		updateIndirectNeighbors(world, pos, block, EnumFacing.values());
	}

	public static void updateIndirectNeighbors(World world, BlockPos pos, Block block, EnumFacing... directions) {
		world.notifyNeighborsOfStateChange(pos, block, false);

		for (EnumFacing dir : directions) {
			world.notifyNeighborsOfStateChange(pos.offset(dir), block, false);
		}
	}

	public static void removeInSequence(BiPredicate<EnumFacing, IBlockState> stateMatcher, World world, BlockPos pos, EnumFacing... directions) {
		for (EnumFacing direction : directions) {
			int i = 1;
			BlockPos modifiedPos = pos.offset(direction, i);

			while (stateMatcher.test(direction, world.getBlockState(modifiedPos))) {
				world.setBlockToAir(modifiedPos);
				modifiedPos = pos.offset(direction, ++i);
			}
		}
	}
}
