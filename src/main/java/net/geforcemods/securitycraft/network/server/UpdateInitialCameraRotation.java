package net.geforcemods.securitycraft.network.server;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blockentities.SecurityCameraBlockEntity;
import net.geforcemods.securitycraft.entity.camera.SecurityCamera;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

public class UpdateInitialCameraRotation implements CustomPacketPayload {
	public static final ResourceLocation ID = new ResourceLocation(SecurityCraft.MODID, "update_initial_camera_rotation");
	private int id;
	private float initialXRotation, initialYRotation;

	public UpdateInitialCameraRotation() {}

	public UpdateInitialCameraRotation(SecurityCamera cam) {
		id = cam.getId();
		initialXRotation = cam.getXRot();
		initialYRotation = cam.getYRot();
	}

	public UpdateInitialCameraRotation(FriendlyByteBuf buf) {
		id = buf.readVarInt();
		initialXRotation = buf.readFloat();
		initialYRotation = buf.readFloat();
	}

	@Override
	public void write(FriendlyByteBuf buf) {
		buf.writeVarInt(id);
		buf.writeFloat(initialXRotation);
		buf.writeFloat(initialYRotation);
	}

	@Override
	public ResourceLocation id() {
		return ID;
	}

	public void handle(PlayPayloadContext ctx) {
		ServerPlayer player = (ServerPlayer) ctx.player().orElseThrow();

		//TODO: translate
		if (player.getCamera() instanceof SecurityCamera camera && camera.getId() == id && camera.level().getBlockEntity(camera.blockPosition()) instanceof SecurityCameraBlockEntity be) {
			if (!be.isOwnedBy(player)) {
				player.displayClientMessage(Component.literal("No permission to set initial rotation"), true);
				return;
			}

			if (be.isModuleEnabled(ModuleType.SMART)) {
				be.setInitialRotation(initialXRotation, initialYRotation);
				player.displayClientMessage(Component.literal("Initial rotation set"), true);
			}
			else
				player.displayClientMessage(Component.literal("Smart Module required"), true);
		}
	}
}
