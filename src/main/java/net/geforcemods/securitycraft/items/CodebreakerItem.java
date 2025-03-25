package net.geforcemods.securitycraft.items;

import java.util.function.Consumer;

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
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;

public class CodebreakerItem extends Item {
	private static final Component DISABLED = Component.translatable("tooltip.securitycraft.component.success_chance.disabled").withStyle(ChatFormatting.RED);

	public CodebreakerItem(Item.Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResult use(Level level, Player player, InteractionHand hand) {
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
								return InteractionResult.PASS;

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

				return InteractionResult.SUCCESS_SERVER;
			}
		}

		return InteractionResult.PASS;
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext ctx, TooltipDisplay display, Consumer<Component> tooltipAdder, TooltipFlag flag) {
		if (display.shows(SCContent.SUCCESS_CHANCE.get())) {
			double chance = getSuccessChance(stack) * 100;

			if (chance < 0.0D)
				tooltipAdder.accept(DISABLED);
			else
				tooltipAdder.accept(Component.translatable("tooltip.securitycraft.component.success_chance", chance + "%").withStyle(ChatFormatting.GRAY));
		}
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
	public boolean supportsEnchantment(ItemStack stack, Holder<Enchantment> enchantment) {
		return false;
	}

	public static double getSuccessChance(ItemStack codebreaker) {
		return codebreaker.getOrDefault(SCContent.SUCCESS_CHANCE, 1.0D);
	}
}
