package net.geforcemods.securitycraft.blocks;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.tileentity.PortableRadarTileEntity;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
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
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

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
	public VoxelShape getShape(BlockState state, IBlockReader source, BlockPos pos, ISelectionContext ctx)
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

		return VoxelShapes.block();
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext ctx)
	{
		Direction facing = ctx.getClickedFace();

		return BlockUtils.isSideSolid(ctx.getLevel(), ctx.getClickedPos().relative(facing.getOpposite()), facing) ? defaultBlockState().setValue(FACING, facing) : null;
	}

	@Override
	public boolean canSurvive(BlockState state, IWorldReader world, BlockPos pos){
		Direction facing = state.getValue(FACING);

		return BlockUtils.isSideSolid(world, pos.relative(facing.getOpposite()), facing);
	}

	@Override
	public void neighborChanged(BlockState state, World world, BlockPos pos, Block blockIn, BlockPos fromPos, boolean flag) {
		if (!canSurvive(state, world, pos))
			world.destroyBlock(pos, true);
	}

	public static void togglePowerOutput(World world, BlockPos pos, boolean par5) {
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
	public int getSignal(BlockState blockState, IBlockReader world, BlockPos pos, Direction side){
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
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
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
