package net.geforcemods.securitycraft.network.client;

import java.util.List;
import java.util.function.Supplier;

import io.netty.buffer.ByteBuf;
import net.geforcemods.securitycraft.entity.SentryEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

public class InitSentryAnimation
{
	public BlockPos pos;
	public boolean animate, animateUpwards;

	public InitSentryAnimation() {}

	public InitSentryAnimation(BlockPos sentryPos, boolean animate, boolean animateUpwards)
	{
		pos = sentryPos;
		this.animate = animate;
		this.animateUpwards = animateUpwards;
	}

	public void fromBytes(ByteBuf buf)
	{
		pos = BlockPos.fromLong(buf.readLong());
		animate = buf.readBoolean();
		animateUpwards = buf.readBoolean();
	}

	public void toBytes(ByteBuf buf)
	{
		buf.writeLong(pos.toLong());
		buf.writeBoolean(animate);
		buf.writeBoolean(animateUpwards);
	}

	public static void encode(InitSentryAnimation message, PacketBuffer packet)
	{
		message.toBytes(packet);
	}

	public static InitSentryAnimation decode(PacketBuffer packet)
	{
		InitSentryAnimation message = new InitSentryAnimation();

		message.fromBytes(packet);
		return message;
	}

	public static void onMessage(InitSentryAnimation message, Supplier<NetworkEvent.Context> ctx)
	{
		List<CreatureEntity> sentries = Minecraft.getInstance().world.<CreatureEntity>getEntitiesWithinAABB(SentryEntity.class, new AxisAlignedBB(message.pos));

		if(!sentries.isEmpty())
		{
			((SentryEntity)sentries.get(0)).animateUpwards = message.animateUpwards;
			((SentryEntity)sentries.get(0)).animate = message.animate;
		}

		ctx.get().setPacketHandled(true);
	}
}
