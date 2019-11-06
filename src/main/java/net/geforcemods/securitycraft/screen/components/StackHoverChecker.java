package net.geforcemods.securitycraft.screen.components;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.client.config.HoverChecker;

public class StackHoverChecker extends HoverChecker
{
	private ItemStack stack;

	public StackHoverChecker(int top, int bottom, int left, int right, int threshold, ItemStack stack)
	{
		super(top, bottom, left, right, threshold);
		this.stack = stack;
	}

	public ItemStack getStack()
	{
		return stack;
	}
}
