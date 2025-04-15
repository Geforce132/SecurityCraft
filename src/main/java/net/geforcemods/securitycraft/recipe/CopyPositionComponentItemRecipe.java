package net.geforcemods.securitycraft.recipe;

import java.util.function.Predicate;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.items.CameraMonitorItem;
import net.geforcemods.securitycraft.items.MineRemoteAccessToolItem;
import net.geforcemods.securitycraft.items.SentryRemoteAccessToolItem;
import net.geforcemods.securitycraft.items.SonicSecuritySystemItem;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class CopyPositionComponentItemRecipe extends CombineRecipe {
	private final Holder<Item> item;
	private final Predicate<CompoundTag> isDataEmpty;
	private final RecipeSerializer<? extends CustomRecipe> serializer;

	public CopyPositionComponentItemRecipe(CraftingBookCategory craftingBookCategory, Holder<Item> item, Predicate<CompoundTag> isDataEmpty, RecipeSerializer<? extends CustomRecipe> serializer) {
		super(craftingBookCategory);
		this.item = item;
		this.isDataEmpty = isDataEmpty;
		this.serializer = serializer;
	}

	public static CopyPositionComponentItemRecipe cameraMonitor(CraftingBookCategory craftingBookCategory) {
		return new CopyPositionComponentItemRecipe(craftingBookCategory, SCContent.CAMERA_MONITOR, tag -> !CameraMonitorItem.hasCameraAdded(tag), SCContent.COPY_CAMERA_MONITOR_RECIPE_SERIALIZER.get());
	}

	public static CopyPositionComponentItemRecipe mineRemoteAccessTool(CraftingBookCategory craftingBookCategory) {
		return new CopyPositionComponentItemRecipe(craftingBookCategory, SCContent.MINE_REMOTE_ACCESS_TOOL, tag -> !MineRemoteAccessToolItem.hasMineAdded(tag), SCContent.COPY_MINE_REMOTE_ACCESS_TOOL_RECIPE_SERIALIZER.get());
	}

	public static CopyPositionComponentItemRecipe sentryRemoteAccessTool(CraftingBookCategory craftingBookCategory) {
		return new CopyPositionComponentItemRecipe(craftingBookCategory, SCContent.SENTRY_REMOTE_ACCESS_TOOL, tag -> !SentryRemoteAccessToolItem.hasSentryAdded(tag), SCContent.COPY_SENTRY_REMOTE_ACCESS_TOOL_RECIPE_SERIALIZER.get());
	}

	public static CopyPositionComponentItemRecipe sonicSecuritySystem(CraftingBookCategory craftingBookCategory) {
		return new CopyPositionComponentItemRecipe(craftingBookCategory, SCContent.SONIC_SECURITY_SYSTEM_ITEM, tag -> !SonicSecuritySystemItem.hasLinkedBlock(tag), SCContent.COPY_SONIC_SECURITY_SYSTEM_RECIPE_SERIALIZER.get());
	}

	@Override
	public boolean matchesFirstItem(ItemStack stack) {
		return stack.is(item) && !isDataEmpty.test(stack.getOrCreateTag());
	}

	@Override
	public boolean matchesSecondItem(ItemStack stack) {
		return stack.is(item) && isDataEmpty.test(stack.getOrCreateTag());
	}

	@Override
	public ItemStack combine(ItemStack itemWithPositions, ItemStack emptyItem) {
		ItemStack result = new ItemStack(item, 2);

		result.getOrCreateTag().merge(itemWithPositions.getOrCreateTag());
		return result;
	}

	@Override
	public RecipeSerializer<? extends CustomRecipe> getSerializer() {
		return serializer;
	}
}
