package net.geforcemods.securitycraft.blocks.reinforced;

import java.util.Random;
import java.util.function.Supplier;
import java.util.stream.IntStream;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.StairsShape;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ReinforcedStairsBlock extends BaseReinforcedBlock implements SimpleWaterloggedBlock
{
	public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
	public static final EnumProperty<Half> HALF = BlockStateProperties.HALF;
	public static final EnumProperty<StairsShape> SHAPE = BlockStateProperties.STAIRS_SHAPE;
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
	protected static final VoxelShape AABB_SLAB_TOP = Block.box(0.0D, 8.0D, 0.0D, 16.0D, 16.0D, 16.0D);
	protected static final VoxelShape AABB_SLAB_BOTTOM = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D);
	protected static final VoxelShape NWD_CORNER = Block.box(0.0D, 0.0D, 0.0D, 8.0D, 8.0D, 8.0D);
	protected static final VoxelShape SWD_CORNER = Block.box(0.0D, 0.0D, 8.0D, 8.0D, 8.0D, 16.0D);
	protected static final VoxelShape NWU_CORNER = Block.box(0.0D, 8.0D, 0.0D, 8.0D, 16.0D, 8.0D);
	protected static final VoxelShape SWU_CORNER = Block.box(0.0D, 8.0D, 8.0D, 8.0D, 16.0D, 16.0D);
	protected static final VoxelShape NED_CORNER = Block.box(8.0D, 0.0D, 0.0D, 16.0D, 8.0D, 8.0D);
	protected static final VoxelShape SED_CORNER = Block.box(8.0D, 0.0D, 8.0D, 16.0D, 8.0D, 16.0D);
	protected static final VoxelShape NEU_CORNER = Block.box(8.0D, 8.0D, 0.0D, 16.0D, 16.0D, 8.0D);
	protected static final VoxelShape SEU_CORNER = Block.box(8.0D, 8.0D, 8.0D, 16.0D, 16.0D, 16.0D);
	protected static final VoxelShape[] SLAB_TOP_SHAPES = makeShapes(AABB_SLAB_TOP, NWD_CORNER, NED_CORNER, SWD_CORNER, SED_CORNER);
	protected static final VoxelShape[] SLAB_BOTTOM_SHAPES = makeShapes(AABB_SLAB_BOTTOM, NWU_CORNER, NEU_CORNER, SWU_CORNER, SEU_CORNER);
	private static final int[] SHAPE_BY_STATE = {12, 5, 3, 10, 14, 13, 7, 11, 13, 7, 11, 14, 8, 4, 1, 2, 4, 1, 2, 8};
	private final Block modelBlock;
	private final BlockState modelState;

	public ReinforcedStairsBlock(Block.Properties properties, Block vB)
	{
		this(properties, () -> vB);
	}

	public ReinforcedStairsBlock(Block.Properties properties, Supplier<Block> vB)
	{
		super(properties, vB);

		registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(HALF, Half.BOTTOM).setValue(SHAPE, StairsShape.STRAIGHT).setValue(WATERLOGGED, false));
		modelBlock = getVanillaBlock();
		modelState = modelBlock.defaultBlockState();
	}

	private static VoxelShape[] makeShapes(VoxelShape slabShape, VoxelShape nwCorner, VoxelShape neCorner, VoxelShape swCorner, VoxelShape seCorner)
	{
		return IntStream.range(0, 16).mapToObj(shape -> combineShapes(shape, slabShape, nwCorner, neCorner, swCorner, seCorner)).toArray(size -> new VoxelShape[size]);
	}

	private static VoxelShape combineShapes(int bitfield, VoxelShape slabShape, VoxelShape nwCorner, VoxelShape neCorner, VoxelShape swCorner, VoxelShape seCorner)
	{
		VoxelShape shape = slabShape;

		if((bitfield & 1) != 0)
			shape = Shapes.or(slabShape, nwCorner);

		if((bitfield & 2) != 0)
			shape = Shapes.or(shape, neCorner);

		if((bitfield & 4) != 0)
			shape = Shapes.or(shape, swCorner);

		if((bitfield & 8) != 0)
			shape = Shapes.or(shape, seCorner);

		return shape;
	}

	@Override
	public boolean useShapeForLightOcclusion(BlockState state)
	{
		return true;
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context)
	{
		return (state.getValue(HALF) == Half.TOP ? SLAB_TOP_SHAPES : SLAB_BOTTOM_SHAPES)[SHAPE_BY_STATE[getShapeIndex(state)]];
	}

	private int getShapeIndex(BlockState state)
	{
		return state.getValue(SHAPE).ordinal() * 4 + state.getValue(FACING).get2DDataValue();
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void animateTick(BlockState stateIn, Level world, BlockPos pos, Random rand)
	{
		modelBlock.animateTick(stateIn, world, pos, rand);
	}

	@Override
	public void attack(BlockState state, Level world, BlockPos pos, Player player)
	{
		modelState.attack(world, pos, player);
	}

	@Override
	public void destroy(LevelAccessor world, BlockPos pos, BlockState state)
	{
		modelBlock.destroy(world, pos, state);
	}

	@Override
	public void onPlace(BlockState state, Level world, BlockPos pos, BlockState oldState, boolean isMoving)
	{
		if(state.getBlock() != oldState.getBlock())
		{
			modelState.neighborChanged(world, pos, Blocks.AIR, pos, false);
			modelBlock.onPlace(modelState, world, pos, oldState, false);
		}
	}

	@Override
	public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean isMoving)
	{
		if(state.getBlock() != newState.getBlock())
			modelState.onRemove(world, pos, newState, isMoving);
	}

	@Override
	public void stepOn(Level world, BlockPos pos, BlockState state, Entity entity)
	{
		modelBlock.stepOn(world, pos, state, entity);
	}

	@Override
	public void tick(BlockState state, ServerLevel world, BlockPos pos, Random random)
	{
		modelState.tick(world, pos, random);
	}

	@Override
	public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit)
	{
		return modelState.use(world, player, hand, hit);
	}

	@Override
	public void wasExploded(Level world, BlockPos pos, Explosion explosion)
	{
		modelBlock.wasExploded(world, pos, explosion);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext ctx)
	{
		Direction dir = ctx.getClickedFace();
		BlockPos pos = ctx.getClickedPos();
		FluidState fluidState = ctx.getLevel().getFluidState(pos);
		BlockState state = defaultBlockState().setValue(FACING, ctx.getHorizontalDirection()).setValue(HALF, dir != Direction.DOWN && (dir == Direction.UP || !(ctx.getClickLocation().y - pos.getY() > 0.5D)) ? Half.BOTTOM : Half.TOP).setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER);

		return state.setValue(SHAPE, getShapeProperty(state, ctx.getLevel(), pos));
	}

	@Override
	public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor world, BlockPos currentPos, BlockPos facingPos)
	{
		if(state.getValue(WATERLOGGED))
			world.getLiquidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(world));

		return facing.getAxis().isHorizontal() ? state.setValue(SHAPE, getShapeProperty(state, world, currentPos)) : super.updateShape(state, facing, facingState, world, currentPos, facingPos);
	}

	private static StairsShape getShapeProperty(BlockState state, BlockGetter world, BlockPos pos)
	{
		Direction dir = state.getValue(FACING);
		BlockState offsetState = world.getBlockState(pos.relative(dir));

		if(isBlockStairs(offsetState) && state.getValue(HALF) == offsetState.getValue(HALF))
		{
			Direction offsetDir = offsetState.getValue(FACING);

			if(offsetDir.getAxis() != state.getValue(FACING).getAxis() && isDifferentStairs(state, world, pos, offsetDir.getOpposite()))
			{
				if(offsetDir == dir.getCounterClockWise())
					return StairsShape.OUTER_LEFT;
				else return StairsShape.OUTER_RIGHT;
			}
		}

		BlockState offsetOppositeState = world.getBlockState(pos.relative(dir.getOpposite()));

		if (isBlockStairs(offsetOppositeState) && state.getValue(HALF) == offsetOppositeState.getValue(HALF))
		{
			Direction offsetOppositeDir = offsetOppositeState.getValue(FACING);

			if(offsetOppositeDir.getAxis() != state.getValue(FACING).getAxis() && isDifferentStairs(state, world, pos, offsetOppositeDir))
			{
				if(offsetOppositeDir == dir.getCounterClockWise())
					return StairsShape.INNER_LEFT;
				else return StairsShape.INNER_RIGHT;
			}
		}

		return StairsShape.STRAIGHT;
	}

	private static boolean isDifferentStairs(BlockState state, BlockGetter world, BlockPos pos, Direction face)
	{
		BlockState offsetState = world.getBlockState(pos.relative(face));

		return !isBlockStairs(offsetState) || offsetState.getValue(FACING) != state.getValue(FACING) || offsetState.getValue(HALF) != state.getValue(HALF);
	}

	public static boolean isBlockStairs(BlockState state)
	{
		return state.getBlock() instanceof ReinforcedStairsBlock || state.getBlock() instanceof StairBlock;
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rot)
	{
		return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
	}

	@Override
	public BlockState mirror(BlockState state, Mirror mirror)
	{
		Direction direction = state.getValue(FACING);
		StairsShape shape = state.getValue(SHAPE);

		switch(mirror)
		{
			case LEFT_RIGHT:
				if (direction.getAxis() == Direction.Axis.Z)
				{
					switch(shape)
					{
						case INNER_LEFT:
							return state.rotate(Rotation.CLOCKWISE_180).setValue(SHAPE, StairsShape.INNER_RIGHT);
						case INNER_RIGHT:
							return state.rotate(Rotation.CLOCKWISE_180).setValue(SHAPE, StairsShape.INNER_LEFT);
						case OUTER_LEFT:
							return state.rotate(Rotation.CLOCKWISE_180).setValue(SHAPE, StairsShape.OUTER_RIGHT);
						case OUTER_RIGHT:
							return state.rotate(Rotation.CLOCKWISE_180).setValue(SHAPE, StairsShape.OUTER_LEFT);
						default:
							return state.rotate(Rotation.CLOCKWISE_180);
					}
				}
				break;
			case FRONT_BACK:
				if (direction.getAxis() == Direction.Axis.X)
				{
					switch(shape)
					{
						case INNER_LEFT:
							return state.rotate(Rotation.CLOCKWISE_180).setValue(SHAPE, StairsShape.INNER_LEFT);
						case INNER_RIGHT:
							return state.rotate(Rotation.CLOCKWISE_180).setValue(SHAPE, StairsShape.INNER_RIGHT);
						case OUTER_LEFT:
							return state.rotate(Rotation.CLOCKWISE_180).setValue(SHAPE, StairsShape.OUTER_RIGHT);
						case OUTER_RIGHT:
							return state.rotate(Rotation.CLOCKWISE_180).setValue(SHAPE, StairsShape.OUTER_LEFT);
						case STRAIGHT:
							return state.rotate(Rotation.CLOCKWISE_180);
					}
				}
				break;
			default:
				break;
		}

		return super.mirror(state, mirror);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
	{
		builder.add(FACING, HALF, SHAPE, WATERLOGGED);
	}

	@Override
	public FluidState getFluidState(BlockState state) {
		return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
	}

	@Override
	public boolean isPathfindable(BlockState state, BlockGetter world, BlockPos pos, PathComputationType type)
	{
		return false;
	}

	@Override
	public BlockState getConvertedState(BlockState vanillaState)
	{
		return defaultBlockState().setValue(SHAPE, vanillaState.getValue(SHAPE)).setValue(FACING, vanillaState.getValue(FACING)).setValue(HALF, vanillaState.getValue(HALF)).setValue(WATERLOGGED, vanillaState.getValue(WATERLOGGED));
	}
}