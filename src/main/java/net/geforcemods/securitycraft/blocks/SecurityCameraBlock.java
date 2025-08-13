package net.geforcemods.securitycraft.blocks;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.IDisguisable;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.blockentities.SecurityCameraBlockEntity;
import net.geforcemods.securitycraft.entity.camera.SecurityCamera;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.network.client.SetCameraView;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.Direction.Plane;
import net.minecraft.util.Hand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.SectionPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.world.ForgeChunkManager;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.fml.network.PacketDistributor;

public class SecurityCameraBlock extends DisguisableBlock implements IWaterLoggable {
	public static final DirectionProperty FACING = DirectionProperty.create("facing", facing -> facing != Direction.UP);
	public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
	public static final BooleanProperty BEING_VIEWED = BooleanProperty.create("being_viewed");
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
	private static final VoxelShape SHAPE_SOUTH = VoxelShapes.create(new AxisAlignedBB(0.275F, 0.250F, 0.000F, 0.700F, 0.800F, 0.850F));
	private static final VoxelShape SHAPE_NORTH = VoxelShapes.create(new AxisAlignedBB(0.275F, 0.250F, 0.150F, 0.700F, 0.800F, 1.000F));
	private static final VoxelShape SHAPE_WEST = VoxelShapes.create(new AxisAlignedBB(0.125F, 0.250F, 0.275F, 1.000F, 0.800F, 0.725F));
	private static final VoxelShape SHAPE = VoxelShapes.create(new AxisAlignedBB(0.000F, 0.250F, 0.275F, 0.850F, 0.800F, 0.725F));
	private static final VoxelShape SHAPE_DOWN = VoxelShapes.or(Block.box(7, 15, 5, 9, 16, 11), VoxelShapes.or(Block.box(6, 15, 6, 7, 16, 10), VoxelShapes.or(Block.box(5, 15, 7, 6, 16, 9), VoxelShapes.or(Block.box(9, 15, 6, 10, 16, 10), VoxelShapes.or(Block.box(10, 15, 7, 11, 16, 9), Block.box(7, 14, 7, 9, 15, 9))))));

	public SecurityCameraBlock(AbstractBlock.Properties properties) {
		super(properties);
		registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(POWERED, false).setValue(BEING_VIEWED, false).setValue(WATERLOGGED, false));
	}

	@Override
	public ActionResultType use(BlockState state, World level, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
		if (player.getItemInHand(hand).getItem() != SCContent.CAMERA_MONITOR.get()) {
			TileEntity be = level.getBlockEntity(pos);

			if (be instanceof SecurityCameraBlockEntity && ((SecurityCameraBlockEntity) be).isOwnedBy(player)) {
				if (!level.isClientSide)
					NetworkHooks.openGui((ServerPlayerEntity) player, (SecurityCameraBlockEntity) be, pos);

				return ActionResultType.SUCCESS;
			}
		}

		return ActionResultType.PASS;
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

			if (te instanceof SecurityCameraBlockEntity) {
				if (!ConfigHandler.SERVER.vanillaToolBlockBreaking.get())
					((SecurityCameraBlockEntity) te).dropAllModules();

				InventoryHelper.dropContents(level, pos, ((SecurityCameraBlockEntity) te).getLensContainer());
			}
		}

		level.updateNeighborsAt(pos.north(), level.getBlockState(pos).getBlock());
		level.updateNeighborsAt(pos.south(), level.getBlockState(pos).getBlock());
		level.updateNeighborsAt(pos.east(), level.getBlockState(pos).getBlock());
		level.updateNeighborsAt(pos.west(), level.getBlockState(pos).getBlock());
		super.onRemove(state, level, pos, newState, isMoving);
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader level, BlockPos pos, ISelectionContext ctx) {
		BlockState disguisedState = IDisguisable.getDisguisedBlockState(level.getBlockEntity(pos)).orElse(state);

		if (disguisedState.getBlock() != this)
			return disguisedState.getShape(level, pos, ctx);
		else {
			Direction dir = state.getValue(FACING);

			if (dir == Direction.SOUTH)
				return SHAPE_SOUTH;
			else if (dir == Direction.NORTH)
				return SHAPE_NORTH;
			else if (dir == Direction.WEST)
				return SHAPE_WEST;
			else if (dir == Direction.DOWN)
				return SHAPE_DOWN;
			else
				return SHAPE;
		}
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext ctx) {
		Direction facing = ctx.getClickedFace();

		if (facing != Direction.UP) {
			World level = ctx.getLevel();
			BlockPos pos = ctx.getClickedPos();
			BlockState state = defaultBlockState().setValue(FACING, facing);

			if (!canSurvive(state, level, pos)) {
				for (Direction newFacing : Plane.HORIZONTAL) {
					state = state.setValue(FACING, newFacing);

					if (canSurvive(state, level, pos))
						break;
				}
			}

			return state.setValue(WATERLOGGED, level.getFluidState(pos).getType() == Fluids.WATER);
		}
		else
			return null;
	}

	public void mountCamera(World level, BlockPos pos, PlayerEntity player) {
		if (!level.isClientSide) {
			ServerWorld serverLevel = (ServerWorld) level;
			ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;
			SecurityCamera dummyEntity;
			SectionPos chunkPos = SectionPos.of(pos);
			int viewDistance = serverPlayer.server.getPlayerList().getViewDistance();
			TileEntity te = level.getBlockEntity(pos);

			if (serverPlayer.getCamera() instanceof SecurityCamera)
				serverPlayer.getCamera().remove();

			dummyEntity = new SecurityCamera(level, pos);
			dummyEntity.setChunkLoadingDistance(viewDistance);
			//can't use ServerPlayer#setCamera here because it also teleports the player
			serverPlayer.camera = dummyEntity;
			level.addFreshEntity(dummyEntity);
			SecurityCraft.channel.send(PacketDistributor.PLAYER.with(() -> serverPlayer), new SetCameraView(dummyEntity)); //The camera view packet is sent before the surrounding chunks are forceloaded, so the client knows that the chunks are relevant and saves them

			for (int x = chunkPos.getX() - viewDistance; x <= chunkPos.getX() + viewDistance; x++) {
				for (int z = chunkPos.getZ() - viewDistance; z <= chunkPos.getZ() + viewDistance; z++) {
					ForgeChunkManager.forceChunk(serverLevel, SecurityCraft.MODID, dummyEntity, x, z, true, false);
				}
			}

			if (te instanceof SecurityCameraBlockEntity)
				((SecurityCameraBlockEntity) te).startViewing();
		}
	}

	@Override
	public boolean canSurvive(BlockState state, IWorldReader level, BlockPos pos) {
		Direction facing = state.getValue(FACING);

		return BlockUtils.isSideSolid(level, pos.relative(facing.getOpposite()), facing);
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
	public boolean isSignalSource(BlockState state) {
		return true;
	}

	@Override
	public int getSignal(BlockState state, IBlockReader level, BlockPos pos, Direction side) {
		if (state.getValue(POWERED) && ((IModuleInventory) level.getBlockEntity(pos)).isModuleEnabled(ModuleType.REDSTONE))
			return 15;
		else
			return 0;
	}

	@Override
	public int getDirectSignal(BlockState state, IBlockReader level, BlockPos pos, Direction side) {
		if (state.getValue(POWERED) && ((IModuleInventory) level.getBlockEntity(pos)).isModuleEnabled(ModuleType.REDSTONE) && state.getValue(FACING) == side)
			return 15;
		else
			return 0;
	}

	@Override
	public void neighborChanged(BlockState state, World level, BlockPos pos, Block block, BlockPos fromPos, boolean flag) {
		if (!canSurvive(state, level, pos))
			level.destroyBlock(pos, true);
	}

	@Override
	protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(FACING, POWERED, BEING_VIEWED, WATERLOGGED);
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader level) {
		return new SecurityCameraBlockEntity();
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
