package net.geforcemods.securitycraft.network.client;

import net.geforcemods.securitycraft.ClientHandler;
import net.geforcemods.securitycraft.SCClientEventHandler;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.entity.camera.CameraController;
import net.geforcemods.securitycraft.entity.camera.SecurityCamera;
import net.geforcemods.securitycraft.misc.OverlayToggleHandler;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.client.gui.overlay.VanillaGuiOverlay;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

public class SetCameraView implements CustomPacketPayload {
	public static final ResourceLocation ID = new ResourceLocation(SecurityCraft.MODID, "set_camera_view");
	private int id;

	public SetCameraView() {}

	public SetCameraView(Entity camera) {
		id = camera.getId();
	}

	public SetCameraView(FriendlyByteBuf buf) {
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
