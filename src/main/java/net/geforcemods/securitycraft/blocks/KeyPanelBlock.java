package net.geforcemods.securitycraft.blocks;

import java.util.Random;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.blockentities.KeyPanelBlockEntity;
import net.geforcemods.securitycraft.util.ModuleUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class KeyPanelBlock extends OwnableBlock implements SimpleWaterloggedBlock
{
	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
	public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
	public static final EnumProperty<AttachFace> FACE = BlockStateProperties.ATTACH_FACE;
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
	public static final VoxelShape FLOOR_NS = Block.box(2.0D, 0.0D, 1.0D, 14.0D, 1.0D, 15.0D);
	public static final VoxelShape FLOOR_EW = Block.box(1.0D, 0.0D, 2.0D, 15.0D, 1.0D, 14.0D);
	public static final VoxelShape CEILING_NS = Block.box(2.0D, 15.0D, 1.0D, 14.0D, 16.0D, 15.0D);
	public static final VoxelShape CEILING_EW = Block.box(1.0D, 15.0D, 2.0D, 15.0D, 16.0D, 14.0D);
	public static final VoxelShape WALL_N = Block.box(2.0D, 1.0D, 15.0D, 14.0D, 15.0D, 16.0D);
	public static final VoxelShape WALL_E = Block.box(0.0D, 1.0D, 2.0D, 1.0D, 15.0D, 14.0D);
	public static final VoxelShape WALL_S = Block.box(2.0D, 1.0D, 0.0D, 14.0D, 15.0D, 1.0D);
	public static final VoxelShape WALL_W = Block.box(15.0D, 1.0D, 2.0D, 16.0D, 15.0D, 14.0D);

	public KeyPanelBlock(Properties properties)
	{
		super(properties);
		registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(POWERED, false).setValue(FACE, AttachFace.WALL).setValue(WATERLOGGED, false));
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx)
	{
		return switch(state.getValue(FACE)) {
			case FLOOR -> switch(state.getValue(FACING)) {
				case NORTH -> FLOOR_NS;
				case EAST -> FLOOR_EW;
				case SOUTH -> FLOOR_NS;
				case WEST -> FLOOR_EW;
				default -> Shapes.empty();
			};
			case CEILING -> switch(state.getValue(FACING)) {
				case NORTH -> CEILING_NS;
				case EAST -> CEILING_EW;
				case SOUTH -> CEILING_NS;
				case WEST -> CEILING_EW;
				default -> Shapes.empty();
			};
			case WALL -> switch(state.getValue(FACING)) {
				case NORTH -> WALL_N;
				case EAST -> WALL_E;
				case SOUTH -> WALL_S;
				case WEST -> WALL_W;
				default -> Shapes.empty();
			};
		};
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx)
	{
		return Shapes.empty();
	}

	@Override
	public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit)
	{
		if(state.getValue(POWERED))
			return InteractionResult.PASS;
		else
		{
			KeyPanelBlockEntity te = (KeyPanelBlockEntity)world.getBlockEntity(pos);

			if(ModuleUtils.isDenied(te, player))
			{
				if(te.sendsMessages())
					PlayerUtils.sendMessageToPlayer(player, Utils.localize(getDescriptionId()), Utils.localize("messages.securitycraft:module.onDenylist"), ChatFormatting.RED);
			}
			else if(ModuleUtils.isAllowed(te, player))
			{
				if(te.sendsMessages())
					PlayerUtils.sendMessageToPlayer(player, Utils.localize(getDescriptionId()), Utils.localize("messages.securitycraft:module.onAllowlist"), ChatFormatting.GREEN);

				activate(state, world, pos, te.getSignalLength());
			}
			else if(!PlayerUtils.isHoldingItem(player, SCContent.CODEBREAKER, hand))
				te.openPasswordGUI(player);
		}

		return InteractionResult.SUCCESS;
	}

	@Override
	public void tick(BlockState state, ServerLevel level, BlockPos pos, Random random)
	{
		level.setBlockAndUpdate(pos, state.setValue(POWERED, false));
		updateNeighbours(state, level, pos);
	}

	@Override
	public boolean isSignalSource(BlockState state)
	{
		return true;
	}

	@Override
	public int getSignal(BlockState state, BlockGetter level, BlockPos pos, Direction side)
	{
		return state.getValue(POWERED) ? 15 : 0;
	}

	@Override
	public int getDirectSignal(BlockState state, BlockGetter level, BlockPos pos, Direction side)
	{
		return state.getValue(POWERED) && getConnectedDirection(state) == side ? 15 : 0;
	}

	@Override
	public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos)
	{
		return canAttach(level, pos, getConnectedDirection(state).getOpposite());
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext ctx)
	{
		Level level = ctx.getLevel();
		BlockPos pos = ctx.getClickedPos();

		for(Direction direction : ctx.getNearestLookingDirections())
		{
			BlockState state;

			if(direction.getAxis() == Direction.Axis.Y)
				state = defaultBlockState().setValue(FACE, direction == Direction.UP ? AttachFace.CEILING : AttachFace.FLOOR).setValue(FACING, ctx.getHorizontalDirection());
			else
				state = defaultBlockState().setValue(FACE, AttachFace.WALL).setValue(FACING, direction.getOpposite());

			if(state.canSurvive(level, pos))
				return state.setValue(POWERED, false).setValue(WATERLOGGED, level.getFluidState(pos).getType() == Fluids.WATER);
		}

		return null;
	}

	@Override
	public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos pos, BlockPos facingPos)
	{
		if(state.getValue(WATERLOGGED))
			level.getLiquidTicks().scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));

		return getConnectedDirection(state).getOpposite() == facing && !state.canSurvive(level, pos) ? Blocks.AIR.defaultBlockState() : super.updateShape(state, facing, facingState, level, pos, facingPos);
	}

	@Override
	public FluidState getFluidState(BlockState state)
	{
		return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rot)
	{
		return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
	}

	@Override
	public BlockState mirror(BlockState state, Mirror mirror)
	{
		return state.rotate(mirror.getRotation(state.getValue(FACING)));
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder)
	{
		builder.add(FACING, POWERED, FACE, WATERLOGGED);
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
	{
		return new KeyPanelBlockEntity(pos, state);
	}

	public void activate(BlockState state, Level level, BlockPos pos, int signalLength)
	{
		level.setBlockAndUpdate(pos, state.setValue(POWERED, true));
		updateNeighbours(state, level, pos);
		level.getBlockTicks().scheduleTick(pos, this, signalLength);
	}

	private void updateNeighbours(BlockState state, Level level, BlockPos pos)
	{
		level.updateNeighborsAt(pos, this);
		level.updateNeighborsAt(pos.relative(getConnectedDirection(state).getOpposite()), this);
	}

	protected static Direction getConnectedDirection(BlockState state)
	{
		return switch(state.getValue(FACE))  {
			case CEILING -> Direction.DOWN;
			case FLOOR -> Direction.UP;
			default -> state.getValue(FACING);
		};
	}

	public static boolean canAttach(LevelReader level, BlockPos pos, Direction direction)
	{
		BlockPos relativePos = pos.relative(direction);

		return level.getBlockState(relativePos).isFaceSturdy(level, relativePos, direction.getOpposite());
	}
}
