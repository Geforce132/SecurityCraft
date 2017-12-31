package net.geforcemods.securitycraft.items;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.gui.GuiHandler;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class ItemBriefcase extends Item {

	public ItemBriefcase() {}

	@Override
	public boolean isFull3D() {
		return true;
	}

	@Override
	public boolean onItemUse(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, World par3World, BlockPos pos, EnumFacing side, float par8, float par9, float par10) {
		if(par3World.isRemote) {
			if(!par1ItemStack.hasTagCompound()) {
				par1ItemStack.setTagCompound(new NBTTagCompound());
				ClientUtils.syncItemNBT(par1ItemStack);
			}

			if(!par1ItemStack.getTagCompound().hasKey("passcode"))
				par2EntityPlayer.openGui(SecurityCraft.instance, GuiHandler.BRIEFCASE_CODE_SETUP_GUI_ID, par3World, (int) par2EntityPlayer.posX, (int) par2EntityPlayer.posY, (int) par2EntityPlayer.posZ);
			else
				par2EntityPlayer.openGui(SecurityCraft.instance, GuiHandler.BRIEFCASE_INSERT_CODE_GUI_ID, par3World, (int) par2EntityPlayer.posX, (int) par2EntityPlayer.posY, (int) par2EntityPlayer.posZ);
		}

		return false;
	}

	@Override
	public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer) {
		if(par2World.isRemote) {
			if(!par1ItemStack.hasTagCompound()) {
				par1ItemStack.setTagCompound(new NBTTagCompound());
				ClientUtils.syncItemNBT(par1ItemStack);
			}

			if(!par1ItemStack.getTagCompound().hasKey("passcode"))
				par3EntityPlayer.openGui(SecurityCraft.instance, GuiHandler.BRIEFCASE_CODE_SETUP_GUI_ID, par2World, (int) par3EntityPlayer.posX, (int) par3EntityPlayer.posY, (int) par3EntityPlayer.posZ);
			else
				par3EntityPlayer.openGui(SecurityCraft.instance, GuiHandler.BRIEFCASE_INSERT_CODE_GUI_ID, par2World, (int) par3EntityPlayer.posX, (int) par3EntityPlayer.posY, (int) par3EntityPlayer.posZ);
		}

		return par1ItemStack;
	}

	@Override
	public ItemStack getContainerItem(ItemStack stack)
	{
		if(stack.getTagCompound() != null && stack.getTagCompound().hasKey("passcode"))
			stack.getTagCompound().removeTag("passcode");

		return stack;
	}

	@Override
	public boolean hasContainerItem()
	{
		return true;
	}
}
