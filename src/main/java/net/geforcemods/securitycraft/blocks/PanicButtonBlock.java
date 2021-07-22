package net.geforcemods.securitycraft.blocks;

import net.geforcemods.securitycraft.api.OwnableTileEntity;
import net.geforcemods.securitycraft.misc.OwnershipEvent;
import net.minecraft.block.AbstractButtonBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.properties.AttachFace;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

public class PanicButtonBlock extends AbstractButtonBlock {
	private static final VoxelShape FLOOR_NS_POWERED = Block.box(3, 0, 5, 13, 1, 11);
	private static final VoxelShape FLOOR_NS_UNPOWERED = Block.box(3, 0, 5, 13, 2, 11);
	private static final VoxelShape FLOOR_EW_POWERED = Block.box(5, 0, 3, 11, 1, 13);
	private static final VoxelShape FLOOR_EW_UNPOWERED = Block.box(5, 0, 3, 11, 2, 13);
	private static final VoxelShape WALL_N_POWERED = Block.box(3, 5, 15, 13, 11, 16);
	private static final VoxelShape WALL_N_UNPOWERED = Block.box(3, 5, 14, 13, 11, 16);
	private static final VoxelShape WALL_S_POWERED = Block.box(3, 5, 1, 13, 11, 0);
	private static final VoxelShape WALL_S_UNPOWERED = Block.box(3, 5, 2, 13, 11, 0);
	private static final VoxelShape WALL_E_POWERED = Block.box(1, 5, 3, 0, 11, 13);
	private static final VoxelShape WALL_E_UNPOWERED = Block.box(2, 5, 3, 0, 11, 13);
	private static final VoxelShape WALL_W_POWERED = Block.box(15, 5, 3, 16, 11, 13);
	private static final VoxelShape WALL_W_UNPOWERED = Block.box(14, 5, 3, 16, 11, 13);
	private static final VoxelShape CEILING_NS_POWERED = Block.box(3, 15, 5, 13, 16, 11);
	private static final VoxelShape CEILING_NS_UNPOWERED = Block.box(3, 14, 5, 13, 16, 11);
	private static final VoxelShape CEILING_EW_POWERED = Block.box(5, 15, 3, 11, 16, 13);
	private static final VoxelShape CEILING_EW_UNPOWERED = Block.box(5, 14, 3, 11, 16, 13);

	public PanicButtonBlock(boolean isWooden, Block.Properties properties) {
		super(isWooden, properties);
	}

	@Override
	public void setPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack)
	{
		if(placer instanceof PlayerEntity)
			MinecraftForge.EVENT_BUS.post(new OwnershipEvent(world, pos, (PlayerEntity)placer));
	}

	@Override
	public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit)
	{
		boolean newPowered = !state.getValue(POWERED);

		world.setBlockAndUpdate(pos, state.setValue(POWERED, newPowered));
		playSound(player, world, pos, newPowered);

		if(state.getValue(FACE) == AttachFace.WALL)
			notifyNeighbors(world, pos, state.getValue(FACING));
		else if(state.getValue(FACE) == AttachFace.CEILING)
			notifyNeighbors(world, pos, Direction.DOWN);
		else if(state.getValue(FACE) == AttachFace.FLOOR)
			notifyNeighbors(world, pos, Direction.UP);

		return ActionResultType.SUCCESS;
	}

	private void notifyNeighbors(World world, BlockPos pos, Direction facing)
	{
		world.updateNeighborsAt(pos, this);
		world.updateNeighborsAt(pos.relative(facing.getOpposite()), this);
	}

	@Override
	public void onRemove(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving)
	{
		super.onRemove(state, world, pos, newState, isMoving);
		world.removeBlockEntity(pos);
	}

	@Override
	public boolean triggerEvent(BlockState state, World world, BlockPos pos, int id, int param){
		super.triggerEvent(state, world, pos, id, param);
		TileEntity tileentity = world.getBlockEntity(pos);
		return tileentity == null ? false : tileentity.triggerEvent(id, param);
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader source, BlockPos pos, ISelectionContext ctx)
	{
		switch(state.getValue(FACE))
		{
			case FLOOR:
				switch(state.getValue(FACING))
				{
					case NORTH: case SOUTH:
						if(state.getValue(POWERED))
							return FLOOR_NS_POWERED;
						else
							return FLOOR_NS_UNPOWERED;
					case EAST: case WEST:
						if(state.getValue(POWERED))
							return FLOOR_EW_POWERED;
						else
							return FLOOR_EW_UNPOWERED;
					default: break;
				}
				break;
			case WALL:
				switch(state.getValue(FACING))
				{
					case NORTH:
						if(state.getValue(POWERED))
							return WALL_N_POWERED;
						else
							return WALL_N_UNPOWERED;
					case SOUTH:
						if(state.getValue(POWERED))
							return WALL_S_POWERED;
						else
							return WALL_S_UNPOWERED;
					case EAST:
						if(state.getValue(POWERED))
							return WALL_E_POWERED;
						else
							return WALL_E_UNPOWERED;
					case WEST:
						if(state.getValue(POWERED))
							return WALL_W_POWERED;
						else
							return WALL_W_UNPOWERED;
					default: break;
				}
				break;
			case CEILING:
				switch(state.getValue(FACING))
				{
					case NORTH: case SOUTH:
						if(state.getValue(POWERED))
							return CEILING_NS_POWERED;
						else
							return CEILING_NS_UNPOWERED;
					case EAST: case WEST:
						if(state.getValue(POWERED))
							return CEILING_EW_POWERED;
						else
							return CEILING_EW_UNPOWERED;
					default: break;
				}
		}

		return VoxelShapes.block();
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext ctx)
	{
		return VoxelShapes.empty();
	}

	@Override
	public boolean hasTileEntity(BlockState state)
	{
		return true;
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new OwnableTileEntity();
	}

	@Override
	protected SoundEvent getSound(boolean turningOn)
	{
		return turningOn ? SoundEvents.STONE_BUTTON_CLICK_ON : SoundEvents.STONE_BUTTON_CLICK_OFF;
	}
}
