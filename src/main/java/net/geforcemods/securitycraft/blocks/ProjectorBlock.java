package net.geforcemods.securitycraft.blocks;

import java.util.Random;

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
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkHooks;

public class ProjectorBlock extends OwnableBlock {

	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
	private static final VoxelShape SHAPE = Block.makeCuboidShape(0, 0, 0, 16, 10, 16);

	public ProjectorBlock(Properties properties) {
		super(SoundType.METAL, properties.notSolid());
		setDefaultState(stateContainer.getBaseState().with(FACING, Direction.NORTH));
	}

	@Override
	public boolean isNormalCube(BlockState state, IBlockReader reader, BlockPos pos) 
	{
		return false;
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader source, BlockPos pos, ISelectionContext ctx)
	{
		return SHAPE;
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
	public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) 
	{
		if(!worldIn.isRemote)
		{
			if(worldIn.getTileEntity(pos) instanceof ProjectorTileEntity && ((ProjectorTileEntity) worldIn.getTileEntity(pos)).isActivatedByRedstone()) 
			{
				if (worldIn.isBlockPowered(pos)) {
					((ProjectorTileEntity) worldIn.getTileEntity(pos)).setActive(true);
					((ProjectorTileEntity) worldIn.getTileEntity(pos)).sync();
				}
				else
				{
		            ((ProjectorTileEntity) worldIn.getTileEntity(pos)).setActive(false);
					((ProjectorTileEntity) worldIn.getTileEntity(pos)).sync();
				}
		    }
		}
	}
	
	@Override
	public void tick(BlockState state, ServerWorld worldIn, BlockPos pos, Random rand) 
	{
		if (!worldIn.isBlockPowered(pos) && worldIn.getTileEntity(pos) instanceof ProjectorTileEntity && ((ProjectorTileEntity) worldIn.getTileEntity(pos)).isActivatedByRedstone())
		{
			((ProjectorTileEntity) worldIn.getTileEntity(pos)).setActive(false);
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
