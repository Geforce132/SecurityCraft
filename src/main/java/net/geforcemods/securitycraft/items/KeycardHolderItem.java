package net.geforcemods.securitycraft.items;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.inventory.ItemContainer;
import net.geforcemods.securitycraft.inventory.KeycardHolderMenu;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

public class KeycardHolderItem extends Item {
	public static final ResourceLocation COUNT_PROPERTY = new ResourceLocation(SecurityCraft.MODID, "keycard_count");

	public KeycardHolderItem(Properties properties) {
		super(properties);
	}

	@Override
	public ActionResult<ItemStack> use(World level, PlayerEntity player, Hand hand) {
		ItemStack stack = player.getItemInHand(hand);

		if (!level.isClientSide) {
			player.openMenu(new INamedContainerProvider() {
				@Override
				public Container createMenu(int id, PlayerInventory playerInventory, PlayerEntity player) {
					return new KeycardHolderMenu(id, playerInventory, ItemContainer.keycardHolder(stack));
				}

				@Override
				public ITextComponent getDisplayName() {
					return stack.getHoverName();
				}
			});
		}

		return ActionResult.consume(stack);
	}

	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		return oldStack.getItem() != newStack.getItem();
	}

	public static int getCardCount(ItemStack stack) {
		int count = 0;
		ListNBT items = stack.getOrCreateTag().getList("ItemInventory", Constants.NBT.TAG_COMPOUND);

		for (int i = 0; i < items.size(); i++) {
			CompoundNBT item = items.getCompound(i);
			int slot = item.getInt("Slot");

			if (slot < KeycardHolderMenu.CONTAINER_SIZE && ItemStack.of(item).getItem() instanceof KeycardItem)
				count++;
		}

		return count;
	}
}
