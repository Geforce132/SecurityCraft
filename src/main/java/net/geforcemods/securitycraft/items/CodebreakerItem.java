package net.geforcemods.securitycraft.items;

import java.util.Random;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.containers.BriefcaseContainer;
import net.geforcemods.securitycraft.inventory.BriefcaseInventory;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionHand;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkHooks;

public class CodebreakerItem extends Item {

	public CodebreakerItem(Item.Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
		ItemStack codebreaker = player.getItemInHand(hand);

		if (hand == InteractionHand.MAIN_HAND && player.getOffhandItem().getItem() == SCContent.BRIEFCASE.get()) {
			if(!ConfigHandler.SERVER.allowCodebreakerItem.get()) {
				PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.BRIEFCASE.get().getDescriptionId()), Utils.localize("messages.securitycraft:codebreakerDisabled"), ChatFormatting.RED);
				return InteractionResultHolder.success(codebreaker);
			}
			else {
				codebreaker.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(hand));

				if (!world.isClientSide && new Random().nextInt(3) == 1) {
					ItemStack briefcase = player.getOffhandItem();

					NetworkHooks.openGui((ServerPlayer)player, new MenuProvider() {
						@Override
						public AbstractContainerMenu createMenu(int windowId, Inventory inv, Player player)
						{
							return new BriefcaseContainer(windowId, inv, new BriefcaseInventory(briefcase));
						}

						@Override
						public Component getDisplayName()
						{
							return briefcase.getHoverName();
						}
					}, player.blockPosition());
				}
				else
					PlayerUtils.sendMessageToPlayer(player, new TranslatableComponent(SCContent.CODEBREAKER.get().getDescriptionId()), Utils.localize("messages.securitycraft:codebreaker.failed"), ChatFormatting.RED);
			}

			return InteractionResultHolder.success(codebreaker);
		}

		return InteractionResultHolder.pass(codebreaker);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public boolean isFoil(ItemStack stack){
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
