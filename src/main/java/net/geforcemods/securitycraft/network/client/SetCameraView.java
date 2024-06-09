package net.geforcemods.securitycraft.network.client;

import net.geforcemods.securitycraft.ClientHandler;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.entity.camera.CameraController;
import net.geforcemods.securitycraft.entity.camera.SecurityCamera;
import net.geforcemods.securitycraft.misc.LayerToggleHandler;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SetCameraView(int id) implements CustomPacketPayload {
	public static final Type<SetCameraView> TYPE = new Type<>(SecurityCraft.resLoc("set_camera_view"));
	//@formatter:off
	public static final StreamCodec<RegistryFriendlyByteBuf, SetCameraView> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.VAR_INT, SetCameraView::id,
			SetCameraView::new);
	//@formatter:on

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public void handle(IPayloadContext ctx) {
		Minecraft mc = Minecraft.getInstance();
		Entity entity = mc.level.getEntity(id);
		boolean isMountingCamera = entity instanceof SecurityCamera;

		if (isMountingCamera || entity instanceof Player) {
			mc.setCameraEntity(entity);

			if (isMountingCamera) {
				CameraController.previousCameraType = mc.options.getCameraType();
				mc.options.setCameraType(CameraType.FIRST_PERSON);
				mc.gui.setOverlayMessage(Utils.localize("mount.onboard", mc.options.keyShift.getTranslatedKeyMessage()), false);
				CameraController.setRenderPosition(entity);
				LayerToggleHandler.disable(VanillaGuiLayers.JUMP_METER);
				LayerToggleHandler.disable(VanillaGuiLayers.EXPERIENCE_BAR);
				LayerToggleHandler.disable(VanillaGuiLayers.EFFECTS);
				LayerToggleHandler.enable(ClientHandler.CAMERA_LAYER);
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
