package net.geforcemods.securitycraft.network.client;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.ClientHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class OpenSRATScreen {
	private int viewDistance;

	public OpenSRATScreen() {}

	public OpenSRATScreen(int viewDistance) {
		this.viewDistance = viewDistance;
	}

	public static void encode(OpenSRATScreen message, PacketBuffer buf) {
		buf.writeInt(message.viewDistance);
	}

	public static OpenSRATScreen decode(PacketBuffer buf) {
		OpenSRATScreen message = new OpenSRATScreen();

		message.viewDistance = buf.readInt();
		return message;
	}

	public static void onMessage(OpenSRATScreen message, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> ClientHandler.displaySRATScreen(PlayerUtils.getSelectedItemStack(ClientHandler.getClientPlayer(), SCContent.REMOTE_ACCESS_SENTRY.get()), message.viewDistance));
		ctx.get().setPacketHandled(true);
	}
}
