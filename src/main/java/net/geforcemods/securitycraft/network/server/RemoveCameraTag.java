package net.geforcemods.securitycraft.network.server;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.items.CameraMonitorItem;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record RemoveCameraTag(int camID) implements CustomPacketPayload {
	public static final Type<RemoveCameraTag> TYPE = new Type<>(new ResourceLocation(SecurityCraft.MODID, "remove_camera_tag"));
	//@formatter:off
	public static final StreamCodec<RegistryFriendlyByteBuf, RemoveCameraTag> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.VAR_INT, RemoveCameraTag::camID,
			RemoveCameraTag::new);
	//@formatter:on

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public void handle(IPayloadContext ctx) {
		ItemStack monitor = PlayerUtils.getItemStackFromAnyHand(ctx.player(), SCContent.CAMERA_MONITOR.get());

		if (!monitor.isEmpty())
			CustomData.update(DataComponents.CUSTOM_DATA, monitor, tag -> tag.remove(CameraMonitorItem.getTagNameFromPosition(tag, CameraMonitorItem.getCameraPositions(tag).get(camID - 1))));
	}
}
