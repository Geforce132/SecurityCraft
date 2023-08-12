package net.geforcemods.securitycraft.screen;

import java.io.IOException;

import org.lwjgl.input.Mouse;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blockentities.TrophySystemBlockEntity;
import net.geforcemods.securitycraft.inventory.TrophySystemMenu;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.screen.components.ToggleScrollList;
import net.geforcemods.securitycraft.util.GuiUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.EntityEntry;

public class TrophySystemScreen extends GuiContainer {
	private static final ResourceLocation GUI_TEXTURE = new ResourceLocation(SecurityCraft.MODID, "textures/gui/container/trophy_system.png");
	private final String inventoryText = Utils.localize("container.inventory").getFormattedText();
	private final String title, scrollListTitle, smartModuleTooltip;
	private boolean hasSmartModule;
	private boolean hasRedstoneModule;
	private TrophySystemBlockEntity be;
	private ToggleScrollList<EntityEntry> entryList;

	public TrophySystemScreen(TrophySystemMenu menu) {
		super(menu);
		this.be = menu.be;
		title = be.getDisplayName().getFormattedText();
		hasSmartModule = be.isModuleEnabled(ModuleType.SMART);
		hasRedstoneModule = be.isModuleEnabled(ModuleType.REDSTONE);
		scrollListTitle = Utils.localize("gui.securitycraft:trophy_system.targetableProjectiles").getFormattedText();
		smartModuleTooltip = (hasSmartModule ? Utils.localize("gui.securitycraft:trophy_system.toggle") : Utils.localize("gui.securitycraft:trophy_system.moduleRequired")).getFormattedText();
		ySize = 248;
	}

	@Override
	public void initGui() {
		super.initGui();
		entryList = new ToggleScrollList<>(be, hasSmartModule, hasRedstoneModule, mc, xSize - 24, 106, guiTop + 40, guiLeft + 12, width, height);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		drawDefaultBackground();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(GUI_TEXTURE);
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		super.drawScreen(mouseX, mouseY, partialTicks);
		GlStateManager.disableLighting();

		if (entryList != null)
			entryList.drawScreen(mouseX, mouseY, partialTicks);

		GuiUtils.renderModuleInfo(ModuleType.SMART, smartModuleTooltip, hasSmartModule, guiLeft + 5, guiTop + 5, width, height, mouseX, mouseY);
		renderHoveredToolTip(mouseX, mouseY);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		fontRenderer.drawString(title, xSize / 2 - fontRenderer.getStringWidth(title) / 2, 6, 4210752);
		fontRenderer.drawString(scrollListTitle, xSize / 2 - fontRenderer.getStringWidth(scrollListTitle) / 2, 31, 4210752);
		fontRenderer.drawString(inventoryText, 8, ySize - 94, 4210752);
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

		entryList.handleMouseInput(mouseX, mouseY);
	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}
}
