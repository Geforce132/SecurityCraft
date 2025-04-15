package net.geforcemods.securitycraft.network.server;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

import net.geforcemods.securitycraft.entity.sentry.Sentry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;

public class SetSentryMode {
	private List<Info> sentriesToUpdate;

	public SetSentryMode() {}

	public SetSentryMode(List<Info> sentriesToUpdate) {
		sentriesToUpdate.removeIf(Objects::isNull);
		this.sentriesToUpdate = sentriesToUpdate;
	}

	public SetSentryMode(PacketBuffer buf) {
		int size = buf.readVarInt();

		sentriesToUpdate = new ArrayList<>();

		for (int i = 0; i < size; i++) {
			sentriesToUpdate.add(Info.read(buf));
		}
	}

	public void encode(PacketBuffer buf) {
		buf.writeVarInt(sentriesToUpdate.size());
		sentriesToUpdate.forEach(info -> info.write(buf));
	}

	public void handle(Supplier<NetworkEvent.Context> ctx) {
		PlayerEntity player = ctx.get().getSender();
		World level = player.level;

		if (!player.isSpectator()) {
			for (Info info : sentriesToUpdate) {
				if (level.isLoaded(info.pos)) {
					List<Sentry> sentries = level.<Sentry>getEntitiesOfClass(Sentry.class, new AxisAlignedBB(info.pos));

					if (!sentries.isEmpty() && sentries.get(0).isOwnedBy(player))
						sentries.get(0).toggleMode(player, info.mode, false);
				}
			}
		}
	}

	public static class Info {
		private final BlockPos pos;
		private final int mode;

		public Info(BlockPos pos, int mode) {
			this.pos = pos;
			this.mode = mode;
		}

		public static Info read(PacketBuffer buf) {
			return new Info(buf.readBlockPos(), buf.readVarInt());
		}

		public void write(PacketBuffer buf) {
			buf.writeBlockPos(pos);
			buf.writeVarInt(mode);
		}
	}
}
