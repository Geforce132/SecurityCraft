package net.geforcemods.securitycraft.blocks.reinforced;

import java.util.Random;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.blocks.BlockKeycardReader;
import net.geforcemods.securitycraft.blocks.BlockKeypad;
import net.geforcemods.securitycraft.blocks.BlockLaserBlock;
import net.geforcemods.securitycraft.blocks.BlockRetinalScanner;
import net.geforcemods.securitycraft.tileentity.TileEntityInventoryScanner;
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

	public BlockReinforcedDoor(Material materialIn) {
		super(materialIn);
		isBlockContainer = true;
	}

	/**
	 * Lets the block know when one of its neighbor changes. Doesn't know which neighbor changed (coordinates passed are
	 * their own) Args: x, y, z, neighbor Block
	 */
	@Override
	public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock){
		if (state.getValue(HALF) == BlockDoor.EnumDoorHalf.UPPER)
		{
			BlockPos blockpos1 = pos.down();
			IBlockState iblockstate1 = worldIn.getBlockState(blockpos1);

			if (iblockstate1.getBlock() != this)
				worldIn.setBlockToAir(pos);
			else if (neighborBlock != this)
				onNeighborBlockChange(worldIn, blockpos1, iblockstate1, neighborBlock);
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

			if (!World.doesBlockHaveSolidTopSurface(worldIn, pos.down()))
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
				boolean hasActiveSCBlock = hasActiveKeypadNextTo(worldIn, pos) || hasActiveKeypadNextTo(worldIn, pos.up()) || hasActiveInventoryScannerNextTo(worldIn, pos) || hasActiveInventoryScannerNextTo(worldIn, pos.up()) || hasActiveReaderNextTo(worldIn, pos) || hasActiveReaderNextTo(worldIn, pos.up()) || hasActiveScannerNextTo(worldIn, pos) || hasActiveScannerNextTo(worldIn, pos.up()) || hasActiveLaserNextTo(worldIn, pos) || hasActiveLaserNextTo(worldIn, pos.up());

				//                if(flag && !(hasActiveKeypadNextTo(worldIn, pos) || hasActiveKeypadNextTo(worldIn, pos.up()) || hasActiveInventoryScannerNextTo(worldIn, pos) || hasActiveInventoryScannerNextTo(worldIn, pos.up()) || hasActiveReaderNextTo(worldIn, pos) || hasActiveReaderNextTo(worldIn, pos.up()) || hasActiveScannerNextTo(worldIn, pos) || hasActiveScannerNextTo(worldIn, pos.up()) || hasActiveLaserNextTo(worldIn, pos) || hasActiveLaserNextTo(worldIn, pos.up()) && neighborBlock != this)){
				//                	System.out.println("Powered by vanilla block");
				//                }else if(hasActiveKeypadNextTo(worldIn, pos) || hasActiveKeypadNextTo(worldIn, pos.up()) || hasActiveInventoryScannerNextTo(worldIn, pos) || hasActiveInventoryScannerNextTo(worldIn, pos.up()) || hasActiveReaderNextTo(worldIn, pos) || hasActiveReaderNextTo(worldIn, pos.up()) || hasActiveScannerNextTo(worldIn, pos) || hasActiveScannerNextTo(worldIn, pos.up()) || hasActiveLaserNextTo(worldIn, pos) || hasActiveLaserNextTo(worldIn, pos.up()) && neighborBlock != this){
				//                	System.out.println("Powered by SC block");
				//                }

				if (((hasActiveSCBlock || neighborBlock.canProvidePower())) && neighborBlock != this && hasActiveSCBlock != iblockstate2.getValue(POWERED).booleanValue())
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

						worldIn.playAuxSFXAtEntity((EntityPlayer)null, hasActiveSCBlock ? 1003 : 1006, pos, 0);
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
	public boolean onBlockEventReceived(World worldIn, BlockPos pos, IBlockState state, int eventID, int eventParam)
	{
		super.onBlockEventReceived(worldIn, pos, state, eventID, eventParam);
		TileEntity tileentity = worldIn.getTileEntity(pos);
		return tileentity == null ? false : tileentity.receiveClientEvent(eventID, eventParam);
	}

	private boolean hasActiveLaserNextTo(World par1World, BlockPos pos) {
		if(BlockUtils.getBlock(par1World, pos.east()) == SCContent.laserBlock && ((Boolean) BlockUtils.getBlockPropertyAsBoolean(par1World, pos.east(), BlockLaserBlock.POWERED)).booleanValue())
			return ((IOwnable) par1World.getTileEntity(pos.east())).getOwner().owns((IOwnable) par1World.getTileEntity(pos));
		else if(BlockUtils.getBlock(par1World, pos.west()) == SCContent.laserBlock && ((Boolean) BlockUtils.getBlockPropertyAsBoolean(par1World, pos.west(), BlockLaserBlock.POWERED)).booleanValue())
			return ((IOwnable) par1World.getTileEntity(pos.west())).getOwner().owns((IOwnable) par1World.getTileEntity(pos));
		else if(BlockUtils.getBlock(par1World, pos.south()) == SCContent.laserBlock && ((Boolean) BlockUtils.getBlockPropertyAsBoolean(par1World, pos.south(), BlockLaserBlock.POWERED)).booleanValue())
			return ((IOwnable) par1World.getTileEntity(pos.south())).getOwner().owns((IOwnable) par1World.getTileEntity(pos));
		else if(BlockUtils.getBlock(par1World, pos.north()) == SCContent.laserBlock && ((Boolean) BlockUtils.getBlockPropertyAsBoolean(par1World, pos.north(), BlockLaserBlock.POWERED)).booleanValue())
			return ((IOwnable) par1World.getTileEntity(pos.north())).getOwner().owns((IOwnable) par1World.getTileEntity(pos));
		else
			return false;
	}

	private boolean hasActiveScannerNextTo(World par1World, BlockPos pos) {
		if(BlockUtils.getBlock(par1World, pos.east()) == SCContent.retinalScanner && ((Boolean) BlockUtils.getBlockPropertyAsBoolean(par1World, pos.east(), BlockRetinalScanner.POWERED)).booleanValue())
			return ((IOwnable) par1World.getTileEntity(pos.east())).getOwner().owns((IOwnable) par1World.getTileEntity(pos));
		else if(BlockUtils.getBlock(par1World, pos.west()) == SCContent.retinalScanner && ((Boolean) BlockUtils.getBlockPropertyAsBoolean(par1World, pos.west(), BlockRetinalScanner.POWERED)).booleanValue())
			return ((IOwnable) par1World.getTileEntity(pos.west())).getOwner().owns((IOwnable) par1World.getTileEntity(pos));
		else if(BlockUtils.getBlock(par1World, pos.south()) == SCContent.retinalScanner && ((Boolean) BlockUtils.getBlockPropertyAsBoolean(par1World, pos.south(), BlockRetinalScanner.POWERED)).booleanValue())
			return ((IOwnable) par1World.getTileEntity(pos.south())).getOwner().owns((IOwnable) par1World.getTileEntity(pos));
		else if(BlockUtils.getBlock(par1World, pos.north()) == SCContent.retinalScanner && ((Boolean) BlockUtils.getBlockPropertyAsBoolean(par1World, pos.north(), BlockRetinalScanner.POWERED)).booleanValue())
			return ((IOwnable) par1World.getTileEntity(pos.north())).getOwner().owns((IOwnable) par1World.getTileEntity(pos));
		else
			return false;
	}

	private boolean hasActiveKeypadNextTo(World par1World, BlockPos pos){
		if(BlockUtils.getBlock(par1World, pos.east()) == SCContent.keypad && ((Boolean) BlockUtils.getBlockPropertyAsBoolean(par1World, pos.east(), BlockKeypad.POWERED)).booleanValue())
			return ((IOwnable) par1World.getTileEntity(pos.east())).getOwner().owns((IOwnable) par1World.getTileEntity(pos));
		else if(BlockUtils.getBlock(par1World, pos.west()) == SCContent.keypad && ((Boolean) BlockUtils.getBlockPropertyAsBoolean(par1World, pos.west(), BlockKeypad.POWERED)).booleanValue())
			return ((IOwnable) par1World.getTileEntity(pos.west())).getOwner().owns((IOwnable) par1World.getTileEntity(pos));
		else if(BlockUtils.getBlock(par1World, pos.south()) == SCContent.keypad && ((Boolean) BlockUtils.getBlockPropertyAsBoolean(par1World, pos.south(), BlockKeypad.POWERED)).booleanValue())
			return ((IOwnable) par1World.getTileEntity(pos.south())).getOwner().owns((IOwnable) par1World.getTileEntity(pos));
		else if(BlockUtils.getBlock(par1World, pos.north()) == SCContent.keypad && ((Boolean) BlockUtils.getBlockPropertyAsBoolean(par1World, pos.north(), BlockKeypad.POWERED)).booleanValue())
			return ((IOwnable) par1World.getTileEntity(pos.north())).getOwner().owns((IOwnable) par1World.getTileEntity(pos));
		else
			return false;
	}

	private boolean hasActiveReaderNextTo(World par1World, BlockPos pos){
		if(BlockUtils.getBlock(par1World, pos.east()) == SCContent.keycardReader && BlockUtils.getBlockPropertyAsBoolean(par1World, pos.east(), BlockKeycardReader.POWERED))
			return ((IOwnable) par1World.getTileEntity(pos.east())).getOwner().owns((IOwnable) par1World.getTileEntity(pos));
		else if(BlockUtils.getBlock(par1World, pos.west()) == SCContent.keycardReader && BlockUtils.getBlockPropertyAsBoolean(par1World, pos.west(), BlockKeycardReader.POWERED))
			return ((IOwnable) par1World.getTileEntity(pos.west())).getOwner().owns((IOwnable) par1World.getTileEntity(pos));
		else if(BlockUtils.getBlock(par1World, pos.south()) == SCContent.keycardReader && BlockUtils.getBlockPropertyAsBoolean(par1World, pos.south(), BlockKeycardReader.POWERED))
			return ((IOwnable) par1World.getTileEntity(pos.south())).getOwner().owns((IOwnable) par1World.getTileEntity(pos));
		else if(BlockUtils.getBlock(par1World, pos.north()) == SCContent.keycardReader && BlockUtils.getBlockPropertyAsBoolean(par1World, pos.north(), BlockKeycardReader.POWERED))
			return ((IOwnable) par1World.getTileEntity(pos.north())).getOwner().owns((IOwnable) par1World.getTileEntity(pos));
		else
			return false;
	}

	private boolean hasActiveInventoryScannerNextTo(World par1World, BlockPos pos){
		if(BlockUtils.getBlock(par1World, pos.east()) == SCContent.inventoryScanner && ((TileEntityInventoryScanner) par1World.getTileEntity(pos.east())).getType().matches("redstone") && ((TileEntityInventoryScanner) par1World.getTileEntity(pos.east())).shouldProvidePower())
			return ((IOwnable) par1World.getTileEntity(pos.east())).getOwner().owns((IOwnable) par1World.getTileEntity(pos));
		else if(BlockUtils.getBlock(par1World, pos.west()) == SCContent.inventoryScanner && ((TileEntityInventoryScanner) par1World.getTileEntity(pos.west())).getType().matches("redstone") && ((TileEntityInventoryScanner) par1World.getTileEntity(pos.west())).shouldProvidePower())
			return ((IOwnable) par1World.getTileEntity(pos.west())).getOwner().owns((IOwnable) par1World.getTileEntity(pos));
		else if(BlockUtils.getBlock(par1World, pos.south()) == SCContent.inventoryScanner && ((TileEntityInventoryScanner) par1World.getTileEntity(pos.south())).getType().matches("redstone") && ((TileEntityInventoryScanner) par1World.getTileEntity(pos.south())).shouldProvidePower())
			return ((IOwnable) par1World.getTileEntity(pos.south())).getOwner().owns((IOwnable) par1World.getTileEntity(pos));
		else if(BlockUtils.getBlock(par1World, pos.north()) == SCContent.inventoryScanner && ((TileEntityInventoryScanner) par1World.getTileEntity(pos.north())).getType().matches("redstone") && ((TileEntityInventoryScanner) par1World.getTileEntity(pos.north())).shouldProvidePower())
			return ((IOwnable) par1World.getTileEntity(pos.north())).getOwner().owns((IOwnable) par1World.getTileEntity(pos));
		else
			return false;
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
	public TileEntity createNewTileEntity(World var1, int var2) {
		return new TileEntityOwnable();
	}
}