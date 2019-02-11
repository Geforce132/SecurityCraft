package net.geforcemods.securitycraft.network.packets;

import io.netty.buffer.ByteBuf;
import net.geforcemods.securitycraft.util.WorldUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketSUpdateNBTTag implements IMessage{

	private NBTTagCompound stackTag;
	private String itemName;

	public PacketSUpdateNBTTag(){

	}

	public PacketSUpdateNBTTag(ItemStack stack){
		if(stack != null && stack.hasTagCompound()){
			stackTag = stack.getTagCompound();
			itemName = stack.getUnlocalizedName();
		}
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		stackTag = ByteBufUtils.readTag(buf);
		itemName = ByteBufUtils.readUTF8String(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeTag(buf, stackTag);
		ByteBufUtils.writeUTF8String(buf, itemName);
	}

	public static class Handler extends PacketHelper implements IMessageHandler<PacketSUpdateNBTTag, IMessage> {

		@Override
		public IMessage onMessage(PacketSUpdateNBTTag message, MessageContext context) {
			WorldUtils.addScheduledTask(getWorld(context.getServerHandler().playerEntity), () -> {
				if(context.getServerHandler().playerEntity.inventory.getCurrentItem() != null && context.getServerHandler().playerEntity.inventory.getCurrentItem().getItem().getUnlocalizedName().equals(message.itemName))
					context.getServerHandler().playerEntity.inventory.getCurrentItem().setTagCompound(message.stackTag);
			});

			return null;
		}
	}

}
