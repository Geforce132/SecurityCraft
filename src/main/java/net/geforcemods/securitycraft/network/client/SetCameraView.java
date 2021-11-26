package net.geforcemods.securitycraft.network.client;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.entity.camera.CameraController;
import net.geforcemods.securitycraft.entity.camera.SecurityCameraEntity;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.PointOfView;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class SetCameraView
{
	private int id;

	public SetCameraView() {}

	public SetCameraView(Entity camera)
	{
		id = camera.getEntityId();
	}

	public static void encode(SetCameraView message, PacketBuffer buf)
	{
		buf.writeVarInt(message.id);
	}

	public static SetCameraView decode(PacketBuffer buf)
	{
		SetCameraView message = new SetCameraView();

		message.id = buf.readVarInt();
		return message;
	}

	public static void onMessage(SetCameraView message, Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(() -> {
			Minecraft mc = Minecraft.getInstance();
			Entity entity = mc.world.getEntityByID(message.id);
			boolean isCamera = entity instanceof SecurityCameraEntity;

			if(isCamera || entity instanceof PlayerEntity)
			{
				mc.setRenderViewEntity(entity);

				if (isCamera) {
					CameraController.previousCameraType = mc.gameSettings.getPointOfView();
					mc.gameSettings.setPointOfView(PointOfView.FIRST_PERSON);
					mc.ingameGUI.setOverlayMessage(Utils.localize("mount.onboard", mc.gameSettings.keyBindSneak.func_238171_j_()), false);
					CameraController.setRenderPosition(entity);
				}
				else if(CameraController.previousCameraType != null)
					mc.gameSettings.setPointOfView(CameraController.previousCameraType);

				mc.worldRenderer.loadRenderers();
			}
		});
		ctx.get().setPacketHandled(true);
	}
}
