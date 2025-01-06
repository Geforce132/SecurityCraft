package net.geforcemods.securitycraft.screen.components;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public class IngredientDisplay implements Renderable {
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

		Minecraft.getInstance().getItemRenderer().renderAndDecorateItem(pose, stacks[currentRenderingStack], x, y);
	}

	public void tick() {
		if (!Screen.hasShiftDown() && --ticksToChange <= 0) {
			changeRenderingStack(1);
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

	public void changeRenderingStack(double direction) {
		currentRenderingStack += Math.signum(direction);

		if (currentRenderingStack < 0)
			currentRenderingStack = stacks.length - 1;
		else if (currentRenderingStack >= stacks.length)
			currentRenderingStack = 0;
	}
}
