package net.geforcemods.securitycraft.blocks;

import java.util.Iterator;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.entity.SecurityCameraEntity;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.tileentity.SecurityCameraTileEntity;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.WorldUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SecurityCameraBlock extends OwnableBlock{

	public static final DirectionProperty FACING = DirectionProperty.create("facing", facing -> facing != Direction.UP);
	public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
	private static final VoxelShape SHAPE_SOUTH = Shapes.create(new AABB(0.275F, 0.250F, 0.000F, 0.700F, 0.800F, 0.850F));
	private static final VoxelShape SHAPE_NORTH = Shapes.create(new AABB(0.275F, 0.250F, 0.150F, 0.700F, 0.800F, 1.000F));
	private static final VoxelShape SHAPE_WEST = Shapes.create(new AABB(0.125F, 0.250F, 0.275F, 1.000F, 0.800F, 0.725F));
	private static final VoxelShape SHAPE = Shapes.create(new AABB(0.000F, 0.250F, 0.275F, 0.850F, 0.800F, 0.725F));
	private static final VoxelShape SHAPE_DOWN = Shapes.or(Block.box(7, 15, 5, 9, 16, 11), Shapes.or(Block.box(6, 15, 6, 7, 16, 10), Shapes.or(Block.box(5, 15, 7, 6, 16, 9), Shapes.or(Block.box(9, 15, 6, 10, 16, 10), Shapes.or(Block.box(10, 15, 7, 11, 16, 9), Block.box(7, 14, 7, 9, 15, 9))))));

	public SecurityCameraBlock(Block.Properties properties) {
		super(properties);
		stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(POWERED, false);
	}

	@Override
	public VoxelShape getCollisionShape(BlockState blockState, BlockGetter access, BlockPos pos, CollisionContext ctx){
		return Shapes.empty();
	}

	@Override
	public RenderShape getRenderShape(BlockState state){
		return state.getValue(FACING) == Direction.DOWN ? RenderShape.MODEL : RenderShape.ENTITYBLOCK_ANIMATED;
	}

	@Override
	public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean isMoving)
	{
		super.onRemove(state, world, pos, newState, isMoving);

		world.updateNeighborsAt(pos.north(), state.getBlock());
		world.updateNeighborsAt(pos.south(), state.getBlock());
		world.updateNeighborsAt(pos.east(), state.getBlock());
		world.updateNeighborsAt(pos.west(), state.getBlock());
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter source, BlockPos pos, CollisionContext ctx)
	{
		return switch(state.getValue(FACING)) {
			case SOUTH -> SHAPE_SOUTH;
			case NORTH -> SHAPE_NORTH;
			case WEST -> SHAPE_WEST;
			case DOWN -> SHAPE_DOWN;
			default -> SHAPE;
		};
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext ctx)
	{
		return ctx.getClickedFace() != Direction.UP ? getStateForPlacement(ctx.getLevel(), ctx.getClickedPos(), ctx.getClickedFace(), ctx.getClickLocation().x, ctx.getClickLocation().y, ctx.getClickLocation().z, ctx.getPlayer()) : null;
	}

	public BlockState getStateForPlacement(Level world, BlockPos pos, Direction facing, double hitX, double hitY, double hitZ, Player placer)
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

	public void mountCamera(Level world, BlockPos pos, int id, Player player){
		if(player.getVehicle() instanceof SecurityCameraEntity cam){
			SecurityCameraEntity dummyEntity = new SecurityCameraEntity(world, pos, id, cam);
			WorldUtils.addScheduledTask(world, () -> world.addFreshEntity(dummyEntity));
			player.startRiding(dummyEntity);
			return;
		}

		SecurityCameraEntity dummyEntity = new SecurityCameraEntity(world, pos, id, player);
		WorldUtils.addScheduledTask(world, () -> world.addFreshEntity(dummyEntity));
		player.startRiding(dummyEntity);

		if(world instanceof ServerLevel serverWorld)
		{
			serverWorld.getEntities().getAll().forEach(e -> {
				if(e instanceof Mob mob)
				{
					if(mob.getTarget() == player)
						mob.setTarget(null);
				}
			});
		}
	}

	@Override
	public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos){
		Direction facing = state.getValue(FACING);

		return BlockUtils.isSideSolid(world, pos.relative(facing.getOpposite()), facing);
	}

	@Override
	public boolean isSignalSource(BlockState state){
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
	public int getDirectSignal(BlockState blockState, BlockGetter world, BlockPos pos, Direction side){
		if(blockState.getValue(POWERED) && ((IModuleInventory) world.getBlockEntity(pos)).hasModule(ModuleType.REDSTONE))
			return 15;
		else
			return 0;
	}

	@Override
	public void neighborChanged(BlockState state, Level world, BlockPos pos, Block block, BlockPos fromPos, boolean flag) {
		if (!canSurvive(world.getBlockState(pos), world, pos) && !canSurvive(state, world, pos))
			world.destroyBlock(pos, true);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
	{
		builder.add(FACING);
		builder.add(POWERED);
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new SecurityCameraTileEntity(pos, state).nameable();
	}

	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
		return createTickerHelper(type, SCContent.teTypeSecurityCamera, SecurityCameraTileEntity::tick);
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
