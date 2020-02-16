package net.geforcemods.securitycraft.network.client;

import java.util.UUID;
import java.util.function.Supplier;

import net.geforcemods.securitycraft.tileentity.UsernameLoggerTileEntity;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkEvent;

public class UpdateLogger {

	private int x, y, z, i;
	private String username;
	private UUID uuid;
	private long timestamp;

	public UpdateLogger(){

	}

	public UpdateLogger(int x, int y, int z, int i, String username, UUID uuid, long timestamp){
		this.x = x;
		this.y = y;
		this.z = z;
		this.i = i;
		this.username = username;
		this.uuid = uuid;
		this.timestamp = timestamp;
	}

	public void toBytes(PacketBuffer buf) {
		buf.writeInt(x);
		buf.writeInt(y);
		buf.writeInt(z);
		buf.writeInt(i);
		buf.writeString(username);
		buf.writeUniqueId(uuid == null ? new UUID(0, 0) : uuid);
		buf.writeLong(timestamp);
	}

	public void fromBytes(PacketBuffer buf) {
		x = buf.readInt();
		y = buf.readInt();
		z = buf.readInt();
		i = buf.readInt();
		username = buf.readString(Integer.MAX_VALUE / 4);
		uuid = buf.readUniqueId();
		timestamp = buf.readLong();
	}

	public static void encode(UpdateLogger message, PacketBuffer packet)
	{
		message.toBytes(packet);
	}

	public static UpdateLogger decode(PacketBuffer packet)
	{
		UpdateLogger message = new UpdateLogger();

		message.fromBytes(packet);
		return message;
	}

	public static void onMessage(UpdateLogger message, Supplier<NetworkEvent.Context> ctx)
	{
		DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> handleMessage(message, ctx));
	}

	@OnlyIn(Dist.CLIENT)
	public static void handleMessage(UpdateLogger message, Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(() -> {
			BlockPos pos = BlockUtils.toPos(message.x, message.y, message.z);
			int i = message.i;

			UsernameLoggerTileEntity te = (UsernameLoggerTileEntity) Minecraft.getInstance().player.world.getTileEntity(pos);

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
