package net.geforcemods.securitycraft.items;

import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.blocks.BlockKeypad;
import net.geforcemods.securitycraft.main.mod_SecurityCraft;
import net.geforcemods.securitycraft.main.Utils.BlockUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class ItemKeyPanel extends Item {
	
	public ItemKeyPanel(){
		super();
	}
	
    public boolean onItemUse(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, World par3World, BlockPos pos, EnumFacing par5EnumFacing, float hitX, float hitY, float hitZ){
		if(!par3World.isRemote){
			if(BlockUtils.getBlock(par3World, pos) == mod_SecurityCraft.frame){
				String owner = ((IOwnable) par3World.getTileEntity(pos)).getOwnerName();
				String uuid = ((IOwnable) par3World.getTileEntity(pos)).getOwnerUUID();
		        EnumFacing enumfacing = (EnumFacing) par3World.getBlockState(pos).getValue(BlockKeypad.FACING);
				par3World.setBlockState(pos, mod_SecurityCraft.Keypad.getDefaultState().withProperty(BlockKeypad.FACING, enumfacing).withProperty(BlockKeypad.POWERED, false));
				((IOwnable) par3World.getTileEntity(pos)).setOwner(uuid, owner);
				par1ItemStack.stackSize -= 1;
			}
			
			return true;
		}
		
		return false;
    }


}
