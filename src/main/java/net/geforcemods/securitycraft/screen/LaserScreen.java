package net.geforcemods.securitycraft.screen;

import java.util.EnumMap;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blockentities.LaserBlockBlockEntity;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.network.server.SyncLaserSideConfig;
import net.geforcemods.securitycraft.screen.components.CallbackCheckbox;
import net.geforcemods.securitycraft.screen.components.ClickButton;
import net.geforcemods.securitycraft.util.GuiUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

public class LaserScreen extends GuiScreen {
	private static final ResourceLocation TEXTURE = new ResourceLocation(SecurityCraft.MODID, "textures/gui/container/block_pocket_manager.png");
	private final boolean hasSmartModule;
	private String smartModuleTooltip, title;
	private int xSize = 176, ySize = 194, leftPos, topPos;
	private LaserBlockBlockEntity be;
	private EnumMap<EnumFacing, Boolean> sideConfig;

	public LaserScreen(LaserBlockBlockEntity be, EnumMap<EnumFacing, Boolean> sideConfig) {
		title = be.getDisplayName().getFormattedText();
		this.be = be;
		this.sideConfig = sideConfig;
		hasSmartModule = be.isModuleEnabled(ModuleType.SMART);
	}

	@Override
	public void initGui() {
		super.initGui();
		leftPos = (width - xSize) / 2;
		topPos = (height - ySize) / 2;

		if (hasSmartModule) {
			sideConfig.forEach((dir, enabled) -> {
				CallbackCheckbox checkbox = new CallbackCheckbox(-1, leftPos + 10, topPos + dir.getIndex() * 25 + 25, 20, 20, Utils.localize("gui.securitycraft:laser." + dir.getName() + "Enabled").getFormattedText(), enabled, newValue -> onChangeValue(dir, newValue), 0x404040);

				checkbox.enabled = be.isEnabled();
				addButton(checkbox);
			});

			smartModuleTooltip = Utils.localize("gui.securitycraft:laser.smartModule").getFormattedText();
		}
		else
			smartModuleTooltip = Utils.localize("gui.securitycraft:laser.noSmartModule").getFormattedText();
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		drawDefaultBackground();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(TEXTURE);
		drawTexturedModalRect(leftPos, topPos, 0, 0, xSize, ySize);
		super.drawScreen(mouseX, mouseY, partialTicks);
		fontRenderer.drawString(title, leftPos + xSize / 2 - fontRenderer.getStringWidth(title) / 2, topPos + 6, 0x404040);
		GuiUtils.renderModuleInfo(ModuleType.SMART, smartModuleTooltip, hasSmartModule, leftPos + 5, topPos + 5, width, height, mouseX, mouseY);
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
