package net.geforcemods.securitycraft.blocks;

import net.geforcemods.securitycraft.tileentity.BlockPocketManagerTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class BlockPocketManagerBlock extends OwnableBlock
{
	public static final DirectionProperty FACING = DirectionProperty.create("facing", Direction.Plane.HORIZONTAL);

	public BlockPocketManagerBlock(Block.Properties properties)
	{
		super(properties);
	}

	@Override
	public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit)
	{
		if(!world.isClientSide)
		{
			TileEntity te = world.getBlockEntity(pos);

			if(te instanceof BlockPocketManagerTileEntity && !((BlockPocketManagerTileEntity)te).isPlacingBlocks())
				NetworkHooks.openGui((ServerPlayerEntity)player, (BlockPocketManagerTileEntity)te, pos);
		}

		return ActionResultType.SUCCESS;
	}

	@Override
	public void onRemove(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving)
	{
		if(world.isClientSide || state.getBlock() == newState.getBlock())
			return;

		TileEntity tile = world.getBlockEntity(pos);

		if(tile instanceof BlockPocketManagerTileEntity)
		{
			BlockPocketManagerTileEntity te = (BlockPocketManagerTileEntity)tile;

			te.getStorageHandler().ifPresent(handler -> {
				for(int i = 0; i < handler.getSlots(); i++)
				{
					InventoryHelper.dropItemStack(world, pos.getX(), pos.getY(), pos.getZ(), handler.getStackInSlot(i));
				}
			});
		}

		super.onRemove(state, world, pos, newState, isMoving);
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context)
	{
		return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder)
	{
		builder.add(FACING);
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world)
	{
		return new BlockPocketManagerTileEntity();
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rot)
	{
		return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
	}

	@Override
	public BlockState mirror(BlockState state, Mirror mirror)
	{
		return state.rotate(mirror.getRotation(state.getValue(FACING)));
	}
}
