package net.geforcemods.securitycraft.network.client;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.entity.camera.CameraController;
import net.geforcemods.securitycraft.entity.camera.SecurityCamera;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class SetCameraView {
	private int id;

	public SetCameraView() {}

	public SetCameraView(Entity camera) {
		id = camera.getId();
	}

	public static void encode(SetCameraView message, PacketBuffer buf) {
		buf.writeVarInt(message.id);
	}

	public static SetCameraView decode(PacketBuffer buf) {
		SetCameraView message = new SetCameraView();

		message.id = buf.readVarInt();
		return message;
	}

	public static void onMessage(SetCameraView message, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			Minecraft mc = Minecraft.getInstance();
			Entity entity = mc.level.getEntity(message.id);
			boolean isCamera = entity instanceof SecurityCamera;

			if (isCamera || entity instanceof PlayerEntity) {
				mc.setCameraEntity(entity);

				if (isCamera) {
					CameraController.previousCameraType = mc.options.thirdPersonView;
					mc.options.thirdPersonView = 0;
					mc.gui.setOverlayMessage(Utils.localize("mount.onboard", mc.options.keyShift.getTranslatedKeyMessage()), false);
					CameraController.setRenderPosition(entity);
				}
				else if (CameraController.previousCameraType >= 0 && CameraController.previousCameraType < 3)
					mc.options.thirdPersonView = CameraController.previousCameraType;

				mc.levelRenderer.allChanged();
			}
		});
		ctx.get().setPacketHandled(true);
	}
}
