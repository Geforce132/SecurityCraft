package net.geforcemods.securitycraft.screen.components;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraftforge.client.gui.ScreenUtils;

public class SmallXButton extends Button {
	public SmallXButton(int xPos, int yPos, OnPress handler) {
		super(xPos, yPos, 8, 8, Component.literal("x"), handler, DEFAULT_NARRATION);
	}

	@Override
	public void renderWidget(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
		Minecraft mc = Minecraft.getInstance();
		int v = !active ? 0 : (isHoveredOrFocused() ? 2 : 1);

		ScreenUtils.blitWithBorder(poseStack, WIDGETS_LOCATION, getX(), getY(), 0, 46 + v * 20, width, height, 200, 20, 2, 3, 2, 2, 0);
		drawCenteredString(poseStack, mc.font, Language.getInstance().getVisualOrder(getMessage()), getX() + width / 2, getY() + (height - 8) / 2, getFGColor());
	}
}
