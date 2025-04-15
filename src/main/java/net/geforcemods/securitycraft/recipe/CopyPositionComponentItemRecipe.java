package net.geforcemods.securitycraft.recipe;

import java.util.function.Predicate;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.items.CameraMonitorItem;
import net.geforcemods.securitycraft.items.MineRemoteAccessToolItem;
import net.geforcemods.securitycraft.items.SentryRemoteAccessToolItem;
import net.geforcemods.securitycraft.items.SonicSecuritySystemItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class CopyPositionComponentItemRecipe extends CombineRecipe {
	private final Item item;
	private final Predicate<NBTTagCompound> isDataEmpty;

	public CopyPositionComponentItemRecipe(Item item, Predicate<NBTTagCompound> hasData) {
		this.item = item;
		this.isDataEmpty = tag -> !hasData.test(tag);
	}

	public static CopyPositionComponentItemRecipe cameraMonitor() {
		return new CopyPositionComponentItemRecipe(SCContent.cameraMonitor, CameraMonitorItem::hasCameraAdded);
	}

	public static CopyPositionComponentItemRecipe mineRemoteAccessTool() {
		return new CopyPositionComponentItemRecipe(SCContent.mineRemoteAccessTool, MineRemoteAccessToolItem::hasMineAdded);
	}

	public static CopyPositionComponentItemRecipe sentryRemoteAccessTool() {
		return new CopyPositionComponentItemRecipe(SCContent.sentryRemoteAccessTool, SentryRemoteAccessToolItem::hasSentryAdded);
	}

	public static CopyPositionComponentItemRecipe sonicSecuritySystem() {
		return new CopyPositionComponentItemRecipe(SCContent.sonicSecuritySystemItem, SonicSecuritySystemItem::hasLinkedBlock);
	}

	@Override
	public boolean matchesFirstItem(ItemStack stack) {
		return stack.getItem() == item && !isDataEmpty.test(getOrCreateTag(stack));
	}

	@Override
	public boolean matchesSecondItem(ItemStack stack) {
		return stack.getItem() == item && isDataEmpty.test(getOrCreateTag(stack));
	}

	@Override
	public ItemStack combine(ItemStack itemWithPositions, ItemStack emptyItem) {
		ItemStack result = new ItemStack(item, 2);

		getOrCreateTag(result).merge(getOrCreateTag(itemWithPositions));
		return result;
	}
}
