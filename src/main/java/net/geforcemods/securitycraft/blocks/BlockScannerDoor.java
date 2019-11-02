package net.geforcemods.securitycraft.blocks;

import java.util.Random;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.tileentity.TileEntityScannerDoor;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockScannerDoor extends BlockDoor implements ITileEntityProvider
{
	public BlockScannerDoor(Material material)
	{
		super(material);
		isBlockContainer = true;
		setSoundType(SoundType.METAL);
	}

	@Override
	public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block)
	{
		//leave this here so the parent code does not get executed
	}

	@Override
	public void onNeighborChange(IBlockAccess access, BlockPos pos, BlockPos neighbor)
	{
		World world = (World)access;
		IBlockState state = world.getBlockState(pos);
		Block neighborBlock = world.getBlockState(neighbor).getBlock();

		if(state.getValue(HALF) == BlockDoor.EnumDoorHalf.UPPER)
		{
			BlockPos blockBelow = pos.down();
			IBlockState stateBelow = world.getBlockState(blockBelow);

			if(stateBelow.getBlock() != this)
				world.setBlockToAir(pos);
			else if (neighborBlock != this)
				onNeighborChange(world, blockBelow, neighbor);
		}
		else
		{
			boolean drop = false;
			BlockPos blockAbove = pos.up();
			IBlockState stateAbove = world.getBlockState(blockAbove);

			if(stateAbove.getBlock() != this)
			{
				world.setBlockToAir(pos);
				drop = true;
			}

			if(!world.isSideSolid(pos.down(), EnumFacing.UP))
			{
				world.setBlockToAir(pos);
				drop = true;

				if(stateAbove.getBlock() == this)
					world.setBlockToAir(blockAbove);
			}

			if(drop)
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
	public boolean eventReceived(IBlockState state, World world, BlockPos pos, int id, int param)
	{
		super.eventReceived(state, world, pos, id, param);

		TileEntity tileentity = world.getTileEntity(pos);

		return tileentity == null ? false : tileentity.receiveClientEvent(id, param);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public ItemStack getItem(World world, BlockPos pos, IBlockState state)
	{
		return new ItemStack(SCContent.scannerDoorItem);
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
