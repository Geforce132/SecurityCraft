package net.geforcemods.securitycraft.blocks.reinforced;

import java.util.Random;
import java.util.stream.IntStream;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.block.IBucketPickupHandler;
import net.minecraft.block.ILiquidContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.StairsBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
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

public class ReinforcedStairsBlock extends BaseReinforcedBlock implements IBucketPickupHandler, ILiquidContainer
{
	public static final DirectionProperty FACING = HorizontalBlock.HORIZONTAL_FACING;
	public static final EnumProperty<Half> HALF = BlockStateProperties.HALF;
	public static final EnumProperty<StairsShape> SHAPE = BlockStateProperties.STAIRS_SHAPE;
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
	/**
	 * B: .. T: xx
	 * B: .. T: xx
	 */
	protected static final VoxelShape AABB_SLAB_TOP = Block.makeCuboidShape(0.0D, 8.0D, 0.0D, 16.0D, 16.0D, 16.0D);
	/**
	 * B: xx T: ..
	 * B: xx T: ..
	 */
	protected static final VoxelShape AABB_SLAB_BOTTOM = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D);
	protected static final VoxelShape field_196512_A = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 8.0D, 8.0D, 8.0D);
	protected static final VoxelShape field_196513_B = Block.makeCuboidShape(0.0D, 0.0D, 8.0D, 8.0D, 8.0D, 16.0D);
	protected static final VoxelShape field_196514_C = Block.makeCuboidShape(0.0D, 8.0D, 0.0D, 8.0D, 16.0D, 8.0D);
	protected static final VoxelShape field_196515_D = Block.makeCuboidShape(0.0D, 8.0D, 8.0D, 8.0D, 16.0D, 16.0D);
	protected static final VoxelShape field_196516_E = Block.makeCuboidShape(8.0D, 0.0D, 0.0D, 16.0D, 8.0D, 8.0D);
	protected static final VoxelShape field_196517_F = Block.makeCuboidShape(8.0D, 0.0D, 8.0D, 16.0D, 8.0D, 16.0D);
	protected static final VoxelShape field_196518_G = Block.makeCuboidShape(8.0D, 8.0D, 0.0D, 16.0D, 16.0D, 8.0D);
	protected static final VoxelShape field_196519_H = Block.makeCuboidShape(8.0D, 8.0D, 8.0D, 16.0D, 16.0D, 16.0D);
	protected static final VoxelShape[] field_196520_I = func_199779_a(AABB_SLAB_TOP, field_196512_A, field_196516_E, field_196513_B, field_196517_F);
	protected static final VoxelShape[] field_196521_J = func_199779_a(AABB_SLAB_BOTTOM, field_196514_C, field_196518_G, field_196515_D, field_196519_H);
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

	private static VoxelShape[] func_199779_a(VoxelShape p_199779_0_, VoxelShape p_199779_1_, VoxelShape p_199779_2_, VoxelShape p_199779_3_, VoxelShape p_199779_4_)
	{
		return IntStream.range(0, 16).mapToObj((p_199780_5_) -> {
			return func_199781_a(p_199780_5_, p_199779_0_, p_199779_1_, p_199779_2_, p_199779_3_, p_199779_4_);
		}).toArray((p_199778_0_) -> {
			return new VoxelShape[p_199778_0_];
		});
	}

	private static VoxelShape func_199781_a(int p_199781_0_, VoxelShape p_199781_1_, VoxelShape p_199781_2_, VoxelShape p_199781_3_, VoxelShape p_199781_4_, VoxelShape p_199781_5_)
	{
		VoxelShape voxelshape = p_199781_1_;

		if((p_199781_0_ & 1) != 0)
			voxelshape = VoxelShapes.or(p_199781_1_, p_199781_2_);

		if((p_199781_0_ & 2) != 0)
			voxelshape = VoxelShapes.or(voxelshape, p_199781_3_);

		if((p_199781_0_ & 4) != 0)
			voxelshape = VoxelShapes.or(voxelshape, p_199781_4_);

		if((p_199781_0_ & 8) != 0)
			voxelshape = VoxelShapes.or(voxelshape, p_199781_5_);

		return voxelshape;
	}

	@Override
	public int getOpacity(BlockState state, IBlockReader world, BlockPos pos)
	{
		return world.getMaxLightLevel();
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext ctx)
	{
		return (state.get(HALF) == Half.TOP ? field_196520_I : field_196521_J)[field_196522_K[func_196511_x(state)]];
	}

	private int func_196511_x(BlockState p_196511_1_)
	{
		return p_196511_1_.get(SHAPE).ordinal() * 4 + p_196511_1_.get(FACING).getHorizontalIndex();
	}

	@OnlyIn(Dist.CLIENT)
	@Override
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
	public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean flag)
	{
		if(state.getBlock() != state.getBlock())
		{
			modelState.neighborChanged(world, pos, Blocks.AIR, pos, flag);
			modelBlock.onBlockAdded(modelState, world, pos, oldState, flag);
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
	public void func_225534_a_(BlockState state, ServerWorld world, BlockPos pos, Random random) //tick
	{
		modelState.func_227033_a_(world, pos, random); //tick
	}

	@Override
	public ActionResultType func_225533_a_(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) //onBlockActivated
	{
		return modelState.func_227031_a_(world, player, hand, hit); //onBlockActivated
	}

	@Override
	public void onExplosionDestroy(World world, BlockPos pos, Explosion explosion)
	{
		modelBlock.onExplosionDestroy(world, pos, explosion);
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext ctx)
	{
		Direction direction = ctx.getFace();
		IFluidState ifluidstate = ctx.getWorld().getFluidState(ctx.getPos());
		BlockState BlockState = getDefaultState().with(FACING, ctx.getPlacementHorizontalFacing()).with(HALF, direction != Direction.DOWN && (direction == Direction.UP || !(ctx.getHitVec().y > 0.5D)) ? Half.BOTTOM : Half.TOP).with(WATERLOGGED, ifluidstate.getFluid() == Fluids.WATER);
		return BlockState.with(SHAPE, func_208064_n(BlockState, ctx.getWorld(), ctx.getPos()));
	}

	@Override
	public BlockState updatePostPlacement(BlockState state, Direction facing, BlockState facingState, IWorld world, BlockPos currentPos, BlockPos facingPos)
	{
		if(state.get(WATERLOGGED))
			world.getPendingFluidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickRate(world));

		return facing.getAxis().isHorizontal() ? state.with(SHAPE, func_208064_n(state, world, currentPos)) : super.updatePostPlacement(state, facing, facingState, world, currentPos, facingPos);
	}

	private static StairsShape func_208064_n(BlockState state, IBlockReader world, BlockPos pos)
	{
		Direction Direction = state.get(FACING);
		BlockState BlockState = world.getBlockState(pos.offset(Direction));

		if(isBlockStairs(BlockState) && state.get(HALF) == BlockState.get(HALF))
		{
			Direction Direction1 = BlockState.get(FACING);

			if(Direction1.getAxis() != state.get(FACING).getAxis() && isDifferentStairs(state, world, pos, Direction1.getOpposite()))
			{
				if(Direction1 == Direction.rotateYCCW())
					return StairsShape.OUTER_LEFT;

				return StairsShape.OUTER_RIGHT;
			}
		}

		BlockState BlockState1 = world.getBlockState(pos.offset(Direction.getOpposite()));

		if (isBlockStairs(BlockState1) && state.get(HALF) == BlockState1.get(HALF))
		{
			Direction Direction2 = BlockState1.get(FACING);

			if(Direction2.getAxis() != state.get(FACING).getAxis() && isDifferentStairs(state, world, pos, Direction2))
			{
				if(Direction2 == Direction.rotateYCCW())
					return StairsShape.INNER_LEFT;

				return StairsShape.INNER_RIGHT;
			}
		}

		return StairsShape.STRAIGHT;
	}

	private static boolean isDifferentStairs(BlockState state, IBlockReader world, BlockPos pos, Direction face)
	{
		BlockState BlockState = world.getBlockState(pos.offset(face));
		return !isBlockStairs(BlockState) || BlockState.get(FACING) != state.get(FACING) || BlockState.get(HALF) != state.get(HALF);
	}

	public static boolean isBlockStairs(BlockState state)
	{
		return state.getBlock() instanceof ReinforcedStairsBlock;
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
		StairsShape stairsshape = state.get(SHAPE);

		switch(mirror)
		{
			case LEFT_RIGHT:
				if(direction.getAxis() == Direction.Axis.Z)
				{
					switch(stairsshape)
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
				if(direction.getAxis() == Direction.Axis.X)
				{
					switch(stairsshape)
					{
						case STRAIGHT:
							return state.rotate(Rotation.CLOCKWISE_180);
						case INNER_LEFT:
							return state.rotate(Rotation.CLOCKWISE_180).with(SHAPE, StairsShape.INNER_LEFT);
						case INNER_RIGHT:
							return state.rotate(Rotation.CLOCKWISE_180).with(SHAPE, StairsShape.INNER_RIGHT);
						case OUTER_LEFT:
							return state.rotate(Rotation.CLOCKWISE_180).with(SHAPE, StairsShape.OUTER_RIGHT);
						case OUTER_RIGHT:
							return state.rotate(Rotation.CLOCKWISE_180).with(SHAPE, StairsShape.OUTER_LEFT);
					}
				}
				break;
			case NONE: return super.mirror(state, mirror);
		}

		return super.mirror(state, mirror);
	}

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
	{
		builder.add(FACING, HALF, SHAPE, WATERLOGGED);
	}

	@Override
	public Fluid pickupFluid(IWorld world, BlockPos pos, BlockState state)
	{
		if(state.get(WATERLOGGED))
		{
			world.setBlockState(pos, state.with(WATERLOGGED, false), 3);
			return Fluids.WATER;
		}
		else return Fluids.EMPTY;
	}

	@Override
	public IFluidState getFluidState(BlockState state)
	{
		return state.get(WATERLOGGED) ? Fluids.WATER.getStillFluidState(false) : super.getFluidState(state);
	}

	@Override
	public boolean canContainFluid(IBlockReader world, BlockPos pos, BlockState state, Fluid fluid)
	{
		return !state.get(WATERLOGGED) && fluid == Fluids.WATER;
	}

	@Override
	public boolean receiveFluid(IWorld world, BlockPos pos, BlockState state, IFluidState fluidState)
	{
		if (!state.get(WATERLOGGED) && fluidState.getFluid() == Fluids.WATER)
		{
			if (!world.isRemote())
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
	public boolean allowsMovement(BlockState state, IBlockReader world, BlockPos pos, PathType type)
	{
		return false;
	}

	@Override
	public BlockState getConvertedState(BlockState vanillaState)
	{
		return getDefaultState().with(FACING, vanillaState.get(StairsBlock.FACING)).with(HALF, vanillaState.get(StairsBlock.HALF)).with(SHAPE, vanillaState.get(StairsBlock.SHAPE)).with(WATERLOGGED, vanillaState.get(StairsBlock.WATERLOGGED));
	}
}