package net.geforcemods.securitycraft.items;

import java.util.List;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.components.CodebreakerData;
import net.geforcemods.securitycraft.inventory.BriefcaseMenu;
import net.geforcemods.securitycraft.inventory.ItemContainer;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;

public class CodebreakerItem extends Item {
	public static final ResourceLocation STATE_PROPERTY = SecurityCraft.resLoc("codebreaker_state");
	private static final Component DISABLED = Component.translatable("tooltip.securitycraft.component.success_chance.disabled").withStyle(ChatFormatting.RED);

	public CodebreakerItem(Item.Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		ItemStack codebreaker = player.getItemInHand(hand);

		if (hand == InteractionHand.MAIN_HAND) {
			ItemStack briefcase = player.getOffhandItem();

			if (briefcase.is(SCContent.BRIEFCASE.get())) {
				if (BriefcaseItem.isOwnedBy(briefcase, player) && !player.isCreative())
					PlayerUtils.sendMessageToPlayer(player, Utils.localize(getDescriptionId()), Utils.localize("messages.securitycraft:codebreaker.owned"), ChatFormatting.RED);
				else {
					double chance = getSuccessChance(codebreaker);

					if (chance < 0.0D)
						PlayerUtils.sendMessageToPlayer(player, Utils.localize(getDescriptionId()), Utils.localize("messages.securitycraft:codebreakerDisabled"), ChatFormatting.RED);
					else {
						codebreaker.hurtAndBreak(1, player, LivingEntity.getSlotForHand(hand));

						if (!level.isClientSide) {
							if (!player.isCreative() && codebreaker.getOrDefault(SCContent.CODEBREAKER_DATA, CodebreakerData.DEFAULT).wasRecentlyUsed())
								return InteractionResultHolder.pass(codebreaker);

							boolean isSuccessful = player.isCreative() || SecurityCraft.RANDOM.nextDouble() < chance;

							codebreaker.set(SCContent.CODEBREAKER_DATA, new CodebreakerData(System.currentTimeMillis(), isSuccessful));

							if (isSuccessful) {
								player.openMenu(new MenuProvider() {
									@Override
									public AbstractContainerMenu createMenu(int windowId, Inventory inv, Player player) {
										return new BriefcaseMenu(windowId, inv, ItemContainer.briefcase(briefcase));
									}

									@Override
									public Component getDisplayName() {
										return briefcase.getHoverName();
									}
								}, player.blockPosition());
							}
							else
								PlayerUtils.sendMessageToPlayer(player, Component.translatable(getDescriptionId()), Utils.localize("messages.securitycraft:codebreaker.failed"), ChatFormatting.RED);
						}
					}
				}

				return InteractionResultHolder.success(codebreaker);
			}
		}

		return InteractionResultHolder.pass(codebreaker);
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext ctx, List<Component> list, TooltipFlag flag) {
		double chance = getSuccessChance(stack) * 100;

		if (chance < 0.0D)
			list.add(DISABLED);
		else
			list.add(Component.translatable("tooltip.securitycraft.component.success_chance", chance + "%").withStyle(ChatFormatting.GRAY));
	}

	@Override
	public boolean isFoil(ItemStack stack) {
		return true;
	}

	@Override
	public boolean isPrimaryItemFor(ItemStack stack, Holder<Enchantment> enchantment) {
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

	public static double getSuccessChance(ItemStack codebreaker) {
		return codebreaker.getOrDefault(SCContent.SUCCESS_CHANCE, 1.0D);
	}
}
