package net.geforcemods.securitycraft.blocks;

import java.util.Random;
import java.util.stream.Stream;

import net.geforcemods.securitycraft.tileentity.ProjectorTileEntity;
import net.geforcemods.securitycraft.util.WorldUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkHooks;

public class ProjectorBlock extends DisguisableBlock {

	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
	private static final VoxelShape NORTH = Stream.of(Block.makeCuboidShape(3, 5, 0.9, 6, 8, 1.9), Block.makeCuboidShape(0, 3, 1, 16, 10, 16), Block.makeCuboidShape(2, 8, 0.5, 7, 9, 1), Block.makeCuboidShape(2, 4, 0.5, 7, 5, 1), Block.makeCuboidShape(6, 5, 0.5, 7, 8, 1), Block.makeCuboidShape(2, 5, 0.5, 3, 8, 1), Block.makeCuboidShape(0, 0, 1, 2, 3, 3), Block.makeCuboidShape(14, 0, 1, 16, 3, 3), Block.makeCuboidShape(14, 0, 14, 16, 3, 16), Block.makeCuboidShape(0, 0, 14, 2, 3, 16)).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, IBooleanFunction.OR)).orElse(VoxelShapes.fullCube());
	private static final VoxelShape SOUTH = Stream.of(Block.makeCuboidShape(0, 3, 0, 16, 10, 15), Block.makeCuboidShape(10, 5, 14.1, 13, 8, 15.100000000000001), Block.makeCuboidShape(9, 8, 15, 14, 9, 15.5), Block.makeCuboidShape(9, 4, 15, 14, 5, 15.5), Block.makeCuboidShape(9, 5, 15, 10, 8, 15.5), Block.makeCuboidShape(13, 5, 15, 14, 8, 15.5), Block.makeCuboidShape(14, 0, 13, 16, 3, 15), Block.makeCuboidShape(0, 0, 13, 2, 3, 15), Block.makeCuboidShape(0, 0, 0, 2, 3, 2), Block.makeCuboidShape(14, 0, 0, 16, 3, 2)).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, IBooleanFunction.OR)).orElse(VoxelShapes.fullCube());
	private static final VoxelShape WEST = Stream.of(Block.makeCuboidShape(0.5, 5, 13, 1, 8, 14), Block.makeCuboidShape(0.5, 5, 9, 1, 8, 10), Block.makeCuboidShape(0.5, 4, 9, 1, 5, 14), Block.makeCuboidShape(0.5, 8, 9, 1, 9, 14), Block.makeCuboidShape(0.75, 5, 10, 1.75, 8, 13), Block.makeCuboidShape(1, 0, 14, 3, 3, 16), Block.makeCuboidShape(14, 0, 14, 16, 3, 16), Block.makeCuboidShape(14, 0, 0, 16, 3, 2), Block.makeCuboidShape(1, 0, 0, 3, 3, 2), Block.makeCuboidShape(1, 3, 0, 16, 10, 16)).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, IBooleanFunction.OR)).orElse(VoxelShapes.fullCube());
	private static final VoxelShape EAST = Stream.of(Block.makeCuboidShape(15, 5, 2, 15.5, 8, 3), Block.makeCuboidShape(15, 5, 6, 15.5, 8, 7), Block.makeCuboidShape(15, 4, 2, 15.5, 5, 7), Block.makeCuboidShape(15, 8, 2, 15.5, 9, 7), Block.makeCuboidShape(14.25, 5, 3, 15.25, 8, 6), Block.makeCuboidShape(13, 0, 0, 15, 3, 2), Block.makeCuboidShape(0, 0, 0, 2, 3, 2), Block.makeCuboidShape(0, 0, 14, 2, 3, 16), Block.makeCuboidShape(13, 0, 14, 15, 3, 16), Block.makeCuboidShape(0, 3, 0, 15, 10, 16)).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, IBooleanFunction.OR)).orElse(VoxelShapes.fullCube());

	public ProjectorBlock(Properties properties) {
		super(SoundType.METAL, properties);
		setDefaultState(stateContainer.getBaseState().with(FACING, Direction.NORTH));
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext ctx)
	{
		BlockState extendedState = getExtendedState(state, world, pos);

		if(extendedState.getBlock() != this)
			return extendedState.getShape(world, pos, ctx);
		else
		{
			switch(state.get(FACING))
			{
				case NORTH:
					return SOUTH;
				case EAST:
					return WEST;
				case SOUTH:
					return NORTH;
				case WEST:
					return EAST;
				default: return VoxelShapes.fullCube();
			}
		}
	}

	@Override
	public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit)
	{
		if (!world.isRemote)
		{
			TileEntity te = world.getTileEntity(pos);

			NetworkHooks.openGui((ServerPlayerEntity)player, (INamedContainerProvider) te, pos);
		}

		return ActionResultType.SUCCESS;
	}

	@Override
	public void onReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving)
	{
		TileEntity tileentity = world.getTileEntity(pos);

		if (tileentity instanceof ProjectorTileEntity)
		{
			// Drop the block being projected
			ItemEntity item = new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), ((ProjectorTileEntity) world.getTileEntity(pos)).getStackInSlot(9));
			WorldUtils.addScheduledTask(world, () -> world.addEntity(item));
		}

		super.onReplaced(state, world, pos, newState, isMoving);
	}

	@Override
	public void neighborChanged(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving)
	{
		if(!world.isRemote)
		{
			if(world.getTileEntity(pos) instanceof ProjectorTileEntity && ((ProjectorTileEntity) world.getTileEntity(pos)).isActivatedByRedstone())
			{
				((ProjectorTileEntity) world.getTileEntity(pos)).setActive(world.isBlockPowered(pos));
				((ProjectorTileEntity) world.getTileEntity(pos)).sync();
			}
		}
	}

	@Override
	public void tick(BlockState state, ServerWorld world, BlockPos pos, Random rand)
	{
		if (!world.isBlockPowered(pos) && world.getTileEntity(pos) instanceof ProjectorTileEntity && ((ProjectorTileEntity) world.getTileEntity(pos)).isActivatedByRedstone())
		{
			((ProjectorTileEntity) world.getTileEntity(pos)).setActive(false);
		}
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext ctx)
	{
		return getStateForPlacement(ctx.getWorld(), ctx.getPos(), ctx.getFace(), ctx.getHitVec().x, ctx.getHitVec().y, ctx.getHitVec().z, ctx.getPlayer());
	}

	public BlockState getStateForPlacement(World world, BlockPos pos, Direction facing, double hitX, double hitY, double hitZ, PlayerEntity placer)
	{
		return getDefaultState().with(FACING, placer.getHorizontalFacing().getOpposite());
	}

	@Override
	protected void fillStateContainer(Builder<Block, BlockState> builder)
	{
		builder.add(FACING);
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new ProjectorTileEntity();
	}

}
