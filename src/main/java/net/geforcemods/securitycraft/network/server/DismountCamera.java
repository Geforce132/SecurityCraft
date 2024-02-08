package net.geforcemods.securitycraft.network.server;

import io.netty.buffer.ByteBuf;
import net.geforcemods.securitycraft.entity.camera.SecurityCamera;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class DismountCamera implements IMessage {
	public DismountCamera() {}

	@Override
	public void fromBytes(ByteBuf buf) {}

	@Override
	public void toBytes(ByteBuf buf) {}

	public static class Handler implements IMessageHandler<DismountCamera, IMessage> {
		@Override
		public IMessage onMessage(DismountCamera message, MessageContext ctx) {
			Utils.addScheduledTask(ctx.getServerHandler().player.world, () -> {
				EntityPlayerMP player = ctx.getServerHandler().player;

				if (player.getSpectatingEntity() instanceof SecurityCamera)
					((SecurityCamera) player.getSpectatingEntity()).stopViewing(player);
			});

			return null;
		}
	}
}
