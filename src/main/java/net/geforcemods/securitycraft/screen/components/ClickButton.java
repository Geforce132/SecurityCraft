package net.geforcemods.securitycraft.screen.components;

import java.util.function.Consumer;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.client.config.GuiButtonExt;

public class ClickButton extends GuiButtonExt {
	private Consumer<ClickButton> onClick;
	public Tooltip tooltip;

	public ClickButton(int id, int xPos, int yPos, int width, int height, String displayString, Consumer<ClickButton> onClick) {
		super(id, xPos, yPos, width, height, displayString);
		this.onClick = onClick;
	}

	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY, float partial) {
		super.drawButton(mc, mouseX, mouseY, partial);

		if (visible && hovered && tooltip != null)
			tooltip.render(this, mouseX, mouseY);
	}

	public void onClick() {
		if (onClick != null)
			onClick.accept(this);
	}
}
