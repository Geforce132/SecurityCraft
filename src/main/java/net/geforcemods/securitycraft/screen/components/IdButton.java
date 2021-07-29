package net.geforcemods.securitycraft.screen.components;

import java.util.function.Consumer;

import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;

public class IdButton extends Button
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
