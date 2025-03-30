package net.geforcemods.securitycraft.screen.components;

import net.geforcemods.securitycraft.util.ClientUtils;
import net.geforcemods.securitycraft.util.GuiUtils;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;

public class IngredientDisplay {
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

	public void render(float partialTick) {
		if (stacks == null || stacks.length == 0)
			return;

		GuiUtils.drawItemStackToGui(stacks[currentRenderingStack], x, y, !(stacks[currentRenderingStack].getItem() instanceof ItemBlock));
	}

	public void tick() {
		if (!ClientUtils.hasShiftDown() && --ticksToChange <= 0) {
			changeRenderingStack(1);
			ticksToChange = DISPLAY_LENGTH;
		}
	}

	public void setIngredient(Ingredient ingredient) {
		stacks = ingredient.getMatchingStacks();
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
