package net.geforcemods.securitycraft.network.server;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.entity.camera.SecurityCameraEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class DismountCamera
{
	public DismountCamera() {}

	public static void encode(DismountCamera message, PacketBuffer buf) {}

	public static DismountCamera decode(PacketBuffer buf)
	{
		return new DismountCamera();
	}

	public static void onMessage(DismountCamera message, Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(() -> {
			ServerPlayerEntity player = ctx.get().getSender();

			if(player.getSpectatingEntity() instanceof SecurityCameraEntity)
				((SecurityCameraEntity)player.getSpectatingEntity()).stopViewing(player);
		});

		ctx.get().setPacketHandled(true);
	}
}
