package net.geforcemods.securitycraft.screen;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.items.CameraMonitorItem;
import net.geforcemods.securitycraft.network.server.MountCamera;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.GlobalPos;

public class CameraMonitorScreen extends CameraSelectScreen {
	private final ItemStack stack;

	public CameraMonitorScreen(ItemStack stack) {
		super(CameraMonitorItem.getCameraPositions(stack.getTag()), false);
		this.stack = stack;
	}

	@Override
	protected void viewCamera(GlobalPos cameraPos) {
		SecurityCraft.channel.sendToServer(new MountCamera(cameraPos.pos()));
		super.viewCamera(cameraPos);
	}

	@Override
	protected void unbindCamera(int camID) {
		CameraMonitorItem.removeCameraOnClient(camID, stack.getTag());
	}
}
