package net.geforcemods.securitycraft.network.server;

import io.netty.buffer.ByteBuf;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.items.CameraMonitorItem;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class RemoveCameraTag implements IMessage {
	private int camID;

	public RemoveCameraTag() {}

	public RemoveCameraTag(int cid) {
		camID = cid;
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(camID);
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		camID = buf.readInt();
	}

	public static class Handler implements IMessageHandler<RemoveCameraTag, IMessage> {
		@Override
		public IMessage onMessage(RemoveCameraTag message, MessageContext context) {
			Utils.addScheduledTask(context.getServerHandler().player.world, () -> {
				EntityPlayer player = context.getServerHandler().player;
				ItemStack monitor = PlayerUtils.getItemStackFromAnyHand(player, SCContent.cameraMonitor);

				if (!player.isSpectator() && !monitor.isEmpty() && monitor.hasTagCompound())
					monitor.getTagCompound().removeTag(CameraMonitorItem.getTagNameFromPosition(monitor.getTagCompound(), CameraMonitorItem.getCameraPositions(monitor.getTagCompound()).get(message.camID - 1).getLeft()));
			});

			return null;
		}
	}
}
