package net.geforcemods.securitycraft.items;

import java.util.List;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.gui.GuiHandler;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemBriefcase extends Item {

	@Override
	public boolean isFull3D() {
		return true;
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if(hand == EnumHand.MAIN_HAND)
		{
			ItemStack stack = player.getHeldItem(hand);

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
		}

		return EnumActionResult.FAIL;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		ItemStack stack = player.getHeldItem(hand);

		if(world.isRemote && hand == EnumHand.MAIN_HAND) {
			if(!stack.hasTagCompound()) {
				stack.setTagCompound(new NBTTagCompound());
				ClientUtils.syncItemNBT(stack);
			}

			if(!stack.getTagCompound().hasKey("passcode"))
				player.openGui(SecurityCraft.instance, GuiHandler.BRIEFCASE_CODE_SETUP_GUI_ID, world, (int) player.posX, (int) player.posY, (int) player.posZ);
			else
				player.openGui(SecurityCraft.instance, GuiHandler.BRIEFCASE_INSERT_CODE_GUI_ID, world, (int) player.posX, (int) player.posY, (int) player.posZ);
		}

		return ActionResult.newResult(EnumActionResult.PASS, stack);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack briefcase, World world, List<String> tooltip, ITooltipFlag flag)
	{
		if (briefcase.hasTagCompound() && briefcase.getTagCompound().hasKey("owner"))
			tooltip.add(TextFormatting.GRAY + ClientUtils.localize("tooltip.securitycraft:briefcase.owner", briefcase.getTagCompound().getString("owner")));
	}

	public static boolean isOwnedBy(ItemStack briefcase, EntityPlayer player) {
		return !briefcase.hasTagCompound() || !briefcase.getTagCompound().hasKey("owner") || briefcase.getTagCompound().getString("ownerUUID").equals(player.getUniqueID().toString()) || (briefcase.getTagCompound().getString("ownerUUID").equals("ownerUUID") && briefcase.getTagCompound().getString("owner").equals(player.getName()));
	}
}
