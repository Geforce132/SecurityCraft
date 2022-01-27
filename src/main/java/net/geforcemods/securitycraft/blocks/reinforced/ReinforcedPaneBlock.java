package net.geforcemods.securitycraft.blocks.reinforced;

import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.IBucketPickupHandler;
import net.minecraft.block.ILiquidContainer;
import net.minecraft.block.PaneBlock;
import net.minecraft.block.SixWayBlock;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;
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

public class ReinforcedPaneBlock extends BaseReinforcedBlock implements IBucketPickupHandler, ILiquidContainer {
	public static final BooleanProperty NORTH = SixWayBlock.NORTH;
	public static final BooleanProperty EAST = SixWayBlock.EAST;
	public static final BooleanProperty SOUTH = SixWayBlock.SOUTH;
	public static final BooleanProperty WEST = SixWayBlock.WEST;
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
	protected static final Map<Direction, BooleanProperty> FACING_TO_PROPERTY_MAP = SixWayBlock.PROPERTY_BY_DIRECTION.entrySet().stream().filter(entry -> entry.getKey().getAxis().isHorizontal()).collect(Util.toMap());
	protected final VoxelShape[] collisionShapeByIndex;
	protected final VoxelShape[] shapeByIndex;

	public ReinforcedPaneBlock(Block.Properties properties, Block vB) {
		super(properties, vB);
		collisionShapeByIndex = makeShapes(1.0F, 1.0F, 16.0F, 0.0F, 16.0F);
		shapeByIndex = makeShapes(1.0F, 1.0F, 16.0F, 0.0F, 16.0F);
		registerDefaultState(stateDefinition.any().setValue(NORTH, false).setValue(EAST, false).setValue(SOUTH, false).setValue(WEST, false).setValue(WATERLOGGED, false));
	}

	protected VoxelShape[] makeShapes(float p_196408_1_, float p_196408_2_, float p_196408_3_, float p_196408_4_, float p_196408_5_) {
		float f = 8.0F - p_196408_1_;
		float f1 = 8.0F + p_196408_1_;
		float f2 = 8.0F - p_196408_2_;
		float f3 = 8.0F + p_196408_2_;
		VoxelShape voxelshape = Block.box(f, 0.0D, f, f1, p_196408_3_, f1);
		VoxelShape voxelshape1 = Block.box(f2, p_196408_4_, 0.0D, f3, p_196408_5_, f3);
		VoxelShape voxelshape2 = Block.box(f2, p_196408_4_, f2, f3, p_196408_5_, 16.0D);
		VoxelShape voxelshape3 = Block.box(0.0D, p_196408_4_, f2, f3, p_196408_5_, f3);
		VoxelShape voxelshape4 = Block.box(f2, p_196408_4_, f2, 16.0D, p_196408_5_, f3);
		VoxelShape voxelshape5 = VoxelShapes.or(voxelshape1, voxelshape4);
		VoxelShape voxelshape6 = VoxelShapes.or(voxelshape2, voxelshape3);
		VoxelShape[] avoxelshape = {
				VoxelShapes.empty(), voxelshape2, voxelshape3, voxelshape6, voxelshape1, VoxelShapes.or(voxelshape2, voxelshape1), VoxelShapes.or(voxelshape3, voxelshape1), VoxelShapes.or(voxelshape6, voxelshape1), voxelshape4, VoxelShapes.or(voxelshape2, voxelshape4), VoxelShapes.or(voxelshape3, voxelshape4), VoxelShapes.or(voxelshape6, voxelshape4), voxelshape5, VoxelShapes.or(voxelshape2, voxelshape5), VoxelShapes.or(voxelshape3, voxelshape5), VoxelShapes.or(voxelshape6, voxelshape5)
		};

		for (int i = 0; i < 16; ++i) {
			avoxelshape[i] = VoxelShapes.or(voxelshape, avoxelshape[i]);
		}

		return avoxelshape;
	}

	@Override
	public Fluid takeLiquid(IWorld world, BlockPos pos, BlockState state) {
		if (state.getValue(WATERLOGGED)) {
			world.setBlock(pos, state.setValue(WATERLOGGED, false), 3);
			return Fluids.WATER;
		}
		else
			return Fluids.EMPTY;
	}

	@Override
	public IFluidState getFluidState(BlockState state) {
		return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
	}

	@Override
	public boolean canPlaceLiquid(IBlockReader world, BlockPos pos, BlockState state, Fluid fluid) {
		return !state.getValue(WATERLOGGED) && fluid == Fluids.WATER;
	}

	@Override
	public boolean placeLiquid(IWorld world, BlockPos pos, BlockState state, IFluidState fluidState) {
		if (!state.getValue(WATERLOGGED) && fluidState.getType() == Fluids.WATER) {
			if (!world.isClientSide()) {
				world.setBlock(pos, state.setValue(WATERLOGGED, true), 3);
				world.getLiquidTicks().scheduleTick(pos, fluidState.getType(), fluidState.getType().getTickDelay(world));
			}

			return true;
		}
		else
			return false;
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext ctx) {
		return shapeByIndex[getIndex(state)];
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext ctx) {
		return collisionShapeByIndex[getIndex(state)];
	}

	private static int getMask(Direction facing) {
		return 1 << facing.get2DDataValue();
	}

	protected int getIndex(BlockState p_196406_1_) {
		int i = 0;

		if (p_196406_1_.getValue(NORTH))
			i |= getMask(Direction.NORTH);

		if (p_196406_1_.getValue(EAST))
			i |= getMask(Direction.EAST);

		if (p_196406_1_.getValue(SOUTH))
			i |= getMask(Direction.SOUTH);

		if (p_196406_1_.getValue(WEST))
			i |= getMask(Direction.WEST);

		return i;
	}

	@Override
	public boolean isPathfindable(BlockState state, IBlockReader worldIn, BlockPos pos, PathType type) {
		return false;
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rot) {
		switch (rot) {
			case CLOCKWISE_180:
				return state.setValue(NORTH, state.getValue(SOUTH)).setValue(EAST, state.getValue(WEST)).setValue(SOUTH, state.getValue(NORTH)).setValue(WEST, state.getValue(EAST));
			case COUNTERCLOCKWISE_90:
				return state.setValue(NORTH, state.getValue(EAST)).setValue(EAST, state.getValue(SOUTH)).setValue(SOUTH, state.getValue(WEST)).setValue(WEST, state.getValue(NORTH));
			case CLOCKWISE_90:
				return state.setValue(NORTH, state.getValue(WEST)).setValue(EAST, state.getValue(NORTH)).setValue(SOUTH, state.getValue(EAST)).setValue(WEST, state.getValue(SOUTH));
			default:
				return state;
		}
	}

	@Override
	public BlockState mirror(BlockState state, Mirror mirror) {
		switch (mirror) {
			case LEFT_RIGHT:
				return state.setValue(NORTH, state.getValue(SOUTH)).setValue(SOUTH, state.getValue(NORTH));
			case FRONT_BACK:
				return state.setValue(EAST, state.getValue(WEST)).setValue(WEST, state.getValue(EAST));
			default:
				return super.mirror(state, mirror);
		}
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		return getStateForPlacement(context.getLevel(), context.getClickedPos());
	}

	public BlockState getStateForPlacement(IBlockReader world, BlockPos pos) {
		IFluidState fluidState = world.getFluidState(pos);
		BlockPos northPos = pos.north();
		BlockPos southPos = pos.south();
		BlockPos westPos = pos.west();
		BlockPos eastPos = pos.east();
		BlockState northState = world.getBlockState(northPos);
		BlockState southState = world.getBlockState(southPos);
		BlockState westState = world.getBlockState(westPos);
		BlockState eastState = world.getBlockState(eastPos);
		return defaultBlockState().setValue(NORTH, canAttachTo(northState, Block.isFaceSturdy(northState, world, northPos, Direction.SOUTH))).setValue(SOUTH, canAttachTo(southState, Block.isFaceSturdy(southState, world, southPos, Direction.NORTH))).setValue(WEST, canAttachTo(westState, Block.isFaceSturdy(westState, world, westPos, Direction.EAST))).setValue(EAST, canAttachTo(eastState, Block.isFaceSturdy(eastState, world, eastPos, Direction.WEST))).setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER);
	}

	@Override
	public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, IWorld world, BlockPos currentPos, BlockPos facingPos) {
		if (state.getValue(WATERLOGGED))
			world.getLiquidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(world));

		return facing.getAxis().isHorizontal() ? state.setValue(FACING_TO_PROPERTY_MAP.get(facing), canAttachTo(facingState, Block.isFaceSturdy(facingState, world, facingPos, facing.getOpposite()))) : super.updateShape(state, facing, facingState, world, currentPos, facingPos);
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public boolean skipRendering(BlockState state, BlockState adjacentBlockState, Direction side) {
		if (adjacentBlockState.getBlock() == this) {
			if (!side.getAxis().isHorizontal())
				return true;

			if (state.getValue(FACING_TO_PROPERTY_MAP.get(side)) && adjacentBlockState.getValue(FACING_TO_PROPERTY_MAP.get(side.getOpposite())))
				return true;
		}

		return super.skipRendering(state, adjacentBlockState, side);
	}

	public final boolean canAttachTo(BlockState p_220112_1_, boolean p_220112_2_) {
		Block block = p_220112_1_.getBlock();
		return !isExceptionForConnection(block) && p_220112_2_ || block instanceof ReinforcedPaneBlock;
	}

	@Override
	protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(NORTH, EAST, WEST, SOUTH, WATERLOGGED);
	}

	@Override
	public BlockState getConvertedState(BlockState vanillaState) {
		return defaultBlockState().setValue(NORTH, vanillaState.getValue(PaneBlock.NORTH)).setValue(EAST, vanillaState.getValue(PaneBlock.EAST)).setValue(WEST, vanillaState.getValue(PaneBlock.WEST)).setValue(SOUTH, vanillaState.getValue(PaneBlock.SOUTH)).setValue(WATERLOGGED, vanillaState.getValue(PaneBlock.WATERLOGGED));
	}
}