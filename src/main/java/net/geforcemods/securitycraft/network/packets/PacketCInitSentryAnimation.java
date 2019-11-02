package net.geforcemods.securitycraft.network.packets;

import java.util.List;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.geforcemods.securitycraft.entity.EntitySentry;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityCreature;
import net.minecraft.util.AxisAlignedBB;

public class PacketCInitSentryAnimation implements IMessage
{
	public double x, y, z;
	public boolean animate, animateUpwards;

	public PacketCInitSentryAnimation() {}

	public PacketCInitSentryAnimation(double x, double y, double z, boolean animate, boolean animateUpwards)
	{
		this.x = x;
		this.y = y;
		this.z = z;
		this.animate = animate;
		this.animateUpwards = animateUpwards;
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		x = buf.readDouble();
		y = buf.readDouble();
		z = buf.readDouble();
		animate = buf.readBoolean();
		animateUpwards = buf.readBoolean();
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeDouble(x);
		buf.writeDouble(y);
		buf.writeDouble(z);
		buf.writeBoolean(animate);
		buf.writeBoolean(animateUpwards);
	}

	public static class Handler extends PacketHelper implements IMessageHandler<PacketCInitSentryAnimation, IMessage>
	{
		@Override
		public IMessage onMessage(PacketCInitSentryAnimation message, MessageContext ctx)
		{
			List<EntityCreature> sentries = Minecraft.getMinecraft().thePlayer.worldObj.getEntitiesWithinAABB(EntitySentry.class, AxisAlignedBB.getBoundingBox(message.x, message.y, message.z, message.x + 1, message.y + 1, message.z + 1));

			if(!sentries.isEmpty())
			{
				((EntitySentry)sentries.get(0)).animateUpwards = message.animateUpwards;
				((EntitySentry)sentries.get(0)).animate = message.animate;
			}

			return null;
		}
	}
}
