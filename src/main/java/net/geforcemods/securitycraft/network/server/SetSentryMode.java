package net.geforcemods.securitycraft.network.server;

import java.util.List;
import java.util.function.Supplier;

import net.geforcemods.securitycraft.entity.Sentry;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

public class SetSentryMode
{
	public BlockPos pos;
	public int mode;

	public SetSentryMode() {}

	public SetSentryMode(BlockPos sentryPos, int mode)
	{
		pos = sentryPos;
		this.mode = mode;
	}

	public static void encode(SetSentryMode message, FriendlyByteBuf buf)
	{
		buf.writeBlockPos(message.pos);
		buf.writeInt(message.mode);
	}

	public static SetSentryMode decode(FriendlyByteBuf buf)
	{
		SetSentryMode message = new SetSentryMode();

		message.pos = buf.readBlockPos();
		message.mode = buf.readInt();
		return message;
	}

	public static void onMessage(SetSentryMode message, Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(() -> {
			Player player = ctx.get().getSender();

			List<Sentry> sentries = player.level.<Sentry>getEntitiesOfClass(Sentry.class, new AABB(message.pos));

			if(!sentries.isEmpty() && sentries.get(0).getOwner().isOwner(player))
				sentries.get(0).toggleMode(player, message.mode, false);
		});

		ctx.get().setPacketHandled(true);
	}
}
