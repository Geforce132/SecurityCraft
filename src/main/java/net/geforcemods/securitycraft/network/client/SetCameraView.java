package net.geforcemods.securitycraft.network.client;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.SCClientEventHandler;
import net.geforcemods.securitycraft.entity.camera.CameraController;
import net.geforcemods.securitycraft.entity.camera.SecurityCamera;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.PointOfView;
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

	public SetCameraView(PacketBuffer buf) {
		id = buf.readVarInt();
	}

	public void encode(PacketBuffer buf) {
		buf.writeVarInt(id);
	}

	public void handle(Supplier<NetworkEvent.Context> ctx) {
		Minecraft mc = Minecraft.getInstance();
		Entity entity = mc.level.getEntity(id);
		boolean isMountingCamera = entity instanceof SecurityCamera;

		if (isMountingCamera || entity instanceof PlayerEntity) {
			mc.setCameraEntity(entity);

			if (isMountingCamera) {
				CameraController.previousCameraType = mc.options.getCameraType();
				mc.options.setCameraType(PointOfView.FIRST_PERSON);
				mc.gui.setOverlayMessage(Utils.localize("mount.onboard", mc.options.keyShift.getTranslatedKeyMessage()), false);
				SCClientEventHandler.resetCameraInfoMessageTime();
			}
			else if (CameraController.previousCameraType != null)
				mc.options.setCameraType(CameraController.previousCameraType);

			mc.levelRenderer.allChanged();
		}
	}
}
