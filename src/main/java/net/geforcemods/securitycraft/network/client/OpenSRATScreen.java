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

	public OpenSRATScreen(PacketBuffer buf) {
		viewDistance = buf.readInt();
	}

	public void encode(PacketBuffer buf) {
		buf.writeInt(viewDistance);
	}

	public void handle(Supplier<NetworkEvent.Context> ctx) {
		ClientHandler.displaySRATScreen(PlayerUtils.getSelectedItemStack(ClientHandler.getClientPlayer(), SCContent.REMOTE_ACCESS_SENTRY.get()), viewDistance);
	}
}
