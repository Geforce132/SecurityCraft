package net.geforcemods.securitycraft.misc;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import net.minecraft.advancements.critereon.NBTPredicate;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.NBTTagCompound;

public class PartialNBTIngredient extends Ingredient { //Copied and adapted from 1.18.2
	private final Set<Item> items;
	private final NBTPredicate predicate;

	protected PartialNBTIngredient(Set<Item> items, NBTTagCompound nbt) {
		super(items.stream().map(item -> {
			ItemStack stack = new ItemStack(item);

			// copy NBT to prevent the stack from modifying the original, as capabilities or vanilla item durability will modify the tag
			stack.setTagCompound(nbt.copy());
			return stack;
		}).toArray(ItemStack[]::new));

		if (items.isEmpty())
			throw new IllegalArgumentException("Cannot create a PartialNBTIngredient with no items");

		this.items = Collections.unmodifiableSet(items);
		predicate = new NBTPredicate(nbt);
	}

	public static PartialNBTIngredient of(NBTTagCompound nbt, Item... items) {
		return new PartialNBTIngredient(Arrays.stream(items).collect(Collectors.toSet()), nbt);
	}

	@Override
	public boolean apply(ItemStack input) {
		if (input == null)
			return false;

		return items.contains(input.getItem()) && predicate.test(input.getItem().getNBTShareTag(input));
	}

	@Override
	public boolean isSimple() {
		return false;
	}
}
