package net.geforcemods.securitycraft.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.inventory.SingleLensMenu;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class SingleLensScreen extends ContainerScreen<SingleLensMenu> {
	private static final ResourceLocation TEXTURE = new ResourceLocation(SecurityCraft.MODID, "textures/gui/container/single_lens.png");

	public SingleLensScreen(SingleLensMenu menu, PlayerInventory inv, ITextComponent title) {
		super(menu, inv, title);
	}

	@Override
	protected void init() {
		super.init();
		titleLabelX = imageWidth / 2 - font.width(title) / 2;
	}

	@Override
	public void render(MatrixStack pose, int mouseX, int mouseY, float partialTicks) {
		super.render(pose, mouseX, mouseY, partialTicks);
		renderTooltip(pose, mouseX, mouseY);
	}

	@Override
	protected void renderBg(MatrixStack pose, float partialTicks, int mouseX, int mouseY) {
		renderBackground(pose);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		minecraft.getTextureManager().bind(TEXTURE);
		blit(pose, leftPos, topPos, 0, 0, imageWidth, imageHeight);
	}
}
