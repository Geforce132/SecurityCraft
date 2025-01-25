package net.geforcemods.securitycraft.blocks.reinforced;

import java.util.Map;
import java.util.function.BooleanSupplier;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.Owner;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.MultifaceBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.WallSide;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ReinforcedMossyCarpetBlock extends BaseReinforcedBlock implements BonemealableBlock {
	public static final BooleanProperty BASE = BlockStateProperties.BOTTOM;
	private static final EnumProperty<WallSide> NORTH = BlockStateProperties.NORTH_WALL;
	private static final EnumProperty<WallSide> EAST = BlockStateProperties.EAST_WALL;
	private static final EnumProperty<WallSide> SOUTH = BlockStateProperties.SOUTH_WALL;
	private static final EnumProperty<WallSide> WEST = BlockStateProperties.WEST_WALL;
	private static final Map<Direction, EnumProperty<WallSide>> PROPERTY_BY_DIRECTION = ImmutableMap.copyOf(Util.make(Maps.newEnumMap(Direction.class), map -> {
		map.put(Direction.NORTH, NORTH);
		map.put(Direction.EAST, EAST);
		map.put(Direction.SOUTH, SOUTH);
		map.put(Direction.WEST, WEST);
	}));
	private static final VoxelShape DOWN_AABB = Block.box(0.0, 0.0, 0.0, 16.0, 1.0, 16.0);
	private static final VoxelShape WEST_AABB = Block.box(0.0, 0.0, 0.0, 1.0, 16.0, 16.0);
	private static final VoxelShape EAST_AABB = Block.box(15.0, 0.0, 0.0, 16.0, 16.0, 16.0);
	private static final VoxelShape NORTH_AABB = Block.box(0.0, 0.0, 0.0, 16.0, 16.0, 1.0);
	private static final VoxelShape SOUTH_AABB = Block.box(0.0, 0.0, 15.0, 16.0, 16.0, 16.0);
	private static final VoxelShape WEST_SHORT_AABB = Block.box(0.0, 0.0, 0.0, 1.0, 10.0, 16.0);
	private static final VoxelShape EAST_SHORT_AABB = Block.box(15.0, 0.0, 0.0, 16.0, 10.0, 16.0);
	private static final VoxelShape NORTH_SHORT_AABB = Block.box(0.0, 0.0, 0.0, 16.0, 10.0, 1.0);
	private static final VoxelShape SOUTH_SHORT_AABB = Block.box(0.0, 0.0, 15.0, 16.0, 10.0, 16.0);
	private final Map<BlockState, VoxelShape> shapesCache;

	public ReinforcedMossyCarpetBlock(BlockBehaviour.Properties properties, Block block) {
		super(properties, block);
		registerDefaultState(stateDefinition.any().setValue(BASE, true).setValue(NORTH, WallSide.NONE).setValue(EAST, WallSide.NONE).setValue(SOUTH, WallSide.NONE).setValue(WEST, WallSide.NONE));
		shapesCache = ImmutableMap.copyOf(stateDefinition.getPossibleStates().stream().collect(Collectors.toMap(Function.identity(), ReinforcedMossyCarpetBlock::calculateShape)));
	}

	@Override
	protected VoxelShape getOcclusionShape(BlockState state) {
		return Shapes.empty();
	}

	private static VoxelShape calculateShape(BlockState state) {
		VoxelShape voxelshape = Shapes.empty();

		if (state.getValue(BASE))
			voxelshape = DOWN_AABB;

		voxelshape = switch (state.getValue(NORTH)) {
			case NONE -> voxelshape;
			case LOW -> Shapes.or(voxelshape, NORTH_SHORT_AABB);
			case TALL -> Shapes.or(voxelshape, NORTH_AABB);
		};
		voxelshape = switch (state.getValue(SOUTH)) {
			case NONE -> voxelshape;
			case LOW -> Shapes.or(voxelshape, SOUTH_SHORT_AABB);
			case TALL -> Shapes.or(voxelshape, SOUTH_AABB);
		};
		voxelshape = switch (state.getValue(EAST)) {
			case NONE -> voxelshape;
			case LOW -> Shapes.or(voxelshape, EAST_SHORT_AABB);
			case TALL -> Shapes.or(voxelshape, EAST_AABB);
		};
		voxelshape = switch (state.getValue(WEST)) {
			case NONE -> voxelshape;
			case LOW -> Shapes.or(voxelshape, WEST_SHORT_AABB);
			case TALL -> Shapes.or(voxelshape, WEST_AABB);
		};

		return voxelshape.isEmpty() ? Shapes.block() : voxelshape;
	}

	@Override
	protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
		return shapesCache.get(state);
	}

	@Override
	protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
		return state.getValue(BASE) ? DOWN_AABB : Shapes.empty();
	}

	@Override
	protected boolean propagatesSkylightDown(BlockState state) {
		return true;
	}

	@Override
	protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
		BlockState stateBelow = level.getBlockState(pos.below());

		return state.getValue(BASE) || stateBelow.is(this) && stateBelow.getValue(BASE);
	}

	private static boolean hasFaces(BlockState state) {
		if (state.getValue(BASE))
			return true;
		else {
			for (EnumProperty<WallSide> wallSide : PROPERTY_BY_DIRECTION.values()) {
				if (state.getValue(wallSide) != WallSide.NONE)
					return true;
			}

			return false;
		}
	}

	private static boolean canSupportAtFace(BlockGetter level, BlockPos pos, Direction direction) {
		if (direction == Direction.UP)
			return false;
		else {
			BlockPos relativePos = pos.relative(direction);

			return MultifaceBlock.canAttachTo(level, direction, relativePos, level.getBlockState(relativePos));
		}
	}

	private static BlockState getUpdatedState(BlockState state, BlockGetter level, BlockPos pos, boolean tip) {
		BlockState stateAbove = null;
		BlockState stateBelow = null;

		tip |= state.getValue(BASE);

		for (Direction direction : Direction.Plane.HORIZONTAL) {
			EnumProperty<WallSide> wallSideProperty = getPropertyForFace(direction);
			WallSide wallSide = canSupportAtFace(level, pos, direction) ? (tip ? WallSide.LOW : state.getValue(wallSideProperty)) : WallSide.NONE;

			if (wallSide == WallSide.LOW) {
				if (stateAbove == null)
					stateAbove = level.getBlockState(pos.above());

				if (stateAbove.is(SCContent.REINFORCED_PALE_MOSS_CARPET) && stateAbove.getValue(wallSideProperty) != WallSide.NONE && !stateAbove.getValue(BASE))
					wallSide = WallSide.TALL;

				if (!state.getValue(BASE)) {
					if (stateBelow == null)
						stateBelow = level.getBlockState(pos.below());

					if (stateBelow.is(SCContent.REINFORCED_PALE_MOSS_CARPET) && stateBelow.getValue(wallSideProperty) == WallSide.NONE)
						wallSide = WallSide.NONE;
				}
			}

			state = state.setValue(wallSideProperty, wallSide);
		}

		return state;
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext ctx) {
		return getUpdatedState(defaultBlockState(), ctx.getLevel(), ctx.getClickedPos(), true);
	}

	public static void placeAt(LevelAccessor level, BlockPos pos, RandomSource random, int flags) {
		BlockState updatedState = getUpdatedState(SCContent.REINFORCED_PALE_MOSS_CARPET.get().defaultBlockState(), level, pos, true);
		BlockState topper;

		level.setBlock(pos, updatedState, 3);
		topper = createTopperWithSideChance(level, pos, random::nextBoolean);

		if (!topper.isAir())
			setBlockAndOwner(level, pos.above(), topper, flags, (IOwnable) level.getBlockEntity(pos));
	}

	@Override
	public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity entity, ItemStack stack) {
		super.setPlacedBy(level, pos, state, entity, stack);

		if (!level.isClientSide) {
			BlockState topper = createTopperWithSideChance(level, pos, level.getRandom()::nextBoolean);

			if (!topper.isAir())
				setBlockAndOwner(level, pos.above(), topper, UPDATE_ALL, (IOwnable) level.getBlockEntity(pos));
		}
	}

	public static void setBlockAndOwner(LevelAccessor level, BlockPos pos, BlockState state, int flags, IOwnable parent) {
		level.setBlock(pos, state, flags);

		if (level.getBlockEntity(pos) instanceof IOwnable ownable) {
			Owner owner = parent.getOwner();

			ownable.setOwner(owner.getUUID(), owner.getName());
		}
	}

	private static BlockState createTopperWithSideChance(BlockGetter level, BlockPos pos, BooleanSupplier chanceGetter) {
		BlockPos posAbove = pos.above();
		BlockState stateAbove = level.getBlockState(posAbove);
		boolean flag = stateAbove.is(SCContent.REINFORCED_PALE_MOSS_CARPET);

		if ((!flag || !stateAbove.getValue(BASE)) && (flag || stateAbove.canBeReplaced())) {
			BlockState notBase = SCContent.REINFORCED_PALE_MOSS_CARPET.get().defaultBlockState().setValue(BASE, false);
			BlockState updatedState = getUpdatedState(notBase, level, pos.above(), true);

			for (Direction direction : Direction.Plane.HORIZONTAL) {
				EnumProperty<WallSide> wallSide = getPropertyForFace(direction);

				if (updatedState.getValue(wallSide) != WallSide.NONE && !chanceGetter.getAsBoolean())
					updatedState = updatedState.setValue(wallSide, WallSide.NONE);
			}

			return hasFaces(updatedState) && updatedState != stateAbove ? updatedState : Blocks.AIR.defaultBlockState();
		}
		else
			return Blocks.AIR.defaultBlockState();
	}

	@Override
	protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess tickAccess, BlockPos pos, Direction direction, BlockPos fromPos, BlockState fromState, RandomSource random) {
		if (!state.canSurvive(level, pos))
			return Blocks.AIR.defaultBlockState();
		else {
			BlockState updatedState = getUpdatedState(state, level, pos, false);

			return !hasFaces(updatedState) ? Blocks.AIR.defaultBlockState() : updatedState;
		}
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> buidler) {
		buidler.add(BASE, NORTH, EAST, SOUTH, WEST);
	}

	@Override
	protected BlockState rotate(BlockState state, Rotation rotation) {
		return switch (rotation) {
			case CLOCKWISE_180 -> state.setValue(NORTH, state.getValue(SOUTH)).setValue(EAST, state.getValue(WEST)).setValue(SOUTH, state.getValue(NORTH)).setValue(WEST, state.getValue(EAST));
			case COUNTERCLOCKWISE_90 -> state.setValue(NORTH, state.getValue(EAST)).setValue(EAST, state.getValue(SOUTH)).setValue(SOUTH, state.getValue(WEST)).setValue(WEST, state.getValue(NORTH));
			case CLOCKWISE_90 -> state.setValue(NORTH, state.getValue(WEST)).setValue(EAST, state.getValue(NORTH)).setValue(SOUTH, state.getValue(EAST)).setValue(WEST, state.getValue(SOUTH));
			default -> state;
		};
	}

	@Override
	protected BlockState mirror(BlockState state, Mirror mirror) {
		return switch (mirror) {
			case LEFT_RIGHT -> state.setValue(NORTH, state.getValue(SOUTH)).setValue(SOUTH, state.getValue(NORTH));
			case FRONT_BACK -> state.setValue(EAST, state.getValue(WEST)).setValue(WEST, state.getValue(EAST));
			default -> super.mirror(state, mirror);
		};
	}

	public static EnumProperty<WallSide> getPropertyForFace(Direction direction) {
		return PROPERTY_BY_DIRECTION.get(direction);
	}

	@Override
	public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state) {
		return state.getValue(BASE) && !createTopperWithSideChance(level, pos, () -> true).isAir();
	}

	@Override
	public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state) {
		return true;
	}

	@Override
	public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
		BlockState topper = createTopperWithSideChance(level, pos, () -> true);

		if (!topper.isAir())
			setBlockAndOwner(level, pos.above(), topper, UPDATE_ALL, (IOwnable) level.getBlockEntity(pos));
	}
}
