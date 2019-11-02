package net.geforcemods.securitycraft.network.server;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.misc.BaseInteractionObject;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkHooks;

public class OpenGui {

	private ResourceLocation id;
	private int x;
	private int y;
	private int z;

	public OpenGui(){}

	public OpenGui(ResourceLocation id, BlockPos pos){
		this(id, pos.getX(), pos.getY(), pos.getZ());
	}


	public OpenGui(ResourceLocation id, int x, int y, int z){
		this.id = id;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public void toBytes(PacketBuffer buf) {
		buf.writeString(id.toString());
		buf.writeInt(x);
		buf.writeInt(y);
		buf.writeInt(z);
	}

	public void fromBytes(PacketBuffer buf) {
		id = new ResourceLocation(buf.readString(Integer.MAX_VALUE / 4));
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
			ResourceLocation id = message.id;
			int x = message.x;
			int y = message.y;
			int z = message.z;
			EntityPlayerMP player = ctx.get().getSender();

			NetworkHooks.openGui(player, new BaseInteractionObject(id), new BlockPos(x, y, z));
		});

		ctx.get().setPacketHandled(true);
	}
}
