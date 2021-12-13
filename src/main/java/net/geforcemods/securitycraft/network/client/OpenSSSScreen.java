package net.geforcemods.securitycraft.network.client;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.ClientHandler;
import net.geforcemods.securitycraft.blockentities.SonicSecuritySystemBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

public class OpenSSSScreen {
	private BlockPos pos;

	public OpenSSSScreen() {}

	public OpenSSSScreen(BlockPos pos) {
		this.pos = pos;
	}

	public static void encode(OpenSSSScreen message, FriendlyByteBuf buf) {
		buf.writeBlockPos(message.pos);
	}

	public static OpenSSSScreen decode(FriendlyByteBuf buf) {
		return new OpenSSSScreen(buf.readBlockPos());
	}

	public static void onMessage(OpenSSSScreen message, Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(() -> {
			if (Minecraft.getInstance().level.getBlockEntity(message.pos) instanceof SonicSecuritySystemBlockEntity sss)
				ClientHandler.displaySonicSecuritySystemGui(sss);
		});
		ctx.get().setPacketHandled(true);
	}
}
