package net.geforcemods.securitycraft.blocks;

import java.util.Map;

import net.geforcemods.securitycraft.blockentities.ElectrifiedFenceAndGateBlockEntity;
import net.geforcemods.securitycraft.misc.CustomDamageSources;
import net.geforcemods.securitycraft.util.LevelUtils;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ElectrifiedIronFenceBlock extends OwnableBlock {
	public static final BooleanProperty NORTH = PipeBlock.NORTH;
	public static final BooleanProperty EAST = PipeBlock.EAST;
	public static final BooleanProperty SOUTH = PipeBlock.SOUTH;
	public static final BooleanProperty WEST = PipeBlock.WEST;
	protected static final Map<Direction, BooleanProperty> FACING_TO_PROPERTY_MAP = PipeBlock.PROPERTY_BY_DIRECTION.entrySet().stream().filter(entry -> entry.getKey().getAxis().isHorizontal()).collect(Util.toMap());
	protected final VoxelShape[] collisionShapes;
	protected final VoxelShape[] shapes;
	private final VoxelShape[] renderShapes;

	public ElectrifiedIronFenceBlock(BlockBehaviour.Properties properties) {
		super(properties);

		registerDefaultState(stateDefinition.any().setValue(NORTH, false).setValue(EAST, false).setValue(SOUTH, false).setValue(WEST, false));
		renderShapes = makeShapes(2.0F, 1.0F, 16.0F, 6.0F, 15.0F);
		collisionShapes = makeShapes(2.0F, 2.0F, 24.0F, 0.0F, 24.0F);
		shapes = makeShapes(2.0F, 2.0F, 16.0F, 0.0F, 16.0F);
	}

	@Override
	public VoxelShape getOcclusionShape(BlockState state) {
		return renderShapes[getIndex(state)];
	}

	protected VoxelShape[] makeShapes(float nodeWith, float extensionWidth, float nodeHeight, float extensionBottom, float extensionHeight) {
		float f = 8.0F - nodeWith;
		float f1 = 8.0F + nodeWith;
		float f2 = 8.0F - extensionWidth;
		float f3 = 8.0F + extensionWidth;
		VoxelShape voxelshape = Block.box(f, 0.0D, f, f1, nodeHeight, f1);
		VoxelShape voxelshape1 = Block.box(f2, extensionBottom, 0.0D, f3, extensionHeight, f3);
		VoxelShape voxelshape2 = Block.box(f2, extensionBottom, f2, f3, extensionHeight, 16.0D);
		VoxelShape voxelshape3 = Block.box(0.0D, extensionBottom, f2, f3, extensionHeight, f3);
		VoxelShape voxelshape4 = Block.box(f2, extensionBottom, f2, 16.0D, extensionHeight, f3);
		VoxelShape voxelshape5 = Shapes.or(voxelshape1, voxelshape4);
		VoxelShape voxelshape6 = Shapes.or(voxelshape2, voxelshape3);
		//@formatter:off
		VoxelShape[] returnValue = {
				Shapes.empty(),
				voxelshape2,
				voxelshape3,
				voxelshape6,
				voxelshape1,
				Shapes.or(voxelshape2, voxelshape1),
				Shapes.or(voxelshape3, voxelshape1),
				Shapes.or(voxelshape6, voxelshape1),
				voxelshape4,
				Shapes.or(voxelshape2, voxelshape4),
				Shapes.or(voxelshape3, voxelshape4),
				Shapes.or(voxelshape6, voxelshape4),
				voxelshape5,
				Shapes.or(voxelshape2, voxelshape5),
				Shapes.or(voxelshape3, voxelshape5),
				Shapes.or(voxelshape6, voxelshape5)
		};
		//@formatter:on

		for (int i = 0; i < 16; ++i) {
			returnValue[i] = Shapes.or(voxelshape, returnValue[i]);
		}

		return returnValue;
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
		return shapes[getIndex(state)];
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
		return collisionShapes[getIndex(state)];
	}

	public boolean connectsTo(BlockState state, boolean isFaceSturdy, Direction direction) {
		Block block = state.getBlock();
		boolean isFence = state.is(BlockTags.FENCES) && !state.is(BlockTags.WOODEN_FENCES);
		boolean isFenceGate = block instanceof FenceGateBlock && FenceGateBlock.connectsToDirection(state, direction);

		return !isExceptionForConnection(state) && isFaceSturdy || isFence || isFenceGate;
	}

	private static int getMask(Direction facing) {
		return 1 << facing.get2DDataValue();
	}

	protected int getIndex(BlockState state) {
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
	public boolean isPathfindable(BlockState state, PathComputationType type) {
		return false;
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rot) {
		return switch (rot) {
			//@formatter:off
			case CLOCKWISE_180 -> state.setValue(NORTH, state.getValue(SOUTH)).setValue(EAST, state.getValue(WEST)).setValue(SOUTH, state.getValue(NORTH)).setValue(WEST, state.getValue(EAST));
			case COUNTERCLOCKWISE_90 -> state.setValue(NORTH, state.getValue(EAST)).setValue(EAST, state.getValue(SOUTH)).setValue(SOUTH, state.getValue(WEST)).setValue(WEST, state.getValue(NORTH));
			case CLOCKWISE_90 -> state.setValue(NORTH, state.getValue(WEST)).setValue(EAST, state.getValue(NORTH)).setValue(SOUTH, state.getValue(EAST)).setValue(WEST, state.getValue(SOUTH));
			default -> state;
			//@formatter:on
		};
	}

	@Override
	public BlockState mirror(BlockState state, Mirror mirror) {
		return switch (mirror) {
			case LEFT_RIGHT -> state.setValue(NORTH, state.getValue(SOUTH)).setValue(SOUTH, state.getValue(NORTH));
			case FRONT_BACK -> state.setValue(EAST, state.getValue(WEST)).setValue(WEST, state.getValue(EAST));
			default -> super.mirror(state, mirror);
		};
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext ctx) {
		BlockGetter level = ctx.getLevel();
		BlockPos clickedPos = ctx.getClickedPos();
		BlockPos northPos = clickedPos.north();
		BlockPos eastPos = clickedPos.east();
		BlockPos southPos = clickedPos.south();
		BlockPos westPos = clickedPos.west();
		BlockState northState = level.getBlockState(northPos);
		BlockState eastState = level.getBlockState(eastPos);
		BlockState southState = level.getBlockState(southPos);
		BlockState westState = level.getBlockState(westPos);
		return super.getStateForPlacement(ctx).setValue(NORTH, connectsTo(northState, northState.isFaceSturdy(level, northPos, Direction.SOUTH), Direction.SOUTH)).setValue(EAST, connectsTo(eastState, eastState.isFaceSturdy(level, eastPos, Direction.WEST), Direction.WEST)).setValue(SOUTH, connectsTo(southState, southState.isFaceSturdy(level, southPos, Direction.NORTH), Direction.NORTH)).setValue(WEST, connectsTo(westState, westState.isFaceSturdy(level, westPos, Direction.EAST), Direction.EAST));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(NORTH, EAST, WEST, SOUTH);
	}

	@Override
	public BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess tickAccess, BlockPos pos, Direction facing, BlockPos facingPos, BlockState facingState, RandomSource random) {
		return facing.getAxis().getPlane() == Direction.Plane.HORIZONTAL ? state.setValue(FACING_TO_PROPERTY_MAP.get(facing), connectsTo(facingState, facingState.isFaceSturdy(level, facingPos, facing.getOpposite()), facing.getOpposite())) : super.updateShape(state, level, tickAccess, pos, facing, facingPos, facingState, random);
	}

	@Override
	public InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
		return InteractionResult.FAIL;
	}

	@Override
	public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
		hurtOrConvertEntity(this::getShape, state, level, pos, entity);
	}

	public static void hurtOrConvertEntity(ShapeGetter shapeGetter, BlockState state, Level level, BlockPos pos, Entity entity) {
		if (level.getGameTime() % 20 != 0)
			return;
		else if (entity.isRemoved() || !shapeGetter.getShape(state, level, pos, CollisionContext.of(entity)).bounds().move(pos).inflate(0.01D).intersects(entity.getBoundingBox()))
			return;
		else if (entity instanceof ItemEntity) //so dropped items don't get destroyed
			return;

		ElectrifiedFenceAndGateBlockEntity be = (ElectrifiedFenceAndGateBlockEntity) level.getBlockEntity(pos);

		if (entity instanceof Player player) {
			if (be.isOwnedBy(player) || be.isAllowed(player))
				return;
		}
		else if (entity instanceof OwnableEntity ownableEntity) {
			if (be.allowsOwnableEntity(ownableEntity))
				return;
		}
		else if (!level.isClientSide) {
			LightningBolt lightning = LevelUtils.createLightning(level, Vec3.atBottomCenterOf(pos), true);

			entity.thunderHit((ServerLevel) level, lightning);
			entity.clearFire();
			return;
		}

		entity.hurt(CustomDamageSources.electricity(level.registryAccess()), 6.0F); //3 hearts per attack
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new ElectrifiedFenceAndGateBlockEntity(pos, state);
	}

	@FunctionalInterface
	public interface ShapeGetter {
		VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx);
	}
}