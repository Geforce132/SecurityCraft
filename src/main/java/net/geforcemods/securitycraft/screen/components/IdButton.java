package net.geforcemods.securitycraft.screen.components;

import java.util.function.Consumer;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraftforge.fmlclient.gui.widget.ExtendedButton;

public class IdButton extends ExtendedButton
{
	public final int id;

	public IdButton(int id, int xPos, int yPos, int width, int height, String displayString, Consumer<IdButton> onClick)
	{
		this(id, xPos, yPos, width, height, new TextComponent(displayString), onClick);
	}

	public IdButton(int id, int xPos, int yPos, int width, int height, Component displayString, Consumer<IdButton> onClick)
	{
		super(xPos, yPos, width, height, displayString, b -> {
			if(onClick != null)
				onClick.accept((IdButton)b);
		});

		this.id = id;
	}
}
