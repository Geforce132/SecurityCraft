package net.geforcemods.securitycraft.blocks.reinforced;

import java.util.Random;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.tileentity.TileEntityOwnable;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockReinforcedDoor extends BlockDoor implements ITileEntityProvider{

	public BlockReinforcedDoor(Material material) {
		super(material);
		isBlockContainer = true;
	}

	/**
	 * Lets the block know when one of its neighbor changes. Doesn't know which neighbor changed (coordinates passed are
	 * their own) Args: x, y, z, neighbor Block
	 */
	@Override
	public void onNeighborBlockChange(World world, BlockPos pos, IBlockState state, Block neighborBlock){
		if (state.getValue(HALF) == BlockDoor.EnumDoorHalf.UPPER)
		{
			BlockPos blockBelow = pos.down();
			IBlockState stateBelow = world.getBlockState(blockBelow);

			if (stateBelow.getBlock() != this)
				world.setBlockToAir(pos);
			else if (neighborBlock != this)
				onNeighborBlockChange(world, blockBelow, stateBelow, neighborBlock);
		}
		else
		{
			boolean isNotDoor = false;
			BlockPos blockAbove = pos.up();
			IBlockState stateAbove = world.getBlockState(blockAbove);

			if (stateAbove.getBlock() != this)
			{
				world.setBlockToAir(pos);
				isNotDoor = true;
			}

			if (!World.doesBlockHaveSolidTopSurface(world, pos.down()))
			{
				world.setBlockToAir(pos);
				isNotDoor = true;

				if (stateAbove.getBlock() == this)
					world.setBlockToAir(blockAbove);
			}

			if (isNotDoor)
			{
				if (!world.isRemote)
					dropBlockAsItem(world, pos, state, 0);
			}
			else
			{
				boolean hasActiveSCBlock = BlockUtils.hasActiveSCBlockNextTo(world, pos) || BlockUtils.hasActiveSCBlockNextTo(world, pos.up());

				if (((hasActiveSCBlock || neighborBlock.canProvidePower())) && neighborBlock != this && hasActiveSCBlock != stateAbove.getValue(POWERED).booleanValue())
				{
					world.setBlockState(blockAbove, stateAbove.withProperty(POWERED, Boolean.valueOf(hasActiveSCBlock)), 2);

					if (hasActiveSCBlock != state.getValue(OPEN).booleanValue())
					{
						world.setBlockState(pos, state.withProperty(OPEN, Boolean.valueOf(hasActiveSCBlock)), 2);
						world.markBlockRangeForRenderUpdate(pos, pos);

						IBlockState secondDoorState;

						if(state.getValue(FACING) == EnumFacing.WEST)
						{
							secondDoorState = world.getBlockState(pos.north());

							if(secondDoorState != null && secondDoorState.getBlock() == SCContent.reinforcedDoor && secondDoorState.getValue(OPEN).booleanValue() != hasActiveSCBlock)
							{
								world.setBlockState(pos.north(), secondDoorState.withProperty(OPEN, hasActiveSCBlock), 2);
								world.markBlockRangeForRenderUpdate(pos.north(), pos.north());
							}
							else
							{
								secondDoorState = world.getBlockState(pos.south());

								if(secondDoorState != null && secondDoorState.getBlock() == SCContent.reinforcedDoor && secondDoorState.getValue(OPEN).booleanValue() != hasActiveSCBlock)
								{
									world.setBlockState(pos.south(), secondDoorState.withProperty(OPEN, hasActiveSCBlock), 2);
									world.markBlockRangeForRenderUpdate(pos.south(), pos.south());
								}
							}
						}
						else if(state.getValue(FACING) == EnumFacing.NORTH)
						{
							secondDoorState = world.getBlockState(pos.east());

							if(secondDoorState != null && secondDoorState.getBlock() == SCContent.reinforcedDoor && secondDoorState.getValue(OPEN).booleanValue() != hasActiveSCBlock)
							{
								world.setBlockState(pos.east(), secondDoorState.withProperty(OPEN, hasActiveSCBlock), 2);
								world.markBlockRangeForRenderUpdate(pos.east(), pos.east());
							}
							else
							{
								secondDoorState = world.getBlockState(pos.west());

								if(secondDoorState != null && secondDoorState.getBlock() == SCContent.reinforcedDoor && secondDoorState.getValue(OPEN).booleanValue() != hasActiveSCBlock)
								{
									world.setBlockState(pos.west(), secondDoorState.withProperty(OPEN, hasActiveSCBlock), 2);
									world.markBlockRangeForRenderUpdate(pos.west(), pos.west());
								}
							}
						}
						else if(state.getValue(FACING) == EnumFacing.EAST)
						{
							secondDoorState = world.getBlockState(pos.south());

							if(secondDoorState != null && secondDoorState.getBlock() == SCContent.reinforcedDoor && secondDoorState.getValue(OPEN).booleanValue() != hasActiveSCBlock)
							{
								world.setBlockState(pos.south(), secondDoorState.withProperty(OPEN, hasActiveSCBlock), 2);
								world.markBlockRangeForRenderUpdate(pos.south(), pos.south());
							}
							else
							{
								secondDoorState = world.getBlockState(pos.north());

								if(secondDoorState != null && secondDoorState.getBlock() == SCContent.reinforcedDoor && secondDoorState.getValue(OPEN).booleanValue() != hasActiveSCBlock)
								{
									world.setBlockState(pos.north(), secondDoorState.withProperty(OPEN, hasActiveSCBlock), 2);
									world.markBlockRangeForRenderUpdate(pos.north(), pos.north());
								}
							}
						}
						else if(state.getValue(FACING) == EnumFacing.SOUTH)
						{
							secondDoorState = world.getBlockState(pos.west());

							if(secondDoorState != null && secondDoorState.getBlock() == SCContent.reinforcedDoor && secondDoorState.getValue(OPEN).booleanValue() != hasActiveSCBlock)
							{
								world.setBlockState(pos.west(), secondDoorState.withProperty(OPEN, hasActiveSCBlock), 2);
								world.markBlockRangeForRenderUpdate(pos.west(), pos.west());
							}
							else
							{
								secondDoorState = world.getBlockState(pos.east());

								if(secondDoorState != null && secondDoorState.getBlock() == SCContent.reinforcedDoor && secondDoorState.getValue(OPEN).booleanValue() != hasActiveSCBlock)
								{
									world.setBlockState(pos.east(), secondDoorState.withProperty(OPEN, hasActiveSCBlock), 2);
									world.markBlockRangeForRenderUpdate(pos.east(), pos.east());
								}
							}
						}

						world.playAuxSFXAtEntity((EntityPlayer)null, hasActiveSCBlock ? 1003 : 1006, pos, 0);
					}
				}
			}
		}
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
	@SideOnly(Side.CLIENT)
	public Item getItem(World world, BlockPos pos){
		return SCContent.reinforcedDoorItem;
	}

	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune){
		return state.getValue(HALF) == BlockDoor.EnumDoorHalf.UPPER ? null : SCContent.reinforcedDoorItem;
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileEntityOwnable();
	}
}