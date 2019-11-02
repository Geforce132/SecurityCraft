package net.geforcemods.securitycraft.blocks;

import net.geforcemods.securitycraft.tileentity.TileEntityOwnable;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockTrapDoor;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockIronTrapDoor extends BlockTrapDoor implements ITileEntityProvider {

	public BlockIronTrapDoor(Material material) {
		super(material);
		setSoundType(SoundType.METAL);
	}

	@Override
	public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos neighbor)
	{
		boolean hasActiveSCBlock = BlockUtils.hasActiveSCBlockNextTo(world, pos);

		if(hasActiveSCBlock != state.getValue(OPEN))
		{
			world.setBlockState(pos, state.withProperty(OPEN, Boolean.valueOf(BlockUtils.hasActiveSCBlockNextTo(world, pos))), 2);
			world.markBlockRangeForRenderUpdate(pos, pos);
			playSound((EntityPlayer)null, world, pos, hasActiveSCBlock);
		}
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ){
		return false;
	}

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state)
	{
		super.breakBlock(world, pos, state);
		world.removeTileEntity(pos);
	}

	@Override
	public boolean eventReceived(IBlockState state, World world, BlockPos pos, int id, int param)
	{
		super.eventReceived(state, world, pos, id, param);
		TileEntity tileentity = world.getTileEntity(pos);
		return tileentity == null ? false : tileentity.receiveClientEvent(id, param);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileEntityOwnable();
	}
}