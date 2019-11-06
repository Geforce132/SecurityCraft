package net.geforcemods.securitycraft.network.server;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.tileentity.UsernameLoggerTileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

public class ClearLoggerServer
{
	private BlockPos pos;

	public ClearLoggerServer() {}

	public ClearLoggerServer(BlockPos pos)
	{
		this.pos = pos;
	}

	public void toBytes(PacketBuffer buf)
	{
		buf.writeBlockPos(pos);
	}

	public void fromBytes(PacketBuffer buf)
	{
		pos = buf.readBlockPos();
	}

	public static void encode(ClearLoggerServer message, PacketBuffer packet)
	{
		message.toBytes(packet);
	}

	public static ClearLoggerServer decode(PacketBuffer packet)
	{
		ClearLoggerServer message = new ClearLoggerServer();

		message.fromBytes(packet);
		return message;
	}

	public static void onMessage(ClearLoggerServer message, Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(() -> {
			PlayerEntity player = ctx.get().getSender();
			UsernameLoggerTileEntity te = (UsernameLoggerTileEntity)player.world.getTileEntity(message.pos);

			if(te != null)
			{
				te.players = new String[100];
				te.sendChangeToClient(true);
			}
		});

		ctx.get().setPacketHandled(true);
	}
}
