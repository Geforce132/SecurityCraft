package net.geforcemods.securitycraft.screen;

import java.util.EnumMap;

import com.mojang.blaze3d.platform.InputConstants;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blockentities.LaserBlockBlockEntity;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.network.server.SyncLaserSideConfig;
import net.geforcemods.securitycraft.screen.components.CallbackCheckbox;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class LaserScreen extends Screen {
	private static final ResourceLocation TEXTURE = new ResourceLocation(SecurityCraft.MODID, "textures/gui/container/block_pocket_manager.png");
	private final boolean hasSmartModule;
	private Component smartModuleTooltip;
	private int xSize = 176, ySize = 194, leftPos, topPos;
	private LaserBlockBlockEntity be;
	private EnumMap<Direction, Boolean> sideConfig;

	public LaserScreen(LaserBlockBlockEntity be, EnumMap<Direction, Boolean> sideConfig) {
		super(be.getName());
		this.be = be;
		this.sideConfig = sideConfig;
		hasSmartModule = be.isModuleEnabled(ModuleType.SMART);
	}

	@Override
	public void init() {
		super.init();
		leftPos = (width - xSize) / 2;
		topPos = (height - ySize) / 2;

		if (hasSmartModule) {
			sideConfig.forEach((dir, enabled) -> {
				CallbackCheckbox checkbox = new CallbackCheckbox(leftPos + 10, topPos + dir.get3DDataValue() * 25 + 30, 20, 20, Utils.localize("gui.securitycraft:laser." + dir.getName() + "Enabled"), enabled, newValue -> onChangeValue(dir, newValue), 0x404040);

				checkbox.active = be.isEnabled();
				addRenderableWidget(checkbox);
			});

			smartModuleTooltip = Utils.localize("gui.securitycraft:laser.smartModule");
		}
		else
			smartModuleTooltip = Utils.localize("gui.securitycraft:laser.noSmartModule");
	}

	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
		renderBackground(guiGraphics);
		guiGraphics.blit(TEXTURE, leftPos, topPos, 0, 0, xSize, ySize);
		super.render(guiGraphics, mouseX, mouseY, partialTicks);
		guiGraphics.drawString(font, title, leftPos + xSize / 2 - font.width(title) / 2, topPos + 6, 0x404040, false);
		ClientUtils.renderModuleInfo(guiGraphics, font, ModuleType.SMART, smartModuleTooltip, hasSmartModule, leftPos + 5, topPos + 5, width, height, mouseX, mouseY);
	}

	public void onChangeValue(Direction dir, boolean newValue) {
		sideConfig.put(dir, newValue);
		SecurityCraft.channel.sendToServer(new SyncLaserSideConfig(be.getBlockPos(), sideConfig));
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (minecraft.options.keyInventory.isActiveAndMatches(InputConstants.getKey(keyCode, scanCode))) {
			onClose();
			return true;
		}

		return super.keyPressed(keyCode, scanCode, modifiers);
	}
}
