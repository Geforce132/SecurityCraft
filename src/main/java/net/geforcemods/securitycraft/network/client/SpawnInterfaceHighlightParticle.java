package net.geforcemods.securitycraft.network.client;

import io.netty.buffer.ByteBuf;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.particle.InterfaceHighlightParticle;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class SpawnInterfaceHighlightParticle implements IMessage {
	private double x;
	private double y;
	private double z;
	private double r;
	private double g;
	private double b;
	private double dirX;
	private double dirY;
	private double dirZ;

	public SpawnInterfaceHighlightParticle() {}

	public SpawnInterfaceHighlightParticle(double x, double y, double z, double r, double g, double b, double dirX, double dirY, double dirZ) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.r = r;
		this.g = g;
		this.b = b;
		this.dirX = dirX;
		this.dirY = dirY;
		this.dirZ = dirZ;
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeDouble(x);
		buf.writeDouble(y);
		buf.writeDouble(z);
		buf.writeDouble(r);
		buf.writeDouble(g);
		buf.writeDouble(b);
		buf.writeDouble(dirX);
		buf.writeDouble(dirY);
		buf.writeDouble(dirZ);
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		x = buf.readDouble();
		y = buf.readDouble();
		z = buf.readDouble();
		r = buf.readDouble();
		g = buf.readDouble();
		b = buf.readDouble();
		dirX = buf.readDouble();
		dirY = buf.readDouble();
		dirZ = buf.readDouble();
	}

	public static class Handler implements IMessageHandler<SpawnInterfaceHighlightParticle, IMessage> {
		@Override
		@SideOnly(Side.CLIENT)
		public IMessage onMessage(SpawnInterfaceHighlightParticle message, MessageContext context) {
			Minecraft.getMinecraft().addScheduledTask(() -> {
				Minecraft.getMinecraft().effectRenderer.addEffect(new InterfaceHighlightParticle(SecurityCraft.proxy.getClientLevel(), message.x, message.y, message.z, message.r, message.g, message.b, message.dirX, message.dirY, message.dirZ));
			});

			return null;
		}
	}
}
