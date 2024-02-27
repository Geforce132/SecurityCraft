package net.geforcemods.securitycraft.network.server;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blockentities.SecurityCameraBlockEntity;
import net.geforcemods.securitycraft.entity.camera.SecurityCamera;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

public class UpdateInitialCameraRotation implements CustomPacketPayload {
	public static final ResourceLocation ID = new ResourceLocation(SecurityCraft.MODID, "update_initial_camera_rotation");
	private int id;

	public UpdateInitialCameraRotation() {}

	public UpdateInitialCameraRotation(SecurityCamera cam) {
		id = cam.getId();
	}

	public UpdateInitialCameraRotation(FriendlyByteBuf buf) {
		id = buf.readVarInt();
	}

	@Override
	public void write(FriendlyByteBuf buf) {
		buf.writeVarInt(id);
	}

	@Override
	public ResourceLocation id() {
		return ID;
	}

	public void handle(PlayPayloadContext ctx) {
		Player player = ctx.player().orElseThrow();

		if (((ServerPlayer) player).getCamera() instanceof SecurityCamera camera && camera.getId() == id && camera.level().getBlockEntity(camera.blockPosition()) instanceof SecurityCameraBlockEntity be && be.isOwnedBy(player) && be.isModuleEnabled(ModuleType.SMART))
			be.setInitialRotationBasedOnCamera(camera);
	}
}
