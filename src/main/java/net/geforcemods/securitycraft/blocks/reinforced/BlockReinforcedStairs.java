package net.geforcemods.securitycraft.blocks.reinforced;

import java.util.Random;
import java.util.stream.IntStream;

import net.minecraft.block.Block;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.IBucketPickupHandler;
import net.minecraft.block.ILiquidContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.IFluidState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.Half;
import net.minecraft.state.properties.StairsShape;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BlockReinforcedStairs extends BlockReinforcedBase implements IBucketPickupHandler, ILiquidContainer
{
	public static final DirectionProperty FACING = BlockHorizontal.HORIZONTAL_FACING;
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
	private final IBlockState modelState;

	public BlockReinforcedStairs(SoundType soundType, Material mat, Block vB, String registryPath)
	{
		super(soundType, mat, vB, registryPath, 0);

		setDefaultState(stateContainer.getBaseState().with(FACING, EnumFacing.NORTH).with(HALF, Half.BOTTOM).with(SHAPE, StairsShape.STRAIGHT).with(WATERLOGGED, false));

		if(vB == Blocks.STONE)
			modelBlock = vB;
		else
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
	public int getOpacity(IBlockState state, IBlockReader world, BlockPos pos)
	{
		return world.getMaxLightLevel();
	}

	@Override
	public VoxelShape getShape(IBlockState state, IBlockReader world, BlockPos pos)
	{
		return (state.get(HALF) == Half.TOP ? field_196520_I : field_196521_J)[field_196522_K[func_196511_x(state)]];
	}

	private int func_196511_x(IBlockState p_196511_1_)
	{
		return p_196511_1_.get(SHAPE).ordinal() * 4 + p_196511_1_.get(FACING).getHorizontalIndex();
	}

	@Override
	public BlockFaceShape getBlockFaceShape(IBlockReader world, IBlockState state, BlockPos pos, EnumFacing face)
	{
		if (face.getAxis() == EnumFacing.Axis.Y)
			return face == EnumFacing.UP == (state.get(HALF) == Half.TOP) ? BlockFaceShape.SOLID : BlockFaceShape.UNDEFINED;
		else
		{
			StairsShape stairsshape = state.get(SHAPE);

			if(stairsshape != StairsShape.OUTER_LEFT && stairsshape != StairsShape.OUTER_RIGHT)
			{
				EnumFacing enumfacing = state.get(FACING);

				switch(stairsshape)
				{
					case STRAIGHT:
						return enumfacing == face ? BlockFaceShape.SOLID : BlockFaceShape.UNDEFINED;
					case INNER_LEFT:
						return enumfacing != face && enumfacing != face.rotateY() ? BlockFaceShape.UNDEFINED : BlockFaceShape.SOLID;
					case INNER_RIGHT:
						return enumfacing != face && enumfacing != face.rotateYCCW() ? BlockFaceShape.UNDEFINED : BlockFaceShape.SOLID;
					default:
						return BlockFaceShape.UNDEFINED;
				}
			}
			else
				return BlockFaceShape.UNDEFINED;
		}
	}

	@Override
	public Block getVanillaBlock()
	{
		return modelBlock == Blocks.STONE ? Blocks.AIR : super.getVanillaBlock();
	}

	@Override
	public boolean isFullCube(IBlockState state)
	{
		return false;
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void animateTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand)
	{
		modelBlock.animateTick(stateIn, worldIn, pos, rand);
	}

	@Override
	public void onBlockClicked(IBlockState state, World worldIn, BlockPos pos, EntityPlayer player)
	{
		modelState.onBlockClicked(worldIn, pos, player);
	}

	@Override
	public void onPlayerDestroy(IWorld worldIn, BlockPos pos, IBlockState state)
	{
		modelBlock.onPlayerDestroy(worldIn, pos, state);
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public int getPackedLightmapCoords(IBlockState state, IWorldReader source, BlockPos pos)
	{
		return modelState.getPackedLightmapCoords(source, pos);
	}

	@Override
	public float getExplosionResistance()
	{
		return modelBlock.getExplosionResistance();
	}

	@Override
	public BlockRenderLayer getRenderLayer()
	{
		return modelBlock.getRenderLayer();
	}

	@Override
	public int tickRate(IWorldReaderBase world)
	{
		return modelBlock.tickRate(world);
	}

	@Override
	public boolean isCollidable()
	{
		return modelBlock.isCollidable();
	}

	@Override
	public boolean isCollidable(IBlockState state)
	{
		return modelBlock.isCollidable(state);
	}

	@Override
	public void onBlockAdded(IBlockState state, World world, BlockPos pos, IBlockState oldState)
	{
		if(state.getBlock() != state.getBlock())
		{
			modelState.neighborChanged(world, pos, Blocks.AIR, pos);
			modelBlock.onBlockAdded(modelState, world, pos, oldState);
		}
	}

	@Override
	public void onReplaced(IBlockState state, World world, BlockPos pos, IBlockState newState, boolean isMoving)
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
	public void tick(IBlockState state, World world, BlockPos pos, Random random)
	{
		modelBlock.tick(state, world, pos, random);
	}

	@Override
	public boolean onBlockActivated(IBlockState state, World world, BlockPos pos, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ)
	{
		return modelState.onBlockActivated(world, pos, player, hand, EnumFacing.DOWN, 0.0F, 0.0F, 0.0F);
	}

	@Override
	public void onExplosionDestroy(World world, BlockPos pos, Explosion explosion)
	{
		modelBlock.onExplosionDestroy(world, pos, explosion);
	}

	@Override
	public boolean isTopSolid(IBlockState state)
	{
		return state.get(HALF) == Half.TOP;
	}

	@Override
	public IBlockState getStateForPlacement(BlockItemUseContext ctx)
	{
		EnumFacing enumfacing = ctx.getFace();
		IFluidState ifluidstate = ctx.getWorld().getFluidState(ctx.getPos());
		IBlockState iblockstate = getDefaultState().with(FACING, ctx.getPlacementHorizontalFacing()).with(HALF, enumfacing != EnumFacing.DOWN && (enumfacing == EnumFacing.UP || !(ctx.getHitY() > 0.5D)) ? Half.BOTTOM : Half.TOP).with(WATERLOGGED, ifluidstate.getFluid() == Fluids.WATER);
		return iblockstate.with(SHAPE, func_208064_n(iblockstate, ctx.getWorld(), ctx.getPos()));
	}

	@Override
	public IBlockState updatePostPlacement(IBlockState state, EnumFacing facing, IBlockState facingState, IWorld world, BlockPos currentPos, BlockPos facingPos)
	{
		if(state.get(WATERLOGGED))
			world.getPendingFluidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickRate(world));

		return facing.getAxis().isHorizontal() ? state.with(SHAPE, func_208064_n(state, world, currentPos)) : super.updatePostPlacement(state, facing, facingState, world, currentPos, facingPos);
	}

	private static StairsShape func_208064_n(IBlockState state, IBlockReader world, BlockPos pos)
	{
		EnumFacing enumfacing = state.get(FACING);
		IBlockState iblockstate = world.getBlockState(pos.offset(enumfacing));

		if(isBlockStairs(iblockstate) && state.get(HALF) == iblockstate.get(HALF))
		{
			EnumFacing enumfacing1 = iblockstate.get(FACING);

			if(enumfacing1.getAxis() != state.get(FACING).getAxis() && isDifferentStairs(state, world, pos, enumfacing1.getOpposite()))
			{
				if(enumfacing1 == enumfacing.rotateYCCW())
					return StairsShape.OUTER_LEFT;

				return StairsShape.OUTER_RIGHT;
			}
		}

		IBlockState iblockstate1 = world.getBlockState(pos.offset(enumfacing.getOpposite()));

		if (isBlockStairs(iblockstate1) && state.get(HALF) == iblockstate1.get(HALF))
		{
			EnumFacing enumfacing2 = iblockstate1.get(FACING);

			if(enumfacing2.getAxis() != state.get(FACING).getAxis() && isDifferentStairs(state, world, pos, enumfacing2))
			{
				if(enumfacing2 == enumfacing.rotateYCCW())
					return StairsShape.INNER_LEFT;

				return StairsShape.INNER_RIGHT;
			}
		}

		return StairsShape.STRAIGHT;
	}

	private static boolean isDifferentStairs(IBlockState state, IBlockReader world, BlockPos pos, EnumFacing face)
	{
		IBlockState iblockstate = world.getBlockState(pos.offset(face));
		return !isBlockStairs(iblockstate) || iblockstate.get(FACING) != state.get(FACING) || iblockstate.get(HALF) != state.get(HALF);
	}

	public static boolean isBlockStairs(IBlockState state)
	{
		return state.getBlock() instanceof BlockReinforcedStairs;
	}

	@Override
	public IBlockState rotate(IBlockState state, Rotation rot)
	{
		return state.with(FACING, rot.rotate(state.get(FACING)));
	}

	@Override
	public IBlockState mirror(IBlockState state, Mirror mirror)
	{
		EnumFacing enumfacing = state.get(FACING);
		StairsShape stairsshape = state.get(SHAPE);

		switch(mirror)
		{
			case LEFT_RIGHT:
				if(enumfacing.getAxis() == EnumFacing.Axis.Z)
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
				if(enumfacing.getAxis() == EnumFacing.Axis.X)
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
	protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> builder)
	{
		builder.add(FACING, HALF, SHAPE, WATERLOGGED);
	}

	@Override
	public Fluid pickupFluid(IWorld world, BlockPos pos, IBlockState state)
	{
		if(state.get(WATERLOGGED))
		{
			world.setBlockState(pos, state.with(WATERLOGGED, false), 3);
			return Fluids.WATER;
		}
		else return Fluids.EMPTY;
	}

	@Override
	public IFluidState getFluidState(IBlockState state)
	{
		return state.get(WATERLOGGED) ? Fluids.WATER.getStillFluidState(false) : super.getFluidState(state);
	}

	@Override
	public boolean canContainFluid(IBlockReader world, BlockPos pos, IBlockState state, Fluid fluid)
	{
		return !state.get(WATERLOGGED) && fluid == Fluids.WATER;
	}

	@Override
	public boolean receiveFluid(IWorld world, BlockPos pos, IBlockState state, IFluidState fluidState)
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
	public boolean allowsMovement(IBlockState state, IBlockReader world, BlockPos pos, PathType type)
	{
		return false;
	}

	@Override
	public IBlockState getConvertedState(IBlockState vanillaState)
	{
		return getDefaultState().with(FACING, vanillaState.get(BlockStairs.FACING)).with(HALF, vanillaState.get(BlockStairs.HALF)).with(SHAPE, vanillaState.get(BlockStairs.SHAPE)).with(WATERLOGGED, vanillaState.get(BlockStairs.WATERLOGGED));
	}
}