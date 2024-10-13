package net.geforcemods.securitycraft.screen.components;

import java.util.List;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public class IngredientDisplay implements Renderable {
	private static final int DISPLAY_LENGTH = 20;
	private final int x;
	private final int y;
	private Ingredient ingredient;
	private int currentRenderingStack = 0;
	private float ticksToChange = DISPLAY_LENGTH;

	public IngredientDisplay(int x, int y) {
		this.x = x;
		this.y = y;
	}

	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
		if (ingredient == null)
			return;

		guiGraphics.renderItem(ingredient.stacks().get(currentRenderingStack), x, y);

		if (!Screen.hasShiftDown()) {
			ticksToChange -= partialTick;

			if (ticksToChange <= 0) {
				changeRenderingStack(1);
				ticksToChange = DISPLAY_LENGTH;
			}
		}
	}

	public void setIngredient(Ingredient ingredient) {
		this.ingredient = ingredient;
		currentRenderingStack = 0;
		ticksToChange = DISPLAY_LENGTH;
	}

	public ItemStack getCurrentStack() {
		if (ingredient != null) {
			List<ItemStack> stacks = ingredient.stacks();

			return currentRenderingStack >= 0 && currentRenderingStack < stacks.size() && !stacks.isEmpty() ? stacks.get(currentRenderingStack) : ItemStack.EMPTY;
		}
		else
			return ItemStack.EMPTY;
	}

	public void changeRenderingStack(double direction) {
		if (ingredient == null)
			currentRenderingStack = 0;
		else {
			int size = ingredient.stacks().size();

			currentRenderingStack += Math.signum(direction);

			if (currentRenderingStack < 0)
				currentRenderingStack = size - 1;
			else if (currentRenderingStack >= size)
				currentRenderingStack = 0;
		}
	}
}
