package net.geforcemods.securitycraft.network.server;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.items.CameraMonitorItem;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

public class RemoveCameraTag {
	private int camID;

	public RemoveCameraTag() {}

	public RemoveCameraTag(int cid) {
		camID = cid;
	}

	public RemoveCameraTag(FriendlyByteBuf buf) {
		camID = buf.readInt();
	}

	public void encode(FriendlyByteBuf buf) {
		buf.writeInt(camID);
	}

	public void handle(Supplier<NetworkEvent.Context> ctx) {
		Player player = ctx.get().getSender();
		ItemStack monitor = PlayerUtils.getItemStackFromAnyHand(player, SCContent.CAMERA_MONITOR.get());

		if (!player.isSpectator() && !monitor.isEmpty() && monitor.hasTag())
			monitor.getTag().remove(CameraMonitorItem.getTagNameFromPosition(monitor.getTag(), CameraMonitorItem.getCameraPositions(monitor.getTag()).get(camID - 1).getLeft()));
	}
}
