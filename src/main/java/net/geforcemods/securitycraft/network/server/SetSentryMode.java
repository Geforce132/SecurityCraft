package net.geforcemods.securitycraft.network.server;

import java.util.List;
import java.util.function.Supplier;

import net.geforcemods.securitycraft.entity.sentry.Sentry;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.network.NetworkEvent;

public class SetSentryMode {
	private BlockPos pos;
	private int mode;

	public SetSentryMode() {}

	public SetSentryMode(BlockPos sentryPos, int mode) {
		pos = sentryPos;
		this.mode = mode;
	}

	public SetSentryMode(FriendlyByteBuf buf) {
		pos = buf.readBlockPos();
		mode = buf.readInt();
	}

	public void encode(FriendlyByteBuf buf) {
		buf.writeBlockPos(pos);
		buf.writeInt(mode);
	}

	public void handle(Supplier<NetworkEvent.Context> ctx) {
		Player player = ctx.get().getSender();
		Level level = player.level();

		if (level.isLoaded(pos)) {
			List<Sentry> sentries = level.<Sentry>getEntitiesOfClass(Sentry.class, new AABB(pos));

			if (!sentries.isEmpty() && sentries.get(0).isOwnedBy(player))
				sentries.get(0).toggleMode(player, mode, false);
		}
	}
}
