package net.geforcemods.securitycraft.gui.components;

import cpw.mods.fml.client.config.HoverChecker;
import net.minecraft.item.ItemStack;

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
