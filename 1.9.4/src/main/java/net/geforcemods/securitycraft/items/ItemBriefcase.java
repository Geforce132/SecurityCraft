package net.geforcemods.securitycraft.items;

import net.geforcemods.securitycraft.gui.GuiHandler;
import net.geforcemods.securitycraft.main.mod_SecurityCraft;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemBriefcase extends Item {
	
	public ItemBriefcase() {}
	
	public boolean isFull3D() {
		return true;
	}
	
	@Override
    public EnumActionResult onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
    	if(worldIn.isRemote) {
	    	if(!stack.hasTagCompound()) {
	    		stack.setTagCompound(new NBTTagCompound());
	    		ClientUtils.syncItemNBT(stack);
	    	}
	    	
	    	if(!stack.getTagCompound().hasKey("passcode")) {
	    		playerIn.openGui(mod_SecurityCraft.instance, GuiHandler.BRIEFCASE_CODE_SETUP_GUI_ID, worldIn, (int) playerIn.posX, (int) playerIn.posY, (int) playerIn.posZ);
	    	}
	    	else {
	    		playerIn.openGui(mod_SecurityCraft.instance, GuiHandler.BRIEFCASE_INSERT_CODE_GUI_ID, worldIn, (int) playerIn.posX, (int) playerIn.posY, (int) playerIn.posZ);
	    	}
    	}
    	
    	return EnumActionResult.FAIL;
    }
    
	@Override
    public ActionResult<ItemStack> onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn, EnumHand hand) {  	
    	if(worldIn.isRemote) {
	    	if(!itemStackIn.hasTagCompound()) {
	    		itemStackIn.setTagCompound(new NBTTagCompound());
	    	    ClientUtils.syncItemNBT(itemStackIn);
	    	}
	    	
	    	if(!itemStackIn.getTagCompound().hasKey("passcode")) {
	    		playerIn.openGui(mod_SecurityCraft.instance, GuiHandler.BRIEFCASE_CODE_SETUP_GUI_ID, worldIn, (int) playerIn.posX, (int) playerIn.posY, (int) playerIn.posZ);
	    	}
	    	else {
	    		playerIn.openGui(mod_SecurityCraft.instance, GuiHandler.BRIEFCASE_INSERT_CODE_GUI_ID, worldIn, (int) playerIn.posX, (int) playerIn.posY, (int) playerIn.posZ);
	    	}
    	}
    	
    	return ActionResult.newResult(EnumActionResult.PASS, itemStackIn);
    }
    
	@Override
	public ItemStack getContainerItem(ItemStack stack)
	{
		if(stack.getTagCompound() != null && stack.getTagCompound().hasKey("passcode")) {
		    stack.getTagCompound().removeTag("passcode");
		}

		return stack;
	}
	
	@Override
	public boolean hasContainerItem()
	{
		return true;
	}
}
