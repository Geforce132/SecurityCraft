package net.geforcemods.securitycraft.screen.components;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Button.OnTooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

// TODO: Use this for more classes to be more in line with how tooltips work in 1.19.4+
public record Tooltip(Screen screen, Font font, Component text) implements OnTooltip {
	@Override
	public void onTooltip(Button button, PoseStack poseStack, int mouseX, int mouseY) {
		screen.renderTooltip(poseStack, font.split(text, 150), mouseX, mouseY);
	}
}