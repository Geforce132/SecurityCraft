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
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.network.PacketDistributor;

public class LaserBlockScreen extends AbstractContainerScreen<LaserBlockMenu> {
	private static final ResourceLocation TEXTURE = new ResourceLocation(SecurityCraft.MODID, "textures/gui/container/laser_block.png");
	private final boolean hasSmartModule;
	private Component smartModuleTooltip;
	private LaserBlockBlockEntity be;
	private Map<Direction, Boolean> sideConfig;

	public LaserBlockScreen(LaserBlockMenu menu, Inventory playerInventory, Component title) {
		super(menu, playerInventory, title);

		be = menu.be;
		sideConfig = menu.sideConfig;
		hasSmartModule = be.isModuleEnabled(ModuleType.SMART);
		imageHeight = 256;
	}

	@Override
	public void init() {
		super.init();
		titleLabelX = imageWidth / 2 - font.width(title) / 2;
		inventoryLabelY = imageHeight - 94;
		sideConfig.forEach((dir, enabled) -> {
			CallbackCheckbox checkbox = new CallbackCheckbox(leftPos + 40, topPos + dir.get3DDataValue() * 22 + 25, 20, 20, Utils.localize("gui.securitycraft:laser." + dir.getName() + "Enabled"), enabled, newValue -> onChangeValue(dir, newValue), 0x404040) {
				@Override
				public void onPress() {
					if (hasSmartModule)
						super.onPress();
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
	protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
		guiGraphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);
	}

	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
		super.render(guiGraphics, mouseX, mouseY, partialTicks);
		renderTooltip(guiGraphics, mouseX, mouseY);
		ClientUtils.renderModuleInfo(guiGraphics, font, ModuleType.SMART, smartModuleTooltip, hasSmartModule, leftPos + 5, topPos + 5, mouseX, mouseY);
	}

	public void onChangeValue(Direction dir, boolean newValue) {
		sideConfig.put(dir, newValue);
		PacketDistributor.sendToServer(new SyncLaserSideConfig(be.getBlockPos(), LaserBlockBlockEntity.saveSideConfig(sideConfig)));
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
