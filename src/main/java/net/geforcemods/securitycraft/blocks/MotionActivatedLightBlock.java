package net.geforcemods.securitycraft.blocks;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.tileentity.MotionActivatedLightTileEntity;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
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
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

public class MotionActivatedLightBlock extends OwnableBlock {

	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
	public static final BooleanProperty LIT = BlockStateProperties.LIT;
	private static final VoxelShape SHAPE_NORTH = VoxelShapes.or(Block.box(6, 3, 13, 10, 4, 14), VoxelShapes.or(Block.box(6, 6, 13, 10, 9, 14), VoxelShapes.joinUnoptimized(Block.box(7, 3, 14, 9, 8, 16), Block.box(7, 4, 15, 9, 7, 14), IBooleanFunction.ONLY_FIRST)));
	private static final VoxelShape SHAPE_EAST = VoxelShapes.or(Block.box(3, 3, 6, 2, 4, 10), VoxelShapes.or(Block.box(3, 6, 6, 2, 9, 10), VoxelShapes.joinUnoptimized(Block.box(2, 3, 7, 0, 8, 9), Block.box(1, 4, 7, 2, 7, 9), IBooleanFunction.ONLY_FIRST)));
	private static final VoxelShape SHAPE_SOUTH = VoxelShapes.or(Block.box(6, 3, 2, 10, 4, 3), VoxelShapes.or(Block.box(6, 6, 2, 10, 9, 3), VoxelShapes.joinUnoptimized(Block.box(7, 3, 0, 9, 8, 2), Block.box(7, 4, 1, 9, 7, 2), IBooleanFunction.ONLY_FIRST)));
	private static final VoxelShape SHAPE_WEST = VoxelShapes.or(Block.box(13, 3, 6, 14, 4, 10), VoxelShapes.or(Block.box(13, 6, 6, 14, 9, 10), VoxelShapes.joinUnoptimized(Block.box(14, 3, 7, 16, 8, 9), Block.box(15, 4, 7, 14, 7, 9), IBooleanFunction.ONLY_FIRST)));

	public MotionActivatedLightBlock(Block.Properties properties) {
		super(properties);
		registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(LIT, false));
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext ctx){
		switch(state.getValue(FACING))
		{
			case NORTH: return SHAPE_NORTH;
			case EAST: return SHAPE_EAST;
			case SOUTH: return SHAPE_SOUTH;
			case WEST: return SHAPE_WEST;
			default: return VoxelShapes.block();
		}
	}

	public static void toggleLight(World world, BlockPos pos, BlockState state, Owner owner, boolean isLit) {
		if(!world.isClientSide)
		{
			world.setBlockAndUpdate(pos, state.setValue(LIT, isLit));

			TileEntity te = world.getBlockEntity(pos);

			if(te instanceof IOwnable)
				((IOwnable) te).setOwner(owner.getUUID(), owner.getName());

			BlockUtils.updateAndNotify(world, pos, SCContent.MOTION_ACTIVATED_LIGHT.get(), 1, false);
		}
	}

	@Override
	public boolean canSurvive(BlockState state, IWorldReader world, BlockPos pos){
		Direction side = state.getValue(FACING);

		return side != Direction.UP && side != Direction.DOWN && BlockUtils.isSideSolid(world, pos.relative(side.getOpposite()), side);
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext ctx)
	{
		return getStateForPlacement(ctx.getLevel(), ctx.getClickedPos(), ctx.getClickedFace(), ctx.getClickLocation().x, ctx.getClickLocation().y, ctx.getClickLocation().z, ctx.getPlayer());
	}

	public BlockState getStateForPlacement(World world, BlockPos pos, Direction facing, double hitX, double hitY, double hitZ, PlayerEntity placer)
	{
		return facing != Direction.UP && facing != Direction.DOWN && BlockUtils.isSideSolid(world, pos.relative(facing.getOpposite()), facing) ? defaultBlockState().setValue(FACING, facing) : null;
	}

	@Override
	public void neighborChanged(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean flag) {
		if (!canSurvive(state, world, pos))
			world.destroyBlock(pos, true);
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder)
	{
		builder.add(FACING);
		builder.add(LIT);
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new MotionActivatedLightTileEntity().attacks(LivingEntity.class, 5, 1);
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
