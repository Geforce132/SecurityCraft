package net.geforcemods.securitycraft.network.client;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.ClientHandler;
import net.geforcemods.securitycraft.SCClientEventHandler;
import net.geforcemods.securitycraft.entity.camera.CameraController;
import net.geforcemods.securitycraft.entity.camera.SecurityCamera;
import net.geforcemods.securitycraft.misc.OverlayToggleHandler;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.network.NetworkEvent;

public class SetCameraView {
	private int id;

	public SetCameraView() {}

	public SetCameraView(Entity camera) {
		id = camera.getId();
	}

	public SetCameraView(FriendlyByteBuf buf) {
		id = buf.readVarInt();
	}

	public void encode(FriendlyByteBuf buf) {
		buf.writeVarInt(id);
	}

	public void handle(Supplier<NetworkEvent.Context> ctx) {
		Minecraft mc = Minecraft.getInstance();
		Entity entity = mc.level.getEntity(id);
		boolean isMountingCamera = entity instanceof SecurityCamera;

		if (isMountingCamera || entity instanceof Player) {
			mc.setCameraEntity(entity);

			if (isMountingCamera) {
				CameraController.setCameraMountedTimestamp();
				CameraController.previousCameraType = mc.options.getCameraType();
				mc.options.setCameraType(CameraType.FIRST_PERSON);
				mc.gui.setOverlayMessage(Utils.localize("mount.onboard", mc.options.keyShift.getTranslatedKeyMessage()), false);
				OverlayToggleHandler.disable(VanillaGuiOverlay.EXPERIENCE_BAR);
				OverlayToggleHandler.disable(VanillaGuiOverlay.JUMP_BAR);
				OverlayToggleHandler.disable(VanillaGuiOverlay.POTION_ICONS);
				OverlayToggleHandler.enable(ClientHandler.cameraOverlay);
				SCClientEventHandler.resetCameraInfoMessageTime();
			}
			else {
				if (CameraController.previousCameraType != null)
					mc.options.setCameraType(CameraController.previousCameraType);

				CameraController.resetOverlaysAfterDismount = true;
			}

			mc.levelRenderer.allChanged();
		}
	}
}
