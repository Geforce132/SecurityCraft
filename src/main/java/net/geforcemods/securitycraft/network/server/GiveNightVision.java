package net.geforcemods.securitycraft.network.server;

import io.netty.buffer.ByteBuf;
import net.geforcemods.securitycraft.util.LevelUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class GiveNightVision implements IMessage {
	public GiveNightVision() {}

	@Override
	public void fromBytes(ByteBuf buf) {}

	@Override
	public void toBytes(ByteBuf buf) {}

	public static class Handler implements IMessageHandler<GiveNightVision, IMessage> {
		@Override
		public IMessage onMessage(GiveNightVision message, MessageContext ctx) {
			LevelUtils.addScheduledTask(ctx.getServerHandler().player.world, () -> {
				if (PlayerUtils.isPlayerMountedOnCamera(ctx.getServerHandler().player))
					ctx.getServerHandler().player.addPotionEffect(new PotionEffect(MobEffects.NIGHT_VISION, 3, -1, false, false));
			});
			return null;
		}
	}
}
