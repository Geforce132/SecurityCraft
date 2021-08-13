package net.geforcemods.securitycraft.network.client;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.blockentities.UsernameLoggerBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

public class UpdateLogger {

	private int x, y, z, i;
	private String username;
	private String uuid;
	private long timestamp;

	public UpdateLogger(){

	}

	public UpdateLogger(int x, int y, int z, int i, String username, String uuid, long timestamp){
		this.x = x;
		this.y = y;
		this.z = z;
		this.i = i;
		this.username = username;
		this.uuid = uuid;
		this.timestamp = timestamp;
	}

	public static void encode(UpdateLogger message, FriendlyByteBuf buf)
	{
		buf.writeInt(message.x);
		buf.writeInt(message.y);
		buf.writeInt(message.z);
		buf.writeInt(message.i);
		buf.writeUtf(message.username);
		buf.writeUtf(message.uuid);
		buf.writeLong(message.timestamp);
	}

	public static UpdateLogger decode(FriendlyByteBuf buf)
	{
		UpdateLogger message = new UpdateLogger();

		message.x = buf.readInt();
		message.y = buf.readInt();
		message.z = buf.readInt();
		message.i = buf.readInt();
		message.username = buf.readUtf(Integer.MAX_VALUE / 4);
		message.uuid = buf.readUtf(Integer.MAX_VALUE / 4);
		message.timestamp = buf.readLong();
		return message;
	}

	public static void onMessage(UpdateLogger message, Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(() -> {
			BlockPos pos = new BlockPos(message.x, message.y, message.z);
			int i = message.i;

			UsernameLoggerBlockEntity te = (UsernameLoggerBlockEntity) Minecraft.getInstance().player.level.getBlockEntity(pos);

			if(te != null)
			{
				te.players[i] = message.username;
				te.uuids[i] = message.uuid;
				te.timestamps[i] = message.timestamp;
			}
		});

		ctx.get().setPacketHandled(true);
	}
}
