package net.geforcemods.securitycraft.screen;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.items.CameraMonitorItem;
import net.geforcemods.securitycraft.network.server.MountCamera;
import net.minecraft.core.GlobalPos;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

public class CameraMonitorScreen extends CameraSelectScreen {
	private final ItemStack stack;

	public CameraMonitorScreen(ItemStack stack) {
		super(stack.getOrDefault(SCContent.BOUND_CAMERAS, CameraMonitorItem.DEFAULT_NAMED_POSITIONS).positions(), false);
		this.stack = stack;
	}

	@Override
	protected void viewCamera(GlobalPos cameraPos) {
		ClientPacketDistributor.sendToServer(new MountCamera(cameraPos.pos()));
		super.viewCamera(cameraPos);
	}

	@Override
	protected void unbindCamera(GlobalPos cameraPos) {
		CameraMonitorItem.removeCameraOnClient(cameraPos, stack);
	}
}
