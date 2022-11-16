package net.geforcemods.securitycraft.network.client;

import java.util.List;

import io.netty.buffer.ByteBuf;
import net.geforcemods.securitycraft.entity.EntitySentry;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityCreature;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class InitSentryAnimation implements IMessage {
	public BlockPos pos;
	public boolean animate, animateUpwards, isShutDown;

	public InitSentryAnimation() {}

	public InitSentryAnimation(BlockPos sentryPos, boolean animate, boolean animateUpwards, boolean isShutDown) {
		pos = sentryPos;
		this.animate = animate;
		this.animateUpwards = animateUpwards;
		this.isShutDown = isShutDown;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		pos = BlockPos.fromLong(buf.readLong());
		animate = buf.readBoolean();
		animateUpwards = buf.readBoolean();
		isShutDown = buf.readBoolean();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeLong(pos.toLong());
		buf.writeBoolean(animate);
		buf.writeBoolean(animateUpwards);
		buf.writeBoolean(isShutDown);
	}

	public static class Handler implements IMessageHandler<InitSentryAnimation, IMessage> {
		@Override
		public IMessage onMessage(InitSentryAnimation message, MessageContext ctx) {
			List<EntityCreature> sentries = Minecraft.getMinecraft().player.world.<EntityCreature>getEntitiesWithinAABB(EntitySentry.class, new AxisAlignedBB(message.pos));

			if (!sentries.isEmpty()) {
				EntitySentry sentry = (EntitySentry) sentries.get(0);

				sentry.setShutDown(message.isShutDown);
				sentry.animateUpwards = message.animateUpwards;
				sentry.animate = message.animate;
			}

			return null;
		}
	}
}
