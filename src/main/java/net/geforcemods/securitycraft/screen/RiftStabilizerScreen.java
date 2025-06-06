package net.geforcemods.securitycraft.screen;

import com.mojang.blaze3d.platform.InputConstants;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blockentities.RiftStabilizerBlockEntity;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.screen.components.ToggleScrollList;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.CommonColors;

public class RiftStabilizerScreen extends Screen {
	private static final ResourceLocation GUI_TEXTURE = SecurityCraft.resLoc("textures/gui/container/blank.png");
	public final Component scrollListTitle, smartModuleTooltip;
	private int imageWidth = 176;
	private int imageHeight = 166;
	private int leftPos;
	private int topPos;
	private boolean hasSmartModule;
	private boolean hasRedstoneModule;
	private RiftStabilizerBlockEntity be;

	public RiftStabilizerScreen(RiftStabilizerBlockEntity be) {
		super(be.getName());

		this.be = be;
		hasSmartModule = be.isModuleEnabled(ModuleType.SMART);
		hasRedstoneModule = be.isModuleEnabled(ModuleType.REDSTONE);
		scrollListTitle = Utils.localize("gui.securitycraft:rift_stabilizer.teleportationTypes");
		smartModuleTooltip = hasSmartModule ? Utils.localize("gui.securitycraft:rift_stabilizer.toggle") : Utils.localize("gui.securitycraft:rift_stabilizer.moduleRequired");
	}

	@Override
	protected void init() {
		super.init();
		leftPos = (width - imageWidth) / 2;
		topPos = (height - imageHeight) / 2;
		addRenderableWidget(new ToggleScrollList<>(be, hasSmartModule, hasRedstoneModule, minecraft, imageWidth - 24, imageHeight - 60, topPos + 40, leftPos + 12));
	}

	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
		super.render(guiGraphics, mouseX, mouseY, partialTicks);
		guiGraphics.drawString(font, title, width / 2 - font.width(title) / 2, topPos + 6, CommonColors.DARK_GRAY, false);
		guiGraphics.drawString(font, scrollListTitle, width / 2 - font.width(scrollListTitle) / 2, topPos + 31, CommonColors.DARK_GRAY, false);
		ClientUtils.renderModuleInfo(guiGraphics, font, ModuleType.SMART, smartModuleTooltip, hasSmartModule, leftPos + 5, topPos + 5, mouseX, mouseY);
	}

	@Override
	public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
		renderTransparentBackground(guiGraphics);
		guiGraphics.blit(RenderPipelines.GUI_TEXTURED, GUI_TEXTURE, leftPos, topPos, 0.0F, 0.0F, imageWidth, imageHeight, 256, 256);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (minecraft.options.keyInventory.isActiveAndMatches(InputConstants.getKey(keyCode, scanCode))) {
			onClose();
			return true;
		}

		return super.keyPressed(keyCode, scanCode, modifiers);
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}
}
