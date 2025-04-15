package net.geforcemods.securitycraft.recipe;

import java.util.function.Predicate;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.items.CameraMonitorItem;
import net.geforcemods.securitycraft.items.MineRemoteAccessToolItem;
import net.geforcemods.securitycraft.items.SentryRemoteAccessToolItem;
import net.geforcemods.securitycraft.items.SonicSecuritySystemItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class CopyPositionComponentItemRecipe extends CombineRecipe {
	private final Item item;
	private final Predicate<CompoundTag> isDataEmpty;
	private final RecipeSerializer<? extends CustomRecipe> serializer;

	public CopyPositionComponentItemRecipe(ResourceLocation id, CraftingBookCategory craftingBookCategory, Item item, Predicate<CompoundTag> isDataEmpty, RecipeSerializer<? extends CustomRecipe> serializer) {
		super(id, craftingBookCategory);
		this.item = item;
		this.isDataEmpty = isDataEmpty;
		this.serializer = serializer;
	}

	public static CopyPositionComponentItemRecipe cameraMonitor(ResourceLocation id, CraftingBookCategory craftingBookCategory) {
		return new CopyPositionComponentItemRecipe(id, craftingBookCategory, SCContent.CAMERA_MONITOR.get(), tag -> !CameraMonitorItem.hasCameraAdded(tag), SCContent.COPY_CAMERA_MONITOR_RECIPE_SERIALIZER.get());
	}

	public static CopyPositionComponentItemRecipe mineRemoteAccessTool(ResourceLocation id, CraftingBookCategory craftingBookCategory) {
		return new CopyPositionComponentItemRecipe(id, craftingBookCategory, SCContent.MINE_REMOTE_ACCESS_TOOL.get(), tag -> !MineRemoteAccessToolItem.hasMineAdded(tag), SCContent.COPY_MINE_REMOTE_ACCESS_TOOL_RECIPE_SERIALIZER.get());
	}

	public static CopyPositionComponentItemRecipe sentryRemoteAccessTool(ResourceLocation id, CraftingBookCategory craftingBookCategory) {
		return new CopyPositionComponentItemRecipe(id, craftingBookCategory, SCContent.SENTRY_REMOTE_ACCESS_TOOL.get(), tag -> !SentryRemoteAccessToolItem.hasSentryAdded(tag), SCContent.COPY_SENTRY_REMOTE_ACCESS_TOOL_RECIPE_SERIALIZER.get());
	}

	public static CopyPositionComponentItemRecipe sonicSecuritySystem(ResourceLocation id, CraftingBookCategory craftingBookCategory) {
		return new CopyPositionComponentItemRecipe(id, craftingBookCategory, SCContent.SONIC_SECURITY_SYSTEM_ITEM.get(), tag -> !SonicSecuritySystemItem.hasLinkedBlock(tag), SCContent.COPY_SONIC_SECURITY_SYSTEM_RECIPE_SERIALIZER.get());
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
	public ItemStack combine(ItemStack monitorWithPositions, ItemStack emptyMonitor) {
		ItemStack result = new ItemStack(item, 2);

		result.getOrCreateTag().merge(monitorWithPositions.getOrCreateTag());
		return result;
	}

	@Override
	public RecipeSerializer<? extends CustomRecipe> getSerializer() {
		return serializer;
	}
}
