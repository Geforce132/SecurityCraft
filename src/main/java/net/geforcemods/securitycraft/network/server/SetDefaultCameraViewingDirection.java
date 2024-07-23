package net.geforcemods.securitycraft.network.server;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.blockentities.SecurityCameraBlockEntity;
import net.geforcemods.securitycraft.entity.camera.SecurityCamera;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.network.NetworkEvent;

public class SetDefaultCameraViewingDirection {
	private int id;
	private float initialXRotation, initialYRotation, initialZoom;

	public SetDefaultCameraViewingDirection() {}

	public SetDefaultCameraViewingDirection(SecurityCamera cam) {
		id = cam.getId();
		initialXRotation = cam.xRot;
		initialYRotation = cam.yRot;
		initialZoom = cam.getZoomAmount();
	}

	public SetDefaultCameraViewingDirection(PacketBuffer buf) {
		id = buf.readVarInt();
		initialXRotation = buf.readFloat();
		initialYRotation = buf.readFloat();
		initialZoom = buf.readFloat();
	}

	public void encode(PacketBuffer buf) {
		buf.writeVarInt(id);
		buf.writeFloat(initialXRotation);
		buf.writeFloat(initialYRotation);
		buf.writeFloat(initialZoom);
	}

	public void handle(Supplier<NetworkEvent.Context> ctx) {
		ServerPlayerEntity player = ctx.get().getSender();

		Entity playerCamera = player.getCamera();

		if (playerCamera.getId() == id && playerCamera instanceof SecurityCamera) {
			TileEntity te = playerCamera.level.getBlockEntity(playerCamera.blockPosition());

			if (te instanceof SecurityCameraBlockEntity) {
				SecurityCameraBlockEntity be = (SecurityCameraBlockEntity) te;

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
}
