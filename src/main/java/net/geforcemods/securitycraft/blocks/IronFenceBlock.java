package net.geforcemods.securitycraft.blocks;

import java.util.Map;

import net.geforcemods.securitycraft.api.IIntersectable;
import net.geforcemods.securitycraft.api.OwnableTileEntity;
import net.geforcemods.securitycraft.api.SecurityCraftTileEntity;
import net.geforcemods.securitycraft.misc.CustomDamageSources;
import net.geforcemods.securitycraft.util.WorldUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FenceGateBlock;
import net.minecraft.block.SixWayBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.monster.CreeperEntity;
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

public class IronFenceBlock extends OwnableBlock implements IIntersectable {
	public static final BooleanProperty NORTH = SixWayBlock.NORTH;
	public static final BooleanProperty EAST = SixWayBlock.EAST;
	public static final BooleanProperty SOUTH = SixWayBlock.SOUTH;
	public static final BooleanProperty WEST = SixWayBlock.WEST;
	protected static final Map<Direction, BooleanProperty> FACING_TO_PROPERTY_MAP = SixWayBlock.PROPERTY_BY_DIRECTION.entrySet().stream().filter(entry -> entry.getKey().getAxis().isHorizontal()).collect(Util.toMap());
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
	public VoxelShape getOcclusionShape(BlockState state, IBlockReader world, BlockPos pos)
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
		VoxelShape voxelshape5 = VoxelShapes.or(voxelshape1, voxelshape4);
		VoxelShape voxelshape6 = VoxelShapes.or(voxelshape2, voxelshape3);
		VoxelShape[] returnValue = {VoxelShapes.empty(), voxelshape2, voxelshape3, voxelshape6, voxelshape1, VoxelShapes.or(voxelshape2, voxelshape1), VoxelShapes.or(voxelshape3, voxelshape1), VoxelShapes.or(voxelshape6, voxelshape1), voxelshape4, VoxelShapes.or(voxelshape2, voxelshape4), VoxelShapes.or(voxelshape3, voxelshape4), VoxelShapes.or(voxelshape6, voxelshape4), voxelshape5, VoxelShapes.or(voxelshape2, voxelshape5), VoxelShapes.or(voxelshape3, voxelshape5), VoxelShapes.or(voxelshape6, voxelshape5)};

		for(int i = 0; i < 16; ++i)
		{
			returnValue[i] = VoxelShapes.or(voxelshape, returnValue[i]);
		}

		return returnValue;
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext ctx)
	{
		return shapes[getIndex(state)];
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext ctx)
	{
		return collisionShapes[getIndex(state)];
	}

	public boolean connectsTo(BlockState state, boolean p_220111_2_, Direction direction)
	{
		Block block = state.getBlock();
		boolean flag = block.is(BlockTags.FENCES) && state.getMaterial() == material;
		boolean flag1 = block instanceof FenceGateBlock && FenceGateBlock.connectsToDirection(state, direction);

		return !isExceptionForConnection(block) && p_220111_2_ || flag || flag1;
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
	public boolean isPathfindable(BlockState state, IBlockReader world, BlockPos pos, PathType type)
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
	public BlockState getStateForPlacement(BlockItemUseContext ctx)
	{
		IBlockReader iblockreader = ctx.getLevel();
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
	protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder)
	{
		builder.add(NORTH, EAST, WEST, SOUTH);
	}

	@Override
	public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, IWorld world, BlockPos currentPos, BlockPos facingPos)
	{
		return facing.getAxis().getPlane() == Direction.Plane.HORIZONTAL ? state.setValue(FACING_TO_PROPERTY_MAP.get(facing), connectsTo(facingState, facingState.isFaceSturdy(world, facingPos, facing.getOpposite()), facing.getOpposite())) : super.updateShape(state, facing, facingState, world, currentPos, facingPos);
	}

	@Override
	public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit)
	{
		return ActionResultType.FAIL;
	}

	@Override
	public void onEntityIntersected(World world, BlockPos pos, Entity entity)
	{
		//so dropped items don't get destroyed
		if(entity instanceof ItemEntity)
			return;
		//owner check
		else if(entity instanceof PlayerEntity)
		{
			if(((OwnableTileEntity) world.getBlockEntity(pos)).getOwner().isOwner((PlayerEntity)entity))
				return;
		}
		else if(!world.isClientSide && entity instanceof CreeperEntity)
		{
			CreeperEntity creeper = (CreeperEntity)entity;
			LightningBoltEntity lightning = WorldUtils.createLightning(world, Vector3d.atBottomCenterOf(pos), true);

			creeper.thunderHit((ServerWorld)world, lightning);
			creeper.clearFire();
			return;
		}

		entity.hurt(CustomDamageSources.ELECTRICITY, 6.0F); //3 hearts per attack
	}

	@Override
	public boolean triggerEvent(BlockState state, World world, BlockPos pos, int eventID, int eventParam)
	{
		super.triggerEvent(state, world, pos, eventID, eventParam);
		TileEntity tileentity = world.getBlockEntity(pos);
		return tileentity == null ? false : tileentity.triggerEvent(eventID, eventParam);
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world)
	{
		return new SecurityCraftTileEntity().intersectsEntities();
	}
}