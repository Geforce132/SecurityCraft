package net.geforcemods.securitycraft.screen;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.inventory.SingleLensMenu;
import net.geforcemods.securitycraft.inventory.SingleLensMenu.SingleLensContainer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

public class SingleLensScreen extends AbstractContainerScreen<SingleLensMenu> {
	private static final Identifier TEXTURE = SecurityCraft.resLoc("textures/gui/container/single_lens.png");
	private static final Identifier LENS_SLOT = SecurityCraft.resLoc("slot/lens");

	public SingleLensScreen(SingleLensMenu menu, Inventory inv, Component title) {
		super(menu, inv, title);
	}

	@Override
	protected void init() {
		super.init();
		titleLabelX = imageWidth / 2 - font.width(title) / 2;
	}

	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
		super.render(guiGraphics, mouseX, mouseY, partialTicks);
		renderTooltip(guiGraphics, mouseX, mouseY);
	}

	@Override
	protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
		guiGraphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, leftPos, topPos, 0.0F, 0.0F, imageWidth, imageHeight, 256, 256);

		if (((SingleLensContainer) menu.be).getLensContainer().getItem(0).isEmpty())
			guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, LENS_SLOT, leftPos + 80, topPos + 20, 16, 16);
	}
}
