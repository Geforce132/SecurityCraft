package net.geforcemods.securitycraft.items;

import net.geforcemods.securitycraft.inventory.ItemContainer;
import net.geforcemods.securitycraft.inventory.KeycardHolderMenu;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
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
		int count = 0;
		ListTag items = stack.getOrCreateTag().getList("ItemInventory", Tag.TAG_COMPOUND);

		for (int i = 0; i < items.size(); i++) {
			CompoundTag item = items.getCompound(i);
			int slot = item.getInt("Slot");

			if (slot < KeycardHolderMenu.CONTAINER_SIZE && ItemStack.of(item).getItem() instanceof KeycardItem)
				count++;
		}

		return count;
	}
}
