package net.geforcemods.securitycraft.recipe;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.components.GlobalPositionComponent;
import net.geforcemods.securitycraft.components.GlobalPositions;
import net.geforcemods.securitycraft.components.NamedPositions;
import net.geforcemods.securitycraft.items.CameraMonitorItem;
import net.geforcemods.securitycraft.items.MineRemoteAccessToolItem;
import net.geforcemods.securitycraft.items.SentryRemoteAccessToolItem;
import net.geforcemods.securitycraft.items.SonicSecuritySystemItem;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;

@SuppressWarnings("rawtypes")
public class CopyPositionComponentItemRecipe<T extends GlobalPositionComponent> extends CombineRecipe {
	private final Holder<Item> item;
	private final Supplier<DataComponentType<T>> component;
	private final Function<List, T> creator;
	private final T defaultInstance;
	private final RecipeSerializer<? extends CustomRecipe> serializer;

	public CopyPositionComponentItemRecipe(CraftingBookCategory craftingBookCategory, Holder<Item> item, Supplier<DataComponentType<T>> component, Function<List, T> creator, T defaultInstance, RecipeSerializer<? extends CustomRecipe> serializer) {
		super(craftingBookCategory);
		this.item = item;
		this.component = component;
		this.creator = creator;
		this.defaultInstance = defaultInstance;
		this.serializer = serializer;
	}

	public static CopyPositionComponentItemRecipe<NamedPositions> cameraMonitor(CraftingBookCategory craftingBookCategory) {
		return new CopyPositionComponentItemRecipe<>(craftingBookCategory, SCContent.CAMERA_MONITOR, SCContent.BOUND_CAMERAS, NamedPositions::new, CameraMonitorItem.DEFAULT_NAMED_POSITIONS, SCContent.COPY_CAMERA_MONITOR_RECIPE_SERIALIZER.get());
	}

	public static CopyPositionComponentItemRecipe<GlobalPositions> mineRemoteAccessTool(CraftingBookCategory craftingBookCategory) {
		return new CopyPositionComponentItemRecipe<>(craftingBookCategory, SCContent.MINE_REMOTE_ACCESS_TOOL, SCContent.BOUND_MINES, GlobalPositions::new, MineRemoteAccessToolItem.DEFAULT_POSITIONS, SCContent.COPY_MINE_REMOTE_ACCESS_TOOL_RECIPE_SERIALIZER.get());
	}

	public static CopyPositionComponentItemRecipe<NamedPositions> sentryRemoteAccessTool(CraftingBookCategory craftingBookCategory) {
		return new CopyPositionComponentItemRecipe<>(craftingBookCategory, SCContent.SENTRY_REMOTE_ACCESS_TOOL, SCContent.BOUND_SENTRIES, NamedPositions::new, SentryRemoteAccessToolItem.DEFAULT_NAMED_POSITIONS, SCContent.COPY_SENTRY_REMOTE_ACCESS_TOOL_RECIPE_SERIALIZER.get());
	}

	public static CopyPositionComponentItemRecipe<GlobalPositions> sonicSecuritySystem(CraftingBookCategory craftingBookCategory) {
		return new CopyPositionComponentItemRecipe<>(craftingBookCategory, SCContent.SONIC_SECURITY_SYSTEM_ITEM, SCContent.SSS_LINKED_BLOCKS, GlobalPositions::new, SonicSecuritySystemItem.DEFAULT_POSITIONS, SCContent.COPY_SONIC_SECURITY_SYSTEM_RECIPE_SERIALIZER.get());
	}

	@Override
	public boolean matchesFirstItem(ItemStack stack) {
		return stack.is(item) && !stack.getOrDefault(component, defaultInstance).isEmpty();
	}

	@Override
	public boolean matchesSecondItem(ItemStack stack) {
		return stack.is(item) && stack.getOrDefault(component, defaultInstance).isEmpty();
	}

	@Override
	public ItemStack combine(ItemStack itemWithPositions, ItemStack emptyItem) {
		T positionsToCopy = itemWithPositions.getOrDefault(component, defaultInstance);
		ItemStack result = new ItemStack(item, 2);

		result.set(component, creator.apply(positionsToCopy.positions()));
		return result;
	}

	@Override
	public RecipeSerializer<? extends CustomRecipe> getSerializer() {
		return serializer;
	}
}
