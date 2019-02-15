package net.geforcemods.securitycraft.network.packets;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;

public class PacketCUpdateNBTTag implements IMessage{

	private NBTTagCompound stackTag;
	private String itemName;

	public PacketCUpdateNBTTag(){

	}

	public PacketCUpdateNBTTag(ItemStack stack){
		if(!stack.isEmpty() && stack.hasTagCompound()){
			stackTag = stack.getTagCompound();
			itemName = stack.getTranslationKey();
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
		@OnlyIn(Dist.CLIENT)
		public IMessage onMessage(PacketCUpdateNBTTag message, MessageContext ctx) {
			if(!Minecraft.getInstance().player.inventory.getCurrentItem().isEmpty() && Minecraft.getInstance().player.inventory.getCurrentItem().getItem().getTranslationKey().equals(message.itemName)){
				Minecraft.getInstance().player.inventory.getCurrentItem().setTagCompound(message.stackTag);;
			}

			return null;
		}
	}

}
