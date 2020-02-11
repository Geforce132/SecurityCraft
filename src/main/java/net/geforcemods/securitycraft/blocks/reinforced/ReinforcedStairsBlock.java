package net.geforcemods.securitycraft.blocks.reinforced;

import java.util.Random;
import java.util.stream.IntStream;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.block.SoundType;
import net.minecraft.block.StairsBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.Half;
import net.minecraft.state.properties.StairsShape;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ReinforcedStairsBlock extends BaseReinforcedBlock implements IWaterLoggable
{
	public static final DirectionProperty FACING = HorizontalBlock.HORIZONTAL_FACING;
	public static final EnumProperty<Half> HALF = BlockStateProperties.HALF;
	public static final EnumProperty<StairsShape> SHAPE = BlockStateProperties.STAIRS_SHAPE;
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
	protected static final VoxelShape AABB_SLAB_TOP = Block.makeCuboidShape(0.0D, 8.0D, 0.0D, 16.0D, 16.0D, 16.0D);
	protected static final VoxelShape AABB_SLAB_BOTTOM = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D);
	protected static final VoxelShape NWD_CORNER = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 8.0D, 8.0D, 8.0D);
	protected static final VoxelShape SWD_CORNER = Block.makeCuboidShape(0.0D, 0.0D, 8.0D, 8.0D, 8.0D, 16.0D);
	protected static final VoxelShape NWU_CORNER = Block.makeCuboidShape(0.0D, 8.0D, 0.0D, 8.0D, 16.0D, 8.0D);
	protected static final VoxelShape SWU_CORNER = Block.makeCuboidShape(0.0D, 8.0D, 8.0D, 8.0D, 16.0D, 16.0D);
	protected static final VoxelShape NED_CORNER = Block.makeCuboidShape(8.0D, 0.0D, 0.0D, 16.0D, 8.0D, 8.0D);
	protected static final VoxelShape SED_CORNER = Block.makeCuboidShape(8.0D, 0.0D, 8.0D, 16.0D, 8.0D, 16.0D);
	protected static final VoxelShape NEU_CORNER = Block.makeCuboidShape(8.0D, 8.0D, 0.0D, 16.0D, 16.0D, 8.0D);
	protected static final VoxelShape SEU_CORNER = Block.makeCuboidShape(8.0D, 8.0D, 8.0D, 16.0D, 16.0D, 16.0D);
	protected static final VoxelShape[] SLAB_TOP_SHAPES = makeShapes(AABB_SLAB_TOP, NWD_CORNER, NED_CORNER, SWD_CORNER, SED_CORNER);
	protected static final VoxelShape[] SLAB_BOTTOM_SHAPES = makeShapes(AABB_SLAB_BOTTOM, NWU_CORNER, NEU_CORNER, SWU_CORNER, SEU_CORNER);
	private static final int[] field_196522_K = new int[]{12, 5, 3, 10, 14, 13, 7, 11, 13, 7, 11, 14, 8, 4, 1, 2, 4, 1, 2, 8};
	private final Block modelBlock;
	private final BlockState modelState;

	public ReinforcedStairsBlock(SoundType soundType, Material mat, Block vB, String registryPath)
	{
		super(soundType, mat, vB, registryPath, 0);

		setDefaultState(stateContainer.getBaseState().with(FACING, Direction.NORTH).with(HALF, Half.BOTTOM).with(SHAPE, StairsShape.STRAIGHT).with(WATERLOGGED, false));
		modelBlock = getVanillaBlock();
		modelState = modelBlock.getDefaultState();
	}

	private static VoxelShape[] makeShapes(VoxelShape slabShape, VoxelShape nwCorner, VoxelShape neCorner, VoxelShape swCorner, VoxelShape seCorner)
	{
		return IntStream.range(0, 16).mapToObj((p_199780_5_) -> combineShapes(p_199780_5_, slabShape, nwCorner, neCorner, swCorner, seCorner)).toArray((p_199778_0_) -> new VoxelShape[p_199778_0_]);
	}

	private static VoxelShape combineShapes(int bitfield, VoxelShape slabShape, VoxelShape nwCorner, VoxelShape neCorner, VoxelShape swCorner, VoxelShape seCorner)
	{
		VoxelShape shape = slabShape;

		if((bitfield & 1) != 0)
			shape = VoxelShapes.or(slabShape, nwCorner);

		if((bitfield & 2) != 0)
			shape = VoxelShapes.or(shape, neCorner);

		if((bitfield & 4) != 0)
			shape = VoxelShapes.or(shape, swCorner);

		if((bitfield & 8) != 0)
			shape = VoxelShapes.or(shape, seCorner);

		return shape;
	}

	@Override
	public boolean isTransparent(BlockState state)
	{
		return true;
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
	{
		return (state.get(HALF) == Half.TOP ? SLAB_TOP_SHAPES : SLAB_BOTTOM_SHAPES)[field_196522_K[func_196511_x(state)]];
	}

	private int func_196511_x(BlockState state)
	{
		return state.get(SHAPE).ordinal() * 4 + state.get(FACING).getHorizontalIndex();
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand)
	{
		modelBlock.animateTick(stateIn, worldIn, pos, rand);
	}

	@Override
	public void onBlockClicked(BlockState state, World worldIn, BlockPos pos, PlayerEntity player)
	{
		modelState.onBlockClicked(worldIn, pos, player);
	}

	@Override
	public void onPlayerDestroy(IWorld worldIn, BlockPos pos, BlockState state)
	{
		modelBlock.onPlayerDestroy(worldIn, pos, state);
	}

	@Override
	public float getExplosionResistance()
	{
		return modelBlock.getExplosionResistance();
	}

	@Override
	public int tickRate(IWorldReader world)
	{
		return modelBlock.tickRate(world);
	}

	@Override
	public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean isMoving)
	{
		if(state.getBlock() != state.getBlock())
		{
			modelState.neighborChanged(world, pos, Blocks.AIR, pos, false);
			modelBlock.onBlockAdded(modelState, world, pos, oldState, false);
		}
	}

	@Override
	public void onReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving)
	{
		if(state.getBlock() != newState.getBlock())
			modelState.onReplaced(world, pos, newState, isMoving);
	}

	@Override
	public void onEntityWalk(World worldIn, BlockPos pos, Entity entity)
	{
		modelBlock.onEntityWalk(worldIn, pos, entity);
	}

	@Override
	public void tick(BlockState state, ServerWorld world, BlockPos pos, Random random)
	{
		modelState.tick(world, pos, random);
	}

	@Override
	public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit)
	{
		return modelState.onBlockActivated(world, player, hand, hit);
	}

	@Override
	public void onExplosionDestroy(World world, BlockPos pos, Explosion explosion)
	{
		modelBlock.onExplosionDestroy(world, pos, explosion);
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext ctx)
	{
		Direction dir = ctx.getFace();
		BlockPos pos = ctx.getPos();
		IFluidState fluidState = ctx.getWorld().getFluidState(pos);
		BlockState state = this.getDefaultState().with(FACING, ctx.getPlacementHorizontalFacing()).with(HALF, dir != Direction.DOWN && (dir == Direction.UP || !(ctx.getHitVec().y - pos.getY() > 0.5D)) ? Half.BOTTOM : Half.TOP).with(WATERLOGGED, Boolean.valueOf(fluidState.getFluid() == Fluids.WATER));

		return state.with(SHAPE, getShapeProperty(state, ctx.getWorld(), pos));
	}

	@Override
	public BlockState updatePostPlacement(BlockState state, Direction facing, BlockState facingState, IWorld world, BlockPos currentPos, BlockPos facingPos)
	{
		if(state.get(WATERLOGGED))
			world.getPendingFluidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickRate(world));

		return facing.getAxis().isHorizontal() ? state.with(SHAPE, getShapeProperty(state, world, currentPos)) : super.updatePostPlacement(state, facing, facingState, world, currentPos, facingPos);
	}

	private static StairsShape getShapeProperty(BlockState state, IBlockReader world, BlockPos pos)
	{
		Direction dir = state.get(FACING);
		BlockState offsetState = world.getBlockState(pos.offset(dir));

		if(isBlockStairs(offsetState) && state.get(HALF) == offsetState.get(HALF))
		{
			Direction offsetDir = offsetState.get(FACING);

			if(offsetDir.getAxis() != state.get(FACING).getAxis() && isDifferentStairs(state, world, pos, offsetDir.getOpposite()))
			{
				if(offsetDir == dir.rotateYCCW())
					return StairsShape.OUTER_LEFT;
				else return StairsShape.OUTER_RIGHT;
			}
		}

		BlockState offsetOppositeState = world.getBlockState(pos.offset(dir.getOpposite()));

		if (isBlockStairs(offsetOppositeState) && state.get(HALF) == offsetOppositeState.get(HALF))
		{
			Direction offsetOppositeDir = offsetOppositeState.get(FACING);

			if(offsetOppositeDir.getAxis() != state.get(FACING).getAxis() && isDifferentStairs(state, world, pos, offsetOppositeDir))
			{
				if(offsetOppositeDir == dir.rotateYCCW())
					return StairsShape.INNER_LEFT;
				else return StairsShape.INNER_RIGHT;
			}
		}

		return StairsShape.STRAIGHT;
	}

	private static boolean isDifferentStairs(BlockState state, IBlockReader world, BlockPos pos, Direction face)
	{
		BlockState offsetState = world.getBlockState(pos.offset(face));

		return !isBlockStairs(offsetState) || offsetState.get(FACING) != state.get(FACING) || offsetState.get(HALF) != state.get(HALF);
	}

	public static boolean isBlockStairs(BlockState state)
	{
		return state.getBlock() instanceof ReinforcedStairsBlock || state.getBlock() instanceof StairsBlock;
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rot)
	{
		return state.with(FACING, rot.rotate(state.get(FACING)));
	}

	@Override
	public BlockState mirror(BlockState state, Mirror mirror)
	{
		Direction direction = state.get(FACING);
		StairsShape shape = state.get(SHAPE);

		switch(mirror)
		{
			case LEFT_RIGHT:
				if (direction.getAxis() == Direction.Axis.Z)
				{
					switch(shape)
					{
						case INNER_LEFT:
							return state.rotate(Rotation.CLOCKWISE_180).with(SHAPE, StairsShape.INNER_RIGHT);
						case INNER_RIGHT:
							return state.rotate(Rotation.CLOCKWISE_180).with(SHAPE, StairsShape.INNER_LEFT);
						case OUTER_LEFT:
							return state.rotate(Rotation.CLOCKWISE_180).with(SHAPE, StairsShape.OUTER_RIGHT);
						case OUTER_RIGHT:
							return state.rotate(Rotation.CLOCKWISE_180).with(SHAPE, StairsShape.OUTER_LEFT);
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
							return state.rotate(Rotation.CLOCKWISE_180).with(SHAPE, StairsShape.INNER_LEFT);
						case INNER_RIGHT:
							return state.rotate(Rotation.CLOCKWISE_180).with(SHAPE, StairsShape.INNER_RIGHT);
						case OUTER_LEFT:
							return state.rotate(Rotation.CLOCKWISE_180).with(SHAPE, StairsShape.OUTER_RIGHT);
						case OUTER_RIGHT:
							return state.rotate(Rotation.CLOCKWISE_180).with(SHAPE, StairsShape.OUTER_LEFT);
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
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
	{
		builder.add(FACING, HALF, SHAPE, WATERLOGGED);
	}

	@Override
	public IFluidState getFluidState(BlockState state) {
		return state.get(WATERLOGGED) ? Fluids.WATER.getStillFluidState(false) : super.getFluidState(state);
	}

	@Override
	public boolean allowsMovement(BlockState state, IBlockReader world, BlockPos pos, PathType type)
	{
		return false;
	}

	@Override
	public BlockState getConvertedState(BlockState vanillaState)
	{
		return getDefaultState().with(SHAPE, vanillaState.get(SHAPE)).with(FACING, vanillaState.get(FACING)).with(HALF, vanillaState.get(HALF)).with(WATERLOGGED, vanillaState.get(WATERLOGGED));
	}
}