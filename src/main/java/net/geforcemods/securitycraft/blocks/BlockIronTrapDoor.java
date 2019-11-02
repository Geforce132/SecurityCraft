package net.geforcemods.securitycraft.blocks;

import net.geforcemods.securitycraft.tileentity.TileEntityOwnable;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockTrapDoor;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class BlockIronTrapDoor extends BlockTrapDoor implements ITileEntityProvider {

	public BlockIronTrapDoor(Material material) {
		super(material);
	}

	@Override
	public void onNeighborBlockChange(World world, BlockPos pos, IBlockState state, Block neighborBlock)
	{
		boolean hasActiveSCBlock = BlockUtils.hasActiveSCBlockNextTo(world, pos);

		if(hasActiveSCBlock != state.getValue(OPEN))
		{
			world.setBlockState(pos, state.withProperty(OPEN, Boolean.valueOf(BlockUtils.hasActiveSCBlockNextTo(world, pos))), 2);
			world.markBlockRangeForRenderUpdate(pos, pos);
			world.playAuxSFXAtEntity((EntityPlayer)null, hasActiveSCBlock ? 1003 : 1006, pos, 0);
		}
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ){
		return false;
	}

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state)
	{
		super.breakBlock(world, pos, state);
		world.removeTileEntity(pos);
	}

	@Override
	public boolean onBlockEventReceived(World world, BlockPos pos, IBlockState state, int eventID, int eventParam)
	{
		super.onBlockEventReceived(world, pos, state, eventID, eventParam);
		TileEntity tileentity = world.getTileEntity(pos);
		return tileentity == null ? false : tileentity.receiveClientEvent(eventID, eventParam);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileEntityOwnable();
	}

}