package net.geforcemods.securitycraft.network.server;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blockentities.SecurityCameraBlockEntity;
import net.geforcemods.securitycraft.entity.camera.SecurityCamera;
import net.geforcemods.securitycraft.network.client.SetCameraView;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.fmllegacy.network.NetworkEvent;
import net.minecraftforge.fmllegacy.network.PacketDistributor;

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
				if(player.level.getBlockEntity(cam.blockPosition()) instanceof SecurityCameraBlockEntity camBe)
					camBe.stopViewing();

				player.camera = player;
				cam.discardCamera();
				SecurityCraft.channel.send(PacketDistributor.PLAYER.with(() -> player), new SetCameraView(player));
			}
		});

		ctx.get().setPacketHandled(true);
	}
}
