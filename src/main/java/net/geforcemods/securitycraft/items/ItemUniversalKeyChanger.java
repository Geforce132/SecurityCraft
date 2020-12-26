package net.geforcemods.securitycraft.items;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.IPasswordProtected;
import net.geforcemods.securitycraft.blocks.BlockDisguisable;
import net.geforcemods.securitycraft.gui.GuiHandler;
import net.geforcemods.securitycraft.tileentity.TileEntityDisguisable;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

public class ItemUniversalKeyChanger extends Item {

	@Override
	public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
		EnumActionResult briefcaseResult = handleBriefcase(world, player, hand).getType();

		if(briefcaseResult != EnumActionResult.PASS)
			return briefcaseResult;

		TileEntity te = world.getTileEntity(pos);

		if(!world.isRemote && te instanceof IPasswordProtected) {
			if(((IOwnable) te).getOwner().isOwner(player))
				player.openGui(SecurityCraft.instance, GuiHandler.KEY_CHANGER_GUI_ID, world, pos.getX(), pos.getY(), pos.getZ());
			else if(!(te instanceof TileEntityDisguisable) || (((ItemBlock)((BlockDisguisable)((TileEntityDisguisable)te).getBlockType()).getDisguisedStack(world, pos).getItem()).getBlock() instanceof BlockDisguisable))
				PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize("item.securitycraft:universalKeyChanger.name"), ClientUtils.localize("messages.securitycraft:notOwned").replace("#", ((IOwnable) world.getTileEntity(pos)).getOwner().getName()), TextFormatting.RED);

			return EnumActionResult.SUCCESS;
		}

		return EnumActionResult.FAIL;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		return handleBriefcase(world, player, hand);
	}

	private ActionResult<ItemStack> handleBriefcase(World world, EntityPlayer player, EnumHand hand)
	{
		ItemStack keyChanger = player.getHeldItem(hand);

		if (!world.isRemote) {
			if (hand == EnumHand.MAIN_HAND && player.getHeldItemOffhand().getItem() == SCContent.briefcase) {
				ItemStack briefcase = player.getHeldItemOffhand();

				if (ItemBriefcase.isOwnedBy(briefcase, player)) {
					if (briefcase.hasTagCompound() && briefcase.getTagCompound().hasKey("passcode")) {
						briefcase.getTagCompound().removeTag("passcode");
						PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize(SCContent.universalKeyChanger.getTranslationKey() + ".name"), ClientUtils.localize("messages.securitycraft:universalKeyChanger.briefcase.passcodeReset"), TextFormatting.GREEN);
						return ActionResult.newResult(EnumActionResult.SUCCESS, keyChanger);
					}
					else
						PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize(SCContent.universalKeyChanger.getTranslationKey() + ".name"), ClientUtils.localize("messages.securitycraft:universalKeyChanger.briefcase.noPasscode"), TextFormatting.RED);
				}
				else
					PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize(SCContent.universalKeyChanger.getTranslationKey() + ".name"), ClientUtils.localize("messages.securitycraft:universalKeyChanger.briefcase.notOwned"), TextFormatting.RED);

				return ActionResult.newResult(EnumActionResult.FAIL, keyChanger);
			}
		}

		return ActionResult.newResult(EnumActionResult.PASS, keyChanger);
	}
}
