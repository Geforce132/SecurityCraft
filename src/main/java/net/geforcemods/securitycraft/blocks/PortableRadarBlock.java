package net.geforcemods.securitycraft.blocks;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.tileentity.PortableRadarTileEntity;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class PortableRadarBlock extends OwnableBlock {

	public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
	public static final DirectionProperty FACING = BlockStateProperties.FACING;
	private static final VoxelShape SHAPE_UP = Block.box(5, 0, 5, 11, 7, 11);
	private static final VoxelShape SHAPE_DOWN = Block.box(5, 9, 5, 11, 16, 11);
	private static final VoxelShape SHAPE_EAST = Block.box(0, 5, 5, 7, 11, 11);
	private static final VoxelShape SHAPE_WEST = Block.box(9, 5, 5, 16, 11, 11);
	private static final VoxelShape SHAPE_NORTH = Block.box(5, 5, 9, 11, 11, 16);
	private static final VoxelShape SHAPE_SOUTH = Block.box(5, 5, 0, 11, 11, 7);

	public PortableRadarBlock(Block.Properties properties) {
		super(properties);

		registerDefaultState(stateDefinition.any().setValue(POWERED, false).setValue(FACING, Direction.UP));
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter source, BlockPos pos, CollisionContext ctx)
	{
		Direction facing = state.getValue(FACING);

		switch(facing)
		{
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

		return Shapes.block();
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext ctx)
	{
		Direction facing = ctx.getClickedFace();

		return BlockUtils.isSideSolid(ctx.getLevel(), ctx.getClickedPos().relative(facing.getOpposite()), facing) ? defaultBlockState().setValue(FACING, facing) : null;
	}

	@Override
	public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos){
		Direction facing = state.getValue(FACING);

		return BlockUtils.isSideSolid(world, pos.relative(facing.getOpposite()), facing);
	}

	@Override
	public void neighborChanged(BlockState state, Level world, BlockPos pos, Block blockIn, BlockPos fromPos, boolean flag) {
		if (!canSurvive(state, world, pos))
			world.destroyBlock(pos, true);
	}

	public static void togglePowerOutput(Level world, BlockPos pos, boolean par5) {
		BlockState state = world.getBlockState(pos);

		if(par5 && !state.getValue(POWERED)){
			world.setBlockAndUpdate(pos, state.setValue(POWERED, true));
			BlockUtils.updateAndNotify(world, pos, SCContent.PORTABLE_RADAR.get(), 1, false);
		}else if(!par5 && state.getValue(POWERED)){
			world.setBlockAndUpdate(pos, state.setValue(POWERED, false));
			BlockUtils.updateAndNotify(world, pos, SCContent.PORTABLE_RADAR.get(), 1, false);
		}
	}

	@Override
	public boolean isSignalSource(BlockState state)
	{
		return true;
	}

	@Override
	public int getSignal(BlockState blockState, BlockGetter world, BlockPos pos, Direction side){
		if(blockState.getValue(POWERED) && ((IModuleInventory) world.getBlockEntity(pos)).hasModule(ModuleType.REDSTONE))
			return 15;
		else
			return 0;
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder)
	{
		builder.add(POWERED, FACING);
	}

	@Override
	public BlockEntity createTileEntity(BlockState state, BlockGetter world) {
		return new PortableRadarTileEntity().nameable();
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rot)
	{
		return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
	}

	@Override
	public BlockState mirror(BlockState state, Mirror mirror)
	{
		Direction facing = state.getValue(FACING);

		switch(mirror)
		{
			case LEFT_RIGHT:
				if(facing.getAxis() == Axis.Z)
					return state.setValue(FACING, facing.getOpposite());
				break;
			case FRONT_BACK:
				if(facing.getAxis() == Axis.X)
					return state.setValue(FACING, facing.getOpposite());
				break;
			case NONE: break;
		}

		return state;
	}
}
