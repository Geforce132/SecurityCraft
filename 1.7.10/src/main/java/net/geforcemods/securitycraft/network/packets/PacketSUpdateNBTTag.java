package net.geforcemods.securitycraft.network.packets;


import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class PacketSUpdateNBTTag implements IMessage{

	private NBTTagCompound tag;
	private String itemName;

	public PacketSUpdateNBTTag(){

	}

	public PacketSUpdateNBTTag(ItemStack stack){
		if(stack != null && stack.hasTagCompound()){
			tag = stack.stackTagCompound;
			itemName = stack.getUnlocalizedName();
		}
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		tag = ByteBufUtils.readTag(buf);
		itemName = ByteBufUtils.readUTF8String(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeTag(buf, tag);
		ByteBufUtils.writeUTF8String(buf, itemName);
	}

	public static class Handler extends PacketHelper implements IMessageHandler<PacketSUpdateNBTTag, IMessage> {

		@Override
		public IMessage onMessage(PacketSUpdateNBTTag message, MessageContext context) {
			if(context.getServerHandler().playerEntity.getCurrentEquippedItem() != null && context.getServerHandler().playerEntity.getCurrentEquippedItem().getItem().getUnlocalizedName().equals(message.itemName))
				context.getServerHandler().playerEntity.getCurrentEquippedItem().stackTagCompound = message.tag;

			return null;
		}
	}

}
