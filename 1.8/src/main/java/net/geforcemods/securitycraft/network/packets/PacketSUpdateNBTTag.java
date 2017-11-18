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
			stack = par1ItemStack.getTagCompound();
			itemName = par1ItemStack.getUnlocalizedName();
		}
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		stack = ByteBufUtils.readTag(buf);
		itemName = ByteBufUtils.readUTF8String(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeTag(buf, stack);
		ByteBufUtils.writeUTF8String(buf, itemName);
	}

	public static class Handler extends PacketHelper implements IMessageHandler<PacketSUpdateNBTTag, IMessage> {

		@Override
		public IMessage onMessage(PacketSUpdateNBTTag packet, MessageContext context) {
			if(context.getServerHandler().playerEntity.getCurrentEquippedItem() != null && context.getServerHandler().playerEntity.getCurrentEquippedItem().getItem().getUnlocalizedName().matches(packet.itemName))
				context.getServerHandler().playerEntity.getCurrentEquippedItem().setTagCompound(packet.stack);

			return null;
		}
	}

}
