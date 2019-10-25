package net.geforcemods.securitycraft.network.client;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.tileentity.TileEntityLogger;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkEvent;

public class ClearLoggerClient
{
	private BlockPos pos;

	public ClearLoggerClient() {}

	public ClearLoggerClient(BlockPos pos)
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

	public static void encode(ClearLoggerClient message, PacketBuffer packet)
	{
		message.toBytes(packet);
	}

	public static ClearLoggerClient decode(PacketBuffer packet)
	{
		ClearLoggerClient message = new ClearLoggerClient();

		message.fromBytes(packet);
		return message;
	}

	public static void onMessage(ClearLoggerClient message, Supplier<NetworkEvent.Context> ctx)
	{
		DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> handleMessage(message, ctx));
	}

	@OnlyIn(Dist.CLIENT)
	public static void handleMessage(ClearLoggerClient message, Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(() -> {
			TileEntityLogger te = (TileEntityLogger)Minecraft.getInstance().world.getTileEntity(message.pos);

			if(te != null)
				te.players = new String[100];
		});

		ctx.get().setPacketHandled(true);
	}
}
