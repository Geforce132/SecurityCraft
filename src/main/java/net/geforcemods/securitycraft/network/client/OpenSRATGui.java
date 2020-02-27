package net.geforcemods.securitycraft.network.client;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.SecurityCraft;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkEvent;

public class OpenSRATGui
{
	private int viewDistance;

	public OpenSRATGui() {}

	public OpenSRATGui(int viewDistance)
	{
		this.viewDistance = viewDistance;
	}

	public void toBytes(PacketBuffer buf)
	{
		buf.writeInt(viewDistance);
	}

	public void fromBytes(PacketBuffer buf)
	{
		viewDistance = buf.readInt();
	}

	public static void encode(OpenSRATGui message, PacketBuffer packet)
	{
		message.toBytes(packet);
	}

	public static OpenSRATGui decode(PacketBuffer packet)
	{
		OpenSRATGui message = new OpenSRATGui();

		message.fromBytes(packet);
		return message;
	}

	public static void onMessage(OpenSRATGui message, Supplier<NetworkEvent.Context> ctx)
	{
		DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> handleMessage(message, ctx));
	}

	@OnlyIn(Dist.CLIENT)
	public static void handleMessage(OpenSRATGui message, Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(() -> {
			SecurityCraft.proxy.displaySRATGui(Minecraft.getInstance().player.inventory.getCurrentItem(), message.viewDistance);
		});

		ctx.get().setPacketHandled(true);
	}
}
