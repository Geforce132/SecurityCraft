package net.geforcemods.securitycraft.blocks;

import java.util.Random;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.tileentity.TileEntityScannerDoor;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockScannerDoor extends BlockDoor implements ITileEntityProvider
{
	public BlockScannerDoor(Material material)
	{
		super(material);
		isBlockContainer = true;
	}

	/**
	 * Lets the block know when one of its neighbor changes. Doesn't know which neighbor changed (coordinates passed are
	 * their own) Args: x, y, z, neighbor Block
	 */
	@Override
	public void onNeighborBlockChange(World world, BlockPos pos, IBlockState state, Block neighborBlock)
	{
		if(state.getValue(HALF) == BlockDoor.EnumDoorHalf.UPPER)
		{
			BlockPos blockBelow = pos.down();
			IBlockState stateBelow = world.getBlockState(blockBelow);

			if(stateBelow.getBlock() != this)
				world.setBlockToAir(pos);
			else if (neighborBlock != this)
				onNeighborBlockChange(world, blockBelow, stateBelow, neighborBlock);
		}
		else
		{
			boolean isNotDoor = false;
			BlockPos blockAbove = pos.up();
			IBlockState stateAbove = world.getBlockState(blockAbove);

			if(stateAbove.getBlock() != this)
			{
				world.setBlockToAir(pos);
				isNotDoor = true;
			}

			if(!World.doesBlockHaveSolidTopSurface(world, pos.down()))
			{
				world.setBlockToAir(pos);
				isNotDoor = true;

				if(stateAbove.getBlock() == this)
					world.setBlockToAir(blockAbove);
			}

			if(isNotDoor)
				if(!world.isRemote)
					dropBlockAsItem(world, pos, state, 0);
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
	public Item getItem(World world, BlockPos pos)
	{
		return SCContent.scannerDoorItem;
	}

	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune)
	{
		return state.getValue(HALF) == BlockDoor.EnumDoorHalf.UPPER ? null : SCContent.scannerDoorItem;
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta)
	{
		return new TileEntityScannerDoor().activatedByView();
	}
}
