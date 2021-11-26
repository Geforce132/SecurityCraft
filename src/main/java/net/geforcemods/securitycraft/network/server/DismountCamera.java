package net.geforcemods.securitycraft.network.server;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.entity.camera.SecurityCamera;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

public class DismountCamera
{
	public DismountCamera() {}

	public static void encode(DismountCamera message, FriendlyByteBuf buf) {}

	public static DismountCamera decode(FriendlyByteBuf buf)
	{
		return new DismountCamera();
	}

	public static void onMessage(DismountCamera message, Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(() -> {
			ServerPlayer player = ctx.get().getSender();

			if(player.getCamera() instanceof SecurityCamera cam)
			{
				cam.stopViewing(player);
			}
		});

		ctx.get().setPacketHandled(true);
	}
}
