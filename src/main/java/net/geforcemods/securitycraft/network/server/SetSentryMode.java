package net.geforcemods.securitycraft.network.server;

import java.util.List;
import java.util.function.Supplier;

import net.geforcemods.securitycraft.entity.sentry.Sentry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

public class SetSentryMode {
	public BlockPos pos;
	public int mode;

	public SetSentryMode() {}

	public SetSentryMode(BlockPos sentryPos, int mode) {
		pos = sentryPos;
		this.mode = mode;
	}

	public SetSentryMode(PacketBuffer buf) {
		pos = buf.readBlockPos();
		mode = buf.readInt();
	}

	public void encode(PacketBuffer buf) {
		buf.writeBlockPos(pos);
		buf.writeInt(mode);
	}

	public void handle(Supplier<NetworkEvent.Context> ctx) {
		PlayerEntity player = ctx.get().getSender();

		List<Sentry> sentries = player.level.<Sentry>getEntitiesOfClass(Sentry.class, new AxisAlignedBB(pos));

		if (!sentries.isEmpty() && sentries.get(0).isOwnedBy(player))
			sentries.get(0).toggleMode(player, mode, false);
	}
}
