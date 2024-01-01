package net.geforcemods.securitycraft.network.client;

import java.util.List;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.entity.sentry.Sentry;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

public class InitSentryAnimation implements CustomPacketPayload {
	public static final ResourceLocation ID = new ResourceLocation(SecurityCraft.MODID, "init_sentry_animation");
	private BlockPos pos;
	private boolean animate, animateUpwards, isShutDown;

	public InitSentryAnimation() {}

	public InitSentryAnimation(BlockPos pos, boolean animate, boolean animateUpwards, boolean isShutDown) {
		this.pos = pos;
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

	@Override
	public void write(FriendlyByteBuf buf) {
		buf.writeLong(pos.asLong());
		buf.writeBoolean(animate);
		buf.writeBoolean(animateUpwards);
		buf.writeBoolean(isShutDown);
	}

	@Override
	public ResourceLocation id() {
		return ID;
	}

	public void handle(PlayPayloadContext ctx) {
		List<Sentry> sentries = Minecraft.getInstance().level.<Sentry>getEntitiesOfClass(Sentry.class, new AABB(pos));

		if (!sentries.isEmpty()) {
			Sentry sentry = sentries.get(0);

			sentry.setShutDown(isShutDown);
			sentry.setAnimateUpwards(animateUpwards);
			sentry.setAnimate(animate);
		}
	}
}
