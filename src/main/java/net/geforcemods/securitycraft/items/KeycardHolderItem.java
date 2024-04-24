package net.geforcemods.securitycraft.items;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.inventory.ItemContainer;
import net.geforcemods.securitycraft.inventory.KeycardHolderMenu;
import net.minecraft.core.component.DataComponents;
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
import net.minecraft.world.level.Level;

public class KeycardHolderItem extends Item {
	public static final ResourceLocation COUNT_PROPERTY = new ResourceLocation(SecurityCraft.MODID, "keycard_count");

	public KeycardHolderItem(Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);

		if (!level.isClientSide) {
			player.openMenu(new MenuProvider() {
				@Override
				public AbstractContainerMenu createMenu(int id, Inventory playerInventory, Player player) {
					return new KeycardHolderMenu(id, playerInventory, ItemContainer.keycardHolder(stack));
				}

				@Override
				public Component getDisplayName() {
					return stack.getHoverName();
				}
			});
		}

		return InteractionResultHolder.consume(stack);
	}

	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		return !oldStack.is(newStack.getItem());
	}

	public static int getCardCount(ItemStack stack) {
		return (int) stack.get(DataComponents.CONTAINER).stream().filter(item -> item.getItem() instanceof KeycardItem).count();
	}
}
