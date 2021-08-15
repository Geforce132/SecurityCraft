package net.geforcemods.securitycraft.blocks;

import java.util.Map;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IIntersectable;
import net.geforcemods.securitycraft.api.OwnableBlockEntity;
import net.geforcemods.securitycraft.api.SecurityCraftBlockEntity;
import net.geforcemods.securitycraft.misc.CustomDamageSources;
import net.geforcemods.securitycraft.util.WorldUtils;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class IronFenceBlock extends OwnableBlock implements IIntersectable {
	public static final BooleanProperty NORTH = PipeBlock.NORTH;
	public static final BooleanProperty EAST = PipeBlock.EAST;
	public static final BooleanProperty SOUTH = PipeBlock.SOUTH;
	public static final BooleanProperty WEST = PipeBlock.WEST;
	protected static final Map<Direction, BooleanProperty> FACING_TO_PROPERTY_MAP = PipeBlock.PROPERTY_BY_DIRECTION.entrySet().stream().filter(entry -> entry.getKey().getAxis().isHorizontal()).collect(Util.toMap());
	protected final VoxelShape[] collisionShapes;
	protected final VoxelShape[] shapes;
	private final VoxelShape[] renderShapes;

	public IronFenceBlock(Block.Properties properties)
	{
		super(properties);

		registerDefaultState(stateDefinition.any().setValue(NORTH, false).setValue(EAST, false).setValue(SOUTH, false).setValue(WEST, false));
		renderShapes = makeShapes(2.0F, 1.0F, 16.0F, 6.0F, 15.0F);
		collisionShapes = makeShapes(2.0F, 2.0F, 24.0F, 0.0F, 24.0F);
		shapes = makeShapes(2.0F, 2.0F, 16.0F, 0.0F, 16.0F);
	}

	@Override
	public VoxelShape getOcclusionShape(BlockState state, BlockGetter world, BlockPos pos)
	{
		return renderShapes[getIndex(state)];
	}

	protected VoxelShape[] makeShapes(float p_196408_1_, float p_196408_2_, float p_196408_3_, float p_196408_4_, float p_196408_5_)
	{
		float f = 8.0F - p_196408_1_;
		float f1 = 8.0F + p_196408_1_;
		float f2 = 8.0F - p_196408_2_;
		float f3 = 8.0F + p_196408_2_;
		VoxelShape voxelshape = Block.box(f, 0.0D, f, f1, p_196408_3_, f1);
		VoxelShape voxelshape1 = Block.box(f2, p_196408_4_, 0.0D, f3, p_196408_5_, f3);
		VoxelShape voxelshape2 = Block.box(f2, p_196408_4_, f2, f3, p_196408_5_, 16.0D);
		VoxelShape voxelshape3 = Block.box(0.0D, p_196408_4_, f2, f3, p_196408_5_, f3);
		VoxelShape voxelshape4 = Block.box(f2, p_196408_4_, f2, 16.0D, p_196408_5_, f3);
		VoxelShape voxelshape5 = Shapes.or(voxelshape1, voxelshape4);
		VoxelShape voxelshape6 = Shapes.or(voxelshape2, voxelshape3);
		VoxelShape[] returnValue = {Shapes.empty(), voxelshape2, voxelshape3, voxelshape6, voxelshape1, Shapes.or(voxelshape2, voxelshape1), Shapes.or(voxelshape3, voxelshape1), Shapes.or(voxelshape6, voxelshape1), voxelshape4, Shapes.or(voxelshape2, voxelshape4), Shapes.or(voxelshape3, voxelshape4), Shapes.or(voxelshape6, voxelshape4), voxelshape5, Shapes.or(voxelshape2, voxelshape5), Shapes.or(voxelshape3, voxelshape5), Shapes.or(voxelshape6, voxelshape5)};

		for(int i = 0; i < 16; ++i)
		{
			returnValue[i] = Shapes.or(voxelshape, returnValue[i]);
		}

		return returnValue;
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext ctx)
	{
		return shapes[getIndex(state)];
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext ctx)
	{
		return collisionShapes[getIndex(state)];
	}

	public boolean connectsTo(BlockState state, boolean p_220111_2_, Direction direction)
	{
		Block block = state.getBlock();
		boolean flag = state.is(BlockTags.FENCES) && state.getMaterial() == material;
		boolean flag1 = block instanceof FenceGateBlock && FenceGateBlock.connectsToDirection(state, direction);

		return !isExceptionForConnection(state) && p_220111_2_ || flag || flag1;
	}

	private static int getMask(Direction facing)
	{
		return 1 << facing.get2DDataValue();
	}

	protected int getIndex(BlockState state)
	{
		int i = 0;

		if (state.getValue(NORTH))
			i |= getMask(Direction.NORTH);

		if (state.getValue(EAST))
			i |= getMask(Direction.EAST);

		if (state.getValue(SOUTH))
			i |= getMask(Direction.SOUTH);

		if (state.getValue(WEST))
			i |= getMask(Direction.WEST);

		return i;
	}

	@Override
	public boolean isPathfindable(BlockState state, BlockGetter world, BlockPos pos, PathComputationType type)
	{
		return false;
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rot)
	{
		switch(rot)
		{
			case CLOCKWISE_180:
				return state.setValue(NORTH, state.getValue(SOUTH)).setValue(EAST, state.getValue(WEST)).setValue(SOUTH, state.getValue(NORTH)).setValue(WEST, state.getValue(EAST));
			case COUNTERCLOCKWISE_90:
				return state.setValue(NORTH, state.getValue(EAST)).setValue(EAST, state.getValue(SOUTH)).setValue(SOUTH, state.getValue(WEST)).setValue(WEST, state.getValue(NORTH));
			case CLOCKWISE_90:
				return state.setValue(NORTH, state.getValue(WEST)).setValue(EAST, state.getValue(NORTH)).setValue(SOUTH, state.getValue(EAST)).setValue(WEST, state.getValue(SOUTH));
			default:
				return state;
		}
	}

	@Override
	public BlockState mirror(BlockState state, Mirror mirror)
	{
		switch(mirror)
		{
			case LEFT_RIGHT:
				return state.setValue(NORTH, state.getValue(SOUTH)).setValue(SOUTH, state.getValue(NORTH));
			case FRONT_BACK:
				return state.setValue(EAST, state.getValue(WEST)).setValue(WEST, state.getValue(EAST));
			default:
				return super.mirror(state, mirror);
		}
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext ctx)
	{
		BlockGetter iblockreader = ctx.getLevel();
		BlockPos blockpos = ctx.getClickedPos();
		BlockPos blockpos1 = blockpos.north();
		BlockPos blockpos2 = blockpos.east();
		BlockPos blockpos3 = blockpos.south();
		BlockPos blockpos4 = blockpos.west();
		BlockState blockstate = iblockreader.getBlockState(blockpos1);
		BlockState blockstate1 = iblockreader.getBlockState(blockpos2);
		BlockState blockstate2 = iblockreader.getBlockState(blockpos3);
		BlockState blockstate3 = iblockreader.getBlockState(blockpos4);
		return super.getStateForPlacement(ctx).setValue(NORTH, connectsTo(blockstate, blockstate.isFaceSturdy(iblockreader, blockpos1, Direction.SOUTH), Direction.SOUTH)).setValue(EAST, connectsTo(blockstate1, blockstate1.isFaceSturdy(iblockreader, blockpos2, Direction.WEST), Direction.WEST)).setValue(SOUTH, connectsTo(blockstate2, blockstate2.isFaceSturdy(iblockreader, blockpos3, Direction.NORTH), Direction.NORTH)).setValue(WEST, connectsTo(blockstate3, blockstate3.isFaceSturdy(iblockreader, blockpos4, Direction.EAST), Direction.EAST));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
	{
		builder.add(NORTH, EAST, WEST, SOUTH);
	}

	@Override
	public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor world, BlockPos currentPos, BlockPos facingPos)
	{
		return facing.getAxis().getPlane() == Direction.Plane.HORIZONTAL ? state.setValue(FACING_TO_PROPERTY_MAP.get(facing), connectsTo(facingState, facingState.isFaceSturdy(world, facingPos, facing.getOpposite()), facing.getOpposite())) : super.updateShape(state, facing, facingState, world, currentPos, facingPos);
	}

	@Override
	public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit)
	{
		return InteractionResult.FAIL;
	}

	@Override
	public void onEntityIntersected(Level world, BlockPos pos, Entity entity)
	{
		//so dropped items don't get destroyed
		if(entity instanceof ItemEntity)
			return;
		//owner check
		else if(entity instanceof Player player)
		{
			if(((OwnableBlockEntity) world.getBlockEntity(pos)).getOwner().isOwner(player))
				return;
		}
		else if(!world.isClientSide && entity instanceof Creeper creeper)
		{
			LightningBolt lightning = WorldUtils.createLightning(world, Vec3.atBottomCenterOf(pos), true);

			creeper.thunderHit((ServerLevel)world, lightning);
			creeper.clearFire();
			return;
		}

		entity.hurt(CustomDamageSources.ELECTRICITY, 6.0F); //3 hearts per attack
	}

	@Override
	public boolean triggerEvent(BlockState state, Level world, BlockPos pos, int eventID, int eventParam)
	{
		super.triggerEvent(state, world, pos, eventID, eventParam);
		BlockEntity tileentity = world.getBlockEntity(pos);
		return tileentity == null ? false : tileentity.triggerEvent(eventID, eventParam);
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
	{
		return new SecurityCraftBlockEntity(pos, state).intersectsEntities();
	}

	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
		return createTickerHelper(type, SCContent.beTypeAbstract, SecurityCraftBlockEntity::tick);
	}
}