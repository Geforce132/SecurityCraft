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
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ) {
		if(world.isRemote) {
			if(!stack.hasTagCompound()) {
				stack.setTagCompound(new NBTTagCompound());
				ClientUtils.syncItemNBT(stack);
			}

			if(!stack.getTagCompound().hasKey("passcode"))
				player.openGui(SecurityCraft.instance, GuiHandler.BRIEFCASE_CODE_SETUP_GUI_ID, world, (int) player.posX, (int) player.posY, (int) player.posZ);
			else
				player.openGui(SecurityCraft.instance, GuiHandler.BRIEFCASE_INSERT_CODE_GUI_ID, world, (int) player.posX, (int) player.posY, (int) player.posZ);
		}

		return false;
	}

	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
		if(world.isRemote) {
			if(!stack.hasTagCompound()) {
				stack.setTagCompound(new NBTTagCompound());
				ClientUtils.syncItemNBT(stack);
			}

			if(!stack.getTagCompound().hasKey("passcode"))
				player.openGui(SecurityCraft.instance, GuiHandler.BRIEFCASE_CODE_SETUP_GUI_ID, world, (int) player.posX, (int) player.posY, (int) player.posZ);
			else
				player.openGui(SecurityCraft.instance, GuiHandler.BRIEFCASE_INSERT_CODE_GUI_ID, world, (int) player.posX, (int) player.posY, (int) player.posZ);
		}

		return stack;
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
