package net.geforcemods.securitycraft.network.server;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.blockentities.SecurityCameraBlockEntity;
import net.geforcemods.securitycraft.entity.camera.SecurityCamera;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

public class SetDefaultCameraViewingDirection {
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

	public void encode(FriendlyByteBuf buf) {
		buf.writeVarInt(id);
		buf.writeFloat(initialXRotation);
		buf.writeFloat(initialYRotation);
		buf.writeFloat(initialZoom);
	}

	public void handle(Supplier<NetworkEvent.Context> ctx) {
		ServerPlayer player = ctx.get().getSender();

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
