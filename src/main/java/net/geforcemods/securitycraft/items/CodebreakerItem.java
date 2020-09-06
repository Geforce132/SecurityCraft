package net.geforcemods.securitycraft.items;

import java.util.Random;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.network.server.OpenGui;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CodebreakerItem extends Item {

	public CodebreakerItem() {
		super(new Item.Properties().maxStackSize(1).defaultMaxDamage(3).group(SecurityCraft.groupSCTechnical));
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
		ItemStack codebreaker = player.getHeldItem(hand);

		if (!world.isRemote) {
			if (hand == Hand.MAIN_HAND && player.getHeldItemOffhand().getItem() == SCContent.BRIEFCASE.get()) {
				ItemStack briefcase = player.getHeldItemOffhand();

				if(!ConfigHandler.CONFIG.allowCodebreakerItem.get()) {
					PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize(SCContent.BRIEFCASE.get().getTranslationKey()), ClientUtils.localize("messages.securitycraft:codebreakerDisabled"), TextFormatting.RED);
					return ActionResult.resultFail(codebreaker);
				}
				else if (new Random().nextInt(3) == 1) {
					SecurityCraft.channel.sendToServer(new OpenGui(SCContent.cTypeBriefcaseInventory.getRegistryName(), briefcase.getDisplayName()));
					codebreaker.damageItem(1, player, p -> p.sendBreakAnimation(hand));
					return ActionResult.resultSuccess(codebreaker);
				}
			}
		}
		return ActionResult.resultPass(codebreaker);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public boolean hasEffect(ItemStack stack){
		return true;
	}

	/**
	 * Return an item rarity from Rarity
	 */
	@Override
	public Rarity getRarity(ItemStack stack){
		return Rarity.RARE;
	}

	@Override
	public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment)
	{
		return false;
	}

	@Override
	public boolean isBookEnchantable(ItemStack stack, ItemStack book)
	{
		return false;
	}

	@Override
	public boolean isEnchantable(ItemStack stack)
	{
		return false;
	}
}
