package net.geforcemods.securitycraft.network.packets;

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

	private NBTTagCompound stackTag;
	private String itemName;

	public PacketCUpdateNBTTag(){

	}

	public PacketCUpdateNBTTag(ItemStack stack){
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

	public static class Handler extends PacketHelper implements IMessageHandler<PacketCUpdateNBTTag, IMessage> {

		@Override
		@SideOnly(Side.CLIENT)
		public IMessage onMessage(PacketCUpdateNBTTag message, MessageContext ctx) {
			if(Minecraft.getMinecraft().thePlayer.getCurrentEquippedItem() != null && Minecraft.getMinecraft().thePlayer.getCurrentEquippedItem().getItem().getUnlocalizedName().equals(message.itemName)){
				Minecraft.getMinecraft().thePlayer.getCurrentEquippedItem().setTagCompound(message.stackTag);;
			}

			return null;
		}
	}

}
