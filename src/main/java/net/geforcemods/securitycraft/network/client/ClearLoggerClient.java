package net.geforcemods.securitycraft.network.client;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.tileentity.UsernameLoggerTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

public class ClearLoggerClient
{
	private BlockPos pos;

	public ClearLoggerClient() {}

	public ClearLoggerClient(BlockPos pos)
	{
		this.pos = pos;
	}

	public static void encode(ClearLoggerClient message, FriendlyByteBuf buf)
	{
		buf.writeBlockPos(message.pos);
	}

	public static ClearLoggerClient decode(FriendlyByteBuf buf)
	{
		ClearLoggerClient message = new ClearLoggerClient();

		message.pos = buf.readBlockPos();
		return message;
	}

	public static void onMessage(ClearLoggerClient message, Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(() -> {
			UsernameLoggerTileEntity te = (UsernameLoggerTileEntity)Minecraft.getInstance().level.getBlockEntity(message.pos);

			if(te != null)
				te.players = new String[100];
		});

		ctx.get().setPacketHandled(true);
	}
}
