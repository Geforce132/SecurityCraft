package net.geforcemods.securitycraft.network.server;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.entity.sentry.Sentry;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

public class SetSentryMode implements CustomPacketPayload {
	public static final ResourceLocation ID = new ResourceLocation(SecurityCraft.MODID, "set_sentry_mode");
	private List<Info> sentriesToUpdate;

	public SetSentryMode() {}

	public SetSentryMode(List<Info> sentriesToUpdate) {
		sentriesToUpdate.removeIf(Objects::isNull);
		this.sentriesToUpdate = sentriesToUpdate;
	}

	public SetSentryMode(FriendlyByteBuf buf) {
		int size = buf.readVarInt();

		sentriesToUpdate = new ArrayList<>();

		for (int i = 0; i < size; i++) {
			sentriesToUpdate.add(Info.read(buf));
		}
	}

	@Override
	public void write(FriendlyByteBuf buf) {
		buf.writeVarInt(sentriesToUpdate.size());
		sentriesToUpdate.forEach(info -> info.write(buf));
	}

	@Override
	public ResourceLocation id() {
		return ID;
	}

	public void handle(PlayPayloadContext ctx) {
		Player player = ctx.player().orElseThrow();
		Level level = player.level();

		if (!player.isSpectator()) {
			for (Info info : sentriesToUpdate) {
				if (level.isLoaded(info.pos)) {
					List<Sentry> sentries = level.<Sentry>getEntitiesOfClass(Sentry.class, new AABB(info.pos));

					if (!sentries.isEmpty() && sentries.get(0).isOwnedBy(player))
						sentries.get(0).toggleMode(player, info.mode, false);
				}
			}
		}
	}

	public static record Info(BlockPos pos, int mode) {
		public static Info read(FriendlyByteBuf buf) {
			return new Info(buf.readBlockPos(), buf.readVarInt());
		}

		public void write(FriendlyByteBuf buf) {
			buf.writeBlockPos(pos);
			buf.writeVarInt(mode);
		}
	}
}
