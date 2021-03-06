package net.geforcemods.securitycraft.network.packets;

import io.netty.buffer.ByteBuf;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PacketCUpdateNBTTag implements IMessage{

	private ItemStack stack;

	public PacketCUpdateNBTTag(){

	}

	public PacketCUpdateNBTTag(ItemStack stack){
		if(!stack.isEmpty() && stack.hasTagCompound()){
			this.stack = stack;
		}
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		stack = ByteBufUtils.readItemStack(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeItemStack(buf, stack);
	}

	public static class Handler extends PacketHelper implements IMessageHandler<PacketCUpdateNBTTag, IMessage> {

		@Override
		@SideOnly(Side.CLIENT)
		public IMessage onMessage(PacketCUpdateNBTTag message, MessageContext ctx) {
			ItemStack stackToUpdate = PlayerUtils.getSelectedItemStack(Minecraft.getMinecraft().player.inventory, message.stack.getItem());

			if(!stackToUpdate.isEmpty())
				stackToUpdate.setTagCompound(message.stack.getTagCompound());

			return null;
		}
	}

}
