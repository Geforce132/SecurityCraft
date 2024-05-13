package net.geforcemods.securitycraft.items;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.inventory.BriefcaseMenu;
import net.geforcemods.securitycraft.inventory.ItemContainer;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkHooks;

public class CodebreakerItem extends Item {
	public static final ResourceLocation STATE_PROPERTY = new ResourceLocation(SecurityCraft.MODID, "codebreaker_state");
	public static final String WORKING = "working", LAST_USED_TIME = "last_used_time", WAS_SUCCESSFUL = "was_successful";

	public CodebreakerItem(Item.Properties properties) {
		super(properties);
	}

	@Override
	public ActionResult<ItemStack> use(World level, PlayerEntity player, Hand hand) {
		ItemStack codebreaker = player.getItemInHand(hand);

		if (hand == Hand.MAIN_HAND) {
			ItemStack briefcase = player.getOffhandItem();

			if (briefcase.getItem() == SCContent.BRIEFCASE.get()) {
				if (BriefcaseItem.isOwnedBy(briefcase, player) && !player.isCreative())
					PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.CODEBREAKER.get().getDescriptionId()), Utils.localize("messages.securitycraft:codebreaker.owned"), TextFormatting.RED);
				else {
					double chance = ConfigHandler.SERVER.codebreakerChance.get();

					if (chance < 0.0D)
						PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.BRIEFCASE.get().getDescriptionId()), Utils.localize("messages.securitycraft:codebreakerDisabled"), TextFormatting.RED);
					else {
						codebreaker.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(hand));

						if (!level.isClientSide) {
							if (wasRecentlyUsed(codebreaker))
								return ActionResult.pass(codebreaker);

							boolean isSuccessful = player.isCreative() || SecurityCraft.RANDOM.nextDouble() < chance;
							CompoundNBT tag = codebreaker.getOrCreateTag();

							tag.putLong(LAST_USED_TIME, System.currentTimeMillis());
							tag.putBoolean(WAS_SUCCESSFUL, isSuccessful);

							if (isSuccessful) {
								NetworkHooks.openGui((ServerPlayerEntity) player, new INamedContainerProvider() {
									@Override
									public Container createMenu(int windowId, PlayerInventory inv, PlayerEntity player) {
										return new BriefcaseMenu(windowId, inv, ItemContainer.briefcase(briefcase));
									}

									@Override
									public ITextComponent getDisplayName() {
										return briefcase.getHoverName();
									}
								}, player.blockPosition());
							}
							else
								PlayerUtils.sendMessageToPlayer(player, new TranslationTextComponent(SCContent.CODEBREAKER.get().getDescriptionId()), Utils.localize("messages.securitycraft:codebreaker.failed"), TextFormatting.RED);
						}
					}
				}

				return ActionResult.success(codebreaker);
			}
		}

		return ActionResult.pass(codebreaker);
	}

	public static boolean wasRecentlyUsed(ItemStack stack) {
		long lastUsedTime = stack.getOrCreateTag().getLong(CodebreakerItem.LAST_USED_TIME);

		return lastUsedTime != 0 && System.currentTimeMillis() - lastUsedTime < 3000L;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
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
