package net.geforcemods.securitycraft.recipe;

import java.util.function.Predicate;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.items.CameraMonitorItem;
import net.geforcemods.securitycraft.items.MineRemoteAccessToolItem;
import net.geforcemods.securitycraft.items.SentryRemoteAccessToolItem;
import net.geforcemods.securitycraft.items.SonicSecuritySystemItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.SpecialRecipe;
import net.minecraft.item.crafting.SpecialRecipeSerializer;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ObjectHolder;

public class CopyPositionComponentItemRecipe extends CombineRecipe {
	@ObjectHolder(SecurityCraft.MODID + ":copy_camera_monitor_recipe")
	public static final SpecialRecipeSerializer<CopyPositionComponentItemRecipe> COPY_CAMERA_MONITOR_RECIPE_SERIALIZER = null;
	@ObjectHolder(SecurityCraft.MODID + ":copy_mine_remote_access_tool_recipe")
	public static final SpecialRecipeSerializer<CopyPositionComponentItemRecipe> COPY_MINE_REMOTE_ACCESS_TOOL_RECIPE_SERIALIZER = null;
	@ObjectHolder(SecurityCraft.MODID + ":copy_sentry_remote_access_tool_recipe")
	public static final SpecialRecipeSerializer<CopyPositionComponentItemRecipe> COPY_SENTRY_REMOTE_ACCESS_TOOL_RECIPE_SERIALIZER = null;
	@ObjectHolder(SecurityCraft.MODID + ":copy_sonic_security_system_recipe")
	public static final SpecialRecipeSerializer<CopyPositionComponentItemRecipe> COPY_SONIC_SECURITY_SYSTEM_RECIPE_SERIALIZER = null;
	private final Item item;
	private final Predicate<CompoundNBT> isDataEmpty;
	private final IRecipeSerializer<? extends SpecialRecipe> serializer;

	public CopyPositionComponentItemRecipe(ResourceLocation id, Item item, Predicate<CompoundNBT> hasData, IRecipeSerializer<? extends SpecialRecipe> serializer) {
		super(id);
		this.item = item;
		this.isDataEmpty = tag -> !hasData.test(tag);
		this.serializer = serializer;
	}

	public static CopyPositionComponentItemRecipe cameraMonitor(ResourceLocation id) {
		return new CopyPositionComponentItemRecipe(id, SCContent.CAMERA_MONITOR.get(), CameraMonitorItem::hasCameraAdded, COPY_CAMERA_MONITOR_RECIPE_SERIALIZER);
	}

	public static CopyPositionComponentItemRecipe mineRemoteAccessTool(ResourceLocation id) {
		return new CopyPositionComponentItemRecipe(id, SCContent.MINE_REMOTE_ACCESS_TOOL.get(), MineRemoteAccessToolItem::hasMineAdded, COPY_MINE_REMOTE_ACCESS_TOOL_RECIPE_SERIALIZER);
	}

	public static CopyPositionComponentItemRecipe sentryRemoteAccessTool(ResourceLocation id) {
		return new CopyPositionComponentItemRecipe(id, SCContent.SENTRY_REMOTE_ACCESS_TOOL.get(), SentryRemoteAccessToolItem::hasSentryAdded, COPY_SENTRY_REMOTE_ACCESS_TOOL_RECIPE_SERIALIZER);
	}

	public static CopyPositionComponentItemRecipe sonicSecuritySystem(ResourceLocation id) {
		return new CopyPositionComponentItemRecipe(id, SCContent.SONIC_SECURITY_SYSTEM_ITEM.get(), SonicSecuritySystemItem::hasLinkedBlock, COPY_SONIC_SECURITY_SYSTEM_RECIPE_SERIALIZER);
	}

	@Override
	public boolean matchesFirstItem(ItemStack stack) {
		return stack.getItem() == item && !isDataEmpty.test(stack.getOrCreateTag());
	}

	@Override
	public boolean matchesSecondItem(ItemStack stack) {
		return stack.getItem() == item && isDataEmpty.test(stack.getOrCreateTag());
	}

	@Override
	public ItemStack combine(ItemStack itemWithPositions, ItemStack emptyItem) {
		ItemStack result = new ItemStack(item, 2);

		result.getOrCreateTag().merge(itemWithPositions.getOrCreateTag());
		return result;
	}

	@Override
	public IRecipeSerializer<? extends SpecialRecipe> getSerializer() {
		return serializer;
	}
}
