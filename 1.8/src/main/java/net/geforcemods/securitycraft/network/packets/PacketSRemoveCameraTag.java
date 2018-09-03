package net.geforcemods.securitycraft.network.packets;

import io.netty.buffer.ByteBuf;
import net.geforcemods.securitycraft.items.ItemCameraMonitor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketSRemoveCameraTag implements IMessage
{
	private ItemStack heldItem;
	private int camId;

	public PacketSRemoveCameraTag(){}

	public PacketSRemoveCameraTag(ItemStack stack, int cId)
	{
		heldItem = stack;
		camId = cId;
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		ByteBufUtils.writeItemStack(buf, heldItem);
		buf.writeInt(camId);
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		heldItem = ByteBufUtils.readItemStack(buf);
		camId = buf.readInt();
	}

	public static class Handler extends PacketHelper implements IMessageHandler<PacketSRemoveCameraTag, IMessage>
	{
		@Override
		public IMessage onMessage(PacketSRemoveCameraTag message, MessageContext context)
		{
			ItemStack monitor = context.getServerHandler().playerEntity.inventory.getCurrentItem();
			int id = message.camId;

			monitor.getTagCompound().removeTag(ItemCameraMonitor.getTagNameFromPosition(monitor.getTagCompound(), ((ItemCameraMonitor)monitor.getItem()).getCameraPositions(monitor.getTagCompound()).get(id - 1)));
			return null;
		}
	}
}
