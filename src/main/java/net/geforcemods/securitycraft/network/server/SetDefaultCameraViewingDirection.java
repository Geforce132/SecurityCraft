package net.geforcemods.securitycraft.network.server;

import net.geforcemods.securitycraft.blockentities.SecurityCameraBlockEntity;
import net.geforcemods.securitycraft.entity.camera.SecurityCamera;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.NetworkEvent;

public class SetDefaultCameraViewingDirection {
	private int id;
	private float initialXRotation, initialYRotation;

	public SetDefaultCameraViewingDirection() {}

	public SetDefaultCameraViewingDirection(SecurityCamera cam) {
		id = cam.getId();
		initialXRotation = cam.getXRot();
		initialYRotation = cam.getYRot();
	}

	public SetDefaultCameraViewingDirection(FriendlyByteBuf buf) {
		id = buf.readVarInt();
		initialXRotation = buf.readFloat();
		initialYRotation = buf.readFloat();
	}

	public void encode(FriendlyByteBuf buf) {
		buf.writeVarInt(id);
		buf.writeFloat(initialXRotation);
		buf.writeFloat(initialYRotation);
	}

	public void handle(NetworkEvent.Context ctx) {
		ServerPlayer player = ctx.getSender();

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
