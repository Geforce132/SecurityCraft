package net.geforcemods.securitycraft.screen;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.inventory.SingleLensMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

public class SingleLensScreen extends AbstractContainerScreen<SingleLensMenu> {
	private static final Identifier TEXTURE = SecurityCraft.resLoc("textures/gui/container/single_lens.png");

	public SingleLensScreen(SingleLensMenu menu, Inventory inv, Component title) {
		super(menu, inv, title);
	}

	@Override
	protected void init() {
		super.init();
		titleLabelX = imageWidth / 2 - font.width(title) / 2;
	}

	@Override
	public void extractRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTicks) {
		super.extractRenderState(guiGraphics, mouseX, mouseY, partialTicks);
		extractTooltip(guiGraphics, mouseX, mouseY);
	}

	@Override
	public void extractBackground(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float a) {
		super.extractBackground(guiGraphics, mouseX, mouseY, a);
		guiGraphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, leftPos, topPos, 0.0F, 0.0F, imageWidth, imageHeight, 256, 256);
	}
}
