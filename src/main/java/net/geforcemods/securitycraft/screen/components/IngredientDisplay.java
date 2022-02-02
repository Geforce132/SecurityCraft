package net.geforcemods.securitycraft.screen.components;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public class IngredientDisplay implements Widget {
	private static final int DISPLAY_LENGTH = 20;
	private final int x;
	private final int y;
	private ItemStack[] stacks;
	private int currentRenderingStack = 0;
	private float ticksToChange = DISPLAY_LENGTH;

	public IngredientDisplay(int x, int y) {
		this.x = x;
		this.y = y;
	}

	@Override
	public void render(PoseStack pose, int mouseX, int mouseY, float partialTick) {
		if (stacks == null || stacks.length == 0)
			return;

		Minecraft.getInstance().getItemRenderer().renderAndDecorateItem(stacks[currentRenderingStack], x, y);
		ticksToChange -= partialTick;

		if (ticksToChange <= 0) {
			if (++currentRenderingStack >= stacks.length)
				currentRenderingStack = 0;

			ticksToChange = DISPLAY_LENGTH;
		}
	}

	public void setIngredient(Ingredient ingredient) {
		stacks = ingredient.getItems();
		currentRenderingStack = 0;
		ticksToChange = DISPLAY_LENGTH;
	}

	public ItemStack getCurrentStack() {
		return currentRenderingStack >= 0 && currentRenderingStack < stacks.length && stacks.length != 0 ? stacks[currentRenderingStack] : ItemStack.EMPTY;
	}
}
