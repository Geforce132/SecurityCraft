package net.geforcemods.securitycraft.blocks;

import net.geforcemods.securitycraft.misc.OwnershipEvent;
import net.geforcemods.securitycraft.tileentity.OwnableTileEntity;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.block.AbstractButtonBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
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
	private static final VoxelShape FLOOR_NS_POWERED = Block.makeCuboidShape(3, 0, 5, 13, 1, 11);
	private static final VoxelShape FLOOR_NS_UNPOWERED = Block.makeCuboidShape(3, 0, 5, 13, 2, 11);
	private static final VoxelShape FLOOR_EW_POWERED = Block.makeCuboidShape(5, 0, 3, 11, 1, 13);
	private static final VoxelShape FLOOR_EW_UNPOWERED = Block.makeCuboidShape(5, 0, 3, 11, 2, 13);
	private static final VoxelShape WALL_N_POWERED = Block.makeCuboidShape(3, 5, 15, 13, 11, 16);
	private static final VoxelShape WALL_N_UNPOWERED = Block.makeCuboidShape(3, 5, 14, 13, 11, 16);
	private static final VoxelShape WALL_S_POWERED = Block.makeCuboidShape(3, 5, 1, 13, 11, 0);
	private static final VoxelShape WALL_S_UNPOWERED = Block.makeCuboidShape(3, 5, 2, 13, 11, 0);
	private static final VoxelShape WALL_E_POWERED = Block.makeCuboidShape(1, 5, 3, 0, 11, 13);
	private static final VoxelShape WALL_E_UNPOWERED = Block.makeCuboidShape(2, 5, 3, 0, 11, 13);
	private static final VoxelShape WALL_W_POWERED = Block.makeCuboidShape(15, 5, 3, 16, 11, 13);
	private static final VoxelShape WALL_W_UNPOWERED = Block.makeCuboidShape(14, 5, 3, 16, 11, 13);
	private static final VoxelShape CEILING_NS_POWERED = Block.makeCuboidShape(3, 15, 5, 13, 16, 11);
	private static final VoxelShape CEILING_NS_UNPOWERED = Block.makeCuboidShape(3, 14, 5, 13, 16, 11);
	private static final VoxelShape CEILING_EW_POWERED = Block.makeCuboidShape(5, 15, 3, 11, 16, 13);
	private static final VoxelShape CEILING_EW_UNPOWERED = Block.makeCuboidShape(5, 14, 3, 11, 16, 13);

	public PanicButtonBlock() {
		super(false, Block.Properties.create(Material.ROCK).sound(SoundType.STONE).hardnessAndResistance(-1.0F, 6000000.0F));
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack)
	{
		if(placer instanceof PlayerEntity)
			MinecraftForge.EVENT_BUS.post(new OwnershipEvent(world, pos, (PlayerEntity)placer));
	}

	@Override
	public ActionResultType func_225533_a_(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) //onBlockActivated
	{
		BlockUtils.setBlockProperty(world, pos, POWERED, !state.get(POWERED), true);

		if(state.get(FACE) == AttachFace.WALL)
			notifyNeighbors(world, pos, state.get(HORIZONTAL_FACING));
		else if(state.get(FACE) == AttachFace.CEILING)
			notifyNeighbors(world, pos, Direction.DOWN);
		else if(state.get(FACE) == AttachFace.FLOOR)
			notifyNeighbors(world, pos, Direction.UP);

		return ActionResultType.SUCCESS;
	}

	private void notifyNeighbors(World world, BlockPos pos, Direction facing)
	{
		world.notifyNeighborsOfStateChange(pos, this);
		world.notifyNeighborsOfStateChange(pos.offset(facing.getOpposite()), this);
	}

	@Override
	public void onReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving)
	{
		super.onReplaced(state, world, pos, newState, isMoving);
		world.removeTileEntity(pos);
	}

	@Override
	public boolean eventReceived(BlockState state, World world, BlockPos pos, int id, int param){
		super.eventReceived(state, world, pos, id, param);
		TileEntity tileentity = world.getTileEntity(pos);
		return tileentity == null ? false : tileentity.receiveClientEvent(id, param);
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader source, BlockPos pos, ISelectionContext ctx)
	{
		switch(state.get(FACE))
		{
			case FLOOR:
				switch(state.get(HORIZONTAL_FACING))
				{
					case NORTH: case SOUTH:
						if(state.get(POWERED))
							return FLOOR_NS_POWERED;
						else
							return FLOOR_NS_UNPOWERED;
					case EAST: case WEST:
						if(state.get(POWERED))
							return FLOOR_EW_POWERED;
						else
							return FLOOR_EW_UNPOWERED;
					default: break;
				}
				break;
			case WALL:
				switch(state.get(HORIZONTAL_FACING))
				{
					case NORTH:
						if(state.get(POWERED))
							return WALL_N_POWERED;
						else
							return WALL_N_UNPOWERED;
					case SOUTH:
						if(state.get(POWERED))
							return WALL_S_POWERED;
						else
							return WALL_S_UNPOWERED;
					case EAST:
						if(state.get(POWERED))
							return WALL_E_POWERED;
						else
							return WALL_E_UNPOWERED;
					case WEST:
						if(state.get(POWERED))
							return WALL_W_POWERED;
						else
							return WALL_W_UNPOWERED;
					default: break;
				}
				break;
			case CEILING:
				switch(state.get(HORIZONTAL_FACING))
				{
					case NORTH: case SOUTH:
						if(state.get(POWERED))
							return CEILING_NS_POWERED;
						else
							return CEILING_NS_UNPOWERED;
					case EAST: case WEST:
						if(state.get(POWERED))
							return CEILING_EW_POWERED;
						else
							return CEILING_EW_UNPOWERED;
					default: break;
				}
		}

		return VoxelShapes.fullCube();
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
	protected SoundEvent getSoundEvent(boolean turningOn)
	{
		return turningOn ? SoundEvents.BLOCK_STONE_BUTTON_CLICK_ON : SoundEvents.BLOCK_STONE_BUTTON_CLICK_OFF;
	}
}
