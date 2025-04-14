package net.geforcemods.securitycraft.network.server;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.items.CameraMonitorItem;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

public class RemoveCameraTag implements CustomPacketPayload {
	public static final ResourceLocation ID = new ResourceLocation(SecurityCraft.MODID, "remove_camera_tag");
	private int camID;

	public RemoveCameraTag() {}

	public RemoveCameraTag(int cid) {
		camID = cid;
	}

	public RemoveCameraTag(FriendlyByteBuf buf) {
		camID = buf.readInt();
	}

	@Override
	public void write(FriendlyByteBuf buf) {
		buf.writeInt(camID);
	}

	@Override
	public ResourceLocation id() {
		return ID;
	}

	public void handle(PlayPayloadContext ctx) {
		Player player = ctx.player().orElseThrow();
		ItemStack monitor = PlayerUtils.getItemStackFromAnyHand(ctx.player().orElseThrow(), SCContent.CAMERA_MONITOR.get());

		if (!player.isSpectator() && !monitor.isEmpty())
			monitor.getTag().remove(CameraMonitorItem.getTagNameFromPosition(monitor.getTag(), CameraMonitorItem.getCameraPositions(monitor.getTag()).get(camID - 1).getLeft()));
	}
}
