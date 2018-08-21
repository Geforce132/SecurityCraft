package net.geforcemods.securitycraft.blocks.reinforced;

import java.util.Random;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.blocks.BlockInventoryScanner;
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
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block neighborBlock)
	{
		if(state.getValue(HALF) == BlockDoor.EnumDoorHalf.UPPER)
		{
			boolean isNotPowered = hasNoActiveSCBlocksNear(worldIn, pos) && hasNoActiveSCBlocksNear(worldIn, pos.down());

			if(isNotPowered)
			{
				closeDoor(worldIn, pos.down());
				return;
			}

			BlockPos neighborPos = getNeighboringActiveSCBlock(worldIn, pos.down(), neighborBlock);

			if(neighborPos != null)
				onNeighborChange(worldIn, pos.down(), neighborPos);
			else
			{
				neighborPos = getNeighboringActiveSCBlock(worldIn, pos, neighborBlock);

				if(neighborPos != null)
					onNeighborChange(worldIn, pos, neighborPos);
			}
		}
		else
		{
			boolean isNotPowered = hasNoActiveSCBlocksNear(worldIn, pos) && hasNoActiveSCBlocksNear(worldIn, pos.up());

			if(isNotPowered)
			{
				closeDoor(worldIn, pos);
				return;
			}

			BlockPos neighborPos = getNeighboringActiveSCBlock(worldIn, pos, neighborBlock);

			if(neighborPos != null)
				onNeighborChange(worldIn, pos, neighborPos);
			else
			{
				neighborPos = getNeighboringActiveSCBlock(worldIn, pos.up(), neighborBlock);

				if(neighborPos != null)
					onNeighborChange(worldIn, pos.up(), neighborPos);
			}
		}
	}

	private void closeDoor(World world, BlockPos pos)
	{
		IBlockState state = world.getBlockState(pos);

		if(!state.getValue(OPEN))
			return;

		if (state.getValue(HALF) == BlockDoor.EnumDoorHalf.UPPER)
			pos = pos.down();

		world.setBlockState(pos, state.withProperty(OPEN, Boolean.valueOf(false)).withProperty(POWERED, Boolean.valueOf(false)), 2);
		world.markBlockRangeForRenderUpdate(pos, pos);
		checkAndChangeDoubleDoors(world, pos, state, false);
		world.playEvent((EntityPlayer)null, 1011, pos, 0);
	}

	@Override
	public void onNeighborChange(IBlockAccess world, BlockPos pos, BlockPos neighbor)
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
				onNeighborChange(world, blockpos1, neighbor);
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
				boolean hasActiveSCBlock = hasActiveKeypadNextTo(worldIn, pos) || hasActiveKeypadNextTo(worldIn, pos.up()) || hasActiveInventoryScannerNextTo(worldIn, pos) || hasActiveInventoryScannerNextTo(worldIn, pos.up()) || hasActiveReaderNextTo(worldIn, pos) || hasActiveReaderNextTo(worldIn, pos.up()) || hasActiveScannerNextTo(worldIn, pos) || hasActiveScannerNextTo(worldIn, pos.up()) || hasActiveLaserNextTo(worldIn, pos) || hasActiveLaserNextTo(worldIn, pos.up());

				//                if(flag && !(hasActiveKeypadNextTo(worldIn, pos) || hasActiveKeypadNextTo(worldIn, pos.up()) || hasActiveInventoryScannerNextTo(worldIn, pos) || hasActiveInventoryScannerNextTo(worldIn, pos.up()) || hasActiveReaderNextTo(worldIn, pos) || hasActiveReaderNextTo(worldIn, pos.up()) || hasActiveScannerNextTo(worldIn, pos) || hasActiveScannerNextTo(worldIn, pos.up()) || hasActiveLaserNextTo(worldIn, pos) || hasActiveLaserNextTo(worldIn, pos.up()) && neighborBlock != this)){
				//                	System.out.println("Powered by vanilla block");
				//                }else if(hasActiveKeypadNextTo(worldIn, pos) || hasActiveKeypadNextTo(worldIn, pos.up()) || hasActiveInventoryScannerNextTo(worldIn, pos) || hasActiveInventoryScannerNextTo(worldIn, pos.up()) || hasActiveReaderNextTo(worldIn, pos) || hasActiveReaderNextTo(worldIn, pos.up()) || hasActiveScannerNextTo(worldIn, pos) || hasActiveScannerNextTo(worldIn, pos.up()) || hasActiveLaserNextTo(worldIn, pos) || hasActiveLaserNextTo(worldIn, pos.up()) && neighborBlock != this){
				//                	System.out.println("Powered by SC block");
				//                }

				if (((hasActiveSCBlock || neighborBlock.canProvidePower(iblockstate2))) && neighborBlock != this && hasActiveSCBlock != iblockstate2.getValue(POWERED).booleanValue())
					if (hasActiveSCBlock != state.getValue(OPEN).booleanValue())
					{
						worldIn.setBlockState(pos, state.withProperty(OPEN, Boolean.valueOf(hasActiveSCBlock)).withProperty(POWERED, Boolean.valueOf(hasActiveSCBlock)), 2);
						worldIn.markBlockRangeForRenderUpdate(pos, pos);
						checkAndChangeDoubleDoors(worldIn, pos, state, hasActiveSCBlock);
						worldIn.playEvent((EntityPlayer)null, hasActiveSCBlock ? 1005 : 1011, pos, 0);
					}
			}
		}
	}

	public void checkAndChangeDoubleDoors(World world, BlockPos pos, IBlockState state, boolean open)
	{
		IBlockState secondDoorState;

		if(state.getValue(FACING) == EnumFacing.WEST)
		{
			secondDoorState = world.getBlockState(pos.north());

			if(secondDoorState != null && secondDoorState.getBlock() == SCContent.reinforcedDoor && secondDoorState.getValue(OPEN).booleanValue() != open)
			{
				world.setBlockState(pos.north(), secondDoorState.withProperty(OPEN, open), 2);
				world.markBlockRangeForRenderUpdate(pos.north(), pos.north());
			}
			else
			{
				secondDoorState = world.getBlockState(pos.south());

				if(secondDoorState != null && secondDoorState.getBlock() == SCContent.reinforcedDoor && secondDoorState.getValue(OPEN).booleanValue() != open)
				{
					world.setBlockState(pos.south(), secondDoorState.withProperty(OPEN, open), 2);
					world.markBlockRangeForRenderUpdate(pos.south(), pos.south());
				}
			}
		}
		else if(state.getValue(FACING) == EnumFacing.NORTH)
		{
			secondDoorState = world.getBlockState(pos.east());

			if(secondDoorState != null && secondDoorState.getBlock() == SCContent.reinforcedDoor && secondDoorState.getValue(OPEN).booleanValue() != open)
			{
				world.setBlockState(pos.east(), secondDoorState.withProperty(OPEN, open), 2);
				world.markBlockRangeForRenderUpdate(pos.east(), pos.east());
			}
			else
			{
				secondDoorState = world.getBlockState(pos.west());

				if(secondDoorState != null && secondDoorState.getBlock() == SCContent.reinforcedDoor && secondDoorState.getValue(OPEN).booleanValue() != open)
				{
					world.setBlockState(pos.west(), secondDoorState.withProperty(OPEN, open), 2);
					world.markBlockRangeForRenderUpdate(pos.west(), pos.west());
				}
			}
		}
		else if(state.getValue(FACING) == EnumFacing.EAST)
		{
			secondDoorState = world.getBlockState(pos.south());

			if(secondDoorState != null && secondDoorState.getBlock() == SCContent.reinforcedDoor && secondDoorState.getValue(OPEN).booleanValue() != open)
			{
				world.setBlockState(pos.south(), secondDoorState.withProperty(OPEN, open), 2);
				world.markBlockRangeForRenderUpdate(pos.south(), pos.south());
			}
			else
			{
				secondDoorState = world.getBlockState(pos.north());

				if(secondDoorState != null && secondDoorState.getBlock() == SCContent.reinforcedDoor && secondDoorState.getValue(OPEN).booleanValue() != open)
				{
					world.setBlockState(pos.north(), secondDoorState.withProperty(OPEN, open), 2);
					world.markBlockRangeForRenderUpdate(pos.north(), pos.north());
				}
			}
		}
		else if(state.getValue(FACING) == EnumFacing.SOUTH)
		{
			secondDoorState = world.getBlockState(pos.west());

			if(secondDoorState != null && secondDoorState.getBlock() == SCContent.reinforcedDoor && secondDoorState.getValue(OPEN).booleanValue() != open)
			{
				world.setBlockState(pos.west(), secondDoorState.withProperty(OPEN, open), 2);
				world.markBlockRangeForRenderUpdate(pos.west(), pos.west());
			}
			else
			{
				secondDoorState = world.getBlockState(pos.east());

				if(secondDoorState != null && secondDoorState.getBlock() == SCContent.reinforcedDoor && secondDoorState.getValue(OPEN).booleanValue() != open)
				{
					world.setBlockState(pos.east(), secondDoorState.withProperty(OPEN, open), 2);
					world.markBlockRangeForRenderUpdate(pos.east(), pos.east());
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

	private BlockPos getNeighboringActiveSCBlock(World world, BlockPos pos, Block neighbor)
	{
		if(neighbor instanceof BlockLaserBlock && hasActiveLaserNextTo(world, pos))
		{
			if(BlockUtils.getBlock(world, pos.east()) == SCContent.laserBlock && ((Boolean) BlockUtils.getBlockPropertyAsBoolean(world, pos.east(), BlockLaserBlock.POWERED)).booleanValue())
				return pos.east();
			else if(BlockUtils.getBlock(world, pos.west()) == SCContent.laserBlock && ((Boolean) BlockUtils.getBlockPropertyAsBoolean(world, pos.west(), BlockLaserBlock.POWERED)).booleanValue())
				return pos.west();
			else if(BlockUtils.getBlock(world, pos.south()) == SCContent.laserBlock && ((Boolean) BlockUtils.getBlockPropertyAsBoolean(world, pos.south(), BlockLaserBlock.POWERED)).booleanValue())
				return pos.south();
			else if(BlockUtils.getBlock(world, pos.north()) == SCContent.laserBlock && ((Boolean) BlockUtils.getBlockPropertyAsBoolean(world, pos.north(), BlockLaserBlock.POWERED)).booleanValue())
				return pos.north();
		}
		else if(neighbor instanceof BlockRetinalScanner && hasActiveScannerNextTo(world, pos))
		{
			if(BlockUtils.getBlock(world, pos.east()) == SCContent.retinalScanner && ((Boolean) BlockUtils.getBlockPropertyAsBoolean(world, pos.east(), BlockRetinalScanner.POWERED)).booleanValue())
				return pos.east();
			else if(BlockUtils.getBlock(world, pos.west()) == SCContent.retinalScanner && ((Boolean) BlockUtils.getBlockPropertyAsBoolean(world, pos.west(), BlockRetinalScanner.POWERED)).booleanValue())
				return pos.west();
			else if(BlockUtils.getBlock(world, pos.south()) == SCContent.retinalScanner && ((Boolean) BlockUtils.getBlockPropertyAsBoolean(world, pos.south(), BlockRetinalScanner.POWERED)).booleanValue())
				return pos.south();
			else if(BlockUtils.getBlock(world, pos.north()) == SCContent.retinalScanner && ((Boolean) BlockUtils.getBlockPropertyAsBoolean(world, pos.north(), BlockRetinalScanner.POWERED)).booleanValue())
				return pos.north();
		}
		else if(neighbor instanceof BlockKeypad && hasActiveKeypadNextTo(world, pos))
		{
			if(BlockUtils.getBlock(world, pos.east()) == SCContent.keypad && ((Boolean) BlockUtils.getBlockPropertyAsBoolean(world, pos.east(), BlockKeypad.POWERED)).booleanValue())
				return pos.east();
			else if(BlockUtils.getBlock(world, pos.west()) == SCContent.keypad && ((Boolean) BlockUtils.getBlockPropertyAsBoolean(world, pos.west(), BlockKeypad.POWERED)).booleanValue())
				return pos.west();
			else if(BlockUtils.getBlock(world, pos.south()) == SCContent.keypad && ((Boolean) BlockUtils.getBlockPropertyAsBoolean(world, pos.south(), BlockKeypad.POWERED)).booleanValue())
				return pos.south();
			else if(BlockUtils.getBlock(world, pos.north()) == SCContent.keypad && ((Boolean) BlockUtils.getBlockPropertyAsBoolean(world, pos.north(), BlockKeypad.POWERED)).booleanValue())
				return pos.north();
		}
		else if(neighbor instanceof BlockKeycardReader && hasActiveReaderNextTo(world, pos))
		{
			if(BlockUtils.getBlock(world, pos.east()) == SCContent.keycardReader && ((Boolean) BlockUtils.getBlockPropertyAsBoolean(world, pos.east(), BlockKeycardReader.POWERED)).booleanValue())
				return pos.east();
			else if(BlockUtils.getBlock(world, pos.west()) == SCContent.keycardReader && ((Boolean) BlockUtils.getBlockPropertyAsBoolean(world, pos.west(), BlockKeycardReader.POWERED)).booleanValue())
				return pos.west();
			else if(BlockUtils.getBlock(world, pos.south()) == SCContent.keycardReader && ((Boolean) BlockUtils.getBlockPropertyAsBoolean(world, pos.south(), BlockKeycardReader.POWERED)).booleanValue())
				return pos.south();
			else if(BlockUtils.getBlock(world, pos.north()) == SCContent.keycardReader && ((Boolean) BlockUtils.getBlockPropertyAsBoolean(world, pos.north(), BlockKeycardReader.POWERED)).booleanValue())
				return pos.north();
		}
		else if(neighbor instanceof BlockInventoryScanner && hasActiveInventoryScannerNextTo(world, pos))
			if(BlockUtils.getBlock(world, pos.east()) == SCContent.inventoryScanner && ((TileEntityInventoryScanner) world.getTileEntity(pos.east())).getType().matches("redstone") && ((TileEntityInventoryScanner) world.getTileEntity(pos.east())).shouldProvidePower())
				return pos.east();
			else if(BlockUtils.getBlock(world, pos.west()) == SCContent.inventoryScanner && ((TileEntityInventoryScanner) world.getTileEntity(pos.west())).getType().matches("redstone") && ((TileEntityInventoryScanner) world.getTileEntity(pos.west())).shouldProvidePower())
				return pos.west();
			else if(BlockUtils.getBlock(world, pos.south()) == SCContent.inventoryScanner && ((TileEntityInventoryScanner) world.getTileEntity(pos.south())).getType().matches("redstone") && ((TileEntityInventoryScanner) world.getTileEntity(pos.south())).shouldProvidePower())
				return pos.south();
			else if(BlockUtils.getBlock(world, pos.north()) == SCContent.inventoryScanner && ((TileEntityInventoryScanner) world.getTileEntity(pos.north())).getType().matches("redstone") && ((TileEntityInventoryScanner) world.getTileEntity(pos.north())).shouldProvidePower())
				return pos.north();

		return null;
	}

	private boolean hasNoActiveSCBlocksNear(World world, BlockPos pos)
	{
		return !hasActiveLaserNextTo(world, pos) && !hasActiveScannerNextTo(world, pos) && !hasActiveKeypadNextTo(world, pos) &&
				!hasActiveReaderNextTo(world, pos) && !hasActiveInventoryScannerNextTo(world, pos);
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