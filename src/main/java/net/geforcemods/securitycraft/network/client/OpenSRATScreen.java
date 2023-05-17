package net.geforcemods.securitycraft.network.client;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.ClientHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public class OpenSRATScreen {
	private int viewDistance;

	public OpenSRATScreen() {}

	public OpenSRATScreen(int viewDistance) {
		this.viewDistance = viewDistance;
	}

	public OpenSRATScreen(FriendlyByteBuf buf) {
		viewDistance = buf.readInt();
	}

	public void encode(FriendlyByteBuf buf) {
		buf.writeInt(viewDistance);
	}

	public void handle(Supplier<NetworkEvent.Context> ctx) {
		ClientHandler.displaySRATScreen(PlayerUtils.getSelectedItemStack(ClientHandler.getClientPlayer(), SCContent.REMOTE_ACCESS_SENTRY.get()), viewDistance);
	}
}
