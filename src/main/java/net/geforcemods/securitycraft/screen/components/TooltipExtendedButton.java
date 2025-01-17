package net.geforcemods.securitycraft.screen.components;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.client.gui.widget.ExtendedButton;

public class TooltipExtendedButton extends ExtendedButton {
	public TooltipExtendedButton(int xPos, int yPos, int width, int height, ITextComponent displayString, IPressable handler) {
		super(xPos, yPos, width, height, displayString, handler);
	}

	@Override
	public void renderButton(MatrixStack poseStack, int mouseX, int mouseY, float partialTick) {
		super.renderButton(poseStack, mouseX, mouseY, partialTick);

		if (isHovered())
			renderToolTip(poseStack, mouseX, mouseY);
	}
}
