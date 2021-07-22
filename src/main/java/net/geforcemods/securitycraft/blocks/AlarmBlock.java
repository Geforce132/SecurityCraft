package net.geforcemods.securitycraft.blocks;

import java.util.Random;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.tileentity.AlarmTileEntity;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
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
import net.minecraft.world.server.ServerWorld;

public class AlarmBlock extends OwnableBlock {

	public static final BooleanProperty LIT = BlockStateProperties.LIT;
	public static final DirectionProperty FACING = BlockStateProperties.FACING;
	private static final VoxelShape SHAPE_EAST = Block.box(0, 4, 4, 8, 12, 12);
	private static final VoxelShape SHAPE_WEST = Block.box(8, 4, 4, 16, 12, 12);
	private static final VoxelShape SHAPE_NORTH = Block.box(4, 4, 8, 12, 12, 16);
	private static final VoxelShape SHAPE_SOUTH = Block.box(4, 4, 0, 12, 12, 8);
	private static final VoxelShape SHAPE_UP = Block.box(4, 0, 4, 12, 8, 12);
	private static final VoxelShape SHAPE_DOWN = Block.box(4, 8, 4, 12, 16, 12);

	public AlarmBlock(Block.Properties properties) {
		super(properties);

		registerDefaultState(stateDefinition.any().setValue(FACING, Direction.UP).setValue(LIT, false));
	}

	/**
	 * Check whether this Block can be placed on the given side
	 */
	@Override
	public boolean canSurvive(BlockState state, IWorldReader world, BlockPos pos){
		Direction facing = state.getValue(FACING);

		return facing == Direction.UP && BlockUtils.isSideSolid(world, pos.below(), Direction.UP) ? true : BlockUtils.isSideSolid(world, pos.relative(facing.getOpposite()), facing);
	}

	@Override
	public void neighborChanged(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean flag)
	{
		if (!canSurvive(state, world, pos))
			world.destroyBlock(pos, true);
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext ctx)
	{
		return getStateForPlacement(ctx.getLevel(), ctx.getClickedPos(), ctx.getClickedFace(), ctx.getClickLocation().x, ctx.getClickLocation().y, ctx.getClickLocation().z, ctx.getPlayer());
	}

	public BlockState getStateForPlacement(World world, BlockPos pos, Direction facing, double hitX, double hitY, double hitZ, PlayerEntity placer)
	{
		return BlockUtils.isSideSolid(world, pos.relative(facing.getOpposite()), facing) ? defaultBlockState().setValue(FACING, facing) : null;
	}

	/**
	 * Called whenever the block is added into the world. Args: world, x, y, z
	 */
	@Override
	public void onPlace(BlockState state, World world, BlockPos pos, BlockState oldState, boolean flag) {
		if(world.isClientSide)
			return;
		else
			world.getBlockTicks().scheduleTick(pos, state.getBlock(), 5);
	}

	/**
	 * Ticks the block if it's been scheduled
	 */
	@Override
	public void tick(BlockState state, ServerWorld world, BlockPos pos, Random random)
	{
		if(!world.isClientSide){
			playSoundAndUpdate(world, pos);

			world.getBlockTicks().scheduleTick(pos, state.getBlock(), 5);
		}
	}

	@Override
	public void onNeighborChange(BlockState state, IWorldReader w, BlockPos pos, BlockPos neighbor){
		if(w.isClientSide() || !(w instanceof World))
			return;

		World world = (World)w;

		playSoundAndUpdate((world), pos);

		Direction facing = world.getBlockState(pos).getValue(FACING);

		if (!BlockUtils.isSideSolid(world, pos.relative(facing.getOpposite()), facing))
			world.destroyBlock(pos, true);
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader source, BlockPos pos, ISelectionContext ctx)
	{
		Direction facing = state.getValue(FACING);

		switch(facing){
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

	private void playSoundAndUpdate(World world, BlockPos pos){
		BlockState state = world.getBlockState(pos);

		if(state.getBlock() != SCContent.ALARM.get())
			return;

		TileEntity tile = world.getBlockEntity(pos);

		if(tile instanceof AlarmTileEntity)
		{
			AlarmTileEntity te = (AlarmTileEntity)tile;

			if(world.getBestNeighborSignal(pos) > 0){
				boolean isPowered = te.isPowered();

				if(!isPowered){
					world.setBlockAndUpdate(pos, state.setValue(LIT, true));
					te.setPowered(true);
				}

			}else{
				boolean isPowered = te.isPowered();

				if(isPowered){
					world.setBlockAndUpdate(pos, state.setValue(LIT, false));
					te.setPowered(false);
				}
			}
		}
	}

	@Override
	public ItemStack getCloneItemStack(IBlockReader world, BlockPos pos, BlockState state)
	{
		return new ItemStack(SCContent.ALARM.get().asItem());
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder){
		builder.add(FACING);
		builder.add(LIT);
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader reader){
		return new AlarmTileEntity();
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
