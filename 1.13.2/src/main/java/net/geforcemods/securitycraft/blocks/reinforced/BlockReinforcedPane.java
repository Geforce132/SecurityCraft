package net.geforcemods.securitycraft.blocks.reinforced;

import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockShulkerBox;
import net.minecraft.block.BlockSixWay;
import net.minecraft.block.IBucketPickupHandler;
import net.minecraft.block.ILiquidContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.IFluidState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BlockReinforcedPane extends BlockReinforcedBase implements IBucketPickupHandler, ILiquidContainer
{
	public static final BooleanProperty NORTH = BlockSixWay.NORTH;
	public static final BooleanProperty EAST = BlockSixWay.EAST;
	public static final BooleanProperty SOUTH = BlockSixWay.SOUTH;
	public static final BooleanProperty WEST = BlockSixWay.WEST;
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
	protected static final Map<EnumFacing, BooleanProperty> FACING_TO_PROPERTY_MAP = BlockSixWay.FACING_TO_PROPERTY_MAP.entrySet().stream().filter((p_199775_0_) -> {
		return p_199775_0_.getKey().getAxis().isHorizontal();
	}).collect(Util.toMapCollector());
	protected final VoxelShape[] field_196410_A;
	protected final VoxelShape[] field_196412_B;

	public BlockReinforcedPane(SoundType soundType, Material mat, Block vB, String registryPath)
	{
		super(soundType, mat, vB, registryPath, 0);
		field_196410_A = func_196408_a(1.0F, 1.0F, 16.0F, 0.0F, 16.0F);
		field_196412_B = func_196408_a(1.0F, 1.0F, 16.0F, 0.0F, 16.0F);
		setDefaultState(stateContainer.getBaseState().with(NORTH, false).with(EAST, false).with(SOUTH, false).with(WEST, false).with(WATERLOGGED, false));
	}

	protected VoxelShape[] func_196408_a(float p_196408_1_, float p_196408_2_, float p_196408_3_, float p_196408_4_, float p_196408_5_)
	{
		float f = 8.0F - p_196408_1_;
		float f1 = 8.0F + p_196408_1_;
		float f2 = 8.0F - p_196408_2_;
		float f3 = 8.0F + p_196408_2_;
		VoxelShape voxelshape = Block.makeCuboidShape(f, 0.0D, f, f1, p_196408_3_, f1);
		VoxelShape voxelshape1 = Block.makeCuboidShape(f2, p_196408_4_, 0.0D, f3, p_196408_5_, f3);
		VoxelShape voxelshape2 = Block.makeCuboidShape(f2, p_196408_4_, f2, f3, p_196408_5_, 16.0D);
		VoxelShape voxelshape3 = Block.makeCuboidShape(0.0D, p_196408_4_, f2, f3, p_196408_5_, f3);
		VoxelShape voxelshape4 = Block.makeCuboidShape(f2, p_196408_4_, f2, 16.0D, p_196408_5_, f3);
		VoxelShape voxelshape5 = VoxelShapes.or(voxelshape1, voxelshape4);
		VoxelShape voxelshape6 = VoxelShapes.or(voxelshape2, voxelshape3);
		VoxelShape[] avoxelshape = new VoxelShape[]{VoxelShapes.empty(), voxelshape2, voxelshape3, voxelshape6, voxelshape1, VoxelShapes.or(voxelshape2, voxelshape1), VoxelShapes.or(voxelshape3, voxelshape1), VoxelShapes.or(voxelshape6, voxelshape1), voxelshape4, VoxelShapes.or(voxelshape2, voxelshape4), VoxelShapes.or(voxelshape3, voxelshape4), VoxelShapes.or(voxelshape6, voxelshape4), voxelshape5, VoxelShapes.or(voxelshape2, voxelshape5), VoxelShapes.or(voxelshape3, voxelshape5), VoxelShapes.or(voxelshape6, voxelshape5)};

		for(int i = 0; i < 16; ++i)
		{
			avoxelshape[i] = VoxelShapes.or(voxelshape, avoxelshape[i]);
		}

		return avoxelshape;
	}

	public Fluid pickupFluid(IWorld world, BlockPos pos, IBlockState state)
	{
		if(state.get(WATERLOGGED))
		{
			world.setBlockState(pos, state.with(WATERLOGGED, false), 3);
			return Fluids.WATER;
		}
		else
			return Fluids.EMPTY;
	}

	public IFluidState getFluidState(IBlockState state)
	{
		return state.get(WATERLOGGED) ? Fluids.WATER.getStillFluidState(false) : super.getFluidState(state);
	}

	public boolean canContainFluid(IBlockReader world, BlockPos pos, IBlockState state, Fluid fluid)
	{
		return !state.get(WATERLOGGED) && fluid == Fluids.WATER;
	}

	public boolean receiveFluid(IWorld world, BlockPos pos, IBlockState state, IFluidState fluidState)
	{
		if(!state.get(WATERLOGGED) && fluidState.getFluid() == Fluids.WATER)
		{
			if(!world.isRemote())
			{
				world.setBlockState(pos, state.with(WATERLOGGED, Boolean.valueOf(true)), 3);
				world.getPendingFluidTicks().scheduleTick(pos, fluidState.getFluid(), fluidState.getFluid().getTickRate(world));
			}

			return true;
		}
		else
			return false;
	}

	@Override
	public VoxelShape getShape(IBlockState state, IBlockReader world, BlockPos pos)
	{
		return field_196412_B[getIndex(state)];
	}

	@Override
	public VoxelShape getCollisionShape(IBlockState state, IBlockReader world, BlockPos pos)
	{
		return field_196410_A[getIndex(state)];
	}

	private static int getMask(EnumFacing facing)
	{
		return 1 << facing.getHorizontalIndex();
	}

	protected int getIndex(IBlockState p_196406_1_)
	{
		int i = 0;

		if(p_196406_1_.get(NORTH))
			i |= getMask(EnumFacing.NORTH);

		if(p_196406_1_.get(EAST))
			i |= getMask(EnumFacing.EAST);

		if(p_196406_1_.get(SOUTH))
			i |= getMask(EnumFacing.SOUTH);

		if(p_196406_1_.get(WEST))
			i |= getMask(EnumFacing.WEST);

		return i;
	}

	@Override
	public boolean allowsMovement(IBlockState state, IBlockReader worldIn, BlockPos pos, PathType type)
	{
		return false;
	}

	@Override
	public IBlockState rotate(IBlockState state, Rotation rot)
	{
		switch(rot) {
			case CLOCKWISE_180:
				return state.with(NORTH, state.get(SOUTH)).with(EAST, state.get(WEST)).with(SOUTH, state.get(NORTH)).with(WEST, state.get(EAST));
			case COUNTERCLOCKWISE_90:
				return state.with(NORTH, state.get(EAST)).with(EAST, state.get(SOUTH)).with(SOUTH, state.get(WEST)).with(WEST, state.get(NORTH));
			case CLOCKWISE_90:
				return state.with(NORTH, state.get(WEST)).with(EAST, state.get(NORTH)).with(SOUTH, state.get(EAST)).with(WEST, state.get(SOUTH));
			default:
				return state;
		}
	}

	@Override
	public IBlockState mirror(IBlockState state, Mirror mirror)
	{
		switch(mirror)
		{
			case LEFT_RIGHT:
				return state.with(NORTH, state.get(SOUTH)).with(SOUTH, state.get(NORTH));
			case FRONT_BACK:
				return state.with(EAST, state.get(WEST)).with(WEST, state.get(EAST));
			default:
				return super.mirror(state, mirror);
		}
	}

	public IBlockState getStateForPlacement(BlockItemUseContext ctx)
	{
		IBlockReader iblockreader = ctx.getWorld();
		BlockPos blockpos = ctx.getPos();
		IFluidState ifluidstate = ctx.getWorld().getFluidState(ctx.getPos());
		return this.getDefaultState()
				.with(NORTH, canPaneConnectTo(iblockreader, blockpos, EnumFacing.NORTH))
				.with(SOUTH, canPaneConnectTo(iblockreader, blockpos, EnumFacing.SOUTH))
				.with(WEST, canPaneConnectTo(iblockreader, blockpos, EnumFacing.WEST))
				.with(EAST, canPaneConnectTo(iblockreader, blockpos, EnumFacing.EAST))
				.with(WATERLOGGED, Boolean.valueOf(ifluidstate.getFluid() == Fluids.WATER));
	}

	@Override
	public IBlockState updatePostPlacement(IBlockState state, EnumFacing facing, IBlockState facingState, IWorld world, BlockPos currentPos, BlockPos facingPos)
	{
		if (state.get(WATERLOGGED))
			world.getPendingFluidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickRate(world));

		return facing.getAxis().isHorizontal() ? state.with(FACING_TO_PROPERTY_MAP.get(facing), Boolean.valueOf(canPaneConnectTo(world, currentPos, facing))) : super.updatePostPlacement(state, facing, facingState, world, currentPos, facingPos);
	}

	@Override
	public boolean isFullCube(IBlockState state)
	{
		return false;
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public boolean isSideInvisible(IBlockState state, IBlockState adjacentBlockState, EnumFacing side)
	{
		if(adjacentBlockState.getBlock() == this)
		{
			if(!side.getAxis().isHorizontal())
				return true;

			if(state.get(FACING_TO_PROPERTY_MAP.get(side)) && adjacentBlockState.get(FACING_TO_PROPERTY_MAP.get(side.getOpposite())))
				return true;
		}

		return super.isSideInvisible(state, adjacentBlockState, side);
	}

	public final boolean attachesTo(IBlockState p_196417_1_, BlockFaceShape p_196417_2_)
	{
		Block block = p_196417_1_.getBlock();
		return !shouldSkipAttachment(block) && p_196417_2_ == BlockFaceShape.SOLID || p_196417_2_ == BlockFaceShape.MIDDLE_POLE_THIN;
	}

	public static boolean shouldSkipAttachment(Block p_196418_0_)
	{
		return p_196418_0_ instanceof BlockShulkerBox || p_196418_0_ instanceof BlockLeaves || p_196418_0_ == Blocks.BEACON || p_196418_0_ == Blocks.CAULDRON || p_196418_0_ == Blocks.GLOWSTONE || p_196418_0_ == Blocks.ICE || p_196418_0_ == Blocks.SEA_LANTERN || p_196418_0_ == Blocks.PISTON || p_196418_0_ == Blocks.STICKY_PISTON || p_196418_0_ == Blocks.PISTON_HEAD || p_196418_0_ == Blocks.MELON || p_196418_0_ == Blocks.PUMPKIN || p_196418_0_ == Blocks.CARVED_PUMPKIN || p_196418_0_ == Blocks.JACK_O_LANTERN || p_196418_0_ == Blocks.BARRIER;
	}

	@Override
	protected boolean canSilkHarvest()
	{
		return true;
	}

	@Override
	public BlockRenderLayer getRenderLayer()
	{
		return BlockRenderLayer.CUTOUT_MIPPED;
	}

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> builder)
	{
		builder.add(NORTH, EAST, WEST, SOUTH, WATERLOGGED);
	}

	@Override
	public BlockFaceShape getBlockFaceShape(IBlockReader worldIn, IBlockState state, BlockPos pos, EnumFacing face)
	{
		return face != EnumFacing.UP && face != EnumFacing.DOWN ? BlockFaceShape.MIDDLE_POLE_THIN : BlockFaceShape.CENTER_SMALL;
	}

	@Override
	public boolean canBeConnectedTo(IBlockState state, IBlockReader world, BlockPos pos, EnumFacing facing)
	{
		IBlockState other = world.getBlockState(pos.offset(facing));
		return attachesTo(other, other.getBlockFaceShape(world, pos.offset(facing), facing.getOpposite()));
	}

	private boolean canPaneConnectTo(IBlockReader world, BlockPos pos, EnumFacing facing)
	{
		BlockPos offset = pos.offset(facing);
		IBlockState other = world.getBlockState(offset);
		return other.canBeConnectedTo(world, offset, facing.getOpposite()) || getDefaultState().canBeConnectedTo(world, pos, facing);
	}
}