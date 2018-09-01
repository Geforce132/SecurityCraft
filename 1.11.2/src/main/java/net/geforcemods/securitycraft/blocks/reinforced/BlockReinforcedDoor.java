package net.geforcemods.securitycraft.blocks.reinforced;

import java.util.Random;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.tileentity.TileEntityOwnable;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockReinforcedDoor extends BlockDoor implements ITileEntityProvider{

	public BlockReinforcedDoor(Material materialIn) {
		super(materialIn);
		isBlockContainer = true;
		setSoundType(SoundType.METAL);
	}

	@Override
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos)
	{
		onNeighborChanged(worldIn, pos, fromPos);
	}

	/**
	 * Old method, renamed because I am lazy. Called by neighborChanged
	 * @param world The world the change occured in
	 * @param pos The position of this block
	 * @param neighbor The position of the changed block
	 */
	public void onNeighborChanged(IBlockAccess world, BlockPos pos, BlockPos neighbor)
	{
		World worldIn = (World)world;
		IBlockState state = worldIn.getBlockState(pos);
		Block neighborBlock = worldIn.getBlockState(neighbor).getBlock();

		if (state.getValue(HALF) == BlockDoor.EnumDoorHalf.UPPER)
		{
			BlockPos blockpos1 = pos.down();
			IBlockState iblockstate1 = worldIn.getBlockState(blockpos1);

			if (iblockstate1.getBlock() != this)
				worldIn.setBlockToAir(pos);
			else if (neighborBlock != this)
				onNeighborChanged(world, blockpos1, neighbor);
		}
		else
		{
			boolean flag1 = false;
			BlockPos blockpos2 = pos.up();
			IBlockState iblockstate2 = worldIn.getBlockState(blockpos2);

			if (iblockstate2.getBlock() != this)
			{
				worldIn.setBlockToAir(pos);
				flag1 = true;
			}

			if (!worldIn.isSideSolid(pos.down(), EnumFacing.UP))
			{
				worldIn.setBlockToAir(pos);
				flag1 = true;

				if (iblockstate2.getBlock() == this)
					worldIn.setBlockToAir(blockpos2);
			}

			if (flag1)
			{
				if (!worldIn.isRemote)
					dropBlockAsItem(worldIn, pos, state, 0);
			}
			else
			{
				boolean hasActiveSCBlock = BlockUtils.hasActiveSCBlockNextTo(worldIn, pos) || BlockUtils.hasActiveSCBlockNextTo(worldIn, pos.up());

				if (((hasActiveSCBlock || neighborBlock.canProvidePower(iblockstate2))) && neighborBlock != this && hasActiveSCBlock != iblockstate2.getValue(POWERED).booleanValue())
				{
					worldIn.setBlockState(blockpos2, iblockstate2.withProperty(POWERED, Boolean.valueOf(hasActiveSCBlock)), 2);

					if (hasActiveSCBlock != state.getValue(OPEN).booleanValue())
					{
						worldIn.setBlockState(pos, state.withProperty(OPEN, Boolean.valueOf(hasActiveSCBlock)), 2);
						worldIn.markBlockRangeForRenderUpdate(pos, pos);

						IBlockState secondDoorState;

						if(state.getValue(FACING) == EnumFacing.WEST)
						{
							secondDoorState = worldIn.getBlockState(pos.north());

							if(secondDoorState != null && secondDoorState.getBlock() == SCContent.reinforcedDoor && secondDoorState.getValue(OPEN).booleanValue() != hasActiveSCBlock)
							{
								worldIn.setBlockState(pos.north(), secondDoorState.withProperty(OPEN, hasActiveSCBlock), 2);
								worldIn.markBlockRangeForRenderUpdate(pos.north(), pos.north());
							}
							else
							{
								secondDoorState = worldIn.getBlockState(pos.south());

								if(secondDoorState != null && secondDoorState.getBlock() == SCContent.reinforcedDoor && secondDoorState.getValue(OPEN).booleanValue() != hasActiveSCBlock)
								{
									worldIn.setBlockState(pos.south(), secondDoorState.withProperty(OPEN, hasActiveSCBlock), 2);
									worldIn.markBlockRangeForRenderUpdate(pos.south(), pos.south());
								}
							}
						}
						else if(state.getValue(FACING) == EnumFacing.NORTH)
						{
							secondDoorState = worldIn.getBlockState(pos.east());

							if(secondDoorState != null && secondDoorState.getBlock() == SCContent.reinforcedDoor && secondDoorState.getValue(OPEN).booleanValue() != hasActiveSCBlock)
							{
								worldIn.setBlockState(pos.east(), secondDoorState.withProperty(OPEN, hasActiveSCBlock), 2);
								worldIn.markBlockRangeForRenderUpdate(pos.east(), pos.east());
							}
							else
							{
								secondDoorState = worldIn.getBlockState(pos.west());

								if(secondDoorState != null && secondDoorState.getBlock() == SCContent.reinforcedDoor && secondDoorState.getValue(OPEN).booleanValue() != hasActiveSCBlock)
								{
									worldIn.setBlockState(pos.west(), secondDoorState.withProperty(OPEN, hasActiveSCBlock), 2);
									worldIn.markBlockRangeForRenderUpdate(pos.west(), pos.west());
								}
							}
						}
						else if(state.getValue(FACING) == EnumFacing.EAST)
						{
							secondDoorState = worldIn.getBlockState(pos.south());

							if(secondDoorState != null && secondDoorState.getBlock() == SCContent.reinforcedDoor && secondDoorState.getValue(OPEN).booleanValue() != hasActiveSCBlock)
							{
								worldIn.setBlockState(pos.south(), secondDoorState.withProperty(OPEN, hasActiveSCBlock), 2);
								worldIn.markBlockRangeForRenderUpdate(pos.south(), pos.south());
							}
							else
							{
								secondDoorState = worldIn.getBlockState(pos.north());

								if(secondDoorState != null && secondDoorState.getBlock() == SCContent.reinforcedDoor && secondDoorState.getValue(OPEN).booleanValue() != hasActiveSCBlock)
								{
									worldIn.setBlockState(pos.north(), secondDoorState.withProperty(OPEN, hasActiveSCBlock), 2);
									worldIn.markBlockRangeForRenderUpdate(pos.north(), pos.north());
								}
							}
						}
						else if(state.getValue(FACING) == EnumFacing.SOUTH)
						{
							secondDoorState = worldIn.getBlockState(pos.west());

							if(secondDoorState != null && secondDoorState.getBlock() == SCContent.reinforcedDoor && secondDoorState.getValue(OPEN).booleanValue() != hasActiveSCBlock)
							{
								worldIn.setBlockState(pos.west(), secondDoorState.withProperty(OPEN, hasActiveSCBlock), 2);
								worldIn.markBlockRangeForRenderUpdate(pos.west(), pos.west());
							}
							else
							{
								secondDoorState = worldIn.getBlockState(pos.east());

								if(secondDoorState != null && secondDoorState.getBlock() == SCContent.reinforcedDoor && secondDoorState.getValue(OPEN).booleanValue() != hasActiveSCBlock)
								{
									worldIn.setBlockState(pos.east(), secondDoorState.withProperty(OPEN, hasActiveSCBlock), 2);
									worldIn.markBlockRangeForRenderUpdate(pos.east(), pos.east());
								}
							}
						}

						worldIn.playEvent((EntityPlayer)null, hasActiveSCBlock ? 1005 : 1011, pos, 0);
					}
				}
			}
		}
	}

	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state)
	{
		super.breakBlock(worldIn, pos, state);
		worldIn.removeTileEntity(pos);
	}

	@Override
	public boolean eventReceived(IBlockState state, World worldIn, BlockPos pos, int id, int param)
	{
		super.eventReceived(state, worldIn, pos, id, param);
		TileEntity tileentity = worldIn.getTileEntity(pos);
		return tileentity == null ? false : tileentity.receiveClientEvent(id, param);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public ItemStack getItem(World world, BlockPos pos, IBlockState state){
		return new ItemStack(SCContent.reinforcedDoorItem);
	}

	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune){
		return state.getValue(HALF) == BlockDoor.EnumDoorHalf.UPPER ? null : SCContent.reinforcedDoorItem;
	}

	@Override
	public TileEntity createNewTileEntity(World var1, int var2) {
		return new TileEntityOwnable();
	}
}