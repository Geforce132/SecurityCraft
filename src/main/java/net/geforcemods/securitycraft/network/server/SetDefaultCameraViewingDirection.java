package net.geforcemods.securitycraft.network.server;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blockentities.SecurityCameraBlockEntity;
import net.geforcemods.securitycraft.entity.camera.SecurityCamera;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

public class SetDefaultCameraViewingDirection implements CustomPacketPayload {
	public static final ResourceLocation ID = new ResourceLocation(SecurityCraft.MODID, "set_default_camera_viewing_direction");
	private int id;
	private float initialXRotation, initialYRotation, initialZoom;

	public SetDefaultCameraViewingDirection() {}

	public SetDefaultCameraViewingDirection(SecurityCamera cam) {
		id = cam.getId();
		initialXRotation = cam.getXRot();
		initialYRotation = cam.getYRot();
		initialZoom = cam.getZoomAmount();
	}

	public SetDefaultCameraViewingDirection(FriendlyByteBuf buf) {
		id = buf.readVarInt();
		initialXRotation = buf.readFloat();
		initialYRotation = buf.readFloat();
		initialZoom = buf.readFloat();
	}

	@Override
	public void write(FriendlyByteBuf buf) {
		buf.writeVarInt(id);
		buf.writeFloat(initialXRotation);
		buf.writeFloat(initialYRotation);
		buf.writeFloat(initialZoom);
	}

	@Override
	public ResourceLocation id() {
		return ID;
	}

	public void handle(PlayPayloadContext ctx) {
		ServerPlayer player = (ServerPlayer) ctx.player().orElseThrow();

		if (!player.isSpectator() && player.getCamera() instanceof SecurityCamera camera && camera.getId() == id && camera.level().getBlockEntity(camera.blockPosition()) instanceof SecurityCameraBlockEntity be) {
			if (!be.isOwnedBy(player)) {
				player.displayClientMessage(Utils.localize("messages.securitycraft:security_camera.no_permission"), true);
				return;
			}

			if (be.isModuleEnabled(ModuleType.SMART)) {
				be.setDefaultViewingDirection(initialXRotation, initialYRotation, initialZoom);
				player.displayClientMessage(Utils.localize("messages.securitycraft:security_camera.direction_set"), true);
			}
			else
				player.displayClientMessage(Utils.localize("messages.securitycraft:security_camera.smart_module_needed"), true);
		}
	}
}
