package net.geforcemods.securitycraft.screen;

import net.geforcemods.securitycraft.items.CameraMonitorItem;
import net.geforcemods.securitycraft.network.server.MountCamera;
import net.minecraft.core.GlobalPos;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;

public class CameraMonitorScreen extends CameraSelectScreen {
	private final ItemStack stack;

	public CameraMonitorScreen(ItemStack stack) {
		super(CameraMonitorItem.getCameraPositions(stack.getTag()), false);
		this.stack = stack;
	}

	@Override
	protected void viewCamera(GlobalPos cameraPos) {
		PacketDistributor.SERVER.noArg().send(new MountCamera(cameraPos.pos()));
		super.viewCamera(cameraPos);
	}

	@Override
	protected void unbindCamera(int camID) {
		CameraMonitorItem.removeCameraOnClient(camID, stack.getTag());
	}
}
