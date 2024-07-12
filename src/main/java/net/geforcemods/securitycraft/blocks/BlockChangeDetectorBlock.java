package net.geforcemods.securitycraft.blocks;

import java.util.Random;

import net.geforcemods.securitycraft.api.IDisguisable;
import net.geforcemods.securitycraft.blockentities.BlockChangeDetectorBlockEntity;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
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
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkHooks;

public class BlockChangeDetectorBlock extends DisguisableBlock {
	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
	public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
	public static final EnumProperty<AttachFace> FACE = BlockStateProperties.ATTACH_FACE;
	public static final VoxelShape FLOOR = VoxelShapes.or(Block.box(0.0D, 0.0D, 0.0D, 16.0D, 9.0D, 16.0D), Block.box(7.0D, 9.0D, 7.0D, 9.0D, 16.0D, 9.0D));
	public static final VoxelShape CEILING = VoxelShapes.or(Block.box(0.0D, 7.0D, 0.0D, 16.0D, 16.0D, 16.0D), Block.box(7.0D, 0.0D, 7.0D, 9.0D, 7.0D, 9.0D));
	public static final VoxelShape WALL_N = VoxelShapes.or(Block.box(0.0D, 0.0D, 7.0D, 16.0D, 16.0D, 16.0D), Block.box(7.0D, 7.0D, 0.0D, 9.0D, 9.0D, 7.0D));
	public static final VoxelShape WALL_E = VoxelShapes.or(Block.box(0.0D, 0.0D, 0.0D, 9.0D, 16.0D, 16.0D), Block.box(9.0D, 7.0D, 7.0D, 16.0D, 9.0D, 9.0D));
	public static final VoxelShape WALL_S = VoxelShapes.or(Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 9.0D), Block.box(7.0D, 7.0D, 9.0D, 9.0D, 9.0D, 16.0D));
	public static final VoxelShape WALL_W = VoxelShapes.or(Block.box(7.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D), Block.box(0.0D, 7.0D, 7.0D, 7.0D, 9.0D, 9.0D));

	public BlockChangeDetectorBlock(AbstractBlock.Properties properties) {
		super(properties);
		registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(POWERED, false).setValue(FACE, AttachFace.FLOOR).setValue(WATERLOGGED, false));
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader level, BlockPos pos, ISelectionContext ctx) {
		BlockState disguisedState = IDisguisable.getDisguisedStateOrDefault(state, level, pos);

		if (disguisedState.getBlock() != this)
			return disguisedState.getShape(level, pos, ctx);
		else {
			switch (state.getValue(FACE)) {
				case FLOOR:
					return FLOOR;
				case CEILING:
					return CEILING;
				case WALL: {
					switch (state.getValue(FACING)) {
						case NORTH:
							return WALL_N;
						case EAST:
							return WALL_E;
						case SOUTH:
							return WALL_S;
						case WEST:
							return WALL_W;
						default:
							return VoxelShapes.empty();
					}
				}
				default:
					return VoxelShapes.empty();
			}
		}
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext ctx) {
		World level = ctx.getLevel();
		BlockPos pos = ctx.getClickedPos();
		Direction direction = ctx.getClickedFace();
		BlockState state;

		if (direction.getAxis() == Direction.Axis.Y)
			state = defaultBlockState().setValue(FACE, direction == Direction.UP ? AttachFace.FLOOR : AttachFace.CEILING).setValue(FACING, ctx.getHorizontalDirection().getOpposite());
		else
			state = defaultBlockState().setValue(FACE, AttachFace.WALL).setValue(FACING, direction);

		return state.setValue(WATERLOGGED, level.getFluidState(pos).getType() == Fluids.WATER);
	}

	@Override
	public ActionResultType use(BlockState state, World level, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
		if (!level.isClientSide) {
			TileEntity te = level.getBlockEntity(pos);

			if (te instanceof BlockChangeDetectorBlockEntity) {
				BlockChangeDetectorBlockEntity be = (BlockChangeDetectorBlockEntity) te;

				if (be.isOwnedBy(player) || be.isAllowed(player))
					NetworkHooks.openGui((ServerPlayerEntity) player, be, pos);
			}
		}

		return ActionResultType.SUCCESS;
	}

	@Override
	public void onRemove(BlockState state, World level, BlockPos pos, BlockState newState, boolean isMoving) {
		if (!state.is(newState.getBlock())) {
			TileEntity te = level.getBlockEntity(pos);

			if (te instanceof BlockChangeDetectorBlockEntity)
				Block.popResource(level, pos, ((BlockChangeDetectorBlockEntity) te).getStackInSlot(36));
		}

		super.onRemove(state, level, pos, newState, isMoving);
	}

	@Override
	public void tick(BlockState state, ServerWorld level, BlockPos pos, Random random) {
		if (state.getValue(POWERED)) {
			level.setBlockAndUpdate(pos, state.setValue(POWERED, false));
			BlockUtils.updateIndirectNeighbors(level, pos, this, AbstractPanelBlock.getConnectedDirection(state).getOpposite());
		}
	}

	@Override
	public boolean isSignalSource(BlockState state) {
		return true;
	}

	@Override
	public boolean shouldCheckWeakPower(BlockState state, IWorldReader level, BlockPos pos, Direction side) {
		return false;
	}

	@Override
	public int getSignal(BlockState state, IBlockReader level, BlockPos pos, Direction side) {
		return state.getValue(POWERED) ? 15 : 0;
	}

	@Override
	public int getDirectSignal(BlockState state, IBlockReader level, BlockPos pos, Direction side) {
		return state.getValue(POWERED) && AbstractPanelBlock.getConnectedDirection(state) == side ? 15 : 0;
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		builder.add(FACING, POWERED, WATERLOGGED, FACE);
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader level) {
		return new BlockChangeDetectorBlockEntity();
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rotation) {
		return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
	}

	@Override
	public BlockState mirror(BlockState state, Mirror mirror) {
		return state.rotate(mirror.getRotation(state.getValue(FACING)));
	}
}
