package net.geforcemods.securitycraft.recipe;

import java.util.function.Function;
import java.util.function.Supplier;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.components.GlobalPositions;
import net.geforcemods.securitycraft.components.ListModuleData;
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

public class CopyComponentItemRecipe<T> extends CombineRecipe {
	private final Holder<Item> item;
	private final Supplier<DataComponentType<T>> component;
	private final Function<T, T> creator;
	private final T defaultInstance;
	private final RecipeSerializer<? extends CustomRecipe> serializer;

	public CopyComponentItemRecipe(CraftingBookCategory craftingBookCategory, Holder<Item> item, Supplier<DataComponentType<T>> component, Function<T, T> creator, T defaultInstance, RecipeSerializer<? extends CustomRecipe> serializer) {
		super(craftingBookCategory);
		this.item = item;
		this.component = component;
		this.creator = creator;
		this.defaultInstance = defaultInstance;
		this.serializer = serializer;
	}

	public static CopyComponentItemRecipe<ListModuleData> allowlistModule(CraftingBookCategory craftingBookCategory) {
		return listModule(craftingBookCategory, SCContent.ALLOWLIST_MODULE, SCContent.COPY_ALLOWLIST_MODULE_RECIPE_SERIALIZER.get());
	}

	public static CopyComponentItemRecipe<NamedPositions> cameraMonitor(CraftingBookCategory craftingBookCategory) {
		return new CopyComponentItemRecipe<>(craftingBookCategory, SCContent.CAMERA_MONITOR, SCContent.BOUND_CAMERAS, pos -> new NamedPositions(pos.positions()), CameraMonitorItem.DEFAULT_NAMED_POSITIONS, SCContent.COPY_CAMERA_MONITOR_RECIPE_SERIALIZER.get());
	}

	public static CopyComponentItemRecipe<ListModuleData> denylistModule(CraftingBookCategory craftingBookCategory) {
		return listModule(craftingBookCategory, SCContent.DENYLIST_MODULE, SCContent.COPY_DENYLIST_MODULE_RECIPE_SERIALIZER.get());
	}

	public static CopyComponentItemRecipe<ListModuleData> listModule(CraftingBookCategory craftingBookCategory, Holder<Item> listModuleItem, RecipeSerializer<? extends CustomRecipe> serializer) {
		return new CopyComponentItemRecipe<>(craftingBookCategory, listModuleItem, SCContent.LIST_MODULE_DATA, data -> new ListModuleData(data.players(), data.teams(), data.affectEveryone()), ListModuleData.EMPTY, serializer);
	}

	public static CopyComponentItemRecipe<GlobalPositions> mineRemoteAccessTool(CraftingBookCategory craftingBookCategory) {
		return new CopyComponentItemRecipe<>(craftingBookCategory, SCContent.MINE_REMOTE_ACCESS_TOOL, SCContent.BOUND_MINES, pos -> new GlobalPositions(pos.positions()), MineRemoteAccessToolItem.DEFAULT_POSITIONS, SCContent.COPY_MINE_REMOTE_ACCESS_TOOL_RECIPE_SERIALIZER.get());
	}

	public static CopyComponentItemRecipe<NamedPositions> sentryRemoteAccessTool(CraftingBookCategory craftingBookCategory) {
		return new CopyComponentItemRecipe<>(craftingBookCategory, SCContent.SENTRY_REMOTE_ACCESS_TOOL, SCContent.BOUND_SENTRIES, pos -> new NamedPositions(pos.positions()), SentryRemoteAccessToolItem.DEFAULT_NAMED_POSITIONS, SCContent.COPY_SENTRY_REMOTE_ACCESS_TOOL_RECIPE_SERIALIZER.get());
	}

	public static CopyComponentItemRecipe<GlobalPositions> sonicSecuritySystem(CraftingBookCategory craftingBookCategory) {
		return new CopyComponentItemRecipe<>(craftingBookCategory, SCContent.SONIC_SECURITY_SYSTEM_ITEM, SCContent.SSS_LINKED_BLOCKS, pos -> new GlobalPositions(pos.positions()), SonicSecuritySystemItem.DEFAULT_POSITIONS, SCContent.COPY_SONIC_SECURITY_SYSTEM_RECIPE_SERIALIZER.get());
	}

	@Override
	public boolean matchesFirstItem(ItemStack stack) {
		return stack.is(item) && !stack.getOrDefault(component, defaultInstance).equals(defaultInstance);
	}

	@Override
	public boolean matchesSecondItem(ItemStack stack) {
		return stack.is(item) && stack.getOrDefault(component, defaultInstance).equals(defaultInstance);
	}

	@Override
	public ItemStack combine(ItemStack monitorWithPositions, ItemStack emptyMonitor) {
		T positionsToCopy = monitorWithPositions.getOrDefault(component, defaultInstance);
		ItemStack result = new ItemStack(item, 2);

		result.set(component, creator.apply(positionsToCopy));
		return result;
	}

	@Override
	public RecipeSerializer<? extends CustomRecipe> getSerializer() {
		return serializer;
	}
}
