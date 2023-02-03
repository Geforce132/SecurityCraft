package net.geforcemods.securitycraft.items;

import java.util.Random;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.screen.ScreenHandler;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class CodebreakerItem extends Item {
	public CodebreakerItem() {
		maxStackSize = 1;
		setMaxDamage(4); //5 uses because when the damage is 0 the item has one more use
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		ItemStack codebreaker = player.getHeldItem(hand);

		if (hand == EnumHand.MAIN_HAND && player.getHeldItemOffhand().getItem() == SCContent.briefcase) {
			double chance = ConfigHandler.codebreakerChance;

			if (chance < 0.0D)
				PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.briefcase.getTranslationKey() + ".name"), Utils.localize("messages.securitycraft:codebreakerDisabled"), TextFormatting.RED);
			else {
				codebreaker.damageItem(1, player);

				if (!world.isRemote && (player.isCreative() || new Random().nextDouble() < chance))
					player.openGui(SecurityCraft.instance, ScreenHandler.BRIEFCASE_GUI_ID, world, (int) player.posX, (int) player.posY, (int) player.posZ);
				else
					PlayerUtils.sendMessageToPlayer(player, Utils.localize("item.securitycraft:codebreaker.name"), Utils.localize("messages.securitycraft:codebreaker.failed"), TextFormatting.RED);
			}

			return ActionResult.newResult(EnumActionResult.SUCCESS, codebreaker);
		}

		return ActionResult.newResult(EnumActionResult.PASS, codebreaker);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean hasEffect(ItemStack stack) {
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public EnumRarity getRarity(ItemStack stack) {
		return EnumRarity.RARE;
	}

	@Override
	public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
		return false;
	}

	@Override
	public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
		return false;
	}

	@Override
	public boolean isEnchantable(ItemStack stack) {
		return false;
	}
}
