package net.geforcemods.securitycraft.network.client;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.ClientHandler;
import net.geforcemods.securitycraft.blockentities.TrophySystemBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public class OpenTrophySystemScreen {
	private BlockPos pos;

	public OpenTrophySystemScreen() {}

	public OpenTrophySystemScreen(BlockPos pos) {
		this.pos = pos;
	}

	public static void encode(OpenTrophySystemScreen message, FriendlyByteBuf buf) {
		buf.writeBlockPos(message.pos);
	}

	public static OpenTrophySystemScreen decode(FriendlyByteBuf buf) {
		return new OpenTrophySystemScreen(buf.readBlockPos());
	}

	public static void onMessage(OpenTrophySystemScreen message, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			if (Minecraft.getInstance().level.getBlockEntity(message.pos) instanceof TrophySystemBlockEntity trophySystem)
				ClientHandler.displayTrophySystemGui(trophySystem);
		});
		ctx.get().setPacketHandled(true);
	}
}
