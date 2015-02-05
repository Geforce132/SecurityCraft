package org.freeforums.geforce.securitycraft.items;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import org.freeforums.geforce.securitycraft.blocks.BlockReinforcedDoor;
import org.freeforums.geforce.securitycraft.main.HelpfulMethods;
import org.freeforums.geforce.securitycraft.main.mod_SecurityCraft;
import org.freeforums.geforce.securitycraft.tileentity.TileEntityReinforcedDoor;

public class ItemReinforcedDoor extends Item
{
	
    public ItemReinforcedDoor(Material p_i45334_1_)
    {
        this.maxStackSize = 1;
        this.setCreativeTab(CreativeTabs.tabRedstone);
    }

    /**
     * Callback for item usage. If the item does something special on right clicking, he will have one of those. Return
     * True if something happen and false if it don't. This is for ITEMS, not BLOCKS
     */
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float par8, float par9, float par10)
    {
    	if(world.isRemote){
    		return true;
    	}else{
	        if (side != EnumFacing.UP)
	        {
	            return false;
	        }
	        else
	        {
	        	IBlockState iblockstate = world.getBlockState(pos);
	            Block block = iblockstate.getBlock();

	            if (!block.isReplaceable(world, pos))
	            {
	                pos = pos.offset(side);
	            }

	            if (!player.canPlayerEdit(pos, side, stack))
	            {
	                return false;
	            }
	            else if (!mod_SecurityCraft.doorIndestructableIron.canPlaceBlockAt(world, pos))
	            {
	                return false;
	            }
	            else
	            {
	                placeDoor(world, pos, EnumFacing.fromAngle((double)player.rotationYaw), mod_SecurityCraft.doorIndestructableIron);
	                TileEntityReinforcedDoor TERD = new TileEntityReinforcedDoor();
                    TERD.setOwner(player.getGameProfile().getId().toString(), player.getName());
                    world.setTileEntity(pos, TERD);
	                --stack.stackSize;
	                return true;
	            }
	        }
    	}
    }

    public static void placeDoor(World worldIn, BlockPos pos, EnumFacing facing, Block door)
    {
        BlockPos blockpos1 = pos.offset(facing.rotateY());
        BlockPos blockpos2 = pos.offset(facing.rotateYCCW());
        int i = (worldIn.getBlockState(blockpos2).getBlock().isNormalCube() ? 1 : 0) + (worldIn.getBlockState(blockpos2.up()).getBlock().isNormalCube() ? 1 : 0);
        int j = (worldIn.getBlockState(blockpos1).getBlock().isNormalCube() ? 1 : 0) + (worldIn.getBlockState(blockpos1.up()).getBlock().isNormalCube() ? 1 : 0);
        boolean flag = worldIn.getBlockState(blockpos2).getBlock() == door || worldIn.getBlockState(blockpos2.up()).getBlock() == door;
        boolean flag1 = worldIn.getBlockState(blockpos1).getBlock() == door || worldIn.getBlockState(blockpos1.up()).getBlock() == door;
        boolean flag2 = false;

        if (flag && !flag1 || j > i)
        {
            flag2 = true;
        }

        BlockPos blockpos3 = pos.up();
        IBlockState iblockstate = door.getDefaultState().withProperty(BlockReinforcedDoor.FACING, facing).withProperty(BlockReinforcedDoor.HINGE, flag2 ? BlockReinforcedDoor.EnumHingePosition.RIGHT : BlockReinforcedDoor.EnumHingePosition.LEFT);
        worldIn.setBlockState(pos, iblockstate.withProperty(BlockDoor.HALF, BlockReinforcedDoor.EnumDoorHalf.LOWER), 2);
        worldIn.setBlockState(blockpos3, iblockstate.withProperty(BlockDoor.HALF, BlockReinforcedDoor.EnumDoorHalf.UPPER), 2);
        worldIn.notifyNeighborsOfStateChange(pos, door);
        worldIn.notifyNeighborsOfStateChange(blockpos3, door);
    }
}