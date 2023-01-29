package net.geforcemods.securitycraft.screen;

import java.util.EnumMap;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blockentities.LaserBlockBlockEntity;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.network.server.SyncLaserSideConfig;
import net.geforcemods.securitycraft.screen.components.CallbackCheckbox;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;

public class LaserScreen extends Screen {
	private static final ResourceLocation TEXTURE = new ResourceLocation(SecurityCraft.MODID, "textures/gui/container/block_pocket_manager.png");
	private final boolean hasSmartModule;
	private Component smartModuleTooltip;
	private int xSize = 176, ySize = 194, leftPos, topPos;
	private LaserBlockBlockEntity be;
	private EnumMap<Direction, Boolean> sideConfig;

	public LaserScreen(LaserBlockBlockEntity be, EnumMap<Direction, Boolean> sideConfig) {
		super(new TranslatableComponent(SCContent.LASER_BLOCK.get().getDescriptionId()));
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
				CallbackCheckbox checkbox = new CallbackCheckbox(leftPos + 10, topPos + dir.get3DDataValue() * 25 + 25, 20, 20, Utils.localize("gui.securitycraft:laser." + dir.getName() + "Enabled"), enabled, newValue -> onChangeValue(dir, newValue), 0x404040);

				checkbox.active = be.isEnabled();
				addRenderableWidget(checkbox);
			});

			smartModuleTooltip = Utils.localize("gui.securitycraft:laser.smartModule");
		}
		else
			smartModuleTooltip = Utils.localize("gui.securitycraft:laser.noSmartModule");
	}

	@Override
	public void render(PoseStack pose, int mouseX, int mouseY, float partialTicks) {
		renderBackground(pose);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem._setShaderTexture(0, TEXTURE);
		blit(pose, leftPos, topPos, 0, 0, xSize, ySize);
		super.render(pose, mouseX, mouseY, partialTicks);
		font.draw(pose, title, leftPos + xSize / 2 - font.width(title) / 2, topPos + 6, 0x404040);
		ClientUtils.renderModuleInfo(pose, ModuleType.SMART, smartModuleTooltip, hasSmartModule, leftPos + 5, topPos + 5, width, height, mouseX, mouseY);
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
