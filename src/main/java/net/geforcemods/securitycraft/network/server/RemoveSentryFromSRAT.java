package net.geforcemods.securitycraft.network.server;

import io.netty.buffer.ByteBuf;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class RemoveSentryFromSRAT implements IMessage {
	private int sentryIndex;

	public RemoveSentryFromSRAT() {}

	public RemoveSentryFromSRAT(int sentryIndex) {
		this.sentryIndex = sentryIndex;
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(sentryIndex);
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		sentryIndex = buf.readInt();
	}

	public static class Handler implements IMessageHandler<RemoveSentryFromSRAT, IMessage> {
		@Override
		public IMessage onMessage(RemoveSentryFromSRAT message, MessageContext context) {
			Utils.addScheduledTask(context.getServerHandler().player.world, () -> {
				EntityPlayer player = context.getServerHandler().player;
				ItemStack stack = PlayerUtils.getItemStackFromAnyHand(player, SCContent.mineRemoteAccessTool);

				if (!player.isSpectator() && !stack.isEmpty()) {
					if (!stack.hasTagCompound())
						stack.setTagCompound(new NBTTagCompound());

					NBTTagCompound tag = stack.getTagCompound();
					String key = "sentry" + message.sentryIndex;

					if (tag.hasKey(key)) {
						tag.setIntArray(key, new int[] {
								0, 0, 0
						});
					}
				}
			});

			return null;
		}
	}
}
