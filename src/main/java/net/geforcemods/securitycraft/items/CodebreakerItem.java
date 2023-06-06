package net.geforcemods.securitycraft.items;

import java.util.Random;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.inventory.BriefcaseMenu;
import net.geforcemods.securitycraft.inventory.ItemContainer;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;

public class CodebreakerItem extends Item {
	public CodebreakerItem(Item.Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		ItemStack codebreaker = player.getItemInHand(hand);

		if (hand == InteractionHand.MAIN_HAND && player.getOffhandItem().getItem() == SCContent.BRIEFCASE.get()) {
			double chance = ConfigHandler.SERVER.codebreakerChance.get();

			if (chance < 0.0D)
				PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.BRIEFCASE.get().getDescriptionId()), Utils.localize("messages.securitycraft:codebreakerDisabled"), ChatFormatting.RED);
			else {
				codebreaker.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(hand));

				if (!level.isClientSide && (player.isCreative() || new Random().nextDouble() < chance)) {
					ItemStack briefcase = player.getOffhandItem();

					NetworkHooks.openScreen((ServerPlayer) player, new MenuProvider() {
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
					PlayerUtils.sendMessageToPlayer(player, Component.translatable(SCContent.CODEBREAKER.get().getDescriptionId()), Utils.localize("messages.securitycraft:codebreaker.failed"), ChatFormatting.RED);
			}

			return InteractionResultHolder.success(codebreaker);
		}

		return InteractionResultHolder.pass(codebreaker);
	}

	@Override
	public boolean isFoil(ItemStack stack) {
		return true;
	}

	@Override
	public Rarity getRarity(ItemStack stack) {
		return Rarity.RARE;
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
