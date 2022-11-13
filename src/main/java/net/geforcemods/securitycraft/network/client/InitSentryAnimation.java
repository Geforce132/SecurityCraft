package net.geforcemods.securitycraft.network.client;

import java.util.List;
import java.util.function.Supplier;

import net.geforcemods.securitycraft.entity.Sentry;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

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

	public static void encode(InitSentryAnimation message, PacketBuffer buf) {
		buf.writeLong(message.pos.asLong());
		buf.writeBoolean(message.animate);
		buf.writeBoolean(message.animateUpwards);
		buf.writeBoolean(message.isShutDown);
	}

	public static InitSentryAnimation decode(PacketBuffer buf) {
		InitSentryAnimation message = new InitSentryAnimation();

		message.pos = BlockPos.of(buf.readLong());
		message.animate = buf.readBoolean();
		message.animateUpwards = buf.readBoolean();
		message.isShutDown = buf.readBoolean();
		return message;
	}

	public static void onMessage(InitSentryAnimation message, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			List<Sentry> sentries = Minecraft.getInstance().level.<Sentry>getEntitiesOfClass(Sentry.class, new AxisAlignedBB(message.pos));

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
