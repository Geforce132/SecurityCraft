package net.geforcemods.securitycraft.network.server;

import io.netty.buffer.ByteBuf;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.WorldUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class UpdateNBTTagOnServer implements IMessage{

	private ItemStack stack;

	public UpdateNBTTagOnServer(){

	}

	public UpdateNBTTagOnServer(ItemStack stack){
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

	public static class Handler implements IMessageHandler<UpdateNBTTagOnServer, IMessage> {

		@Override
		public IMessage onMessage(UpdateNBTTagOnServer message, MessageContext context) {
			WorldUtils.addScheduledTask(context.getServerHandler().player.world, () -> {
				EntityPlayer player = context.getServerHandler().player;

				if(PlayerUtils.isHoldingItem(player, message.stack.getItem(), null)) {
					ItemStack stack = PlayerUtils.getSelectedItemStack(player, message.stack.getItem());

					stack.setTagCompound(message.stack.getTagCompound());
				}
			});

			return null;
		}
	}

}
