package net.geforcemods.securitycraft.screen;

import java.io.IOException;

import org.lwjgl.input.Mouse;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blockentities.RiftStabilizerBlockEntity;
import net.geforcemods.securitycraft.blockentities.RiftStabilizerBlockEntity.TeleportationType;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.screen.components.ToggleScrollList;
import net.geforcemods.securitycraft.util.GuiUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class RiftStabilizerScreen extends GuiScreen {
	private static final ResourceLocation GUI_TEXTURE = new ResourceLocation(SecurityCraft.MODID, "textures/gui/container/blank.png");
	private final String title, scrollListTitle, smartModuleTooltip;
	private int imageWidth = 176;
	private int imageHeight = 166;
	private int leftPos;
	private int topPos;
	private final boolean hasSmartModule;
	private final boolean hasRedstoneModule;
	private final RiftStabilizerBlockEntity te;
	private ToggleScrollList<TeleportationType> toggleList;

	public RiftStabilizerScreen(RiftStabilizerBlockEntity te) {
		this.te = te;
		title = te.getDisplayName().getFormattedText();
		hasSmartModule = te.isModuleEnabled(ModuleType.SMART);
		hasRedstoneModule = te.isModuleEnabled(ModuleType.REDSTONE);
		scrollListTitle = Utils.localize("gui.securitycraft:rift_stabilizer.teleportationTypes").getFormattedText();
		smartModuleTooltip = (hasSmartModule ? Utils.localize("gui.securitycraft:rift_stabilizer.toggle") : Utils.localize("gui.securitycraft:rift_stabilizer.moduleRequired")).getFormattedText();
	}

	@Override
	public void initGui() {
		super.initGui();

		leftPos = (width - imageWidth) / 2;
		topPos = (height - imageHeight) / 2;
		toggleList = new ToggleScrollList<>(te, hasSmartModule, hasRedstoneModule, mc, imageWidth - 24, imageHeight - 60, topPos + 40, leftPos + 12, width, height);
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		drawDefaultBackground();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(GUI_TEXTURE);
		drawTexturedModalRect(leftPos, topPos, 0, 0, imageWidth, imageHeight);
		super.drawScreen(mouseX, mouseY, partialTicks);
		GlStateManager.disableLighting();

		if (toggleList != null)
			toggleList.drawScreen(mouseX, mouseY, partialTicks);

		fontRenderer.drawString(title, width / 2 - fontRenderer.getStringWidth(title) / 2, topPos + 6, 4210752);
		fontRenderer.drawString(scrollListTitle, width / 2 - fontRenderer.getStringWidth(scrollListTitle) / 2, topPos + 31, 4210752);
		GuiUtils.renderModuleInfo(ModuleType.SMART, smartModuleTooltip, hasSmartModule, leftPos + 5, topPos + 5, width, height, mouseX, mouseY);
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		super.keyTyped(typedChar, keyCode);

		if (keyCode == 1 || mc.gameSettings.keyBindInventory.isActiveAndMatches(keyCode))
			mc.player.closeScreen();
	}

	@Override
	public void handleMouseInput() throws IOException {
		super.handleMouseInput();

		int mouseX = Mouse.getEventX() * width / mc.displayWidth;
		int mouseY = height - Mouse.getEventY() * height / mc.displayHeight - 1;

		toggleList.handleMouseInput(mouseX, mouseY);
	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}
}
