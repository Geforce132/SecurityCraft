package net.geforcemods.securitycraft.blocks;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.entity.SecurityCameraEntity;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.tileentity.SecurityCameraTileEntity;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.WorldUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class SecurityCameraBlock extends OwnableBlock{

	public static final DirectionProperty FACING = DirectionProperty.create("facing", facing -> facing != Direction.UP);
	public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
	private static final VoxelShape SHAPE_SOUTH = VoxelShapes.create(new AxisAlignedBB(0.275F, 0.250F, 0.000F, 0.700F, 0.800F, 0.850F));
	private static final VoxelShape SHAPE_NORTH = VoxelShapes.create(new AxisAlignedBB(0.275F, 0.250F, 0.150F, 0.700F, 0.800F, 1.000F));
	private static final VoxelShape SHAPE_WEST = VoxelShapes.create(new AxisAlignedBB(0.125F, 0.250F, 0.275F, 1.000F, 0.800F, 0.725F));
	private static final VoxelShape SHAPE = VoxelShapes.create(new AxisAlignedBB(0.000F, 0.250F, 0.275F, 0.850F, 0.800F, 0.725F));
	private static final VoxelShape SHAPE_DOWN = VoxelShapes.or(Block.box(7, 15, 5, 9, 16, 11), VoxelShapes.or(Block.box(6, 15, 6, 7, 16, 10), VoxelShapes.or(Block.box(5, 15, 7, 6, 16, 9), VoxelShapes.or(Block.box(9, 15, 6, 10, 16, 10), VoxelShapes.or(Block.box(10, 15, 7, 11, 16, 9), Block.box(7, 14, 7, 9, 15, 9))))));

	public SecurityCameraBlock(Block.Properties properties) {
		super(properties);
		stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(POWERED, false);
	}

	@Override
	public VoxelShape getCollisionShape(BlockState blockState, IBlockReader access, BlockPos pos, ISelectionContext ctx){
		return VoxelShapes.empty();
	}

	@Override
	public BlockRenderType getRenderShape(BlockState state){
		return state.getValue(FACING) == Direction.DOWN ? BlockRenderType.MODEL : BlockRenderType.ENTITYBLOCK_ANIMATED;
	}

	@Override
	public void onRemove(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving)
	{
		super.onRemove(state, world, pos, newState, isMoving);

		world.updateNeighborsAt(pos.north(), world.getBlockState(pos).getBlock());
		world.updateNeighborsAt(pos.south(), world.getBlockState(pos).getBlock());
		world.updateNeighborsAt(pos.east(), world.getBlockState(pos).getBlock());
		world.updateNeighborsAt(pos.west(), world.getBlockState(pos).getBlock());
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader source, BlockPos pos, ISelectionContext ctx)
	{
		Direction dir = state.getValue(FACING);

		if(dir == Direction.SOUTH)
			return SHAPE_SOUTH;
		else if(dir == Direction.NORTH)
			return SHAPE_NORTH;
		else if(dir == Direction.WEST)
			return SHAPE_WEST;
		else if(dir == Direction.DOWN)
			return SHAPE_DOWN;
		else
			return SHAPE;
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext ctx)
	{
		return ctx.getClickedFace() != Direction.UP ? getStateForPlacement(ctx.getLevel(), ctx.getClickedPos(), ctx.getClickedFace(), ctx.getClickLocation().x, ctx.getClickLocation().y, ctx.getClickLocation().z, ctx.getPlayer()) : null;
	}

	public BlockState getStateForPlacement(World world, BlockPos pos, Direction facing, double hitX, double hitY, double hitZ, PlayerEntity placer)
	{
		BlockState state = defaultBlockState().setValue(POWERED, false);

		if(BlockUtils.isSideSolid(world, pos.relative(facing.getOpposite()), facing))
			return state.setValue(FACING, facing).setValue(POWERED, false);
		else{
			Iterator<?> iterator = Direction.Plane.HORIZONTAL.iterator();
			Direction iFacing;

			do{
				if(!iterator.hasNext())
					return state;

				iFacing = (Direction)iterator.next();
			}while (!BlockUtils.isSideSolid(world, pos.relative(iFacing.getOpposite()), iFacing));

			return state.setValue(FACING, facing).setValue(POWERED, false);
		}
	}

	public void mountCamera(World world, int x, int y, int z, int id, PlayerEntity player){
		if(player.getVehicle() instanceof SecurityCameraEntity){
			SecurityCameraEntity dummyEntity = new SecurityCameraEntity(world, x, y, z, id, (SecurityCameraEntity) player.getVehicle());
			WorldUtils.addScheduledTask(world, () -> world.addFreshEntity(dummyEntity));
			player.startRiding(dummyEntity);
			return;
		}

		SecurityCameraEntity dummyEntity = new SecurityCameraEntity(world, x, y, z, id, player);
		WorldUtils.addScheduledTask(world, () -> world.addFreshEntity(dummyEntity));
		player.startRiding(dummyEntity);

		if(world instanceof ServerWorld)
		{
			ServerWorld serverWorld = (ServerWorld)world;
			List<Entity> loadedEntityList = serverWorld.getEntities().collect(Collectors.toList());

			for(Entity e : loadedEntityList)
			{
				if(e instanceof MobEntity)
				{
					if(((MobEntity)e).getTarget() == player)
						((MobEntity)e).setTarget(null);
				}
			}
		}
	}

	@Override
	public boolean canSurvive(BlockState state, IWorldReader world, BlockPos pos){
		Direction facing = state.getValue(FACING);

		return BlockUtils.isSideSolid(world, pos.relative(facing.getOpposite()), facing);
	}

	@Override
	public boolean isSignalSource(BlockState state){
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
	public int getDirectSignal(BlockState blockState, IBlockReader world, BlockPos pos, Direction side){
		if(blockState.getValue(POWERED) && ((IModuleInventory) world.getBlockEntity(pos)).hasModule(ModuleType.REDSTONE))
			return 15;
		else
			return 0;
	}

	@Override
	public void neighborChanged(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean flag) {
		if (!canSurvive(world.getBlockState(pos), world, pos) && !canSurvive(state, world, pos))
			world.destroyBlock(pos, true);
	}

	@Override
	protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder)
	{
		builder.add(FACING);
		builder.add(POWERED);
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new SecurityCameraTileEntity().nameable();
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
