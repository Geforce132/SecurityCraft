package net.geforcemods.securitycraft.gui.components;

import java.util.function.Consumer;

import net.minecraftforge.fml.client.config.GuiButtonExt;

public class ClickButton extends GuiButtonExt
{
	private Consumer<ClickButton> onClick;

	public ClickButton(int id, int xPos, int yPos, int width, int height, String displayString, Consumer<ClickButton> onClick)
	{
		super(id, xPos, yPos, width, height, displayString);

		this.onClick = onClick;
	}

	public void onClick()
	{
		if(onClick != null)
			onClick.accept(this);
	}
}
