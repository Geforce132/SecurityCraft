package net.geforcemods.securitycraft.screen;

import com.mojang.blaze3d.platform.InputConstants;

import net.geforcemods.securitycraft.blockentities.IMSBlockEntity;
import net.geforcemods.securitycraft.misc.TargetingMode;
import net.geforcemods.securitycraft.network.server.SyncIMSTargetingOption;
import net.geforcemods.securitycraft.screen.components.ToggleComponentButton;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.PacketDistributor;

public class IMSScreen extends Screen {
	private static final ResourceLocation TEXTURE = new ResourceLocation("securitycraft:textures/gui/container/blank.png");
	private final Component target = Utils.localize("gui.securitycraft:ims.target");
	private int imageWidth = 176;
	private int imageHeight = 166;
	private int leftPos;
	private int topPos;
	private IMSBlockEntity be;
	private TargetingMode targetMode;

	public IMSScreen(IMSBlockEntity be) {
		super(be.getName());
		this.be = be;
		targetMode = be.getTargetingMode();
	}

	@Override
	public void init() {
		super.init();

		leftPos = (width - imageWidth) / 2;
		topPos = (height - imageHeight) / 2;
		addRenderableWidget(new ToggleComponentButton(width / 2 - 75, height / 2 - 38, 150, 20, index -> TargetingMode.values()[index].translate(), targetMode.ordinal(), 3, this::modeButtonClicked));
	}

	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
		super.render(guiGraphics, mouseX, mouseY, partialTick);
		guiGraphics.drawString(font, title, width / 2 - font.width(title) / 2, topPos + 6, 4210752, false);
		guiGraphics.drawString(font, target, width / 2 - font.width(target) / 2, topPos + 30, 4210752, false);
	}

	@Override
	public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
		super.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
		guiGraphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);
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

	private void modeButtonClicked(Button button) {
		targetMode = TargetingMode.values()[((ToggleComponentButton) button).getCurrentIndex()];
		be.setTargetingMode(targetMode);
		PacketDistributor.SERVER.noArg().send(new SyncIMSTargetingOption(be.getBlockPos(), be.getTargetingMode()));
	}
}
