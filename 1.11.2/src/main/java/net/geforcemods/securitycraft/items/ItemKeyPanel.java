package net.geforcemods.securitycraft.items;

import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.blocks.BlockKeypad;
import net.geforcemods.securitycraft.main.mod_SecurityCraft;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemKeyPanel extends Item {

	public ItemKeyPanel(){
		super();
	}

	@Override
	public EnumActionResult onItemUse( EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ){
		ItemStack stack = playerIn.getHeldItem(hand);

		if(!worldIn.isRemote){
			if(BlockUtils.getBlock(worldIn, pos) == mod_SecurityCraft.frame){
				Owner owner = ((IOwnable) worldIn.getTileEntity(pos)).getOwner();
				EnumFacing enumfacing = worldIn.getBlockState(pos).getValue(BlockKeypad.FACING);
				worldIn.setBlockState(pos, mod_SecurityCraft.keypad.getDefaultState().withProperty(BlockKeypad.FACING, enumfacing).withProperty(BlockKeypad.POWERED, false));
				((IOwnable) worldIn.getTileEntity(pos)).getOwner().set(owner.getUUID(), owner.getName());
				stack.shrink(1);
			}

			return EnumActionResult.SUCCESS;
		}

		return EnumActionResult.FAIL;
	}


}
