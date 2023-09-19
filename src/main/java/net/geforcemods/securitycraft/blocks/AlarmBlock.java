package net.geforcemods.securitycraft.blocks;

import java.util.Random;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.blockentities.AlarmBlockEntity;
import net.geforcemods.securitycraft.network.client.OpenScreen;
import net.geforcemods.securitycraft.network.client.OpenScreen.DataType;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.Hand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.PacketDistributor;

public class AlarmBlock extends OwnableBlock implements IWaterLoggable {
	public static final BooleanProperty LIT = BlockStateProperties.LIT;
	public static final DirectionProperty FACING = BlockStateProperties.FACING;
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
	private static final VoxelShape SHAPE_EAST = Block.box(0, 4, 4, 8, 12, 12);
	private static final VoxelShape SHAPE_WEST = Block.box(8, 4, 4, 16, 12, 12);
	private static final VoxelShape SHAPE_NORTH = Block.box(4, 4, 8, 12, 12, 16);
	private static final VoxelShape SHAPE_SOUTH = Block.box(4, 4, 0, 12, 12, 8);
	private static final VoxelShape SHAPE_UP = Block.box(4, 0, 4, 12, 8, 12);
	private static final VoxelShape SHAPE_DOWN = Block.box(4, 8, 4, 12, 16, 12);

	public AlarmBlock(AbstractBlock.Properties properties) {
		super(properties);

		registerDefaultState(stateDefinition.any().setValue(FACING, Direction.UP).setValue(LIT, false).setValue(WATERLOGGED, false));
	}

	@Override
	public ActionResultType use(BlockState state, World level, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
		TileEntity tile = level.getBlockEntity(pos);

		if (tile instanceof AlarmBlockEntity) {
			AlarmBlockEntity be = (AlarmBlockEntity) tile;

			if (be.isOwnedBy(player)) {
				if (!level.isClientSide) {
					if (be.isDisabled())
						player.displayClientMessage(Utils.localize("gui.securitycraft:scManual.disabled"), true);
					else
						SecurityCraft.channel.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player), new OpenScreen(DataType.ALARM, pos));
				}

				return ActionResultType.SUCCESS;
			}
		}
		return ActionResultType.PASS;
	}

	@Override
	public boolean canSurvive(BlockState state, IWorldReader world, BlockPos pos) {
		Direction facing = state.getValue(FACING);

		return facing == Direction.UP && BlockUtils.isSideSolid(world, pos.below(), Direction.UP) || BlockUtils.isSideSolid(world, pos.relative(facing.getOpposite()), facing);
	}

	@Override
	public boolean canConnectRedstone(BlockState state, IBlockReader level, BlockPos pos, Direction direction) {
		return direction != null && direction.getAxis() != Axis.Y;
	}

	@Override
	public void neighborChanged(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean flag) {
		if (!canSurvive(state, world, pos))
			world.destroyBlock(pos, true);
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext ctx) {
		World world = ctx.getLevel();
		BlockPos pos = ctx.getClickedPos();
		Direction facing = ctx.getClickedFace();

		return BlockUtils.isSideSolid(world, pos.relative(facing.getOpposite()), facing) ? defaultBlockState().setValue(FACING, facing).setValue(WATERLOGGED, world.getFluidState(pos).getType() == Fluids.WATER) : null;
	}

	@Override
	public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, IWorld level, BlockPos currentPos, BlockPos facingPos) {
		if (state.getValue(WATERLOGGED))
			level.getLiquidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(level));

		return super.updateShape(state, facing, facingState, level, currentPos, facingPos);
	}

	@Override
	public FluidState getFluidState(BlockState state) {
		return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
	}

	@Override
	public void onPlace(BlockState state, World world, BlockPos pos, BlockState oldState, boolean flag) {
		if (!world.isClientSide)
			world.getBlockTicks().scheduleTick(pos, state.getBlock(), 5);
	}

	@Override
	public void tick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		if (!world.isClientSide) {
			playSoundAndUpdate(world, pos);

			world.getBlockTicks().scheduleTick(pos, state.getBlock(), 5);
		}
	}

	@Override
	public void onNeighborChange(BlockState state, IWorldReader w, BlockPos pos, BlockPos neighbor) {
		if (w.isClientSide() || !(w instanceof World))
			return;

		World world = (World) w;

		playSoundAndUpdate(world, pos);

		Direction facing = world.getBlockState(pos).getValue(FACING);

		if (!BlockUtils.isSideSolid(world, pos.relative(facing.getOpposite()), facing))
			world.destroyBlock(pos, true);
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader source, BlockPos pos, ISelectionContext ctx) {
		Direction facing = state.getValue(FACING);

		switch (facing) {
			case EAST:
				return SHAPE_EAST;
			case WEST:
				return SHAPE_WEST;
			case NORTH:
				return SHAPE_NORTH;
			case SOUTH:
				return SHAPE_SOUTH;
			case UP:
				return SHAPE_UP;
			case DOWN:
				return SHAPE_DOWN;
		}

		return VoxelShapes.block();
	}

	private void playSoundAndUpdate(World world, BlockPos pos) {
		BlockState state = world.getBlockState(pos);

		if (state.getBlock() != SCContent.ALARM.get())
			return;

		TileEntity tile = world.getBlockEntity(pos);

		if (tile instanceof AlarmBlockEntity) {
			AlarmBlockEntity te = (AlarmBlockEntity) tile;

			if (world.getBestNeighborSignal(pos) > 0) {
				boolean isPowered = te.isPowered();

				if (!isPowered) {
					world.setBlockAndUpdate(pos, state.setValue(LIT, true));
					te.setPowered(true);
				}
			}
			else {
				boolean isPowered = te.isPowered();

				if (isPowered) {
					world.setBlockAndUpdate(pos, state.setValue(LIT, false));
					te.setPowered(false);
				}
			}
		}
	}

	@Override
	public void playerWillDestroy(World level, BlockPos pos, BlockState state, PlayerEntity player) {
		//prevents dropping twice the amount of modules when breaking the block in creative mode
		if (player.isCreative()) {
			TileEntity te = level.getBlockEntity(pos);

			if (te instanceof IModuleInventory)
				((IModuleInventory) te).getInventory().clear();
		}

		super.playerWillDestroy(level, pos, state, player);
	}

	@Override
	public void onRemove(BlockState state, World level, BlockPos pos, BlockState newState, boolean isMoving) {
		if (!state.is(newState.getBlock())) {
			TileEntity te = level.getBlockEntity(pos);

			if (te instanceof IModuleInventory)
				((IModuleInventory) te).dropAllModules();
		}

		super.onRemove(state, level, pos, newState, isMoving);
	}

	@Override
	public ItemStack getCloneItemStack(IBlockReader world, BlockPos pos, BlockState state) {
		return new ItemStack(SCContent.ALARM.get().asItem());
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		builder.add(FACING, LIT, WATERLOGGED);
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader reader) {
		return new AlarmBlockEntity();
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rot) {
		return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
	}

	@Override
	public BlockState mirror(BlockState state, Mirror mirror) {
		Direction facing = state.getValue(FACING);

		switch (mirror) {
			case LEFT_RIGHT:
				if (facing.getAxis() == Axis.Z)
					return state.setValue(FACING, facing.getOpposite());
				break;
			case FRONT_BACK:
				if (facing.getAxis() == Axis.X)
					return state.setValue(FACING, facing.getOpposite());
				break;
			case NONE:
				break;
		}

		return state;
	}
}
