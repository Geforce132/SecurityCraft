package net.geforcemods.securitycraft.network.server;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.items.CameraMonitorItem;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class RemoveCameraTag {
	private int camID;

	public RemoveCameraTag() {}

	public RemoveCameraTag(int cid) {
		camID = cid;
	}

	public RemoveCameraTag(PacketBuffer buf) {
		camID = buf.readInt();
	}

	public void encode(PacketBuffer buf) {
		buf.writeInt(camID);
	}

	public void handle(Supplier<NetworkEvent.Context> ctx) {
		ItemStack monitor = PlayerUtils.getSelectedItemStack(ctx.get().getSender().inventory, SCContent.CAMERA_MONITOR.get());

		if (!monitor.isEmpty())
			monitor.getTag().remove(CameraMonitorItem.getTagNameFromPosition(monitor.getTag(), ((CameraMonitorItem) monitor.getItem()).getCameraPositions(monitor.getTag()).get(camID - 1)));
	}
}
