package net.geforcemods.securitycraft.blocks;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.blockentities.MotionActivatedLightBlockEntity;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

public class MotionActivatedLightBlock extends OwnableBlock implements IWaterLoggable {
	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
	public static final BooleanProperty LIT = BlockStateProperties.LIT;
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
	private static final VoxelShape SHAPE_NORTH = VoxelShapes.or(Block.box(6, 3, 13, 10, 4, 14), VoxelShapes.or(Block.box(6, 6, 13, 10, 9, 14), VoxelShapes.joinUnoptimized(Block.box(7, 3, 14, 9, 8, 16), Block.box(7, 4, 15, 9, 7, 14), IBooleanFunction.ONLY_FIRST)));
	private static final VoxelShape SHAPE_EAST = VoxelShapes.or(Block.box(3, 3, 6, 2, 4, 10), VoxelShapes.or(Block.box(3, 6, 6, 2, 9, 10), VoxelShapes.joinUnoptimized(Block.box(2, 3, 7, 0, 8, 9), Block.box(1, 4, 7, 2, 7, 9), IBooleanFunction.ONLY_FIRST)));
	private static final VoxelShape SHAPE_SOUTH = VoxelShapes.or(Block.box(6, 3, 2, 10, 4, 3), VoxelShapes.or(Block.box(6, 6, 2, 10, 9, 3), VoxelShapes.joinUnoptimized(Block.box(7, 3, 0, 9, 8, 2), Block.box(7, 4, 1, 9, 7, 2), IBooleanFunction.ONLY_FIRST)));
	private static final VoxelShape SHAPE_WEST = VoxelShapes.or(Block.box(13, 3, 6, 14, 4, 10), VoxelShapes.or(Block.box(13, 6, 6, 14, 9, 10), VoxelShapes.joinUnoptimized(Block.box(14, 3, 7, 16, 8, 9), Block.box(15, 4, 7, 14, 7, 9), IBooleanFunction.ONLY_FIRST)));

	public MotionActivatedLightBlock(AbstractBlock.Properties properties) {
		super(properties);
		registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(LIT, false).setValue(WATERLOGGED, false));
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader level, BlockPos pos, ISelectionContext ctx) {
		switch (state.getValue(FACING)) {
			case NORTH:
				return SHAPE_NORTH;
			case EAST:
				return SHAPE_EAST;
			case SOUTH:
				return SHAPE_SOUTH;
			case WEST:
				return SHAPE_WEST;
			default:
				return VoxelShapes.block();
		}
	}

	@Override
	public boolean canSurvive(BlockState state, IWorldReader level, BlockPos pos) {
		Direction side = state.getValue(FACING);

		return side != Direction.UP && side != Direction.DOWN && BlockUtils.isSideSolid(level, pos.relative(side.getOpposite()), side);
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext ctx) {
		World level = ctx.getLevel();
		BlockPos pos = ctx.getClickedPos();
		Direction facing = ctx.getClickedFace();

		return facing != Direction.UP && facing != Direction.DOWN && BlockUtils.isSideSolid(level, pos.relative(facing.getOpposite()), facing) ? defaultBlockState().setValue(FACING, facing).setValue(WATERLOGGED, level.getFluidState(pos).getType() == Fluids.WATER) : null;
	}

	@Override
	public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, IWorld level, BlockPos currentPos, BlockPos facingPos) {
		if (state.getValue(WATERLOGGED))
			level.getLiquidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(level));

		return super.updateShape(state, facing, facingState, level, currentPos, facingPos);
	}

	@Override
	public FluidState getFluidState(BlockState state) {
		return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
	}

	@Override
	public void neighborChanged(BlockState state, World level, BlockPos pos, Block block, BlockPos fromPos, boolean flag) {
		if (!canSurvive(state, level, pos))
			level.destroyBlock(pos, true);
	}

	@Override
	public void playerWillDestroy(World level, BlockPos pos, BlockState state, PlayerEntity player) {
		//prevents dropping twice the amount of modules when breaking the block in creative mode
		if (player.isCreative()) {
			TileEntity te = level.getBlockEntity(pos);

			if (te instanceof IModuleInventory)
				((IModuleInventory) te).getInventory().clear();
		}

		super.playerWillDestroy(level, pos, state, player);
	}

	@Override
	public void onRemove(BlockState state, World level, BlockPos pos, BlockState newState, boolean isMoving) {
		if (!state.is(newState.getBlock())) {
			TileEntity te = level.getBlockEntity(pos);

			if (!ConfigHandler.SERVER.vanillaToolBlockBreaking.get() && te instanceof IModuleInventory)
				((IModuleInventory) te).dropAllModules();
		}

		super.onRemove(state, level, pos, newState, isMoving);
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		builder.add(FACING, LIT, WATERLOGGED);
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader level) {
		return new MotionActivatedLightBlockEntity();
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rot) {
		return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
	}

	@Override
	public BlockState mirror(BlockState state, Mirror mirror) {
		Direction facing = state.getValue(FACING);

		switch (mirror) {
			case LEFT_RIGHT:
				if (facing.getAxis() == Axis.Z)
					return state.setValue(FACING, facing.getOpposite());
				break;
			case FRONT_BACK:
				if (facing.getAxis() == Axis.X)
					return state.setValue(FACING, facing.getOpposite());
				break;
			case NONE:
				break;
		}

		return state;
	}
}
