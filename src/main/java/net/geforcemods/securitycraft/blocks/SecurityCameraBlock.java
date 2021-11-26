package net.geforcemods.securitycraft.blocks;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.entity.camera.SecurityCameraEntity;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.network.client.SetCameraView;
import net.geforcemods.securitycraft.tileentity.SecurityCameraTileEntity;
import net.geforcemods.securitycraft.util.BlockUtils;
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

public class SecurityCameraBlock extends OwnableBlock{

	public static final DirectionProperty FACING = DirectionProperty.create("facing", facing -> facing != Direction.UP);
	public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
	public static final BooleanProperty BEING_VIEWED = BooleanProperty.create("being_viewed");
	private static final VoxelShape SHAPE_SOUTH = VoxelShapes.create(new AxisAlignedBB(0.275F, 0.250F, 0.000F, 0.700F, 0.800F, 0.850F));
	private static final VoxelShape SHAPE_NORTH = VoxelShapes.create(new AxisAlignedBB(0.275F, 0.250F, 0.150F, 0.700F, 0.800F, 1.000F));
	private static final VoxelShape SHAPE_WEST = VoxelShapes.create(new AxisAlignedBB(0.125F, 0.250F, 0.275F, 1.000F, 0.800F, 0.725F));
	private static final VoxelShape SHAPE = VoxelShapes.create(new AxisAlignedBB(0.000F, 0.250F, 0.275F, 0.850F, 0.800F, 0.725F));
	private static final VoxelShape SHAPE_DOWN = VoxelShapes.or(Block.makeCuboidShape(7, 15, 5, 9, 16, 11), VoxelShapes.or(Block.makeCuboidShape(6, 15, 6, 7, 16, 10), VoxelShapes.or(Block.makeCuboidShape(5, 15, 7, 6, 16, 9), VoxelShapes.or(Block.makeCuboidShape(9, 15, 6, 10, 16, 10), VoxelShapes.or(Block.makeCuboidShape(10, 15, 7, 11, 16, 9), Block.makeCuboidShape(7, 14, 7, 9, 15, 9))))));

	public SecurityCameraBlock(Block.Properties properties) {
		super(properties);
		setDefaultState(stateContainer.getBaseState().with(FACING, Direction.NORTH).with(POWERED, false).with(BEING_VIEWED, false));
	}

	@Override
	public VoxelShape getCollisionShape(BlockState blockState, IBlockReader access, BlockPos pos, ISelectionContext ctx){
		return VoxelShapes.empty();
	}

	@Override
	public BlockRenderType getRenderType(BlockState state){
		return state.get(FACING) == Direction.DOWN ? BlockRenderType.MODEL : BlockRenderType.ENTITYBLOCK_ANIMATED;
	}

	@Override
	public void onReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving)
	{
		super.onReplaced(state, world, pos, newState, isMoving);

		world.notifyNeighborsOfStateChange(pos.north(), world.getBlockState(pos).getBlock());
		world.notifyNeighborsOfStateChange(pos.south(), world.getBlockState(pos).getBlock());
		world.notifyNeighborsOfStateChange(pos.east(), world.getBlockState(pos).getBlock());
		world.notifyNeighborsOfStateChange(pos.west(), world.getBlockState(pos).getBlock());
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader source, BlockPos pos, ISelectionContext ctx)
	{
		Direction dir = state.get(FACING);

		if(dir == Direction.SOUTH)
			return SHAPE_SOUTH;
		else if(dir == Direction.NORTH)
			return SHAPE_NORTH;
		else if(dir == Direction.WEST)
			return SHAPE_WEST;
		else if(dir == Direction.DOWN)
			return SHAPE_DOWN;
		else
			return SHAPE;
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext ctx)
	{
		return ctx.getFace() != Direction.UP ? getStateForPlacement(ctx.getWorld(), ctx.getPos(), ctx.getFace(), ctx.getHitVec().x, ctx.getHitVec().y, ctx.getHitVec().z, ctx.getPlayer()) : null;
	}

	public BlockState getStateForPlacement(World world, BlockPos pos, Direction facing, double hitX, double hitY, double hitZ, PlayerEntity placer)
	{
		BlockState state = getDefaultState().with(FACING, facing);

		if(!isValidPosition(state, world, pos)) {
			for (Direction newFacing : Plane.HORIZONTAL) {
				state = state.with(FACING, newFacing);

				if (isValidPosition(state, world, pos))
					break;
			}
		}

		return state;
	}

	public void mountCamera(World world, BlockPos pos, PlayerEntity player){
		if(world instanceof ServerWorld)
		{
			ServerWorld serverWorld = (ServerWorld)world;
			ServerPlayerEntity serverPlayer = (ServerPlayerEntity)player;
			SecurityCameraEntity dummyEntity;
			SectionPos chunkPos = SectionPos.from(pos);
			int viewDistance = serverPlayer.server.getPlayerList().getViewDistance();
			TileEntity te = world.getTileEntity(pos);

			if(serverPlayer.getSpectatingEntity() instanceof SecurityCameraEntity)
				dummyEntity = new SecurityCameraEntity(world, pos, (SecurityCameraEntity)serverPlayer.getSpectatingEntity());
			else
				dummyEntity = new SecurityCameraEntity(world, pos, player);

			world.addEntity(dummyEntity);

			for (int x = chunkPos.getX() - viewDistance; x <= chunkPos.getX() + viewDistance; x++) {
				for (int z = chunkPos.getZ() - viewDistance; z <= chunkPos.getZ() + viewDistance; z++) {
					ForgeChunkManager.forceChunk(serverWorld, SecurityCraft.MODID, dummyEntity, x, z, true, false);
				}
			}

			//can't use ServerPlayer#setCamera here because it also teleports the player
			serverPlayer.spectatingEntity = dummyEntity;
			SecurityCraft.channel.send(PacketDistributor.PLAYER.with(() -> serverPlayer), new SetCameraView(dummyEntity));

			if(te instanceof SecurityCameraTileEntity)
				((SecurityCameraTileEntity)te).startViewing();
		}
	}

	@Override
	public boolean isValidPosition(BlockState state, IWorldReader world, BlockPos pos){
		Direction facing = state.get(FACING);

		return BlockUtils.isSideSolid(world, pos.offset(facing.getOpposite()), facing);
	}

	@Override
	public boolean canProvidePower(BlockState state){
		return true;
	}

	@Override
	public int getWeakPower(BlockState blockState, IBlockReader world, BlockPos pos, Direction side){
		if(blockState.get(POWERED) && ((IModuleInventory) world.getTileEntity(pos)).hasModule(ModuleType.REDSTONE))
			return 15;
		else
			return 0;
	}

	@Override
	public int getStrongPower(BlockState blockState, IBlockReader world, BlockPos pos, Direction side){
		if(blockState.get(POWERED) && ((IModuleInventory) world.getTileEntity(pos)).hasModule(ModuleType.REDSTONE))
			return 15;
		else
			return 0;
	}

	@Override
	public void neighborChanged(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean flag) {
		if (!isValidPosition(world.getBlockState(pos), world, pos) && !isValidPosition(state, world, pos))
			world.destroyBlock(pos, true);
	}

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
	{
		builder.add(FACING, POWERED, BEING_VIEWED);
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new SecurityCameraTileEntity();
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rot)
	{
		return state.with(FACING, rot.rotate(state.get(FACING)));
	}

	@Override
	public BlockState mirror(BlockState state, Mirror mirror)
	{
		Direction facing = state.get(FACING);

		switch(mirror)
		{
			case LEFT_RIGHT:
				if(facing.getAxis() == Axis.Z)
					return state.with(FACING, facing.getOpposite());
				break;
			case FRONT_BACK:
				if(facing.getAxis() == Axis.X)
					return state.with(FACING, facing.getOpposite());
				break;
			case NONE: break;
		}

		return state;
	}
}
