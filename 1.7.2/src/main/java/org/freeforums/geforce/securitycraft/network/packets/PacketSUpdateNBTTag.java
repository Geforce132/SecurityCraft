package org.freeforums.geforce.securitycraft.network.packets;


import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class PacketSUpdateNBTTag implements IMessage{
	
	private NBTTagCompound stack;
	private String itemName;
	
	public PacketSUpdateNBTTag(){
		
	}
	
	public PacketSUpdateNBTTag(ItemStack par1ItemStack){
		if(par1ItemStack != null && par1ItemStack.hasTagCompound()){
			this.stack = par1ItemStack.stackTagCompound;
			this.itemName = par1ItemStack.getUnlocalizedName();
		}
	}

	public void fromBytes(ByteBuf buf) {
		this.stack = ByteBufUtils.readTag(buf);
		this.itemName = ByteBufUtils.readUTF8String(buf);
	}

	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeTag(buf, this.stack);
		ByteBufUtils.writeUTF8String(buf, this.itemName);
	}
	
public static class Handler extends PacketHelper implements IMessageHandler<PacketSUpdateNBTTag, IMessage> {

	public IMessage onMessage(PacketSUpdateNBTTag message, MessageContext context) {
		if(context.getServerHandler().playerEntity.getCurrentEquippedItem() != null && context.getServerHandler().playerEntity.getCurrentEquippedItem().getItem().getUnlocalizedName().matches(message.itemName)){
			context.getServerHandler().playerEntity.getCurrentEquippedItem().stackTagCompound = message.stack;	
		}
		
		return null;
	}
}

}
