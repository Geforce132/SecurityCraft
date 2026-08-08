package net.geforcemods.securitycraft.screen.components;

import java.util.function.Consumer;
import java.util.function.Predicate;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.util.CommonColors;

public class ErrorMarkingEditBox extends EditBox {
	private Predicate<String> validText;

	public ErrorMarkingEditBox(Font font, int width, int height, Component narration) {
		super(font, width, height, narration);
	}

	public ErrorMarkingEditBox(Font font, int x, int y, int width, int height, Component narration) {
		super(font, x, y, width, height, narration);
	}

	public void setValidText(Predicate<String> validText) {
		this.validText = validText;
		setResponder(_ -> {});
	}

	@Override
	public void setResponder(Consumer<String> responder) {
		super.setResponder(value -> {
			responder.accept(value);

			if (!validText.test(value))
				setTextColor(CommonColors.SOFT_RED);
			else
				setTextColor(CommonColors.WHITE);
		});
	}
}
