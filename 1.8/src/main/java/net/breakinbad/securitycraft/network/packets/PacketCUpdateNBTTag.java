package net.breakinbad.securitycraft.network.packets;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PacketCUpdateNBTTag implements IMessage{
	
	private NBTTagCompound stack;
	private String itemName;
	
	public PacketCUpdateNBTTag(){
		
	}
	
	public PacketCUpdateNBTTag(ItemStack par1ItemStack){
		if(par1ItemStack != null && par1ItemStack.hasTagCompound()){
			this.stack = par1ItemStack.getTagCompound();
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
	
public static class Handler extends PacketHelper implements IMessageHandler<PacketCUpdateNBTTag, IMessage> {

	@SideOnly(Side.CLIENT)
	public IMessage onMessage(PacketCUpdateNBTTag packet, MessageContext ctx) {
		if(Minecraft.getMinecraft().thePlayer.getCurrentEquippedItem() != null && Minecraft.getMinecraft().thePlayer.getCurrentEquippedItem().getItem().getUnlocalizedName().matches(packet.itemName)){
			Minecraft.getMinecraft().thePlayer.getCurrentEquippedItem().setTagCompound(packet.stack);;	
		}
		
		return null;
	}
}

}
