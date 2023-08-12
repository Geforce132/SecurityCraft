package net.geforcemods.securitycraft.screen;

import java.util.EnumMap;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blockentities.LaserBlockBlockEntity;
import net.geforcemods.securitycraft.inventory.LaserBlockMenu;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.network.server.SyncLaserSideConfig;
import net.geforcemods.securitycraft.screen.components.CallbackCheckbox;
import net.geforcemods.securitycraft.screen.components.ClickButton;
import net.geforcemods.securitycraft.util.GuiUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

public class LaserBlockScreen extends GuiContainer {
	private static final ResourceLocation TEXTURE = new ResourceLocation(SecurityCraft.MODID, "textures/gui/container/laser_block.png");
	private final String inventoryText = Utils.localize("container.inventory").getFormattedText();
	private final boolean hasSmartModule;
	private String smartModuleTooltip, title;
	private LaserBlockBlockEntity be;
	private EnumMap<EnumFacing, Boolean> sideConfig;

	public LaserBlockScreen(LaserBlockMenu menu) {
		super(menu);
		this.be = menu.be;
		title = be.getDisplayName().getFormattedText();
		this.sideConfig = menu.sideConfig;
		hasSmartModule = be.isModuleEnabled(ModuleType.SMART);
		ySize = 256;
	}

	@Override
	public void initGui() {
		super.initGui();

		sideConfig.forEach((dir, enabled) -> {
			CallbackCheckbox checkbox = new CallbackCheckbox(-1, guiLeft + 40, guiTop + dir.getIndex() * 22 + 25, 20, 20, Utils.localize("gui.securitycraft:laser." + dir.getName() + "Enabled").getFormattedText(), enabled, newValue -> onChangeValue(dir, newValue), 0x404040) {
				@Override
				public void onClick() {
					if (hasSmartModule)
						super.onClick();
				}
			};

			checkbox.enabled = be.isEnabled();
			addButton(checkbox);
		});

		if (hasSmartModule)
			smartModuleTooltip = Utils.localize("gui.securitycraft:laser.smartModule").getFormattedText();
		else
			smartModuleTooltip = Utils.localize("gui.securitycraft:laser.noSmartModule").getFormattedText();
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		drawDefaultBackground();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(TEXTURE);
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		super.drawScreen(mouseX, mouseY, partialTicks);
		renderHoveredToolTip(mouseX, mouseY);
		GuiUtils.renderModuleInfo(ModuleType.SMART, smartModuleTooltip, hasSmartModule, guiLeft + 5, guiTop + 5, width, height, mouseX, mouseY);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		fontRenderer.drawString(title, xSize / 2 - fontRenderer.getStringWidth(title) / 2, 6, 0x404040);
		fontRenderer.drawString(inventoryText, 8, ySize - 94, 4210752);
	}

	@Override
	protected void actionPerformed(GuiButton button) {
		if (button instanceof ClickButton)
			((ClickButton) button).onClick();
	}

	public void onChangeValue(EnumFacing dir, boolean newValue) {
		sideConfig.put(dir, newValue);
		SecurityCraft.network.sendToServer(new SyncLaserSideConfig(be.getPos(), sideConfig));
	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) {
		if (keyCode == 1 || mc.gameSettings.keyBindInventory.isActiveAndMatches(keyCode))
			mc.player.closeScreen();
	}
}
