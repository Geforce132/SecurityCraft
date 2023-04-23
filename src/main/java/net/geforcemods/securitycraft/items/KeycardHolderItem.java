package net.geforcemods.securitycraft.items;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.inventory.KeycardHolderMenu;
import net.geforcemods.securitycraft.screen.ScreenHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

public class KeycardHolderItem extends Item {
	public KeycardHolderItem() {
		addPropertyOverride(new ResourceLocation(SecurityCraft.MODID, "keycard_count"), (stack, world, entity) -> KeycardHolderItem.getCardCount(stack) / (float) KeycardHolderMenu.CONTAINER_SIZE);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World level, EntityPlayer player, EnumHand hand) {
		ItemStack stack = player.getHeldItem(hand);

		if (!level.isRemote)
			player.openGui(SecurityCraft.instance, ScreenHandler.KEYCARD_HOLDER, level, (int) player.posX, (int) player.posY, (int) player.posZ);

		return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
	}

	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		return oldStack.getItem() != newStack.getItem();
	}

	public static int getCardCount(ItemStack stack) {
		int count = 0;

		if (!stack.hasTagCompound())
			stack.setTagCompound(new NBTTagCompound());

		NBTTagList items = stack.getTagCompound().getTagList("ItemInventory", Constants.NBT.TAG_COMPOUND);

		for (int i = 0; i < items.tagCount(); i++) {
			NBTTagCompound item = items.getCompoundTagAt(i);
			int slot = item.getInteger("Slot");

			if (slot < KeycardHolderMenu.CONTAINER_SIZE && new ItemStack(item).getItem() instanceof KeycardItem)
				count++;
		}

		return count;
	}
}
