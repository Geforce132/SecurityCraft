package net.geforcemods.securitycraft.network.server;

import java.util.List;

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

	@Override
	public void write(FriendlyByteBuf buf) {
		buf.writeBlockPos(pos);
		buf.writeInt(mode);
	}

	@Override
	public ResourceLocation id() {
		return ID;
	}

	public void handle(PlayPayloadContext ctx) {
		Player player = ctx.player().orElseThrow();
		Level level = player.level();

		if (level.isLoaded(pos)) {
			List<Sentry> sentries = level.<Sentry>getEntitiesOfClass(Sentry.class, new AABB(pos));

			if (!sentries.isEmpty() && sentries.get(0).isOwnedBy(player))
				sentries.get(0).toggleMode(player, mode, false);
		}
	}
}
