package net.geforcemods.securitycraft.network.packets;

import io.netty.buffer.ByteBuf;
import net.geforcemods.securitycraft.items.ItemCameraMonitor;
import net.geforcemods.securitycraft.util.WorldUtils;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;

public class PacketSRemoveCameraTag implements IMessage
{
	private ItemStack heldItem;
	private int camID;

	public PacketSRemoveCameraTag(){}

	public PacketSRemoveCameraTag(ItemStack stack, int cid)
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

	public static class Handler extends PacketHelper implements IMessageHandler<PacketSRemoveCameraTag, IMessage>
	{
		@Override
		public IMessage onMessage(PacketSRemoveCameraTag message, MessageContext context)
		{
			WorldUtils.addScheduledTask(getWorld(context.getServerHandler().player), () -> {
				ItemStack monitor = context.getServerHandler().player.inventory.getCurrentItem();
				int id = message.camID;

				monitor.getTagCompound().removeTag(ItemCameraMonitor.getTagNameFromPosition(monitor.getTagCompound(), ((ItemCameraMonitor)monitor.getItem()).getCameraPositions(monitor.getTagCompound()).get(id - 1)));
			});

			return null;
		}
	}
}
