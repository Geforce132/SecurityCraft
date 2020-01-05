package net.geforcemods.securitycraft.screen.components;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;

public class IngredientDisplay
{
	private static final int DISPLAY_LENGTH = 20;
	private final int x;
	private final int y;
	private ItemStack[] stacks;
	private int currentRenderingStack = 0;
	private float ticksToChange = DISPLAY_LENGTH;

	public IngredientDisplay(int x, int y)
	{
		this.x = x;
		this.y = y;
	}

	public IngredientDisplay(int x, int y, Ingredient ingredient)
	{
		this.x = x;
		this.y = y;
		this.stacks = ingredient.getMatchingStacks();
	}

	public void render(Minecraft mc, float partialTicks)
	{
		if(stacks == null || stacks.length == 0)
			return;

		mc.getItemRenderer().renderItemAndEffectIntoGUI(stacks[currentRenderingStack], x, y);
		ticksToChange -= partialTicks;

		if(ticksToChange <= 0)
		{
			if(++currentRenderingStack >= stacks.length)
				currentRenderingStack = 0;

			ticksToChange = DISPLAY_LENGTH;
		}
	}

	public void setIngredient(Ingredient ingredient)
	{
		stacks = ingredient.getMatchingStacks();
		currentRenderingStack = 0;
		ticksToChange = DISPLAY_LENGTH;
	}

	public ItemStack getCurrentStack()
	{
		return currentRenderingStack >= 0 && currentRenderingStack < stacks.length && stacks.length != 0 ? stacks[currentRenderingStack] : ItemStack.EMPTY;
	}
}
