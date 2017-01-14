package net.geforcemods.securitycraft.items;

import net.geforcemods.securitycraft.blocks.BlockScannerDoor;
import net.geforcemods.securitycraft.main.mod_SecurityCraft;
import net.geforcemods.securitycraft.tileentity.TileEntityOwnable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemScannerDoor extends Item
{
	/**
	 * Callback for item usage. If the item does something special on right clicking, he will have one of those. Return
	 * True if something happen and false if it don't. This is for ITEMS, not BLOCKS
	 */
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float par8, float par9, float par10)
	{
		if(world.isRemote)
			return true;
		else
		{
			if(side != EnumFacing.UP)
				return false;
			else
			{
				IBlockState iblockstate = world.getBlockState(pos);
				Block block = iblockstate.getBlock();

				if(!block.isReplaceable(world, pos))
					pos = pos.offset(side);

				if(!player.canPlayerEdit(pos, side, stack))
					return false;
				else if(!mod_SecurityCraft.scannerDoor.canPlaceBlockAt(world, pos))
					return false;
				else
				{
					placeDoor(world, pos, EnumFacing.fromAngle(player.rotationYaw), mod_SecurityCraft.scannerDoor);                    //TERD.getOwner().set(player.getGameProfile().getId().toString(), player.getName());
					((TileEntityOwnable) world.getTileEntity(pos)).getOwner().set(player.getGameProfile().getId().toString(), player.getName());
					((TileEntityOwnable) world.getTileEntity(pos.up())).getOwner().set(player.getGameProfile().getId().toString(), player.getName());
					stack.stackSize--;
					return true;
				}
			}
		}
	}

	public static void placeDoor(World worldIn, BlockPos pos, EnumFacing facing, Block door)
	{
		BlockPos blockpos1 = pos.offset(facing.rotateY());
		BlockPos blockpos2 = pos.offset(facing.rotateYCCW());
		int i = (worldIn.getBlockState(blockpos2).getBlock().isNormalCube(worldIn.getBlockState(pos)) ? 1 : 0) + (worldIn.getBlockState(blockpos2.up()).getBlock().isNormalCube(worldIn.getBlockState(pos)) ? 1 : 0);
		int j = (worldIn.getBlockState(blockpos1).getBlock().isNormalCube(worldIn.getBlockState(pos)) ? 1 : 0) + (worldIn.getBlockState(blockpos1.up()).getBlock().isNormalCube(worldIn.getBlockState(pos)) ? 1 : 0);
		boolean flag = worldIn.getBlockState(blockpos2).getBlock() == door || worldIn.getBlockState(blockpos2.up()).getBlock() == door;
		boolean flag1 = worldIn.getBlockState(blockpos1).getBlock() == door || worldIn.getBlockState(blockpos1.up()).getBlock() == door;
		boolean flag2 = false;

		if(flag && !flag1 || j > i)
			flag2 = true;

		BlockPos blockpos3 = pos.up();
		IBlockState iblockstate = mod_SecurityCraft.scannerDoor.getDefaultState().withProperty(BlockScannerDoor.FACING, facing).withProperty(BlockScannerDoor.HINGE, flag2 ? BlockScannerDoor.EnumHingePosition.RIGHT : BlockScannerDoor.EnumHingePosition.LEFT);

		worldIn.setBlockState(pos, iblockstate.withProperty(BlockDoor.HALF, BlockScannerDoor.EnumDoorHalf.LOWER), 2);
		worldIn.setBlockState(blockpos3, iblockstate.withProperty(BlockDoor.HALF, BlockScannerDoor.EnumDoorHalf.UPPER), 2);
		worldIn.notifyNeighborsOfStateChange(pos, door);
		worldIn.notifyNeighborsOfStateChange(blockpos3, door);
	}	
}
