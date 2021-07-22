package net.geforcemods.securitycraft.network.server;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.tileentity.UsernameLoggerTileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
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

	public static void encode(ClearLoggerServer message, PacketBuffer buf)
	{
		buf.writeBlockPos(message.pos);
	}

	public static ClearLoggerServer decode(PacketBuffer buf)
	{
		ClearLoggerServer message = new ClearLoggerServer();

		message.pos = buf.readBlockPos();
		return message;
	}

	public static void onMessage(ClearLoggerServer message, Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(() -> {
			PlayerEntity player = ctx.get().getSender();
			TileEntity te = player.level.getBlockEntity(message.pos);

			if(te instanceof UsernameLoggerTileEntity && ((UsernameLoggerTileEntity)te).getOwner().isOwner(player))
			{
				((UsernameLoggerTileEntity)te).players = new String[100];
				((UsernameLoggerTileEntity)te).sendChangeToClient(true);
			}
		});

		ctx.get().setPacketHandled(true);
	}
}
