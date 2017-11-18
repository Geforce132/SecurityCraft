package net.geforcemods.securitycraft.network.packets;


import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class PacketSUpdateNBTTag implements IMessage{

	private NBTTagCompound stack;
	private String itemName;

	public PacketSUpdateNBTTag(){

	}

	public PacketSUpdateNBTTag(ItemStack par1ItemStack){
		if(par1ItemStack != null && par1ItemStack.hasTagCompound()){
			stack = par1ItemStack.stackTagCompound;
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
		public IMessage onMessage(PacketSUpdateNBTTag message, MessageContext context) {
			if(context.getServerHandler().playerEntity.getCurrentEquippedItem() != null && context.getServerHandler().playerEntity.getCurrentEquippedItem().getItem().getUnlocalizedName().matches(message.itemName))
				context.getServerHandler().playerEntity.getCurrentEquippedItem().stackTagCompound = message.stack;

			return null;
		}
	}

}
