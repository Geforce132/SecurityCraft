package net.geforcemods.securitycraft.screen;

import java.util.Map;

import com.mojang.blaze3d.platform.InputConstants;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blockentities.LaserBlockBlockEntity;
import net.geforcemods.securitycraft.inventory.LaserBlockMenu;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.network.server.SyncLaserSideConfig;
import net.geforcemods.securitycraft.screen.components.CallbackCheckbox;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.CommonColors;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

public class LaserBlockScreen extends AbstractContainerScreen<LaserBlockMenu> {
	private static final Identifier TEXTURE = SecurityCraft.resLoc("textures/gui/container/laser_block.png");
	private final boolean hasSmartModule;
	private Component smartModuleTooltip;
	private LaserBlockBlockEntity be;
	private Map<Direction, Boolean> sideConfig;

	public LaserBlockScreen(LaserBlockMenu menu, Inventory playerInventory, Component title) {
		super(menu, playerInventory, title, 176, 256);

		be = menu.be;
		sideConfig = menu.sideConfig;
		hasSmartModule = be.isModuleEnabled(ModuleType.SMART);
	}

	@Override
	public void init() {
		super.init();
		titleLabelX = imageWidth / 2 - font.width(title) / 2;
		inventoryLabelY = imageHeight - 94;
		sideConfig.forEach((dir, enabled) -> {
			CallbackCheckbox checkbox = new CallbackCheckbox(leftPos + 40, topPos + dir.get3DDataValue() * 22 + 25, 20, 20, Utils.localize("gui.securitycraft:laser." + dir.getName() + "Enabled"), enabled, newValue -> onChangeValue(dir, newValue), CommonColors.DARK_GRAY) {
				@Override
				public void onPress(InputWithModifiers input) {
					if (hasSmartModule)
						super.onPress(input);
				}
			};

			checkbox.active = be.isEnabled();
			addRenderableWidget(checkbox);
		});

		if (hasSmartModule)
			smartModuleTooltip = Utils.localize("gui.securitycraft:laser.smartModule");
		else
			smartModuleTooltip = Utils.localize("gui.securitycraft:laser.noSmartModule");
	}

	@Override
	public void extractBackground(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float a) {
		super.extractBackground(guiGraphics, mouseX, mouseY, a);
		guiGraphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, leftPos, topPos, 0.0F, 0.0F, imageWidth, imageHeight, 256, 256);
	}

	@Override
	public void extractRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTicks) {
		super.extractRenderState(guiGraphics, mouseX, mouseY, partialTicks);
		extractTooltip(guiGraphics, mouseX, mouseY);
		ClientUtils.renderModuleInfo(guiGraphics, font, ModuleType.SMART, smartModuleTooltip, hasSmartModule, leftPos + 5, topPos + 5, mouseX, mouseY);
	}

	@Override
	public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
		if (minecraft.player.isSpectator())
			return false;

		return super.mouseClicked(event, doubleClick);
	}

	public void onChangeValue(Direction dir, boolean newValue) {
		sideConfig.put(dir, newValue);
		ClientPacketDistributor.sendToServer(new SyncLaserSideConfig(be.getBlockPos(), LaserBlockBlockEntity.saveSideConfigToTag(sideConfig)));
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}

	@Override
	public boolean keyPressed(KeyEvent event) {
		if (minecraft.options.keyInventory.isActiveAndMatches(InputConstants.getKey(event))) {
			onClose();
			return true;
		}

		return super.keyPressed(event);
	}
}
