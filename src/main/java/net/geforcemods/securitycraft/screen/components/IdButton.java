package net.geforcemods.securitycraft.screen.components;

import java.util.function.Consumer;

import net.minecraftforge.fml.client.gui.widget.ExtendedButton;

public class IdButton extends ExtendedButton
{
	public final int id;

	public IdButton(int id, int xPos, int yPos, int width, int height, String displayString, Consumer<IdButton> onClick)
	{
		super(xPos, yPos, width, height, displayString, b -> {
			if(onClick != null)
				onClick.accept((IdButton)b);
		});

		this.id = id;
	}
}
