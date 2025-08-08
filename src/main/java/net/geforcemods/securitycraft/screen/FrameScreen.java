package net.geforcemods.securitycraft.screen;

import net.geforcemods.securitycraft.blockentities.FrameBlockEntity;
import net.geforcemods.securitycraft.misc.GlobalPos;
import net.geforcemods.securitycraft.screen.components.ClickButton;
import net.geforcemods.securitycraft.screen.components.Tooltip;
import net.geforcemods.securitycraft.util.Utils;

public class FrameScreen extends CameraSelectScreen {
	private final FrameBlockEntity be;

	public FrameScreen(boolean readOnly, FrameBlockEntity be) {
		super(be.getCameraPositions(), readOnly);
		this.be = be;
	}

	@Override
	public void initGui() {
		super.initGui();

		ClickButton stopViewingButton = addButton(new ClickButton(-2, width / 2 - 55, height / 2 + 57, 20, 20, "x", b -> viewCamera(null)));

		if (be.getCurrentCamera() == null)
			stopViewingButton.enabled = false;
		else
			stopViewingButton.tooltip = new Tooltip(this, fontRenderer, Utils.localize("gui.securitycraft:monitor.stopViewing"));
	}

	@Override
	protected void viewCamera(GlobalPos cameraPos) {
		be.setCameraOnClientAndUpdate(cameraPos);
		super.viewCamera(cameraPos);
	}

	@Override
	protected void unbindCamera(int camID) {
		if (!readOnly)
			be.removeCameraOnClient(camID);
	}
}
