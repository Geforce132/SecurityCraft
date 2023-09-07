package net.geforcemods.securitycraft.blocks;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.blockentities.SecurityCameraBlockEntity;
import net.geforcemods.securitycraft.entity.camera.SecurityCamera;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.network.client.SetCameraView;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.Direction.Plane;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.SectionPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.world.ForgeChunkManager;
import net.minecraftforge.fml.network.PacketDistributor;

public class SecurityCameraBlock extends OwnableBlock {
	public static final DirectionProperty FACING = DirectionProperty.create("facing", facing -> facing != Direction.UP);
	public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
	public static final BooleanProperty BEING_VIEWED = BooleanProperty.create("being_viewed");
	private static final VoxelShape SHAPE_SOUTH = VoxelShapes.create(new AxisAlignedBB(0.275F, 0.250F, 0.000F, 0.700F, 0.800F, 0.850F));
	private static final VoxelShape SHAPE_NORTH = VoxelShapes.create(new AxisAlignedBB(0.275F, 0.250F, 0.150F, 0.700F, 0.800F, 1.000F));
	private static final VoxelShape SHAPE_WEST = VoxelShapes.create(new AxisAlignedBB(0.125F, 0.250F, 0.275F, 1.000F, 0.800F, 0.725F));
	private static final VoxelShape SHAPE = VoxelShapes.create(new AxisAlignedBB(0.000F, 0.250F, 0.275F, 0.850F, 0.800F, 0.725F));
	private static final VoxelShape SHAPE_DOWN = VoxelShapes.or(Block.box(7, 15, 5, 9, 16, 11), VoxelShapes.or(Block.box(6, 15, 6, 7, 16, 10), VoxelShapes.or(Block.box(5, 15, 7, 6, 16, 9), VoxelShapes.or(Block.box(9, 15, 6, 10, 16, 10), VoxelShapes.or(Block.box(10, 15, 7, 11, 16, 9), Block.box(7, 14, 7, 9, 15, 9))))));

	public SecurityCameraBlock(AbstractBlock.Properties properties) {
		super(properties);
		registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(POWERED, false).setValue(BEING_VIEWED, false));
	}

	@Override
	public VoxelShape getCollisionShape(BlockState blockState, IBlockReader access, BlockPos pos, ISelectionContext ctx) {
		return VoxelShapes.empty();
	}

	@Override
	public BlockRenderType getRenderShape(BlockState state) {
		return state.getValue(FACING) == Direction.DOWN ? BlockRenderType.MODEL : BlockRenderType.ENTITYBLOCK_ANIMATED;
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
	public void onRemove(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving) {
		if (!state.is(newState.getBlock())) {
			TileEntity te = world.getBlockEntity(pos);

			if (te instanceof IModuleInventory)
				((IModuleInventory) te).dropAllModules();

			if (!newState.hasTileEntity())
				world.removeBlockEntity(pos);
		}

		world.updateNeighborsAt(pos.north(), world.getBlockState(pos).getBlock());
		world.updateNeighborsAt(pos.south(), world.getBlockState(pos).getBlock());
		world.updateNeighborsAt(pos.east(), world.getBlockState(pos).getBlock());
		world.updateNeighborsAt(pos.west(), world.getBlockState(pos).getBlock());
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader source, BlockPos pos, ISelectionContext ctx) {
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

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext ctx) {
		Direction facing = ctx.getClickedFace();

		if (facing != Direction.UP) {
			World world = ctx.getLevel();
			BlockPos pos = ctx.getClickedPos();
			BlockState state = defaultBlockState().setValue(FACING, facing);

			if (!canSurvive(state, world, pos)) {
				for (Direction newFacing : Plane.HORIZONTAL) {
					state = state.setValue(FACING, newFacing);

					if (canSurvive(state, world, pos))
						break;
				}
			}

			return state;
		}
		else
			return null;
	}

	public void mountCamera(World world, BlockPos pos, PlayerEntity player) {
		if (!world.isClientSide) {
			ServerWorld serverWorld = (ServerWorld) world;
			ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;
			SecurityCamera dummyEntity;
			SectionPos chunkPos = SectionPos.of(pos);
			int viewDistance = serverPlayer.server.getPlayerList().getViewDistance();
			TileEntity te = world.getBlockEntity(pos);

			if (serverPlayer.getCamera() instanceof SecurityCamera)
				dummyEntity = new SecurityCamera(world, pos, (SecurityCamera) serverPlayer.getCamera());
			else
				dummyEntity = new SecurityCamera(world, pos);

			world.addFreshEntity(dummyEntity);
			dummyEntity.setChunkLoadingDistance(viewDistance);

			for (int x = chunkPos.getX() - viewDistance; x <= chunkPos.getX() + viewDistance; x++) {
				for (int z = chunkPos.getZ() - viewDistance; z <= chunkPos.getZ() + viewDistance; z++) {
					ForgeChunkManager.forceChunk(serverWorld, SecurityCraft.MODID, dummyEntity, x, z, true, false);
				}
			}

			//can't use ServerPlayerEntity#setSpectatingEntity here because it also teleports the player
			serverPlayer.camera = dummyEntity;
			SecurityCraft.channel.send(PacketDistributor.PLAYER.with(() -> serverPlayer), new SetCameraView(dummyEntity));

			if (te instanceof SecurityCameraBlockEntity)
				((SecurityCameraBlockEntity) te).startViewing();
		}
	}

	@Override
	public boolean canSurvive(BlockState state, IWorldReader world, BlockPos pos) {
		Direction facing = state.getValue(FACING);

		return BlockUtils.isSideSolid(world, pos.relative(facing.getOpposite()), facing);
	}

	@Override
	public boolean isSignalSource(BlockState state) {
		return true;
	}

	@Override
	public int getSignal(BlockState blockState, IBlockReader world, BlockPos pos, Direction side) {
		if (blockState.getValue(POWERED) && ((IModuleInventory) world.getBlockEntity(pos)).isModuleEnabled(ModuleType.REDSTONE))
			return 15;
		else
			return 0;
	}

	@Override
	public int getDirectSignal(BlockState blockState, IBlockReader world, BlockPos pos, Direction side) {
		if (blockState.getValue(POWERED) && ((IModuleInventory) world.getBlockEntity(pos)).isModuleEnabled(ModuleType.REDSTONE) && blockState.getValue(FACING) == side)
			return 15;
		else
			return 0;
	}

	@Override
	public void neighborChanged(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean flag) {
		if (!canSurvive(world.getBlockState(pos), world, pos) && !canSurvive(state, world, pos))
			world.destroyBlock(pos, true);
	}

	@Override
	protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(FACING, POWERED, BEING_VIEWED);
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
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
