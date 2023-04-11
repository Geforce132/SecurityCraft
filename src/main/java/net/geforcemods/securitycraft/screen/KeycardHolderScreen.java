package net.geforcemods.securitycraft.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.inventory.BriefcaseMenu;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class KeycardHolderScreen extends AbstractContainerScreen<BriefcaseMenu> {
	private static final ResourceLocation TEXTURE = new ResourceLocation(SecurityCraft.MODID, "textures/gui/container/briefcase_inventory.png");
	private final String formattedTitle;

	public KeycardHolderScreen(BriefcaseMenu menu, Inventory inventory, Component title) {
		super(menu, inventory, title);

		formattedTitle = title.getString();
	}

	@Override
	public void render(PoseStack pose, int mouseX, int mouseY, float partialTicks) {
		super.render(pose, mouseX, mouseY, partialTicks);

		if (getSlotUnderMouse() != null && !getSlotUnderMouse().getItem().isEmpty())
			renderTooltip(pose, getSlotUnderMouse().getItem(), mouseX, mouseY);
	}

	@Override
	protected void renderLabels(PoseStack pose, int mouseX, int mouseY) {
		font.draw(pose, formattedTitle, imageWidth / 2 - font.width(formattedTitle) / 2, 6, 4210752);
	}

	@Override
	protected void renderBg(PoseStack pose, float partialTicks, int mouseX, int mouseY) {
		renderBackground(pose);
		RenderSystem._setShaderTexture(0, TEXTURE);
		blit(pose, leftPos, topPos, 0, 0, imageWidth, imageHeight);
	}
}
