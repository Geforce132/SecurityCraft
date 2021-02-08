package net.geforcemods.securitycraft.screen.components;

import net.minecraft.client.gui.widget.Widget;
import net.minecraft.item.ItemStack;

public class StackHoverChecker extends HoverChecker
{
	private final ItemStack stack;

	public StackHoverChecker(ItemStack stack, int top, int bottom, int left, int right)
	{
		super(top, bottom, left, right);

		this.stack = stack;
	}

	public StackHoverChecker(ItemStack stack, Widget widget)
	{
		super(widget);

		this.stack = stack;
	}

	public ItemStack getStack()
	{
		return stack;
	}
}
