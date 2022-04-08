package net.geforcemods.securitycraft.network.client;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.ClientHandler;
import net.geforcemods.securitycraft.blockentities.IMSBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public class OpenIMSScreen {
	private BlockPos pos;

	public OpenIMSScreen() {}

	public OpenIMSScreen(BlockPos pos) {
		this.pos = pos;
	}

	public static void encode(OpenIMSScreen message, FriendlyByteBuf buf) {
		buf.writeBlockPos(message.pos);
	}

	public static OpenIMSScreen decode(FriendlyByteBuf buf) {
		return new OpenIMSScreen(buf.readBlockPos());
	}

	public static void onMessage(OpenIMSScreen message, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			if (Minecraft.getInstance().level.getBlockEntity(message.pos) instanceof IMSBlockEntity ims)
				ClientHandler.displayIMSGui(ims);
		});
		ctx.get().setPacketHandled(true);
	}
}
