package net.geforcemods.securitycraft.blocks;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.CustomizableTileEntity;
import net.geforcemods.securitycraft.entity.SecurityCameraEntity;
import net.geforcemods.securitycraft.misc.CustomModules;
import net.geforcemods.securitycraft.misc.OwnershipEvent;
import net.geforcemods.securitycraft.tileentity.SecurityCameraTileEntity;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.WorldUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ContainerBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.MinecraftForge;

public class SecurityCameraBlock extends ContainerBlock{

	public static final DirectionProperty FACING = DirectionProperty.create("facing", facing -> facing != Direction.UP);
	public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
	private static final VoxelShape SHAPE_SOUTH = VoxelShapes.create(new AxisAlignedBB(0.275F, 0.250F, 0.000F, 0.700F, 0.800F, 0.850F));
	private static final VoxelShape SHAPE_NORTH = VoxelShapes.create(new AxisAlignedBB(0.275F, 0.250F, 0.150F, 0.700F, 0.800F, 1.000F));
	private static final VoxelShape SHAPE_WEST = VoxelShapes.create(new AxisAlignedBB(0.125F, 0.250F, 0.275F, 1.000F, 0.800F, 0.725F));
	private static final VoxelShape SHAPE = VoxelShapes.create(new AxisAlignedBB(0.000F, 0.250F, 0.275F, 0.850F, 0.800F, 0.725F));
	private static final VoxelShape SHAPE_DOWN = VoxelShapes.or(Block.makeCuboidShape(7, 15, 5, 9, 16, 11), VoxelShapes.or(Block.makeCuboidShape(6, 15, 6, 7, 16, 10), VoxelShapes.or(Block.makeCuboidShape(5, 15, 7, 6, 16, 9), VoxelShapes.or(Block.makeCuboidShape(9, 15, 6, 10, 16, 10), VoxelShapes.or(Block.makeCuboidShape(10, 15, 7, 11, 16, 9), Block.makeCuboidShape(7, 14, 7, 9, 15, 9))))));

	public SecurityCameraBlock(Material material) {
		super(Block.Properties.create(material).hardnessAndResistance(-1.0F, 6000000.0F));
		stateContainer.getBaseState().with(FACING, Direction.NORTH).with(POWERED, false);
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack)
	{
		if(placer instanceof PlayerEntity)
			MinecraftForge.EVENT_BUS.post(new OwnershipEvent(world, pos, (PlayerEntity)placer));
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
		BlockState state = getDefaultState().with(POWERED, Boolean.valueOf(false));

		if(BlockUtils.isSideSolid(world, pos.offset(facing.getOpposite()), facing))
			return state.with(FACING, facing).with(POWERED, false);
		else{
			Iterator<?> iterator = Direction.Plane.HORIZONTAL.iterator();
			Direction iFacing;

			do{
				if(!iterator.hasNext())
					return state;

				iFacing = (Direction)iterator.next();
			}while (!BlockUtils.isSideSolid(world, pos.offset(iFacing.getOpposite()), iFacing));

			return state.with(FACING, facing).with(POWERED, false);
		}
	}

	public void mountCamera(World world, int x, int y, int z, int id, PlayerEntity player){
		if(!world.isRemote && player.getRidingEntity() == null)
			PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize(SCContent.securityCamera.getTranslationKey()), ClientUtils.localize("messages.securitycraft:securityCamera.mounted"), TextFormatting.GREEN);

		if(player.getRidingEntity() != null && player.getRidingEntity() instanceof SecurityCameraEntity){
			SecurityCameraEntity dummyEntity = new SecurityCameraEntity(world, x, y, z, id, (SecurityCameraEntity) player.getRidingEntity());
			WorldUtils.addScheduledTask(world, () -> world.addEntity(dummyEntity));
			player.startRiding(dummyEntity);
			return;
		}

		SecurityCameraEntity dummyEntity = new SecurityCameraEntity(world, x, y, z, id, player);
		WorldUtils.addScheduledTask(world, () -> world.addEntity(dummyEntity));
		player.startRiding(dummyEntity);

		if(world instanceof ServerWorld)
		{
			ServerWorld serverWorld = (ServerWorld)world;
			List<Entity> loadedEntityList = serverWorld.getEntities().collect(Collectors.toList());

			for(Entity e : loadedEntityList)
			{
				if(e instanceof MobEntity)
				{
					if(((MobEntity)e).getAttackTarget() == player)
						((MobEntity)e).setAttackTarget(null);
				}
			}
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
	public int getWeakPower(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side){
		if(blockState.get(POWERED) && ((CustomizableTileEntity) blockAccess.getTileEntity(pos)).hasModule(CustomModules.REDSTONE))
			return 15;
		else
			return 0;
	}

	@Override
	public int getStrongPower(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side){
		if(blockState.get(POWERED) && ((CustomizableTileEntity) blockAccess.getTileEntity(pos)).hasModule(CustomModules.REDSTONE))
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
	protected void fillStateContainer(Builder<Block, BlockState> builder)
	{
		builder.add(FACING);
		builder.add(POWERED);
	}

	@Override
	public TileEntity createNewTileEntity(IBlockReader world){
		return new SecurityCameraTileEntity().nameable();
	}

}
