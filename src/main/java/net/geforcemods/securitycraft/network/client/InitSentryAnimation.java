package net.geforcemods.securitycraft.network.client;

import java.util.List;
import java.util.function.Supplier;

import net.geforcemods.securitycraft.entity.sentry.Sentry;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

public class InitSentryAnimation {
	private BlockPos pos;
	public boolean animate, animateUpwards, isShutDown;

	public InitSentryAnimation() {}

	public InitSentryAnimation(BlockPos sentryPos, boolean animate, boolean animateUpwards, boolean isShutDown) {
		pos = sentryPos;
		this.animate = animate;
		this.animateUpwards = animateUpwards;
		this.isShutDown = isShutDown;
	}

	public static void encode(InitSentryAnimation message, FriendlyByteBuf buf) {
		buf.writeLong(message.pos.asLong());
		buf.writeBoolean(message.animate);
		buf.writeBoolean(message.animateUpwards);
		buf.writeBoolean(message.isShutDown);
	}

	public static InitSentryAnimation decode(FriendlyByteBuf buf) {
		InitSentryAnimation message = new InitSentryAnimation();

		message.pos = BlockPos.of(buf.readLong());
		message.animate = buf.readBoolean();
		message.animateUpwards = buf.readBoolean();
		message.isShutDown = buf.readBoolean();
		return message;
	}

	public static void onMessage(InitSentryAnimation message, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			List<Sentry> sentries = Minecraft.getInstance().level.<Sentry>getEntitiesOfClass(Sentry.class, new AABB(message.pos));

			if (!sentries.isEmpty()) {
				Sentry sentry = sentries.get(0);

				sentry.setShutDown(message.isShutDown);
				sentry.animateUpwards = message.animateUpwards;
				sentry.animate = message.animate;
			}
		});

		ctx.get().setPacketHandled(true);
	}
}
