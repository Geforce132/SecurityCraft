package net.geforcemods.securitycraft.network.server;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.blockentities.UsernameLoggerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

public class ClearLoggerServer
{
	private BlockPos pos;

	public ClearLoggerServer() {}

	public ClearLoggerServer(BlockPos pos)
	{
		this.pos = pos;
	}

	public static void encode(ClearLoggerServer message, FriendlyByteBuf buf)
	{
		buf.writeBlockPos(message.pos);
	}

	public static ClearLoggerServer decode(FriendlyByteBuf buf)
	{
		ClearLoggerServer message = new ClearLoggerServer();

		message.pos = buf.readBlockPos();
		return message;
	}

	public static void onMessage(ClearLoggerServer message, Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(() -> {
			Player player = ctx.get().getSender();

			if(player.level.getBlockEntity(message.pos) instanceof UsernameLoggerBlockEntity te && te.getOwner().isOwner(player))
			{
				te.players = new String[100];
				te.sendChangeToClient(true);
			}
		});

		ctx.get().setPacketHandled(true);
	}
}
