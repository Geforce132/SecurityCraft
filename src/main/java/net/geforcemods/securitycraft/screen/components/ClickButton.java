package net.geforcemods.securitycraft.screen.components;

import java.util.function.Consumer;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.client.gui.widget.ExtendedButton;

public class ClickButton extends ExtendedButton
{
	private Consumer<ClickButton> onClick;
	public int id;

	public ClickButton(int id, int xPos, int yPos, int width, int height, String displayString, Consumer<ClickButton> onClick)
	{
		this(id, xPos, yPos, width, height, new StringTextComponent(displayString), onClick);
	}

	public ClickButton(int id, int xPos, int yPos, int width, int height, ITextComponent displayString, Consumer<ClickButton> onClick)
	{
		super(xPos, yPos, width, height, displayString, b -> {});

		this.id = id;
		this.onClick = onClick;
	}

	@Override
	public void onClick(double mouseX, double mouseY)
	{
		if(onClick != null)
			onClick.accept(this);
	}
}
