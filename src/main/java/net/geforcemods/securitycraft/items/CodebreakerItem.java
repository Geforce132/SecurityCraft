package net.geforcemods.securitycraft.items;

import java.util.Random;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.containers.BriefcaseContainer;
import net.geforcemods.securitycraft.containers.BriefcaseInventory;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkHooks;

public class CodebreakerItem extends Item {

	public CodebreakerItem(Item.Properties properties) {
		super(properties);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
		ItemStack codebreaker = player.getHeldItem(hand);

		if (hand == Hand.MAIN_HAND && player.getHeldItemOffhand().getItem() == SCContent.BRIEFCASE.get()) {
			if(!world.isRemote && !ConfigHandler.CONFIG.allowCodebreakerItem.get()) {
				PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize(SCContent.BRIEFCASE.get().getTranslationKey()), ClientUtils.localize("messages.securitycraft:codebreakerDisabled"), TextFormatting.RED);
				return ActionResult.resultFail(codebreaker);
			}
			else {
				codebreaker.damageItem(1, player, p -> p.sendBreakAnimation(hand));

				if (!world.isRemote && new Random().nextInt(3) == 1) {
					ItemStack briefcase = player.getHeldItemOffhand();

					NetworkHooks.openGui((ServerPlayerEntity)player, new INamedContainerProvider() {
						@Override
						public Container createMenu(int windowId, PlayerInventory inv, PlayerEntity player)
						{
							return new BriefcaseContainer(windowId, inv, new BriefcaseInventory(briefcase));
						}

						@Override
						public ITextComponent getDisplayName()
						{
							return briefcase.getDisplayName();
						}
					}, player.getPosition());
				}
			}

			return ActionResult.resultSuccess(codebreaker);
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
