package net.geforcemods.securitycraft.recipe;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.items.KeycardItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class LimitedUseKeycardRecipe extends CombineRecipe {
	@Override
	public boolean matchesFirstItem(ItemStack stack) {
		return stack.getItem() instanceof KeycardItem && !matchesSecondItem(stack) && !getOrCreateTag(stack).getBoolean("limited");
	}

	@Override
	public boolean matchesSecondItem(ItemStack stack) {
		return stack.getItem() == SCContent.limitedUseKeycard;
	}

	@Override
	public ItemStack combine(ItemStack keycardToCopy, ItemStack limitedUseKeycard) {
		ItemStack outputKeycard = keycardToCopy.copy();
		NBTTagCompound tag = getOrCreateTag(outputKeycard);

		tag.setBoolean("limited", true);
		tag.setInteger("uses", 0);
		outputKeycard.setCount(2);
		return outputKeycard;
	}
}
