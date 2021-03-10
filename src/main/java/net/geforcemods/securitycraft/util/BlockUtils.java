package net.geforcemods.securitycraft.util;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.api.IDoorActivator;
import net.geforcemods.securitycraft.api.IExtractionBlock;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.SecurityCraftAPI;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.BlockLever;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.EmptyHandler;

public class BlockUtils{
	private static final IItemHandler EMPTY_INVENTORY = new EmptyHandler();

	public static void updateAndNotify(World world, BlockPos pos, Block block, int delay, boolean shouldUpdate){
		if(shouldUpdate)
			world.scheduleUpdate(pos, block, delay);

		world.notifyNeighborsOfStateChange(pos, block, false);
	}

	public static int getBlockMeta(World world, BlockPos pos){
		return world.getBlockState(pos).getBlock().getMetaFromState(world.getBlockState(pos));
	}

	public static Block getBlock(IBlockAccess world, BlockPos pos){
		return world.getBlockState(pos).getBlock();
	}

	public static Block getBlock(World world, int x, int y, int z){
		return world.getBlockState(toPos(x, y, z)).getBlock();
	}

	public static void setBlockProperty(World world, BlockPos pos, PropertyBool property, boolean value) {
		IBlockState state = world.getBlockState(pos);

		if(state.getProperties().containsKey(property))
			world.setBlockState(pos, state.withProperty(property, value));
	}

	public static void setBlockProperty(World world, BlockPos pos, PropertyInteger property, int value) {
		IBlockState state = world.getBlockState(pos);

		if(state.getProperties().containsKey(property))
			world.setBlockState(pos, state.withProperty(property, value));
	}

	public static void setFacingProperty(World world, BlockPos pos, PropertyEnum<EnumFacing> property, EnumFacing value) {
		IBlockState state = world.getBlockState(pos);

		if(state.getProperties().containsKey(property))
			world.setBlockState(pos, state.withProperty(property, value));
	}

	public static boolean hasBlockProperty(World world, BlockPos pos, IProperty<?> property){
		try{
			world.getBlockState(pos).getValue(property);
			return true;
		}catch(IllegalArgumentException e){
			return false;
		}
	}

	public static <T extends Comparable<T>> T getBlockProperty(IBlockAccess world, BlockPos pos, IProperty<T> property){
		return world.getBlockState(pos).getValue(property);
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

	public static BlockPos toPos(int x, int y, int z){
		return new BlockPos(x, y, z);
	}

	public static int[] fromPos(BlockPos pos){
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

				return activator.isPowering(world, offsetPos, offsetState, offsetTe) && (!(offsetTe instanceof IOwnable) || ((IOwnable)offsetTe).getOwner().owns((IOwnable)te));
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
								return false;
						}
						else if(offsetState.getPropertyKeys().contains(BlockDirectional.FACING))
						{
							if(dirOffset != offsetState.getValue(BlockDirectional.FACING))
								return false;
						}

						TileEntity offsetTe = world.getTileEntity(newOffsetPos);

						return activator.isPowering(world, newOffsetPos, offsetState, offsetTe) && (!(offsetTe instanceof IOwnable) || ((IOwnable)offsetTe).getOwner().owns((IOwnable)te));
					}
				}
			}
		}

		return false;
	}

	public static <T> T getProtectedCapability(EnumFacing side, TileEntity te, Supplier<T> extractionPermittedHandler, Supplier<T> insertOnlyHandler)
	{
		if(side == null)
			return (T)EMPTY_INVENTORY;

		BlockPos offsetPos = te.getPos().offset(side);
		IBlockState offsetState = te.getWorld().getBlockState(offsetPos);

		for(IExtractionBlock extractionBlock : SecurityCraftAPI.getRegisteredExtractionBlocks())
		{
			if(offsetState.getBlock() == extractionBlock.getBlock())
			{
				if(!extractionBlock.canExtract((IOwnable)te, te.getWorld(), offsetPos, offsetState))
					return (T)EMPTY_INVENTORY;
				else return extractionPermittedHandler.get();
			}
		}

		return insertOnlyHandler.get();
	}
}
