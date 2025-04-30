package net.geforcemods.securitycraft.screen;

import net.geforcemods.securitycraft.blockentities.FrameBlockEntity;
import net.geforcemods.securitycraft.screen.components.Tooltip;
import net.minecraft.client.gui.components.Button;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;

public class FrameScreen extends CameraSelectScreen {
	private final FrameBlockEntity be;

	public FrameScreen(boolean readOnly, FrameBlockEntity be) {
		super(be.getCameraPositions(), readOnly);
		this.be = be;
	}

	@Override
	public void init() {
		super.init();

		Button stopViewingButton = addRenderableWidget(new Button(width / 2 - 55, height / 2 + 57, 20, 20, new TextComponent("x"), b -> viewCamera(null)));

		if (be.getCurrentCamera() == null)
			stopViewingButton.active = false;
		else
			stopViewingButton.onTooltip = new Tooltip(this, font, new TranslatableComponent("gui.securitycraft:monitor.stopViewing"));
	}

	@Override
	protected void viewCamera(GlobalPos cameraPos) {
		be.setCurrentCameraAndUpdate(cameraPos);
		super.viewCamera(cameraPos);
	}

	@Override
	protected void unbindCamera(int camID) {
		if (!readOnly)
			be.removeCameraOnClient(camID);
	}
}
