package net.geforcemods.securitycraft.network.client;

import java.util.List;
import java.util.function.Supplier;

import net.geforcemods.securitycraft.entity.sentry.Sentry;
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

	public InitSentryAnimation(PacketBuffer buf) {
		pos = BlockPos.of(buf.readLong());
		animate = buf.readBoolean();
		animateUpwards = buf.readBoolean();
		isShutDown = buf.readBoolean();
	}

	public void encode(PacketBuffer buf) {
		buf.writeLong(pos.asLong());
		buf.writeBoolean(animate);
		buf.writeBoolean(animateUpwards);
		buf.writeBoolean(isShutDown);
	}

	public void handle(Supplier<NetworkEvent.Context> ctx) {
		List<Sentry> sentries = Minecraft.getInstance().level.<Sentry>getEntitiesOfClass(Sentry.class, new AxisAlignedBB(pos));

		if (!sentries.isEmpty()) {
			Sentry sentry = sentries.get(0);

			sentry.setShutDown(isShutDown);
			sentry.animateUpwards = animateUpwards;
			sentry.animate = animate;
		}
	}
}
