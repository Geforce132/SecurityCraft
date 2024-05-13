package net.geforcemods.securitycraft.network.server;

import io.netty.buffer.ByteBuf;
import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.entity.camera.CameraNightVisionEffectInstance;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class ToggleNightVision implements IMessage {
	public ToggleNightVision() {}

	@Override
	public void fromBytes(ByteBuf buf) {}

	@Override
	public void toBytes(ByteBuf buf) {}

	public static class Handler implements IMessageHandler<ToggleNightVision, IMessage> {
		@Override
		public IMessage onMessage(ToggleNightVision message, MessageContext ctx) {
			EntityPlayerMP player = ctx.getServerHandler().player;

			Utils.addScheduledTask(player.world, () -> {
				if (ConfigHandler.allowCameraNightVision && PlayerUtils.isPlayerMountedOnCamera(player)) {
					PotionEffect nightVision = player.getActivePotionEffect(MobEffects.NIGHT_VISION);

					if (nightVision != null) {
						if (nightVision instanceof CameraNightVisionEffectInstance)
							player.removePotionEffect(MobEffects.NIGHT_VISION);
					}
					else
						player.addPotionEffect(new CameraNightVisionEffectInstance());
				}
			});

			return null;
		}
	}
}
