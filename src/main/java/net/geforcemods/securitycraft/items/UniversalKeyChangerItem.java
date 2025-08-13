package net.geforcemods.securitycraft.items;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.IDisguisable;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.IPasscodeProtected;
import net.geforcemods.securitycraft.blockentities.DisplayCaseBlockEntity;
import net.geforcemods.securitycraft.misc.SaltData;
import net.geforcemods.securitycraft.screen.ScreenHandler.Screens;
import net.geforcemods.securitycraft.util.PasscodeUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

public class UniversalKeyChangerItem extends Item {
	@Override
	public EnumActionResult onItemUseFirst(EntityPlayer player, World level, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
		EnumActionResult briefcaseResult = handleBriefcase(level, player, hand).getType();

		if (briefcaseResult != EnumActionResult.PASS)
			return briefcaseResult;

		TileEntity te = level.getTileEntity(pos);

		if (te instanceof DisplayCaseBlockEntity && (((DisplayCaseBlockEntity) te).isOpen() && ((DisplayCaseBlockEntity) te).getDisplayedStack().isEmpty()))
			return EnumActionResult.PASS;
		else if (te instanceof IPasscodeProtected) {
			if (((IOwnable) te).isOwnedBy(player)) {
				player.openGui(SecurityCraft.instance, Screens.KEY_CHANGER.ordinal(), level, pos.getX(), pos.getY(), pos.getZ());
				return EnumActionResult.SUCCESS;
			}
			else if (PlayerUtils.checkAndReportOwnership(te, player, SCContent.universalKeyChanger))
				return EnumActionResult.SUCCESS;
		}

		return EnumActionResult.PASS;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World level, EntityPlayer player, EnumHand hand) {
		return handleBriefcase(level, player, hand);
	}

	private ActionResult<ItemStack> handleBriefcase(World level, EntityPlayer player, EnumHand hand) {
		ItemStack keyChanger = player.getHeldItem(hand);

		if (hand == EnumHand.MAIN_HAND && player.getHeldItemOffhand().getItem() == SCContent.briefcase) {
			ItemStack briefcase = player.getHeldItemOffhand();

			if (BriefcaseItem.isOwnedBy(briefcase, player) || player.isCreative()) {
				NBTTagCompound tag = briefcase.getTagCompound();

				if (tag != null && tag.hasKey("passcode")) {
					if (tag.hasKey("saltKey") && !level.isRemote)
						SaltData.removeSalt(tag.getUniqueId("saltKey"));

					PasscodeUtils.filterPasscodeAndSaltFromTag(tag);
					PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.universalKeyChanger.getTranslationKey() + ".name"), Utils.localize("messages.securitycraft:universalKeyChanger.briefcase.passcodeReset"), TextFormatting.GREEN);
					return ActionResult.newResult(EnumActionResult.SUCCESS, keyChanger);
				}
				else
					PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.universalKeyChanger.getTranslationKey() + ".name"), Utils.localize("messages.securitycraft:universalKeyChanger.briefcase.noPasscode"), TextFormatting.RED);
			}
			else
				PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.universalKeyChanger.getTranslationKey() + ".name"), Utils.localize("messages.securitycraft:universalKeyChanger.briefcase.notOwned"), TextFormatting.RED);

			return ActionResult.newResult(EnumActionResult.SUCCESS, keyChanger);
		}

		return ActionResult.newResult(EnumActionResult.PASS, keyChanger);
	}
}
