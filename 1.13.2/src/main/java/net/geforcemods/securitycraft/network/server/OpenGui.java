package net.geforcemods.securitycraft.network.server;

import java.util.function.Supplier;

import io.netty.buffer.ByteBuf;
import net.geforcemods.securitycraft.SecurityCraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class OpenGui {

	private int id;
	private int x;
	private int y;
	private int z;

	public OpenGui(){}

	public OpenGui(int id, int x, int y, int z){
		this.id = id;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public void toBytes(ByteBuf buf) {
		buf.writeInt(id);
		buf.writeInt(x);
		buf.writeInt(y);
		buf.writeInt(z);
	}

	public void fromBytes(ByteBuf buf) {
		id = buf.readInt();
		x = buf.readInt();
		y = buf.readInt();
		z = buf.readInt();
	}

	public static void encode(OpenGui message, PacketBuffer packet)
	{
		message.toBytes(packet);
	}

	public static OpenGui decode(PacketBuffer packet)
	{
		OpenGui message = new OpenGui();

		message.fromBytes(packet);
		return message;
	}

	public static void onMessage(OpenGui message, Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(() -> {
			int id = message.id;
			int x = message.x;
			int y = message.y;
			int z = message.z;
			EntityPlayer player = ctx.get().getSender();

			player.openGui(SecurityCraft.instance, id, player.world, x, y, z);
		});

		ctx.get().setPacketHandled(true);
	}
}
