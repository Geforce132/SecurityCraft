package net.geforcemods.securitycraft.screen.components;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.network.chat.Component;
import net.minecraftforge.client.gui.widget.ExtendedButton;

public class TooltipExtendedButton extends ExtendedButton {
	public TooltipExtendedButton(int xPos, int yPos, int width, int height, Component displayString, OnPress handler) {
		super(xPos, yPos, width, height, displayString, handler);
	}

	@Override
	public void renderButton(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
		super.renderButton(poseStack, mouseX, mouseY, partialTick);

		if (isHoveredOrFocused())
			renderToolTip(poseStack, mouseX, mouseY);
	}
}
