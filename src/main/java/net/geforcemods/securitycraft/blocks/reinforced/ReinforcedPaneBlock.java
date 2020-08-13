package net.geforcemods.securitycraft.blocks.reinforced;

import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.IBucketPickupHandler;
import net.minecraft.block.ILiquidContainer;
import net.minecraft.block.PaneBlock;
import net.minecraft.block.SixWayBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ReinforcedPaneBlock extends BaseReinforcedBlock implements IBucketPickupHandler, ILiquidContainer
{
	public static final BooleanProperty NORTH = SixWayBlock.NORTH;
	public static final BooleanProperty EAST = SixWayBlock.EAST;
	public static final BooleanProperty SOUTH = SixWayBlock.SOUTH;
	public static final BooleanProperty WEST = SixWayBlock.WEST;
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
	protected static final Map<Direction, BooleanProperty> FACING_TO_PROPERTY_MAP = SixWayBlock.FACING_TO_PROPERTY_MAP.entrySet().stream().filter(entry -> entry.getKey().getAxis().isHorizontal()).collect(Util.toMapCollector());
	protected final VoxelShape[] field_196410_A;
	protected final VoxelShape[] field_196412_B;

	public ReinforcedPaneBlock(SoundType soundType, Material mat, Block vB)
	{
		super(soundType, mat, vB);
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

	@Override
	public Fluid pickupFluid(IWorld world, BlockPos pos, BlockState state)
	{
		if(state.get(WATERLOGGED))
		{
			world.setBlockState(pos, state.with(WATERLOGGED, false), 3);
			return Fluids.WATER;
		}
		else
			return Fluids.EMPTY;
	}

	@Override
	public FluidState getFluidState(BlockState state)
	{
		return state.get(WATERLOGGED) ? Fluids.WATER.getStillFluidState(false) : super.getFluidState(state);
	}

	@Override
	public boolean canContainFluid(IBlockReader world, BlockPos pos, BlockState state, Fluid fluid)
	{
		return !state.get(WATERLOGGED) && fluid == Fluids.WATER;
	}

	@Override
	public boolean receiveFluid(IWorld world, BlockPos pos, BlockState state, FluidState fluidState)
	{
		if(!state.get(WATERLOGGED) && fluidState.getFluid() == Fluids.WATER)
		{
			if(!world.isRemote())
			{
				world.setBlockState(pos, state.with(WATERLOGGED, true), 3);
				world.getPendingFluidTicks().scheduleTick(pos, fluidState.getFluid(), fluidState.getFluid().getTickRate(world));
			}

			return true;
		}
		else
			return false;
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext ctx)
	{
		return field_196412_B[getIndex(state)];
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext ctx)
	{
		return field_196410_A[getIndex(state)];
	}

	private static int getMask(Direction facing)
	{
		return 1 << facing.getHorizontalIndex();
	}

	protected int getIndex(BlockState p_196406_1_)
	{
		int i = 0;

		if(p_196406_1_.get(NORTH))
			i |= getMask(Direction.NORTH);

		if(p_196406_1_.get(EAST))
			i |= getMask(Direction.EAST);

		if(p_196406_1_.get(SOUTH))
			i |= getMask(Direction.SOUTH);

		if(p_196406_1_.get(WEST))
			i |= getMask(Direction.WEST);

		return i;
	}

	@Override
	public boolean allowsMovement(BlockState state, IBlockReader worldIn, BlockPos pos, PathType type)
	{
		return false;
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rot)
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
	public BlockState mirror(BlockState state, Mirror mirror)
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

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		return getStateForPlacement(context.getWorld(), context.getPos());
	}

	public BlockState getStateForPlacement(IBlockReader world, BlockPos pos)
	{
		FluidState fluidState = world.getFluidState(pos);
		BlockPos northPos = pos.north();
		BlockPos southPos = pos.south();
		BlockPos westPos = pos.west();
		BlockPos eastPos = pos.east();
		BlockState northState = world.getBlockState(northPos);
		BlockState southState = world.getBlockState(southPos);
		BlockState westState = world.getBlockState(westPos);
		BlockState eastState = world.getBlockState(eastPos);
		return getDefaultState().with(NORTH, canAttachTo(northState, northState.isSolidSide(world, northPos, Direction.SOUTH))).with(SOUTH, canAttachTo(southState, southState.isSolidSide(world, southPos, Direction.NORTH))).with(WEST, canAttachTo(westState, westState.isSolidSide(world, westPos, Direction.EAST))).with(EAST, canAttachTo(eastState, eastState.isSolidSide(world, eastPos, Direction.WEST))).with(WATERLOGGED, fluidState.getFluid() == Fluids.WATER);
	}

	@Override
	public BlockState updatePostPlacement(BlockState state, Direction facing, BlockState facingState, IWorld world, BlockPos currentPos, BlockPos facingPos)
	{
		if(state.get(WATERLOGGED))
			world.getPendingFluidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickRate(world));

		return facing.getAxis().isHorizontal() ? state.with(FACING_TO_PROPERTY_MAP.get(facing), canAttachTo(facingState, facingState.isSolidSide(world, facingPos, facing.getOpposite()))) : super.updatePostPlacement(state, facing, facingState, world, currentPos, facingPos);
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public boolean isSideInvisible(BlockState state, BlockState adjacentBlockState, Direction side)
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

	public final boolean canAttachTo(BlockState p_220112_1_, boolean p_220112_2_) {
		Block block = p_220112_1_.getBlock();
		return !cannotAttach(block) && p_220112_2_ || block instanceof ReinforcedPaneBlock;
	}

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
	{
		builder.add(NORTH, EAST, WEST, SOUTH, WATERLOGGED);
	}

	@Override
	public BlockState getConvertedState(BlockState vanillaState)
	{
		return getDefaultState().with(NORTH, vanillaState.get(PaneBlock.NORTH)).with(EAST, vanillaState.get(PaneBlock.EAST)).with(WEST, vanillaState.get(PaneBlock.WEST)).with(SOUTH, vanillaState.get(PaneBlock.SOUTH)).with(WATERLOGGED, vanillaState.get(PaneBlock.WATERLOGGED));
	}
}