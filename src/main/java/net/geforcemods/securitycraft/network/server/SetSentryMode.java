package net.geforcemods.securitycraft.network.server;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.netty.buffer.ByteBuf;
import net.geforcemods.securitycraft.entity.sentry.Sentry;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class SetSentryMode implements IMessage {
	private List<Info> sentriesToUpdate;

	public SetSentryMode() {}

	public SetSentryMode(List<Info> sentriesToUpdate) {
		sentriesToUpdate.removeIf(Objects::isNull);
		this.sentriesToUpdate = sentriesToUpdate;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		int size = buf.readInt();

		sentriesToUpdate = new ArrayList<>();

		for (int i = 0; i < size; i++) {
			sentriesToUpdate.add(Info.read(buf));
		}
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(sentriesToUpdate.size());
		sentriesToUpdate.forEach(info -> info.write(buf));
	}

	public static class Handler implements IMessageHandler<SetSentryMode, IMessage> {
		@Override
		public IMessage onMessage(SetSentryMode message, MessageContext context) {
			Utils.addScheduledTask(context.getServerHandler().player.world, () -> {
				EntityPlayer player = context.getServerHandler().player;
				World level = player.world;

				if (!player.isSpectator()) {
					for (Info info : message.sentriesToUpdate) {
						if (level.isBlockLoaded(info.pos)) {
							List<Sentry> sentries = level.<Sentry>getEntitiesWithinAABB(Sentry.class, new AxisAlignedBB(info.pos));

							if (!sentries.isEmpty() && sentries.get(0).isOwnedBy(player))
								sentries.get(0).toggleMode(player, info.mode, false);
						}
					}
				}
			});

			return null;
		}
	}

	public static class Info {
		private final BlockPos pos;
		private final int mode;

		public Info(BlockPos pos, int mode) {
			this.pos = pos;
			this.mode = mode;
		}

		public static Info read(ByteBuf buf) {
			return new Info(BlockPos.fromLong(buf.readLong()), buf.readInt());
		}

		public void write(ByteBuf buf) {
			buf.writeLong(pos.toLong());
			buf.writeInt(mode);
		}
	}
}
