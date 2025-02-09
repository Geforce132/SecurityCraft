package net.geforcemods.securitycraft.blocks;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.blockentities.PortableRadarBlockEntity;
import net.geforcemods.securitycraft.misc.ModuleType;
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
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

public class PortableRadarBlock extends OwnableBlock implements IWaterLoggable {
	public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
	public static final DirectionProperty FACING = BlockStateProperties.FACING;
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
	private static final VoxelShape SHAPE_UP = Block.box(5, 0, 5, 11, 7, 11);
	private static final VoxelShape SHAPE_DOWN = Block.box(5, 9, 5, 11, 16, 11);
	private static final VoxelShape SHAPE_EAST = Block.box(0, 5, 5, 7, 11, 11);
	private static final VoxelShape SHAPE_WEST = Block.box(9, 5, 5, 16, 11, 11);
	private static final VoxelShape SHAPE_NORTH = Block.box(5, 5, 9, 11, 11, 16);
	private static final VoxelShape SHAPE_SOUTH = Block.box(5, 5, 0, 11, 11, 7);

	public PortableRadarBlock(AbstractBlock.Properties properties) {
		super(properties);

		registerDefaultState(stateDefinition.any().setValue(POWERED, false).setValue(FACING, Direction.UP).setValue(WATERLOGGED, false));
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader level, BlockPos pos, ISelectionContext ctx) {
		Direction facing = state.getValue(FACING);

		switch (facing) {
			case EAST:
				return SHAPE_EAST;
			case WEST:
				return SHAPE_WEST;
			case NORTH:
				return SHAPE_NORTH;
			case SOUTH:
				return SHAPE_SOUTH;
			case UP:
				return SHAPE_UP;
			case DOWN:
				return SHAPE_DOWN;
		}

		return VoxelShapes.block();
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext ctx) {
		Direction facing = ctx.getClickedFace();

		return BlockUtils.isSideSolid(ctx.getLevel(), ctx.getClickedPos().relative(facing.getOpposite()), facing) ? defaultBlockState().setValue(FACING, facing).setValue(WATERLOGGED, ctx.getLevel().getFluidState(ctx.getClickedPos()).getType() == Fluids.WATER) : null;
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
	public boolean canSurvive(BlockState state, IWorldReader level, BlockPos pos) {
		Direction facing = state.getValue(FACING);

		return BlockUtils.isSideSolid(level, pos.relative(facing.getOpposite()), facing);
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

	public static void togglePowerOutput(World level, BlockPos pos, boolean shouldPower) {
		BlockState state = level.getBlockState(pos);

		if (shouldPower != state.getValue(POWERED)) {
			level.setBlockAndUpdate(pos, state.setValue(POWERED, shouldPower));
			BlockUtils.updateIndirectNeighbors(level, pos, SCContent.PORTABLE_RADAR.get(), state.getValue(PortableRadarBlock.FACING).getOpposite());
		}
	}

	@Override
	public boolean isSignalSource(BlockState state) {
		return true;
	}

	@Override
	public int getSignal(BlockState state, IBlockReader level, BlockPos pos, Direction side) {
		if (state.getValue(POWERED) && ((IModuleInventory) level.getBlockEntity(pos)).isModuleEnabled(ModuleType.REDSTONE))
			return 15;
		else
			return 0;
	}

	@Override
	public int getDirectSignal(BlockState state, IBlockReader level, BlockPos pos, Direction side) {
		return state.getValue(POWERED) && ((IModuleInventory) level.getBlockEntity(pos)).isModuleEnabled(ModuleType.REDSTONE) && state.getValue(FACING) == side ? 15 : 0;
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		builder.add(POWERED, FACING, WATERLOGGED);
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader level) {
		return new PortableRadarBlockEntity();
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
