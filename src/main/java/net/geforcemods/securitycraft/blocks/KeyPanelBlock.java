package net.geforcemods.securitycraft.blocks;

import java.util.Random;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.tileentity.KeyPanelTileEntity;
import net.geforcemods.securitycraft.util.ModuleUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.state.properties.AttachFace;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
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
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class KeyPanelBlock extends OwnableBlock implements IWaterLoggable
{
	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
	public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
	public static final EnumProperty<AttachFace> FACE = BlockStateProperties.FACE;
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
	public static final VoxelShape FLOOR_NS = Block.makeCuboidShape(2.0D, 0.0D, 1.0D, 14.0D, 1.0D, 15.0D);
	public static final VoxelShape FLOOR_EW = Block.makeCuboidShape(1.0D, 0.0D, 2.0D, 15.0D, 1.0D, 14.0D);
	public static final VoxelShape CEILING_NS = Block.makeCuboidShape(2.0D, 15.0D, 1.0D, 14.0D, 16.0D, 15.0D);
	public static final VoxelShape CEILING_EW = Block.makeCuboidShape(1.0D, 15.0D, 2.0D, 15.0D, 16.0D, 14.0D);
	public static final VoxelShape WALL_N = Block.makeCuboidShape(2.0D, 1.0D, 15.0D, 14.0D, 15.0D, 16.0D);
	public static final VoxelShape WALL_E = Block.makeCuboidShape(0.0D, 1.0D, 2.0D, 1.0D, 15.0D, 14.0D);
	public static final VoxelShape WALL_S = Block.makeCuboidShape(2.0D, 1.0D, 0.0D, 14.0D, 15.0D, 1.0D);
	public static final VoxelShape WALL_W = Block.makeCuboidShape(15.0D, 1.0D, 2.0D, 16.0D, 15.0D, 14.0D);

	public KeyPanelBlock(Properties properties)
	{
		super(properties);
		setDefaultState(stateContainer.getBaseState().with(FACING, Direction.NORTH).with(POWERED, false).with(FACE, AttachFace.WALL).with(WATERLOGGED, false));
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext ctx)
	{
		switch(state.get(FACE)) {
			case FLOOR:
				switch(state.get(FACING)) {
					case NORTH: return FLOOR_NS;
					case EAST: return FLOOR_EW;
					case SOUTH: return FLOOR_NS;
					case WEST: return FLOOR_EW;
					default: return VoxelShapes.empty();
				}
			case CEILING:
				switch(state.get(FACING)) {
					case NORTH: return CEILING_NS;
					case EAST: return CEILING_EW;
					case SOUTH: return CEILING_NS;
					case WEST: return CEILING_EW;
					default: return VoxelShapes.empty();
				}
			case WALL:
				switch(state.get(FACING)) {
					case NORTH: return WALL_N;
					case EAST: return WALL_E;
					case SOUTH: return WALL_S;
					case WEST: return WALL_W;
					default: return VoxelShapes.empty();
				}
		}

		return VoxelShapes.empty();
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext ctx)
	{
		return VoxelShapes.empty();
	}

	@Override
	public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit)
	{
		if(state.get(POWERED))
			return ActionResultType.PASS;
		else
		{
			KeyPanelTileEntity te = (KeyPanelTileEntity)world.getTileEntity(pos);

			if(ModuleUtils.isDenied(te, player))
			{
				if(te.sendsMessages())
					PlayerUtils.sendMessageToPlayer(player, Utils.localize(getTranslationKey()), Utils.localize("messages.securitycraft:module.onDenylist"), TextFormatting.RED);
			}
			else if(ModuleUtils.isAllowed(te, player))
			{
				if(te.sendsMessages())
					PlayerUtils.sendMessageToPlayer(player, Utils.localize(getTranslationKey()), Utils.localize("messages.securitycraft:module.onAllowlist"), TextFormatting.GREEN);

				activate(state, world, pos, te.getSignalLength());
			}
			else if(!PlayerUtils.isHoldingItem(player, SCContent.CODEBREAKER, hand))
				te.openPasswordGUI(player);
		}

		return ActionResultType.SUCCESS;
	}

	@Override
	public void tick(BlockState state, ServerWorld world, BlockPos pos, Random random)
	{
		world.setBlockState(pos, state.with(POWERED, false));
		updateNeighbours(state, world, pos);
	}

	@Override
	public boolean canProvidePower(BlockState state)
	{
		return true;
	}

	@Override
	public int getWeakPower(BlockState state, IBlockReader world, BlockPos pos, Direction side)
	{
		return state.get(POWERED) ? 15 : 0;
	}

	@Override
	public int getStrongPower(BlockState state, IBlockReader world, BlockPos pos, Direction side)
	{
		return state.get(POWERED) && getConnectedDirection(state) == side ? 15 : 0;
	}

	@Override
	public boolean isValidPosition(BlockState state, IWorldReader level, BlockPos pos)
	{
		return canAttach(level, pos, getConnectedDirection(state).getOpposite());
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext ctx)
	{
		World world = ctx.getWorld();
		BlockPos pos = ctx.getPos();

		for(Direction direction : ctx.getNearestLookingDirections())
		{
			BlockState state;

			if(direction.getAxis() == Direction.Axis.Y)
				state = getDefaultState().with(FACE, direction == Direction.UP ? AttachFace.CEILING : AttachFace.FLOOR).with(FACING, ctx.getPlacementHorizontalFacing());
			else
				state = getDefaultState().with(FACE, AttachFace.WALL).with(FACING, direction.getOpposite());

			if(state.isValidPosition(world, pos))
				return state.with(POWERED, false).with(WATERLOGGED, world.getFluidState(pos).getFluid() == Fluids.WATER);
		}

		return null;
	}

	@Override
	public BlockState updatePostPlacement(BlockState state, Direction facing, BlockState facingState, IWorld world, BlockPos pos, BlockPos facingPos)
	{
		if(state.get(WATERLOGGED))
			world.getPendingFluidTicks().scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));

		return getConnectedDirection(state).getOpposite() == facing && !state.isValidPosition(world, pos) ? Blocks.AIR.getDefaultState() : super.updatePostPlacement(state, facing, facingState, world, pos, facingPos);
	}

	@Override
	public FluidState getFluidState(BlockState state)
	{
		return state.get(WATERLOGGED) ? Fluids.WATER.getStillFluidState(false) : super.getFluidState(state);
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rot)
	{
		return state.with(FACING, rot.rotate(state.get(FACING)));
	}

	@Override
	public BlockState mirror(BlockState state, Mirror mirror)
	{
		return state.rotate(mirror.toRotation(state.get(FACING)));
	}

	@Override
	protected void fillStateContainer(Builder<Block, BlockState> builder)
	{
		builder.add(FACING, POWERED, FACE, WATERLOGGED);
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world)
	{
		return new KeyPanelTileEntity();
	}

	public void activate(BlockState state, World world, BlockPos pos, int signalLength)
	{
		world.setBlockState(pos, state.with(POWERED, true));
		updateNeighbours(state, world, pos);
		world.getPendingBlockTicks().scheduleTick(pos, this, signalLength);
	}

	private void updateNeighbours(BlockState state, World world, BlockPos pos)
	{
		world.notifyNeighborsOfStateChange(pos, this);
		world.notifyNeighborsOfStateChange(pos.offset(getConnectedDirection(state).getOpposite()), this);
	}

	protected static Direction getConnectedDirection(BlockState state)
	{
		switch(state.get(FACE))  {
			case CEILING: return Direction.DOWN;
			case FLOOR: return Direction.UP;
			default: return state.get(FACING);
		}
	}

	public static boolean canAttach(IWorldReader world, BlockPos pos, Direction direction)
	{
		BlockPos relativePos = pos.offset(direction);

		return world.getBlockState(relativePos).isSolidSide(world, relativePos, direction.getOpposite());
	}
}
