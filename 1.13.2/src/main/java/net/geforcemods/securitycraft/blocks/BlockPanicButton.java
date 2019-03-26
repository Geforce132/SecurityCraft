package net.geforcemods.securitycraft.blocks;

import net.geforcemods.securitycraft.misc.OwnershipEvent;
import net.geforcemods.securitycraft.tileentity.TileEntityOwnable;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockButton;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.state.properties.AttachFace;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

public class BlockPanicButton extends BlockButton implements ITileEntityProvider {
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

	public BlockPanicButton() {
		super(false, Block.Properties.create(Material.ROCK).sound(SoundType.STONE).hardnessAndResistance(-1.0F, 6000000.0F));
	}

	/**
	 * If this block doesn't render as an ordinary block it will return False (examples: signs, buttons, stairs, etc)
	 */
	@Override
	public boolean isNormalCube(IBlockState state)
	{
		return false;
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
	{
		if(placer instanceof EntityPlayer)
			MinecraftForge.EVENT_BUS.post(new OwnershipEvent(world, pos, (EntityPlayer)placer));
	}

	@Override
	public boolean onBlockActivated(IBlockState state, World world, BlockPos pos, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ){
		BlockUtils.setBlockProperty(world, pos, POWERED, !state.get(POWERED), true);
		world.markBlockRangeForRenderUpdate(pos, pos);

		if(state.get(FACE) == AttachFace.WALL)
			notifyNeighbors(world, pos, state.get(HORIZONTAL_FACING));
		else if(state.get(FACE) == AttachFace.CEILING)
			notifyNeighbors(world, pos, EnumFacing.UP);
		else if(state.get(FACE) == AttachFace.FLOOR)
			notifyNeighbors(world, pos, EnumFacing.DOWN);

		return true;
	}

	private void notifyNeighbors(World world, BlockPos pos, EnumFacing facing)
	{
		world.notifyNeighborsOfStateChange(pos, this);
		world.notifyNeighborsOfStateChange(pos.offset(facing.getOpposite()), this);
	}

	@Override
	public void onReplaced(IBlockState state, World world, BlockPos pos, IBlockState newState, boolean isMoving)
	{
		super.onReplaced(state, world, pos, newState, isMoving);
		world.removeTileEntity(pos);
	}

	@Override
	public boolean eventReceived(IBlockState state, World world, BlockPos pos, int id, int param){
		super.eventReceived(state, world, pos, id, param);
		TileEntity tileentity = world.getTileEntity(pos);
		return tileentity == null ? false : tileentity.receiveClientEvent(id, param);
	}

	@Override
	public VoxelShape getShape(IBlockState state, IBlockReader source, BlockPos pos)
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
	public VoxelShape getCollisionShape(IBlockState state, IBlockReader world, BlockPos pos)
	{
		return VoxelShapes.empty();
	}

	@Override
	public TileEntity createNewTileEntity(IBlockReader reader) {
		return new TileEntityOwnable();
	}

	@Override
	protected SoundEvent getSoundEvent(boolean turningOn)
	{
		return turningOn ? SoundEvents.BLOCK_STONE_BUTTON_CLICK_ON : SoundEvents.BLOCK_STONE_BUTTON_CLICK_OFF;
	}
}
