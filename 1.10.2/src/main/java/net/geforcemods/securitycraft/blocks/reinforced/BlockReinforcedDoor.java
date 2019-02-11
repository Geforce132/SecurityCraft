package net.geforcemods.securitycraft.blocks.reinforced;

import java.util.Random;

import net.geforcemods.securitycraft.SCContent;
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

	public BlockReinforcedDoor(Material material) {
		super(material);
		isBlockContainer = true;
		setSoundType(SoundType.METAL);
	}

	@Override
	public void neighborChanged(IBlockState state, World world, BlockPos pos, Block neighborBlock)
	{
		if(state.getValue(HALF) == BlockDoor.EnumDoorHalf.UPPER)
		{
			boolean isNotPowered = hasNoActiveSCBlocksNear(world, pos) && hasNoActiveSCBlocksNear(world, pos.down());

			if(isNotPowered)
			{
				closeDoor(world, pos.down());
				return;
			}

			BlockPos neighborPos = getNeighboringActiveSCBlock(world, pos.down(), neighborBlock);

			if(neighborPos != null)
				onNeighborChange(world, pos.down(), neighborPos);
			else
			{
				neighborPos = getNeighboringActiveSCBlock(world, pos, neighborBlock);

				if(neighborPos != null)
					onNeighborChange(world, pos, neighborPos);
			}
		}
		else
		{
			boolean isNotPowered = hasNoActiveSCBlocksNear(world, pos) && hasNoActiveSCBlocksNear(world, pos.up());

			if(isNotPowered)
			{
				closeDoor(world, pos);
				return;
			}

			BlockPos neighborPos = getNeighboringActiveSCBlock(world, pos, neighborBlock);

			if(neighborPos != null)
				onNeighborChange(world, pos, neighborPos);
			else
			{
				neighborPos = getNeighboringActiveSCBlock(world, pos.up(), neighborBlock);

				if(neighborPos != null)
					onNeighborChange(world, pos.up(), neighborPos);
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
	public void onNeighborChange(IBlockAccess access, BlockPos pos, BlockPos neighbor)
	{
		World world = (World)access;
		IBlockState state = world.getBlockState(pos);
		Block neighborBlock = world.getBlockState(neighbor).getBlock();

		if (state.getValue(HALF) == BlockDoor.EnumDoorHalf.UPPER)
		{
			BlockPos blockBelow = pos.down();
			IBlockState stateBelow = world.getBlockState(blockBelow);

			if (stateBelow.getBlock() != this)
				world.setBlockToAir(pos);
			else if (neighborBlock != this)
				onNeighborChange(world, blockBelow, neighbor);
		}
		else
		{
			boolean drop = false;
			BlockPos blockAbove = pos.up();
			IBlockState stateAbove = world.getBlockState(blockAbove);

			if (stateAbove.getBlock() != this)
			{
				world.setBlockToAir(pos);
				drop = true;
			}

			if (!world.isSideSolid(pos.down(), EnumFacing.UP))
			{
				world.setBlockToAir(pos);
				drop = true;

				if (stateAbove.getBlock() == this)
					world.setBlockToAir(blockAbove);
			}

			if (drop)
			{
				if (!world.isRemote)
					dropBlockAsItem(world, pos, state, 0);
			}
			else
			{
				boolean hasActiveSCBlock = BlockUtils.hasActiveSCBlockNextTo(world, pos) || BlockUtils.hasActiveSCBlockNextTo(world, pos.up());

				if (((hasActiveSCBlock || neighborBlock.canProvidePower(stateAbove))) && neighborBlock != this && hasActiveSCBlock != stateAbove.getValue(POWERED).booleanValue())
					if (hasActiveSCBlock != state.getValue(OPEN).booleanValue())
					{
						world.setBlockState(pos, state.withProperty(OPEN, Boolean.valueOf(hasActiveSCBlock)).withProperty(POWERED, Boolean.valueOf(hasActiveSCBlock)), 2);
						world.markBlockRangeForRenderUpdate(pos, pos);
						checkAndChangeDoubleDoors(world, pos, state, hasActiveSCBlock);
						world.playEvent((EntityPlayer)null, hasActiveSCBlock ? 1005 : 1011, pos, 0);
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

	private BlockPos getNeighboringActiveSCBlock(World world, BlockPos pos, Block neighbor)
	{
		if(neighbor instanceof BlockLaserBlock && BlockUtils.hasActiveLaserNextTo(world, pos))
		{
			if(BlockUtils.getBlock(world, pos.east()) == SCContent.laserBlock && ((Boolean) BlockUtils.getBlockPropertyAsBoolean(world, pos.east(), BlockLaserBlock.POWERED)).booleanValue())
				return pos.east();
			else if(BlockUtils.getBlock(world, pos.west()) == SCContent.laserBlock && ((Boolean) BlockUtils.getBlockPropertyAsBoolean(world, pos.west(), BlockLaserBlock.POWERED)).booleanValue())
				return pos.west();
			else if(BlockUtils.getBlock(world, pos.south()) == SCContent.laserBlock && ((Boolean) BlockUtils.getBlockPropertyAsBoolean(world, pos.south(), BlockLaserBlock.POWERED)).booleanValue())
				return pos.south();
			else if(BlockUtils.getBlock(world, pos.north()) == SCContent.laserBlock && ((Boolean) BlockUtils.getBlockPropertyAsBoolean(world, pos.north(), BlockLaserBlock.POWERED)).booleanValue())
				return pos.north();
			else if(BlockUtils.getBlock(world, pos.up()) == SCContent.laserBlock && ((Boolean) BlockUtils.getBlockPropertyAsBoolean(world, pos.up(), BlockLaserBlock.POWERED)).booleanValue())
				return pos.up();
			else if(BlockUtils.getBlock(world, pos.down()) == SCContent.laserBlock && ((Boolean) BlockUtils.getBlockPropertyAsBoolean(world, pos.down(), BlockLaserBlock.POWERED)).booleanValue())
				return pos.down();
		}
		else if(neighbor instanceof BlockRetinalScanner && BlockUtils.hasActiveScannerNextTo(world, pos))
		{
			if(BlockUtils.getBlock(world, pos.east()) == SCContent.retinalScanner && ((Boolean) BlockUtils.getBlockPropertyAsBoolean(world, pos.east(), BlockRetinalScanner.POWERED)).booleanValue())
				return pos.east();
			else if(BlockUtils.getBlock(world, pos.west()) == SCContent.retinalScanner && ((Boolean) BlockUtils.getBlockPropertyAsBoolean(world, pos.west(), BlockRetinalScanner.POWERED)).booleanValue())
				return pos.west();
			else if(BlockUtils.getBlock(world, pos.south()) == SCContent.retinalScanner && ((Boolean) BlockUtils.getBlockPropertyAsBoolean(world, pos.south(), BlockRetinalScanner.POWERED)).booleanValue())
				return pos.south();
			else if(BlockUtils.getBlock(world, pos.north()) == SCContent.retinalScanner && ((Boolean) BlockUtils.getBlockPropertyAsBoolean(world, pos.north(), BlockRetinalScanner.POWERED)).booleanValue())
				return pos.north();
			else if(BlockUtils.getBlock(world, pos.up()) == SCContent.retinalScanner && ((Boolean) BlockUtils.getBlockPropertyAsBoolean(world, pos.up(), BlockRetinalScanner.POWERED)).booleanValue())
				return pos.up();
			else if(BlockUtils.getBlock(world, pos.down()) == SCContent.retinalScanner && ((Boolean) BlockUtils.getBlockPropertyAsBoolean(world, pos.down(), BlockRetinalScanner.POWERED)).booleanValue())
				return pos.down();
		}
		else if(neighbor instanceof BlockKeypad && BlockUtils.hasActiveKeypadNextTo(world, pos))
		{
			if(BlockUtils.getBlock(world, pos.east()) == SCContent.keypad && ((Boolean) BlockUtils.getBlockPropertyAsBoolean(world, pos.east(), BlockKeypad.POWERED)).booleanValue())
				return pos.east();
			else if(BlockUtils.getBlock(world, pos.west()) == SCContent.keypad && ((Boolean) BlockUtils.getBlockPropertyAsBoolean(world, pos.west(), BlockKeypad.POWERED)).booleanValue())
				return pos.west();
			else if(BlockUtils.getBlock(world, pos.south()) == SCContent.keypad && ((Boolean) BlockUtils.getBlockPropertyAsBoolean(world, pos.south(), BlockKeypad.POWERED)).booleanValue())
				return pos.south();
			else if(BlockUtils.getBlock(world, pos.north()) == SCContent.keypad && ((Boolean) BlockUtils.getBlockPropertyAsBoolean(world, pos.north(), BlockKeypad.POWERED)).booleanValue())
				return pos.north();
			else if(BlockUtils.getBlock(world, pos.up()) == SCContent.keypad && ((Boolean) BlockUtils.getBlockPropertyAsBoolean(world, pos.up(), BlockKeypad.POWERED)).booleanValue())
				return pos.up();
			else if(BlockUtils.getBlock(world, pos.down()) == SCContent.keypad && ((Boolean) BlockUtils.getBlockPropertyAsBoolean(world, pos.down(), BlockKeypad.POWERED)).booleanValue())
				return pos.down();
		}
		else if(neighbor instanceof BlockKeycardReader && BlockUtils.hasActiveReaderNextTo(world, pos))
		{
			if(BlockUtils.getBlock(world, pos.east()) == SCContent.keycardReader && ((Boolean) BlockUtils.getBlockPropertyAsBoolean(world, pos.east(), BlockKeycardReader.POWERED)).booleanValue())
				return pos.east();
			else if(BlockUtils.getBlock(world, pos.west()) == SCContent.keycardReader && ((Boolean) BlockUtils.getBlockPropertyAsBoolean(world, pos.west(), BlockKeycardReader.POWERED)).booleanValue())
				return pos.west();
			else if(BlockUtils.getBlock(world, pos.south()) == SCContent.keycardReader && ((Boolean) BlockUtils.getBlockPropertyAsBoolean(world, pos.south(), BlockKeycardReader.POWERED)).booleanValue())
				return pos.south();
			else if(BlockUtils.getBlock(world, pos.north()) == SCContent.keycardReader && ((Boolean) BlockUtils.getBlockPropertyAsBoolean(world, pos.north(), BlockKeycardReader.POWERED)).booleanValue())
				return pos.north();
			else if(BlockUtils.getBlock(world, pos.up()) == SCContent.keycardReader && ((Boolean) BlockUtils.getBlockPropertyAsBoolean(world, pos.up(), BlockKeycardReader.POWERED)).booleanValue())
				return pos.up();
			else if(BlockUtils.getBlock(world, pos.down()) == SCContent.keycardReader && ((Boolean) BlockUtils.getBlockPropertyAsBoolean(world, pos.down(), BlockKeycardReader.POWERED)).booleanValue())
				return pos.down();
		}
		else if(neighbor instanceof BlockInventoryScanner && BlockUtils.hasActiveInventoryScannerNextTo(world, pos))
		{
			if(BlockUtils.getBlock(world, pos.east()) == SCContent.inventoryScanner && ((TileEntityInventoryScanner) world.getTileEntity(pos.east())).getType().equals("redstone") && ((TileEntityInventoryScanner) world.getTileEntity(pos.east())).shouldProvidePower())
				return pos.east();
			else if(BlockUtils.getBlock(world, pos.west()) == SCContent.inventoryScanner && ((TileEntityInventoryScanner) world.getTileEntity(pos.west())).getType().equals("redstone") && ((TileEntityInventoryScanner) world.getTileEntity(pos.west())).shouldProvidePower())
				return pos.west();
			else if(BlockUtils.getBlock(world, pos.south()) == SCContent.inventoryScanner && ((TileEntityInventoryScanner) world.getTileEntity(pos.south())).getType().equals("redstone") && ((TileEntityInventoryScanner) world.getTileEntity(pos.south())).shouldProvidePower())
				return pos.south();
			else if(BlockUtils.getBlock(world, pos.north()) == SCContent.inventoryScanner && ((TileEntityInventoryScanner) world.getTileEntity(pos.north())).getType().equals("redstone") && ((TileEntityInventoryScanner) world.getTileEntity(pos.north())).shouldProvidePower())
				return pos.north();
			else if(BlockUtils.getBlock(world, pos.up()) == SCContent.inventoryScanner && ((TileEntityInventoryScanner) world.getTileEntity(pos.up())).getType().equals("redstone") && ((TileEntityInventoryScanner) world.getTileEntity(pos.up())).shouldProvidePower())
				return pos.up();
			else if(BlockUtils.getBlock(world, pos.down()) == SCContent.inventoryScanner && ((TileEntityInventoryScanner) world.getTileEntity(pos.down())).getType().equals("redstone") && ((TileEntityInventoryScanner) world.getTileEntity(pos.down())).shouldProvidePower())
				return pos.down();
		}

		return null;
	}

	private boolean hasNoActiveSCBlocksNear(World world, BlockPos pos)
	{
		return !BlockUtils.hasActiveSCBlockNextTo(world, pos);
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
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileEntityOwnable();
	}
}