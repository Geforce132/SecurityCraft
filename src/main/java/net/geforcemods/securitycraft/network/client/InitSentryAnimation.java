package net.geforcemods.securitycraft.network.client;

import java.util.List;
import java.util.function.Supplier;

import net.geforcemods.securitycraft.entity.SentryEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

public class InitSentryAnimation
{
	private BlockPos pos;
	private boolean animate, animateUpwards;

	public InitSentryAnimation() {}

	public InitSentryAnimation(BlockPos sentryPos, boolean animate, boolean animateUpwards)
	{
		pos = sentryPos;
		this.animate = animate;
		this.animateUpwards = animateUpwards;
	}

	public static void encode(InitSentryAnimation message, PacketBuffer buf)
	{
		buf.writeLong(message.pos.asLong());
		buf.writeBoolean(message.animate);
		buf.writeBoolean(message.animateUpwards);
	}

	public static InitSentryAnimation decode(PacketBuffer buf)
	{
		InitSentryAnimation message = new InitSentryAnimation();

		message.pos = BlockPos.of(buf.readLong());
		message.animate = buf.readBoolean();
		message.animateUpwards = buf.readBoolean();
		return message;
	}

	public static void onMessage(InitSentryAnimation message, Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(() -> {
			List<CreatureEntity> sentries = Minecraft.getInstance().level.<CreatureEntity>getEntitiesOfClass(SentryEntity.class, new AxisAlignedBB(message.pos));

			if(!sentries.isEmpty())
			{
				((SentryEntity)sentries.get(0)).animateUpwards = message.animateUpwards;
				((SentryEntity)sentries.get(0)).animate = message.animate;
			}
		});

		ctx.get().setPacketHandled(true);
	}
}
