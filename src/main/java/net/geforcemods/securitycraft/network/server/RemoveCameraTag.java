package net.geforcemods.securitycraft.network.server;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.items.CameraMonitorItem;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
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
		ItemStack monitor = PlayerUtils.getItemStackFromAnyHand(ctx.player().orElseThrow(), SCContent.CAMERA_MONITOR.get());

		if (!monitor.isEmpty())
			CustomData.update(DataComponents.CUSTOM_DATA, monitor, tag -> tag.remove(CameraMonitorItem.getTagNameFromPosition(tag, CameraMonitorItem.getCameraPositions(tag).get(camID - 1))));
	}
}
