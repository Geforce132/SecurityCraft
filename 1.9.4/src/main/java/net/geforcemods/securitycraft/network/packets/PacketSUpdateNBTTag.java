package net.geforcemods.securitycraft.network.packets;

import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketSUpdateNBTTag implements IMessage{
	
	private NBTTagCompound stack;
	private String itemName;
	
	public PacketSUpdateNBTTag(){
		
	}
	
	public PacketSUpdateNBTTag(ItemStack par1ItemStack){
		if(par1ItemStack != null && par1ItemStack.hasTagCompound()){
			this.stack = par1ItemStack.getTagCompound();
			this.itemName = par1ItemStack.getUnlocalizedName();
		}
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.stack = ByteBufUtils.readTag(buf);
		this.itemName = ByteBufUtils.readUTF8String(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeTag(buf, this.stack);
		ByteBufUtils.writeUTF8String(buf, this.itemName);
	}
	
public static class Handler extends PacketHelper implements IMessageHandler<PacketSUpdateNBTTag, IMessage> {

	@Override
	public IMessage onMessage(PacketSUpdateNBTTag packet, MessageContext context) {
		if(context.getServerHandler().playerEntity.inventory.getCurrentItem() != null && context.getServerHandler().playerEntity.inventory.getCurrentItem().getItem().getUnlocalizedName().matches(packet.itemName)){
			context.getServerHandler().playerEntity.inventory.getCurrentItem().setTagCompound(packet.stack);	
		}
		
		return null;
	}
}

}
