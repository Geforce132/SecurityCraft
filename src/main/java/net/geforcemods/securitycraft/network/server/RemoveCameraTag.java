package net.geforcemods.securitycraft.network.server;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.components.IndexedPositions;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record RemoveCameraTag(GlobalPos globalPos) implements CustomPacketPayload {
	public static final Type<RemoveCameraTag> TYPE = new Type<>(new ResourceLocation(SecurityCraft.MODID, "remove_camera_tag"));
	//@formatter:off
	public static final StreamCodec<RegistryFriendlyByteBuf, RemoveCameraTag> STREAM_CODEC = StreamCodec.composite(
			GlobalPos.STREAM_CODEC, RemoveCameraTag::globalPos,
			RemoveCameraTag::new);
	//@formatter:on

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public void handle(IPayloadContext ctx) {
		ItemStack stack = PlayerUtils.getItemStackFromAnyHand(ctx.player(), SCContent.CAMERA_MONITOR.get());

		if (!stack.isEmpty()) {
			IndexedPositions cameras = stack.get(SCContent.BOUND_CAMERAS);

			if (cameras != null)
				cameras.remove(SCContent.BOUND_CAMERAS, stack, globalPos);
		}
	}
}
