package net.geforcemods.securitycraft.misc;

import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.advancements.criterion.NBTPredicate;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.JSONUtils;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.IIngredientSerializer;
import net.minecraftforge.registries.ForgeRegistries;

public class PartialNBTIngredient extends Ingredient { //Copied and adapted from 1.18.2
	private final Set<Item> items;
	private final CompoundNBT nbt;
	private final NBTPredicate predicate;

	protected PartialNBTIngredient(Set<Item> items, CompoundNBT nbt) {
		super(items.stream().map(item -> {
			ItemStack stack = new ItemStack(item);

			// copy NBT to prevent the stack from modifying the original, as capabilities or vanilla item durability will modify the tag
			stack.setTag(nbt.copy());
			return new Ingredient.SingleItemList(stack);
		}));

		if (items.isEmpty())
			throw new IllegalArgumentException("Cannot create a PartialNBTIngredient with no items");

		this.items = Collections.unmodifiableSet(items);
		this.nbt = nbt;
		predicate = new NBTPredicate(nbt);
	}

	public static PartialNBTIngredient of(CompoundNBT nbt, IItemProvider... items) {
		return new PartialNBTIngredient(Arrays.stream(items).map(IItemProvider::asItem).collect(Collectors.toSet()), nbt);
	}

	@Override
	public boolean test(ItemStack input) {
		if (input == null)
			return false;

		return items.contains(input.getItem()) && predicate.matches(input.getShareTag());
	}

	@Override
	public boolean isSimple() {
		return false;
	}

	@Override
	public IIngredientSerializer<? extends Ingredient> getSerializer() {
		return Serializer.INSTANCE;
	}

	@Override
	public JsonElement toJson() {
		JsonObject json = new JsonObject();

		json.addProperty("type", CraftingHelper.getID(Serializer.INSTANCE).toString());

		if (items.size() == 1)
			json.addProperty("item", items.iterator().next().getRegistryName().toString());
		else {
			JsonArray jsonItems = new JsonArray();

			// ensure the order of items in the set is deterministic when saved to JSON
			items.stream().map(Item::getRegistryName).sorted().forEach(name -> jsonItems.add(name.toString()));
			json.add("items", jsonItems);
		}

		json.addProperty("nbt", nbt.toString());
		return json;
	}

	public static class Serializer implements IIngredientSerializer<PartialNBTIngredient> {
		public static final Serializer INSTANCE = new Serializer();

		@Override
		public PartialNBTIngredient parse(JsonObject json) {
			// parse items
			Set<Item> items;

			if (json.has("item"))
				items = Sets.newHashSet(CraftingHelper.getItemStack(json, false).getItem());
			else if (json.has("items")) {
				ImmutableSet.Builder<Item> builder = ImmutableSet.builder();
				JsonArray itemArray = JSONUtils.getAsJsonArray(json, "items");

				for (int i = 0; i < itemArray.size(); i++) {
					JsonElement item = itemArray.get(i);

					if (item.isJsonObject())
						builder.add(CraftingHelper.getItemStack(item.getAsJsonObject(), false).getItem());
				}

				items = builder.build();
			}
			else
				throw new JsonSyntaxException("Must set either 'item' or 'items'");

			// parse NBT
			if (!json.has("nbt"))
				throw new JsonSyntaxException("Missing nbt, expected to find a String or JsonObject");

			return new PartialNBTIngredient(items, getNBT(json));
		}

		@Override
		public PartialNBTIngredient parse(PacketBuffer buffer) {
			Set<Item> items = Stream.generate(() -> buffer.readRegistryIdUnsafe(ForgeRegistries.ITEMS)).limit(buffer.readVarInt()).collect(Collectors.toSet());
			CompoundNBT nbt = buffer.readNbt();

			return new PartialNBTIngredient(items, Objects.requireNonNull(nbt));
		}

		@Override
		public void write(PacketBuffer buffer, PartialNBTIngredient ingredient) {
			buffer.writeVarInt(ingredient.items.size());

			for (Item item : ingredient.items) {
				buffer.writeRegistryIdUnsafe(ForgeRegistries.ITEMS, item);
			}

			buffer.writeNbt(ingredient.nbt);
		}

		public static CompoundNBT getNBT(JsonObject json) {
			CompoundNBT tag = new CompoundNBT();

			try {
				tag = JsonToNBT.parseTag(JSONUtils.convertToString(json.get("nbt"), "nbt"));
			}
			catch (CommandSyntaxException e) {
				e.printStackTrace();
			}

			return tag;
		}
	}
}
