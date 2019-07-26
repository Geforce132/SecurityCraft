package net.geforcemods.securitycraft.gui.components;

import java.util.function.Consumer;

import net.minecraftforge.fml.client.config.GuiButtonExt;

public class GuiButtonClick extends GuiButtonExt
{
	private Consumer<GuiButtonClick> onClick;
	public int id;

	public GuiButtonClick(int id, int xPos, int yPos, int width, int height, String displayString, Consumer<GuiButtonClick> onClick)
	{
		super(xPos, yPos, width, height, displayString, b -> {});

		this.id = id;
		this.onClick = onClick;
	}

	@Override
	public void onClick(double mouseX, double mouseY)
	{
		onClick.accept(this);
	}
}
