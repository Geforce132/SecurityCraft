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

public class RemoveMineFromMRAT implements IMessage {
	private int mineIndex;

	public RemoveMineFromMRAT() {}

	public RemoveMineFromMRAT(int mineIndex) {
		this.mineIndex = mineIndex;
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(mineIndex);
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		mineIndex = buf.readInt();
	}

	public static class Handler implements IMessageHandler<RemoveMineFromMRAT, IMessage> {
		@Override
		public IMessage onMessage(RemoveMineFromMRAT message, MessageContext context) {
			Utils.addScheduledTask(context.getServerHandler().player.world, () -> {
				EntityPlayer player = context.getServerHandler().player;
				ItemStack stack = PlayerUtils.getItemStackFromAnyHand(player, SCContent.mineRemoteAccessTool);

				if (!player.isSpectator() && !stack.isEmpty()) {
					if (!stack.hasTagCompound())
						stack.setTagCompound(new NBTTagCompound());

					NBTTagCompound tag = stack.getTagCompound();
					String key = "mine" + message.mineIndex;

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
