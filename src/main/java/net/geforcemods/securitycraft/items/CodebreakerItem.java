package net.geforcemods.securitycraft.items;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.inventory.BriefcaseMenu;
import net.geforcemods.securitycraft.inventory.ItemContainer;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
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

public class CodebreakerItem extends Item {
	public static final ResourceLocation STATE_PROPERTY = new ResourceLocation(SecurityCraft.MODID, "codebreaker_state");
	public static final String WORKING = "working", LAST_USED_TIME = "last_used_time", WAS_SUCCESSFUL = "was_successful";

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
					PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.CODEBREAKER.get().getDescriptionId()), Utils.localize("messages.securitycraft:codebreaker.owned"), ChatFormatting.RED);
				else {
					double chance = ConfigHandler.SERVER.codebreakerChance.get();

					if (chance < 0.0D)
						PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.CODEBREAKER.get().getDescriptionId()), Utils.localize("messages.securitycraft:codebreakerDisabled"), ChatFormatting.RED);
					else {
						codebreaker.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(hand));

						if (!level.isClientSide) {
							if (!player.isCreative() && wasRecentlyUsed(codebreaker))
								return InteractionResultHolder.pass(codebreaker);

							boolean isSuccessful = player.isCreative() || SecurityCraft.RANDOM.nextDouble() < chance;
							CompoundTag tag = codebreaker.getOrCreateTag();

							tag.putLong(LAST_USED_TIME, System.currentTimeMillis());
							tag.putBoolean(WAS_SUCCESSFUL, isSuccessful);

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
								});
							}
							else
								PlayerUtils.sendMessageToPlayer(player, Component.translatable(SCContent.CODEBREAKER.get().getDescriptionId()), Utils.localize("messages.securitycraft:codebreaker.failed"), ChatFormatting.RED);
						}
					}
				}

				return InteractionResultHolder.success(codebreaker);
			}
		}

		return InteractionResultHolder.pass(codebreaker);
	}

	public static boolean wasRecentlyUsed(ItemStack stack) {
		long lastUsedTime = stack.getOrCreateTag().getLong(CodebreakerItem.LAST_USED_TIME);

		return lastUsedTime != 0 && System.currentTimeMillis() - lastUsedTime < 3000L;
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
