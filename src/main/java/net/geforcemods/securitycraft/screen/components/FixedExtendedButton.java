package net.geforcemods.securitycraft.screen.components;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraftforge.fmlclient.gui.widget.ExtendedButton;

public class FixedExtendedButton extends ExtendedButton
{
	public FixedExtendedButton(int xPos, int yPos, int width, int height, Component displayString, OnPress handler)
	{
		super(xPos, yPos, width, height, displayString, handler);
	}

	@Override
	public void renderButton(PoseStack mStack, int mouseX, int mouseY, float partial)
	{
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		super.renderButton(mStack, mouseX, mouseY, partial);
	}
}
