package net.geforcemods.securitycraft.network.server;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

public class UpdateNBTTagOnServer {

	private ItemStack stack;

	public UpdateNBTTagOnServer(){

	}

	public UpdateNBTTagOnServer(ItemStack stack){
		if(!stack.isEmpty() && stack.getTag() != null){
			this.stack = stack;
		}
	}

	public static void encode(UpdateNBTTagOnServer message, FriendlyByteBuf buf)
	{
		buf.writeItem(message.stack);
	}

	public static UpdateNBTTagOnServer decode(FriendlyByteBuf buf)
	{
		UpdateNBTTagOnServer message = new UpdateNBTTagOnServer();

		message.stack = buf.readItem();
		return message;
	}

	public static void onMessage(UpdateNBTTagOnServer message, Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(() -> {
			Player player = ctx.get().getSender();
			if(PlayerUtils.isHoldingItem(player, message.stack.getItem(), null)) {
				ItemStack stack = PlayerUtils.getSelectedItemStack(player, message.stack.getItem());

				stack.setTag(message.stack.getTag());
			}
		});

		ctx.get().setPacketHandled(true);
	}
}
