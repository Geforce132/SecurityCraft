package net.geforcemods.securitycraft.items;

import net.geforcemods.securitycraft.blocks.BlockReinforcedDoor;
import net.geforcemods.securitycraft.main.mod_SecurityCraft;
import net.geforcemods.securitycraft.tileentity.TileEntityOwnable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemReinforcedDoor extends Item
{
    /**
     * Callback for item usage. If the item does something special on right clicking, he will have one of those. Return
     * True if something happen and false if it don't. This is for ITEMS, not BLOCKS
     */
	@Override
	public EnumActionResult onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
	{
		if(worldIn.isRemote) {
			return EnumActionResult.SUCCESS;
		} else {
			if(facing != EnumFacing.UP)
				return EnumActionResult.FAIL;
			else
			{
				IBlockState iblockstate = worldIn.getBlockState(pos);
				Block block = iblockstate.getBlock();

				if(!block.isReplaceable(worldIn, pos))
					pos = pos.offset(facing);

				if(!playerIn.canPlayerEdit(pos, facing, stack))
					return EnumActionResult.FAIL;
				else if(!mod_SecurityCraft.reinforcedDoor.canPlaceBlockAt(worldIn, pos))
					return EnumActionResult.FAIL;
				else
				{
					placeDoor(worldIn, pos, EnumFacing.fromAngle(playerIn.rotationYaw), mod_SecurityCraft.reinforcedDoor);                    //TERD.getOwner().set(player.getGameProfile().getId().toString(), player.getName());
					((TileEntityOwnable) worldIn.getTileEntity(pos)).getOwner().set(playerIn.getGameProfile().getId().toString(), playerIn.getName());
					((TileEntityOwnable) worldIn.getTileEntity(pos.up())).getOwner().set(playerIn.getGameProfile().getId().toString(), playerIn.getName());
					--stack.stackSize;
					return EnumActionResult.SUCCESS;
				}
			}
		}
	}

	public static void placeDoor(World worldIn, BlockPos pos, EnumFacing facing, Block door) {
		BlockPos blockpos1 = pos.offset(facing.rotateY());
		BlockPos blockpos2 = pos.offset(facing.rotateYCCW());
		int i = (worldIn.getBlockState(blockpos2).isNormalCube() ? 1 : 0) + (worldIn.getBlockState(blockpos2.up()).isNormalCube() ? 1 : 0);
		int j = (worldIn.getBlockState(blockpos1).isNormalCube() ? 1 : 0) + (worldIn.getBlockState(blockpos1.up()).isNormalCube() ? 1 : 0);
		boolean flag = worldIn.getBlockState(blockpos2).getBlock() == door || worldIn.getBlockState(blockpos2.up()).getBlock() == door;
		boolean flag1 = worldIn.getBlockState(blockpos1).getBlock() == door || worldIn.getBlockState(blockpos1.up()).getBlock() == door;
		boolean flag2 = false;

		if(flag && !flag1 || j > i)
			flag2 = true;

		BlockPos blockpos3 = pos.up();
		IBlockState iblockstate = mod_SecurityCraft.reinforcedDoor.getDefaultState().withProperty(BlockDoor.FACING, facing).withProperty(BlockDoor.HINGE, flag2 ? BlockReinforcedDoor.EnumHingePosition.RIGHT : BlockReinforcedDoor.EnumHingePosition.LEFT);
		worldIn.setBlockState(pos, iblockstate.withProperty(BlockDoor.HALF, BlockReinforcedDoor.EnumDoorHalf.LOWER), 2);
		worldIn.setBlockState(blockpos3, iblockstate.withProperty(BlockDoor.HALF, BlockReinforcedDoor.EnumDoorHalf.UPPER), 2);
		worldIn.notifyNeighborsOfStateChange(pos, door);
		worldIn.notifyNeighborsOfStateChange(blockpos3, door);
	}	
}
