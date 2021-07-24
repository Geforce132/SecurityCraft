package net.geforcemods.securitycraft.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.geforcemods.securitycraft.containers.BriefcaseContainer;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BriefcaseInventoryScreen extends AbstractContainerScreen<BriefcaseContainer> {

	private static final ResourceLocation TEXTURE = new ResourceLocation("securitycraft:textures/gui/container/briefcase_inventory.png");
	private final String formattedTitle;

	public BriefcaseInventoryScreen(BriefcaseContainer container, Inventory inventory, Component name) {
		super(container, inventory, name);

		formattedTitle = name.getString();
	}

	@Override
	public void render(PoseStack matrix, int mouseX, int mouseY, float partialTicks)
	{
		super.render(matrix, mouseX, mouseY, partialTicks);

		if(getSlotUnderMouse() != null && !getSlotUnderMouse().getItem().isEmpty())
			renderTooltip(matrix, getSlotUnderMouse().getItem(), mouseX, mouseY);
	}

	@Override
	protected void renderLabels(PoseStack matrix, int mouseX, int mouseY) {
		font.draw(matrix, formattedTitle, imageWidth / 2 - font.width(formattedTitle) / 2, 6, 4210752);
	}

	@Override
	protected void renderBg(PoseStack matrix, float partialTicks, int mouseX, int mouseY) {
		renderBackground(matrix);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		minecraft.getTextureManager().bindForSetup(TEXTURE);
		int startX = (width - imageWidth) / 2;
		int startY = (height - imageHeight) / 2;
		this.blit(matrix, startX, startY, 0, 0, imageWidth, imageHeight);
	}

}
