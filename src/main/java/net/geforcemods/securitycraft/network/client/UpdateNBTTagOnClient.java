package net.geforcemods.securitycraft.network.client;

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

public class UpdateNBTTagOnClient implements IMessage{

	private ItemStack stack;

	public UpdateNBTTagOnClient(){

	}

	public UpdateNBTTagOnClient(ItemStack stack){
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

	public static class Handler implements IMessageHandler<UpdateNBTTagOnClient, IMessage> {

		@Override
		@SideOnly(Side.CLIENT)
		public IMessage onMessage(UpdateNBTTagOnClient message, MessageContext ctx) {
			ItemStack stackToUpdate = PlayerUtils.getSelectedItemStack(Minecraft.getMinecraft().player.inventory, message.stack.getItem());

			if(!stackToUpdate.isEmpty())
				stackToUpdate.setTagCompound(message.stack.getTagCompound());

			return null;
		}
	}

}
