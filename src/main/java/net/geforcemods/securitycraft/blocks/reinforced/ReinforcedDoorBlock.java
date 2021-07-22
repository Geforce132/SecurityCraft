package net.geforcemods.securitycraft.blocks.reinforced;

import javax.annotation.Nullable;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.OwnableTileEntity;
import net.geforcemods.securitycraft.api.Owner;
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
		switch(direction) {
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
		if (facing.getAxis() == Direction.Axis.Y && doubleblockhalf == DoubleBlockHalf.LOWER == (facing == Direction.UP)) {
			return facingState.getBlock() == this && facingState.getValue(HALF) != doubleblockhalf ? stateIn.setValue(FACING, facingState.getValue(FACING)).setValue(OPEN, facingState.getValue(OPEN)).setValue(HINGE, facingState.getValue(HINGE)) : Blocks.AIR.defaultBlockState();
		} else {
			return doubleblockhalf == DoubleBlockHalf.LOWER && facing == Direction.DOWN && !stateIn.canSurvive(worldIn, currentPos) ? Blocks.AIR.defaultBlockState() : super.updateShape(stateIn, facing, facingState, worldIn, currentPos, facingPos);
		}
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
		switch(type) {
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
		BlockPos blockpos = context.getClickedPos();
		if (blockpos.getY() < 255 && context.getLevel().getBlockState(blockpos.above()).canBeReplaced(context)) {
			World world = context.getLevel();
			boolean flag = world.hasNeighborSignal(blockpos) || world.hasNeighborSignal(blockpos.above());
			return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection()).setValue(HINGE, this.getHingeSide(context)).setValue(OPEN, flag).setValue(HALF, DoubleBlockHalf.LOWER);
		} else {
			return null;
		}
	}

	@Override
	public void neighborChanged(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean flag)
	{
		onNeighborChanged(world, pos, fromPos);
	}

	@Override
	public void setPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack)
	{
		super.setPlacedBy(world, pos, state, placer, stack);
		world.setBlock(pos.above(), state.setValue(HALF, DoubleBlockHalf.UPPER), 3);
	}

	private DoorHingeSide getHingeSide(BlockItemUseContext p_208073_1_) {
		IBlockReader iblockreader = p_208073_1_.getLevel();
		BlockPos blockpos = p_208073_1_.getClickedPos();
		Direction direction = p_208073_1_.getHorizontalDirection();
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
				Vector3d vec3d = p_208073_1_.getClickLocation();
				double d0 = vec3d.x - blockpos.getX();
				double d1 = vec3d.z - blockpos.getZ();
				return (j >= 0 || !(d1 < 0.5D)) && (j <= 0 || !(d1 > 0.5D)) && (k >= 0 || !(d0 > 0.5D)) && (k <= 0 || !(d0 < 0.5D)) ? DoorHingeSide.LEFT : DoorHingeSide.RIGHT;
			} else {
				return DoorHingeSide.LEFT;
			}
		} else {
			return DoorHingeSide.RIGHT;
		}
	}

	/**
	 * Old method, renamed because I am lazy. Called by neighborChanged
	 * @param world The world the change occured in
	 * @param pos The position of this block
	 * @param neighbor The position of the changed block
	 */
	public void onNeighborChanged(World world, BlockPos pos, BlockPos neighbor)
	{
		BlockState state = world.getBlockState(pos);
		Block neighborBlock = world.getBlockState(neighbor).getBlock();
		Owner previousOwner = null;

		if(world.getBlockEntity(pos) instanceof OwnableTileEntity)
			previousOwner = ((OwnableTileEntity)world.getBlockEntity(pos)).getOwner();

		if(state.getValue(HALF) == DoubleBlockHalf.UPPER)
		{
			BlockPos blockBelow = pos.below();
			BlockState stateBelow = world.getBlockState(blockBelow);

			if(stateBelow.getBlock() != this)
				world.destroyBlock(pos, false);
			else if(neighborBlock != this)
				onNeighborChanged(world, blockBelow, neighbor);
		}
		else
		{
			boolean drop = false;
			BlockPos blockAbove = pos.above();
			BlockState stateAbove = world.getBlockState(blockAbove);

			if(stateAbove.getBlock() != this)
			{
				world.destroyBlock(pos, false);
				drop = true;
			}

			if(!BlockUtils.isSideSolid(world, pos.below(), Direction.UP))
			{
				world.destroyBlock(pos, false);
				drop = true;

				if(stateAbove.getBlock() == this)
					world.destroyBlock(pos, false);
			}

			if(drop)
			{
				if(!world.isClientSide)
				{
					world.destroyBlock(pos, false);
					Block.popResource(world, pos, new ItemStack(SCContent.REINFORCED_DOOR_ITEM.get()));
				}
			}
			else
			{
				boolean hasActiveSCBlock = BlockUtils.hasActiveSCBlockNextTo(world, pos) || BlockUtils.hasActiveSCBlockNextTo(world, pos.above());

				if(neighborBlock != this && hasActiveSCBlock != stateAbove.getValue(OPEN))
				{
					if(hasActiveSCBlock != state.getValue(OPEN))
					{
						world.setBlock(pos, state.setValue(OPEN, hasActiveSCBlock), 2);

						BlockState secondDoorState;

						if(state.getValue(FACING) == Direction.WEST)
						{
							secondDoorState = world.getBlockState(pos.north());

							if(secondDoorState != null && secondDoorState.getBlock() == SCContent.REINFORCED_DOOR.get() && secondDoorState.getValue(OPEN) != hasActiveSCBlock)
								world.setBlock(pos.north(), secondDoorState.setValue(OPEN, hasActiveSCBlock), 2);
							else
							{
								secondDoorState = world.getBlockState(pos.south());

								if(secondDoorState != null && secondDoorState.getBlock() == SCContent.REINFORCED_DOOR.get() && secondDoorState.getValue(OPEN) != hasActiveSCBlock)
									world.setBlock(pos.south(), secondDoorState.setValue(OPEN, hasActiveSCBlock), 2);
							}
						}
						else if(state.getValue(FACING) == Direction.NORTH)
						{
							secondDoorState = world.getBlockState(pos.east());

							if(secondDoorState != null && secondDoorState.getBlock() == SCContent.REINFORCED_DOOR.get() && secondDoorState.getValue(OPEN) != hasActiveSCBlock)
								world.setBlock(pos.east(), secondDoorState.setValue(OPEN, hasActiveSCBlock), 2);
							else
							{
								secondDoorState = world.getBlockState(pos.west());

								if(secondDoorState != null && secondDoorState.getBlock() == SCContent.REINFORCED_DOOR.get() && secondDoorState.getValue(OPEN) != hasActiveSCBlock)
									world.setBlock(pos.west(), secondDoorState.setValue(OPEN, hasActiveSCBlock), 2);
							}
						}
						else if(state.getValue(FACING) == Direction.EAST)
						{
							secondDoorState = world.getBlockState(pos.south());

							if(secondDoorState != null && secondDoorState.getBlock() == SCContent.REINFORCED_DOOR.get() && secondDoorState.getValue(OPEN) != hasActiveSCBlock)
								world.setBlock(pos.south(), secondDoorState.setValue(OPEN, hasActiveSCBlock), 2);
							else
							{
								secondDoorState = world.getBlockState(pos.north());

								if(secondDoorState != null && secondDoorState.getBlock() == SCContent.REINFORCED_DOOR.get() && secondDoorState.getValue(OPEN) != hasActiveSCBlock)
									world.setBlock(pos.north(), secondDoorState.setValue(OPEN, hasActiveSCBlock), 2);
							}
						}
						else if(state.getValue(FACING) == Direction.SOUTH)
						{
							secondDoorState = world.getBlockState(pos.west());

							if(secondDoorState != null && secondDoorState.getBlock() == SCContent.REINFORCED_DOOR.get() && secondDoorState.getValue(OPEN) != hasActiveSCBlock)
								world.setBlock(pos.west(), secondDoorState.setValue(OPEN, hasActiveSCBlock), 2);
							else
							{
								secondDoorState = world.getBlockState(pos.east());

								if(secondDoorState != null && secondDoorState.getBlock() == SCContent.REINFORCED_DOOR.get() && secondDoorState.getValue(OPEN) != hasActiveSCBlock)
									world.setBlock(pos.east(), secondDoorState.setValue(OPEN, hasActiveSCBlock), 2);
							}
						}

						world.levelEvent((PlayerEntity)null, hasActiveSCBlock ? 1005 : 1011, pos, 0);
					}
				}
			}
		}

		if(previousOwner != null && world.getBlockEntity(pos) instanceof OwnableTileEntity && world.getBlockEntity(pos.above()) instanceof OwnableTileEntity)
		{
			((OwnableTileEntity)world.getBlockEntity(pos)).setOwner(previousOwner.getUUID(), previousOwner.getName());
			((OwnableTileEntity)world.getBlockEntity(pos.above())).setOwner(previousOwner.getUUID(), previousOwner.getName());
		}
	}

	@Override
	public boolean canSurvive(BlockState state, IWorldReader worldIn, BlockPos pos) {
		BlockPos blockpos = pos.below();
		BlockState blockstate = worldIn.getBlockState(blockpos);
		if (state.getValue(HALF) == DoubleBlockHalf.LOWER) {
			return blockstate.isFaceSturdy(worldIn, blockpos, Direction.UP);
		} else {
			return blockstate.getBlock() == this;
		}
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
	public void onRemove(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving)
	{
		super.onRemove(state, world, pos, newState, isMoving);

		if(state.getBlock() != newState.getBlock())
			world.removeBlockEntity(pos);
	}

	@Override
	public boolean triggerEvent(BlockState state, World world, BlockPos pos, int id, int param)
	{
		super.triggerEvent(state, world, pos, id, param);
		TileEntity tileentity = world.getBlockEntity(pos);
		return tileentity == null ? false : tileentity.triggerEvent(id, param);
	}

	@Override
	public ItemStack getCloneItemStack(IBlockReader worldIn, BlockPos pos, BlockState state){
		return new ItemStack(SCContent.REINFORCED_DOOR_ITEM.get());
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new OwnableTileEntity();
	}

	@Override
	protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder)
	{
		builder.add(HALF, FACING, OPEN, HINGE);
	}
}