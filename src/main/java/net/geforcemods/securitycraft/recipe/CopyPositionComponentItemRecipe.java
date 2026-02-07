package net.geforcemods.securitycraft.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.components.GlobalPositionComponent;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class CopyPositionComponentItemRecipe extends CombineRecipe {
	public static final MapCodec<CopyPositionComponentItemRecipe> MAP_CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
			Ingredient.CODEC.fieldOf("input").forGetter(CombineRecipe::first),
			Item.CODEC.fieldOf("result").forGetter(CombineRecipe::resultItem),
			DataComponentType.CODEC.fieldOf("component_type").forGetter(CopyPositionComponentItemRecipe::componentType)
	).apply(i, CopyPositionComponentItemRecipe::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, CopyPositionComponentItemRecipe> STREAM_CODEC = StreamCodec.composite(
			Ingredient.CONTENTS_STREAM_CODEC, CombineRecipe::first,
			Item.STREAM_CODEC, CombineRecipe::resultItem,
			DataComponentType.STREAM_CODEC, CopyPositionComponentItemRecipe::componentType,
			CopyPositionComponentItemRecipe::new
	);
	public static final RecipeSerializer<? extends CustomRecipe> SERIALIZER = new RecipeSerializer<>(MAP_CODEC, STREAM_CODEC);
	private final DataComponentType<?> componentType;

	public CopyPositionComponentItemRecipe(Ingredient input, Holder<Item> result, DataComponentType<?> componentType) {
		super(input, input, new ItemStackTemplate(result, 1));
		this.componentType = componentType;
	}

	public static CopyPositionComponentItemRecipe cameraMonitor() {
		return new CopyPositionComponentItemRecipe(Ingredient.of(SCContent.CAMERA_MONITOR), SCContent.CAMERA_MONITOR, SCContent.BOUND_CAMERAS.get());
	}

	public static CopyPositionComponentItemRecipe mineRemoteAccessTool() {
		return new CopyPositionComponentItemRecipe(Ingredient.of(SCContent.MINE_REMOTE_ACCESS_TOOL), SCContent.MINE_REMOTE_ACCESS_TOOL, SCContent.BOUND_MINES.get());
	}

	public static CopyPositionComponentItemRecipe sentryRemoteAccessTool() {
		return new CopyPositionComponentItemRecipe(Ingredient.of(SCContent.SENTRY_REMOTE_ACCESS_TOOL), SCContent.SENTRY_REMOTE_ACCESS_TOOL, SCContent.BOUND_SENTRIES.get());
	}

	public static CopyPositionComponentItemRecipe sonicSecuritySystem() {
		return new CopyPositionComponentItemRecipe(Ingredient.of(SCContent.SONIC_SECURITY_SYSTEM_ITEM), SCContent.SONIC_SECURITY_SYSTEM_ITEM, SCContent.SSS_LINKED_BLOCKS.get());
	}

	@Override
	public boolean matchesFirstItem(ItemStack stack) {
		Object component = stack.get(componentType);

		return super.matchesFirstItem(stack) && component instanceof GlobalPositionComponent<?, ?, ?> positionComponent && !positionComponent.isEmpty();
	}

	@Override
	public boolean matchesSecondItem(ItemStack stack) {
		Object component = stack.get(componentType);

		return super.matchesSecondItem(stack) && (!(component instanceof GlobalPositionComponent<?, ?, ?> positionComponent) || positionComponent.isEmpty());
	}

	@Override
	public ItemStack assemble(CraftingInput inv) {
		ItemStack resultStack = super.assemble(inv);

		resultStack.setCount(2);
		return resultStack;
	}

	@Override
	public RecipeSerializer<? extends CustomRecipe> getSerializer() {
		return SERIALIZER;
	}

	public DataComponentType<?> componentType() {
		return componentType;
	}
}
