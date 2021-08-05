package net.geforcemods.securitycraft.network.client;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.ClientHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class OpenSRATGui
{
	private int viewDistance;

	public OpenSRATGui() {}

	public OpenSRATGui(int viewDistance)
	{
		this.viewDistance = viewDistance;
	}

	public static void encode(OpenSRATGui message, PacketBuffer buf)
	{
		buf.writeInt(message.viewDistance);
	}

	public static OpenSRATGui decode(PacketBuffer buf)
	{
		OpenSRATGui message = new OpenSRATGui();

		message.viewDistance = buf.readInt();
		return message;
	}

	public static void onMessage(OpenSRATGui message, Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(() -> ClientHandler.displaySRATGui(PlayerUtils.getSelectedItemStack(ClientHandler.getClientPlayer(), SCContent.REMOTE_ACCESS_SENTRY.get()), message.viewDistance));
		ctx.get().setPacketHandled(true);
	}
}
