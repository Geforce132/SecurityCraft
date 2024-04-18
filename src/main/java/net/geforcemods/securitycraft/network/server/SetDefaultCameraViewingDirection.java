package net.geforcemods.securitycraft.network.server;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blockentities.SecurityCameraBlockEntity;
import net.geforcemods.securitycraft.entity.camera.SecurityCamera;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

public record SetDefaultCameraViewingDirection(int id, float initialXRotation, float initialYRotation) implements CustomPacketPayload {

	public static final Type<SetDefaultCameraViewingDirection> TYPE = new Type<>(new ResourceLocation(SecurityCraft.MODID, "set_default_camera_viewing_direction"));
	//@formatter:off
	public static final StreamCodec<RegistryFriendlyByteBuf, SetDefaultCameraViewingDirection> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.VAR_INT, SetDefaultCameraViewingDirection::id,
			ByteBufCodecs.FLOAT, SetDefaultCameraViewingDirection::initialXRotation,
			ByteBufCodecs.FLOAT, SetDefaultCameraViewingDirection::initialYRotation,
			SetDefaultCameraViewingDirection::new);
	//@formatter:on
	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public void handle(PlayPayloadContext ctx) {
		ServerPlayer player = (ServerPlayer) ctx.player().orElseThrow();

		if (player.getCamera() instanceof SecurityCamera camera && camera.getId() == id && camera.level().getBlockEntity(camera.blockPosition()) instanceof SecurityCameraBlockEntity be) {
			if (!be.isOwnedBy(player)) {
				player.displayClientMessage(Utils.localize("messages.securitycraft:security_camera.no_permission"), true);
				return;
			}

			if (be.isModuleEnabled(ModuleType.SMART)) {
				be.setDefaultViewingDirection(initialXRotation, initialYRotation);
				player.displayClientMessage(Utils.localize("messages.securitycraft:security_camera.direction_set"), true);
			}
			else
				player.displayClientMessage(Utils.localize("messages.securitycraft:security_camera.smart_module_needed"), true);
		}
	}
}
