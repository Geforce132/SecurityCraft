package net.geforcemods.securitycraft.network.client;

import java.util.List;
import java.util.function.Supplier;

import net.geforcemods.securitycraft.entity.sentry.Sentry;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.network.NetworkEvent;

public class InitSentryAnimation {
	private BlockPos pos;
	private boolean animate, animateUpwards, isShutDown;

	public InitSentryAnimation() {}

	public InitSentryAnimation(BlockPos sentryPos, boolean animate, boolean animateUpwards, boolean isShutDown) {
		pos = sentryPos;
		this.animate = animate;
		this.animateUpwards = animateUpwards;
		this.isShutDown = isShutDown;
	}

	public InitSentryAnimation(FriendlyByteBuf buf) {
		pos = BlockPos.of(buf.readLong());
		animate = buf.readBoolean();
		animateUpwards = buf.readBoolean();
		isShutDown = buf.readBoolean();
	}

	public void encode(FriendlyByteBuf buf) {
		buf.writeLong(pos.asLong());
		buf.writeBoolean(animate);
		buf.writeBoolean(animateUpwards);
		buf.writeBoolean(isShutDown);
	}

	public void handle(Supplier<NetworkEvent.Context> ctx) {
		List<Sentry> sentries = Minecraft.getInstance().level.<Sentry>getEntitiesOfClass(Sentry.class, new AABB(pos));

		if (!sentries.isEmpty()) {
			Sentry sentry = sentries.get(0);

			sentry.setShutDown(isShutDown);
			sentry.setAnimateUpwards(animateUpwards);
			sentry.setAnimate(animate);
		}
	}
}
