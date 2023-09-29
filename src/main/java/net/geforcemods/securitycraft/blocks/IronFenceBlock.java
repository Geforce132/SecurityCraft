package net.geforcemods.securitycraft.blocks;

import java.util.Map;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.OwnableBlockEntity;
import net.geforcemods.securitycraft.misc.CustomDamageSources;
import net.geforcemods.securitycraft.util.LevelUtils;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FenceGateBlock;
import net.minecraft.block.SixWayBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tags.BlockTags;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class IronFenceBlock extends OwnableBlock {
	public static final BooleanProperty NORTH = SixWayBlock.NORTH;
	public static final BooleanProperty EAST = SixWayBlock.EAST;
	public static final BooleanProperty SOUTH = SixWayBlock.SOUTH;
	public static final BooleanProperty WEST = SixWayBlock.WEST;
	protected static final Map<Direction, BooleanProperty> FACING_TO_PROPERTY_MAP = SixWayBlock.PROPERTY_BY_DIRECTION.entrySet().stream().filter(entry -> entry.getKey().getAxis().isHorizontal()).collect(Util.toMap());
	protected final VoxelShape[] collisionShapes;
	protected final VoxelShape[] shapes;
	private final VoxelShape[] renderShapes;

	public IronFenceBlock(AbstractBlock.Properties properties) {
		super(properties);

		registerDefaultState(stateDefinition.any().setValue(NORTH, false).setValue(EAST, false).setValue(SOUTH, false).setValue(WEST, false));
		renderShapes = makeShapes(2.0F, 1.0F, 16.0F, 6.0F, 15.0F);
		collisionShapes = makeShapes(2.0F, 2.0F, 24.0F, 0.0F, 24.0F);
		shapes = makeShapes(2.0F, 2.0F, 16.0F, 0.0F, 16.0F);
	}

	@Override
	public VoxelShape getOcclusionShape(BlockState state, IBlockReader level, BlockPos pos) {
		return renderShapes[getIndex(state)];
	}

	protected VoxelShape[] makeShapes(float nodeWidth, float extensionWidth, float nodeHeight, float extensionBottom, float extensionHeight) {
		float f = 8.0F - nodeWidth;
		float f1 = 8.0F + nodeWidth;
		float f2 = 8.0F - extensionWidth;
		float f3 = 8.0F + extensionWidth;
		VoxelShape voxelshape = Block.box(f, 0.0D, f, f1, nodeHeight, f1);
		VoxelShape voxelshape1 = Block.box(f2, extensionBottom, 0.0D, f3, extensionHeight, f3);
		VoxelShape voxelshape2 = Block.box(f2, extensionBottom, f2, f3, extensionHeight, 16.0D);
		VoxelShape voxelshape3 = Block.box(0.0D, extensionBottom, f2, f3, extensionHeight, f3);
		VoxelShape voxelshape4 = Block.box(f2, extensionBottom, f2, 16.0D, extensionHeight, f3);
		VoxelShape voxelshape5 = VoxelShapes.or(voxelshape1, voxelshape4);
		VoxelShape voxelshape6 = VoxelShapes.or(voxelshape2, voxelshape3);
		//@formatter:off
		VoxelShape[] returnValue = {
				VoxelShapes.empty(),
				voxelshape2,
				voxelshape3,
				voxelshape6,
				voxelshape1,
				VoxelShapes.or(voxelshape2, voxelshape1),
				VoxelShapes.or(voxelshape3, voxelshape1),
				VoxelShapes.or(voxelshape6, voxelshape1),
				voxelshape4,
				VoxelShapes.or(voxelshape2, voxelshape4),
				VoxelShapes.or(voxelshape3, voxelshape4),
				VoxelShapes.or(voxelshape6, voxelshape4),
				voxelshape5,
				VoxelShapes.or(voxelshape2, voxelshape5),
				VoxelShapes.or(voxelshape3, voxelshape5),
				VoxelShapes.or(voxelshape6, voxelshape5)
		};
		//@formatter:on

		for (int i = 0; i < 16; ++i) {
			returnValue[i] = VoxelShapes.or(voxelshape, returnValue[i]);
		}

		return returnValue;
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader level, BlockPos pos, ISelectionContext ctx) {
		return shapes[getIndex(state)];
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, IBlockReader level, BlockPos pos, ISelectionContext ctx) {
		return collisionShapes[getIndex(state)];
	}

	public boolean connectsTo(BlockState state, boolean isFaceSturdy, Direction direction) {
		Block block = state.getBlock();
		boolean isFence = state.is(BlockTags.FENCES) && !state.is(BlockTags.WOODEN_FENCES);
		boolean isFenceGate = block instanceof FenceGateBlock && FenceGateBlock.connectsToDirection(state, direction);

		return !isExceptionForConnection(block) && isFaceSturdy || isFence || isFenceGate;
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
	public boolean isPathfindable(BlockState state, IBlockReader level, BlockPos pos, PathType type) {
		return false;
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rot) {
		switch (rot) {
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
	public BlockState mirror(BlockState state, Mirror mirror) {
		switch (mirror) {
			case LEFT_RIGHT:
				return state.setValue(NORTH, state.getValue(SOUTH)).setValue(SOUTH, state.getValue(NORTH));
			case FRONT_BACK:
				return state.setValue(EAST, state.getValue(WEST)).setValue(WEST, state.getValue(EAST));
			default:
				return super.mirror(state, mirror);
		}
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext ctx) {
		IBlockReader level = ctx.getLevel();
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
	protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(NORTH, EAST, WEST, SOUTH);
	}

	@Override
	public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, IWorld level, BlockPos currentPos, BlockPos facingPos) {
		return facing.getAxis().getPlane() == Direction.Plane.HORIZONTAL ? state.setValue(FACING_TO_PROPERTY_MAP.get(facing), connectsTo(facingState, facingState.isFaceSturdy(level, facingPos, facing.getOpposite()), facing.getOpposite())) : super.updateShape(state, facing, facingState, level, currentPos, facingPos);
	}

	@Override
	public ActionResultType use(BlockState state, World level, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
		return ActionResultType.FAIL;
	}

	@Override
	public void entityInside(BlockState state, World level, BlockPos pos, Entity entity) {
		hurtOrConvertEntity(this, state, level, pos, entity);
	}

	public static void hurtOrConvertEntity(Block electrifiedBlock, BlockState state, World level, BlockPos pos, Entity entity) {
		if (level.getGameTime() % 20 != 0)
			return;
		else if (!electrifiedBlock.getShape(state, level, pos, ISelectionContext.of(entity)).bounds().move(pos).inflate(0.01D).intersects(entity.getBoundingBox()))
			return;
		else if (entity instanceof ItemEntity) //so dropped items don't get destroyed
			return;
		else if (entity instanceof PlayerEntity) { //owner check
			if (((OwnableBlockEntity) level.getBlockEntity(pos)).isOwnedBy((PlayerEntity) entity))
				return;
		}
		else if (((OwnableBlockEntity) level.getBlockEntity(pos)).allowsOwnableEntity(entity))
			return;
		else if (!level.isClientSide) {
			LightningBoltEntity lightning = LevelUtils.createLightning(level, Vector3d.atBottomCenterOf(pos), true);

			entity.thunderHit((ServerWorld) level, lightning);
			entity.clearFire();
			return;
		}

		entity.hurt(CustomDamageSources.ELECTRICITY, 6.0F); //3 hearts per attack
	}

	@Override
	public boolean triggerEvent(BlockState state, World level, BlockPos pos, int eventID, int eventParam) {
		TileEntity be = level.getBlockEntity(pos);

		return be != null && be.triggerEvent(eventID, eventParam);
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader level) {
		return new OwnableBlockEntity(SCContent.ABSTRACT_BLOCK_ENTITY.get());
	}
}