package net.geforcemods.securitycraft.network.client;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.ClientHandler;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.client.gui.ForgeIngameGui;
import net.minecraftforge.client.gui.OverlayRegistry;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

public class SetCameraView
{
	private int id;

	public SetCameraView() {}

	public SetCameraView(Entity camera)
	{
		id = camera.getId();
	}

	public static void encode(SetCameraView message, FriendlyByteBuf buf)
	{
		buf.writeVarInt(message.id);
	}

	public static SetCameraView decode(FriendlyByteBuf buf)
	{
		SetCameraView message = new SetCameraView();

		message.id = buf.readVarInt();
		return message;
	}

	public static void onMessage(SetCameraView message, Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(() -> {
			Minecraft mc = Minecraft.getInstance();
			Entity entity = mc.level.getEntity(message.id);

			if(entity != null)
			{
				mc.setCameraEntity(entity);
				mc.gui.setOverlayMessage(Utils.localize("mount.onboard", mc.options.keyShift.getTranslatedKeyMessage()), false);
				OverlayRegistry.enableOverlay(ForgeIngameGui.EXPERIENCE_BAR_ELEMENT, false);
				OverlayRegistry.enableOverlay(ClientHandler.cameraOverlay, true);
				OverlayRegistry.enableOverlay(ClientHandler.hotbarBindOverlay, false);
			}
		});
		ctx.get().setPacketHandled(true);
	}
}
