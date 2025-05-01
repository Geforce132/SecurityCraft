package net.geforcemods.securitycraft.screen;

import net.geforcemods.securitycraft.blockentities.FrameBlockEntity;
import net.geforcemods.securitycraft.screen.components.Tooltip;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class FrameScreen extends CameraSelectScreen {
	private final FrameBlockEntity be;

	public FrameScreen(boolean readOnly, FrameBlockEntity be) {
		super(be.getCameraPositions(), readOnly);
		this.be = be;
	}

	@Override
	public void init() {
		super.init();

		Button stopViewingButton = addButton(new Button(width / 2 - 55, height / 2 + 57, 20, 20, new StringTextComponent("x"), b -> viewCamera(null)));

		if (be.getCurrentCamera() == null)
			stopViewingButton.active = false;
		else
			stopViewingButton.onTooltip = new Tooltip(this, font, new TranslationTextComponent("gui.securitycraft:monitor.stopViewing"));
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
