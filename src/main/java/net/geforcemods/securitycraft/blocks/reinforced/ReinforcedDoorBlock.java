package net.geforcemods.securitycraft.blocks.reinforced;

import javax.annotation.Nullable;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.OwnableBlockEntity;
import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.blockentities.ReinforcedDoorBlockEntity;
import net.geforcemods.securitycraft.blocks.OwnableBlock;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.block.material.PushReaction;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.DoorHingeSide;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ReinforcedDoorBlock extends OwnableBlock {
	public static final DirectionProperty FACING = HorizontalBlock.FACING;
	public static final BooleanProperty OPEN = BlockStateProperties.OPEN;
	public static final EnumProperty<DoorHingeSide> HINGE = BlockStateProperties.DOOR_HINGE;
	public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;
	protected static final VoxelShape SOUTH_AABB = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 3.0D);
	protected static final VoxelShape NORTH_AABB = Block.box(0.0D, 0.0D, 13.0D, 16.0D, 16.0D, 16.0D);
	protected static final VoxelShape WEST_AABB = Block.box(13.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
	protected static final VoxelShape EAST_AABB = Block.box(0.0D, 0.0D, 0.0D, 3.0D, 16.0D, 16.0D);

	public ReinforcedDoorBlock(Block.Properties properties) {
		super(properties);
		registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(OPEN, false).setValue(HINGE, DoorHingeSide.LEFT).setValue(HALF, DoubleBlockHalf.LOWER));
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		Direction direction = state.getValue(FACING);
		boolean flag = !state.getValue(OPEN);
		boolean flag1 = state.getValue(HINGE) == DoorHingeSide.RIGHT;
		switch (direction) {
			case EAST:
			default:
				return flag ? EAST_AABB : (flag1 ? NORTH_AABB : SOUTH_AABB);
			case SOUTH:
				return flag ? SOUTH_AABB : (flag1 ? EAST_AABB : WEST_AABB);
			case WEST:
				return flag ? WEST_AABB : (flag1 ? SOUTH_AABB : NORTH_AABB);
			case NORTH:
				return flag ? NORTH_AABB : (flag1 ? WEST_AABB : EAST_AABB);
		}
	}

	@Override
	public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
		DoubleBlockHalf doubleblockhalf = stateIn.getValue(HALF);

		if (facing.getAxis() == Direction.Axis.Y && doubleblockhalf == DoubleBlockHalf.LOWER == (facing == Direction.UP))
			return facingState.getBlock() == this && facingState.getValue(HALF) != doubleblockhalf ? stateIn.setValue(FACING, facingState.getValue(FACING)).setValue(OPEN, facingState.getValue(OPEN)).setValue(HINGE, facingState.getValue(HINGE)) : Blocks.AIR.defaultBlockState();
		else
			return doubleblockhalf == DoubleBlockHalf.LOWER && facing == Direction.DOWN && !stateIn.canSurvive(worldIn, currentPos) ? Blocks.AIR.defaultBlockState() : super.updateShape(stateIn, facing, facingState, worldIn, currentPos, facingPos);
	}

	@Override
	public void playerDestroy(World worldIn, PlayerEntity player, BlockPos pos, BlockState state, @Nullable TileEntity te, ItemStack stack) {
		super.playerDestroy(worldIn, player, pos, Blocks.AIR.defaultBlockState(), te, stack);
	}

	@Override
	public void playerWillDestroy(World world, BlockPos pos, BlockState state, PlayerEntity player) {
		if (!world.isClientSide && player.isCreative()) {
			DoubleBlockHalf doubleblockhalf = state.getValue(HALF);

			if (doubleblockhalf == DoubleBlockHalf.UPPER) {
				BlockPos blockpos = pos.below();
				BlockState blockstate = world.getBlockState(blockpos);

				if (blockstate.getBlock() == state.getBlock() && blockstate.getValue(HALF) == DoubleBlockHalf.LOWER) {
					world.setBlock(blockpos, Blocks.AIR.defaultBlockState(), 35);
					world.levelEvent(player, 2001, blockpos, Block.getId(blockstate));
				}
			}
		}

		super.playerWillDestroy(world, pos, state, player);
	}

	@Override
	public boolean isPathfindable(BlockState state, IBlockReader worldIn, BlockPos pos, PathType type) {
		switch (type) {
			case LAND:
				return state.getValue(OPEN);
			case WATER:
				return false;
			case AIR:
				return state.getValue(OPEN);
			default:
				return false;
		}
	}

	@Override
	@Nullable
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		BlockPos pos = context.getClickedPos();
		World world = context.getLevel();

		if (pos.getY() < 255 && world.getBlockState(pos.above()).canBeReplaced(context)) {
			boolean hasNeighborSignal = world.hasNeighborSignal(pos) || world.hasNeighborSignal(pos.above());
			return defaultBlockState().setValue(FACING, context.getHorizontalDirection()).setValue(HINGE, getHingeSide(context)).setValue(OPEN, hasNeighborSignal).setValue(HALF, DoubleBlockHalf.LOWER);
		}
		else
			return null;
	}

	@Override
	public void neighborChanged(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean flag) {
		onNeighborChanged(world, pos, fromPos);
	}

	@Override
	public void setPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		super.setPlacedBy(world, pos, state, placer, stack);
		world.setBlock(pos.above(), state.setValue(HALF, DoubleBlockHalf.UPPER), 3);
	}

	private DoorHingeSide getHingeSide(BlockItemUseContext pContext) {
		IBlockReader iblockreader = pContext.getLevel();
		BlockPos blockpos = pContext.getClickedPos();
		Direction direction = pContext.getHorizontalDirection();
		BlockPos blockpos1 = blockpos.above();
		Direction direction1 = direction.getCounterClockWise();
		BlockPos blockpos2 = blockpos.relative(direction1);
		BlockState blockstate = iblockreader.getBlockState(blockpos2);
		BlockPos blockpos3 = blockpos1.relative(direction1);
		BlockState blockstate1 = iblockreader.getBlockState(blockpos3);
		Direction direction2 = direction.getClockWise();
		BlockPos blockpos4 = blockpos.relative(direction2);
		BlockState blockstate2 = iblockreader.getBlockState(blockpos4);
		BlockPos blockpos5 = blockpos1.relative(direction2);
		BlockState blockstate3 = iblockreader.getBlockState(blockpos5);
		int i = (blockstate.isCollisionShapeFullBlock(iblockreader, blockpos2) ? -1 : 0) + (blockstate1.isCollisionShapeFullBlock(iblockreader, blockpos3) ? -1 : 0) + (blockstate2.isCollisionShapeFullBlock(iblockreader, blockpos4) ? 1 : 0) + (blockstate3.isCollisionShapeFullBlock(iblockreader, blockpos5) ? 1 : 0);
		boolean flag = blockstate.getBlock() == this && blockstate.getValue(HALF) == DoubleBlockHalf.LOWER;
		boolean flag1 = blockstate2.getBlock() == this && blockstate2.getValue(HALF) == DoubleBlockHalf.LOWER;

		if ((!flag || flag1) && i <= 0) {
			if ((!flag1 || flag) && i >= 0) {
				int j = direction.getStepX();
				int k = direction.getStepZ();
				Vector3d vec3d = pContext.getClickLocation();
				double d0 = vec3d.x - blockpos.getX();
				double d1 = vec3d.z - blockpos.getZ();
				return (j >= 0 || !(d1 < 0.5D)) && (j <= 0 || !(d1 > 0.5D)) && (k >= 0 || !(d0 > 0.5D)) && (k <= 0 || !(d0 < 0.5D)) ? DoorHingeSide.LEFT : DoorHingeSide.RIGHT;
			}
			else
				return DoorHingeSide.LEFT;
		}
		else
			return DoorHingeSide.RIGHT;
	}

	/**
	 * Old method, renamed because I am lazy. Called by neighborChanged
	 *
	 * @param level The level the change occured in
	 * @param firstDoorPos The position of this block
	 * @param neighbor The position of the changed block
	 */
	public void onNeighborChanged(World level, BlockPos firstDoorPos, BlockPos neighbor) {
		BlockState firstDoorState = level.getBlockState(firstDoorPos);
		Block neighborBlock = level.getBlockState(neighbor).getBlock();
		Owner previousOwner = null;

		if (level.getBlockEntity(firstDoorPos) instanceof OwnableBlockEntity)
			previousOwner = ((OwnableBlockEntity) level.getBlockEntity(firstDoorPos)).getOwner();

		if (firstDoorState.getValue(HALF) == DoubleBlockHalf.UPPER) {
			BlockPos blockBelow = firstDoorPos.below();
			BlockState stateBelow = level.getBlockState(blockBelow);

			if (stateBelow.getBlock() != this)
				level.destroyBlock(firstDoorPos, false);
			else if (neighborBlock != this)
				onNeighborChanged(level, blockBelow, neighbor);
		}
		else {
			boolean drop = false;
			BlockPos blockAbove = firstDoorPos.above();
			BlockState stateAbove = level.getBlockState(blockAbove);

			if (stateAbove.getBlock() != this) {
				level.destroyBlock(firstDoorPos, false);
				drop = true;
			}

			if (!BlockUtils.isSideSolid(level, firstDoorPos.below(), Direction.UP)) {
				level.destroyBlock(firstDoorPos, false);
				drop = true;

				if (stateAbove.getBlock() == this)
					level.destroyBlock(firstDoorPos, false);
			}

			if (drop) {
				if (!level.isClientSide) {
					level.destroyBlock(firstDoorPos, false);
					Block.popResource(level, firstDoorPos, new ItemStack(SCContent.REINFORCED_DOOR_ITEM.get()));
				}
			}
			else if (neighborBlock != this) {
				boolean hasActiveSCBlock = BlockUtils.hasActiveSCBlockNextTo(level, firstDoorPos) || BlockUtils.hasActiveSCBlockNextTo(level, firstDoorPos.above());
				Direction directionToCheck = firstDoorState.getValue(FACING).getClockWise();
				BlockPos secondDoorPos = null;
				BlockState secondDoorState = level.getBlockState(secondDoorPos = firstDoorPos.relative(directionToCheck));

				if (!(secondDoorState != null && secondDoorState.getBlock() == SCContent.REINFORCED_DOOR.get() && secondDoorState.getValue(HINGE) == DoorHingeSide.RIGHT && firstDoorState.getValue(HINGE) != secondDoorState.getValue(HINGE))) {
					secondDoorState = level.getBlockState(secondDoorPos = firstDoorPos.relative(directionToCheck.getOpposite()));

					if (!(secondDoorState != null && secondDoorState.getBlock() == SCContent.REINFORCED_DOOR.get() && secondDoorState.getValue(HINGE) == DoorHingeSide.LEFT && firstDoorState.getValue(HINGE) != secondDoorState.getValue(HINGE)))
						secondDoorPos = null;
				}

				boolean hasSecondDoorActiveSCBlock = secondDoorPos != null && (BlockUtils.hasActiveSCBlockNextTo(level, secondDoorPos) || BlockUtils.hasActiveSCBlockNextTo(level, secondDoorPos.above()));
				boolean shouldBeOpen = hasActiveSCBlock != hasSecondDoorActiveSCBlock || hasActiveSCBlock;

				if (shouldBeOpen != firstDoorState.getValue(OPEN))
					setDoorState(level, firstDoorPos, firstDoorState, shouldBeOpen);

				if (secondDoorPos != null && shouldBeOpen != secondDoorState.getValue(OPEN))
					setDoorState(level, secondDoorPos, secondDoorState, shouldBeOpen);
			}
		}

		if (previousOwner != null && level.getBlockEntity(firstDoorPos) instanceof OwnableBlockEntity && level.getBlockEntity(firstDoorPos.above()) instanceof OwnableBlockEntity) {
			((OwnableBlockEntity) level.getBlockEntity(firstDoorPos)).setOwner(previousOwner.getUUID(), previousOwner.getName());
			((OwnableBlockEntity) level.getBlockEntity(firstDoorPos.above())).setOwner(previousOwner.getUUID(), previousOwner.getName());
		}
	}

	public void setDoorState(World level, BlockPos pos, BlockState state, boolean open) {
		level.setBlock(pos, state.setValue(OPEN, open), 2);
		level.levelEvent((PlayerEntity) null, open ? 1005 : 1011, pos, 0);
	}

	@Override
	public boolean canSurvive(BlockState state, IWorldReader world, BlockPos pos) {
		BlockPos posBelow = pos.below();
		BlockState stateBelow = world.getBlockState(posBelow);

		if (state.getValue(HALF) == DoubleBlockHalf.LOWER)
			return stateBelow.isFaceSturdy(world, posBelow, Direction.UP);
		else
			return stateBelow.getBlock() == this;
	}

	@Override
	public PushReaction getPistonPushReaction(BlockState state) {
		return PushReaction.BLOCK;
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rot) {
		return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
	}

	@Override
	public BlockState mirror(BlockState state, Mirror mirrorIn) {
		return mirrorIn == Mirror.NONE ? state : state.rotate(mirrorIn.getRotation(state.getValue(FACING))).cycle(HINGE);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public long getSeed(BlockState state, BlockPos pos) {
		return MathHelper.getSeed(pos.getX(), pos.below(state.getValue(HALF) == DoubleBlockHalf.LOWER ? 0 : 1).getY(), pos.getZ());
	}

	@Override
	public boolean triggerEvent(BlockState state, World world, BlockPos pos, int id, int param) {
		super.triggerEvent(state, world, pos, id, param);
		TileEntity tileentity = world.getBlockEntity(pos);
		return tileentity == null ? false : tileentity.triggerEvent(id, param);
	}

	@Override
	public ItemStack getCloneItemStack(IBlockReader worldIn, BlockPos pos, BlockState state) {
		return new ItemStack(SCContent.REINFORCED_DOOR_ITEM.get());
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new ReinforcedDoorBlockEntity();
	}

	@Override
	protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(HALF, FACING, OPEN, HINGE);
	}
}