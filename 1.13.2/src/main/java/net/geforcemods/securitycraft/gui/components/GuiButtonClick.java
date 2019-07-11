package net.geforcemods.securitycraft.gui.components;

import java.util.function.Consumer;

import net.minecraft.client.gui.GuiButton;
import net.minecraftforge.fml.client.config.GuiButtonExt;

public class GuiButtonClick extends GuiButtonExt
{
	private Consumer<GuiButton> onClick;

	public GuiButtonClick(int id, int xPos, int yPos, String displayString, Consumer<GuiButton> onClick)
	{
		super(id, xPos, yPos, displayString);

		this.onClick = onClick;
	}

	public GuiButtonClick(int id, int xPos, int yPos, int width, int height, String displayString, Consumer<GuiButton> onClick)
	{
		super(id, xPos, yPos, width, height, displayString);

		this.onClick = onClick;
	}

	@Override
	public void onClick(double mouseX, double mouseY)
	{
		if(onClick != null)
			onClick.accept(this);
	}
}
