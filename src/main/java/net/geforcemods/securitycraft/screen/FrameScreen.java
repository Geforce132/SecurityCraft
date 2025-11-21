package net.geforcemods.securitycraft.screen;

import net.geforcemods.securitycraft.blockentities.FrameBlockEntity;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.chat.Component;

public class FrameScreen extends CameraSelectScreen {
	private final FrameBlockEntity be;

	public FrameScreen(boolean readOnly, FrameBlockEntity be) {
		super(be.getCameraPositions(), readOnly);
		this.be = be;
	}

	@Override
	public void init() {
		super.init();

		Button stopViewingButton = addRenderableWidget(Button.builder(Component.literal("x"), b -> viewCamera(null)).pos(width / 2 - 55, height / 2 + 57).size(20, 20).build());

		if (be.getCurrentCamera() == null)
			stopViewingButton.active = false;
		else
			stopViewingButton.setTooltip(Tooltip.create(Component.translatable("gui.securitycraft:monitor.stopViewing")));
	}

	@Override
	protected void viewCamera(GlobalPos cameraPos) {
		be.setCameraOnClientAndUpdate(cameraPos);
		super.viewCamera(cameraPos);
	}

	@Override
	protected void unbindCamera(GlobalPos cameraPos) {
		if (!readOnly)
			be.removeCameraOnClient(cameraPos);
	}
}
