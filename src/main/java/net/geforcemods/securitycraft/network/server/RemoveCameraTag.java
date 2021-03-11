package net.geforcemods.securitycraft.network.server;

import io.netty.buffer.ByteBuf;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.items.ItemCameraMonitor;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.WorldUtils;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class RemoveCameraTag implements IMessage
{
	private ItemStack heldItem;
	private int camID;

	public RemoveCameraTag(){}

	public RemoveCameraTag(ItemStack stack, int cid)
	{
		heldItem = stack;
		camID = cid;
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		ByteBufUtils.writeItemStack(buf, heldItem);
		buf.writeInt(camID);
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		heldItem = ByteBufUtils.readItemStack(buf);
		camID = buf.readInt();
	}

	public static class Handler implements IMessageHandler<RemoveCameraTag, IMessage>
	{
		@Override
		public IMessage onMessage(RemoveCameraTag message, MessageContext context)
		{
			WorldUtils.addScheduledTask(context.getServerHandler().player.world, () -> {
				ItemStack monitor = PlayerUtils.getSelectedItemStack(context.getServerHandler().player, SCContent.cameraMonitor);

				if(!monitor.isEmpty())
					monitor.getTagCompound().removeTag(ItemCameraMonitor.getTagNameFromPosition(monitor.getTagCompound(), ((ItemCameraMonitor)monitor.getItem()).getCameraPositions(monitor.getTagCompound()).get(message.camID - 1)));
			});

			return null;
		}
	}
}
