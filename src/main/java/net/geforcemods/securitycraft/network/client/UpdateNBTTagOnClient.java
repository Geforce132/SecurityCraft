package net.geforcemods.securitycraft.network.client;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fml.network.NetworkEvent;

public class UpdateNBTTagOnClient{

	private ItemStack stack;

	public UpdateNBTTagOnClient(){

	}

	public UpdateNBTTagOnClient(ItemStack stack){
		if(!stack.isEmpty() && stack.hasTag()){
			this.stack = stack;
		}
	}

	public static void encode(UpdateNBTTagOnClient message, FriendlyByteBuf buf)
	{
		buf.writeItem(message.stack);
	}

	public static UpdateNBTTagOnClient decode(FriendlyByteBuf buf)
	{
		UpdateNBTTagOnClient message = new UpdateNBTTagOnClient();

		message.stack = buf.readItem();
		return message;
	}

	public static void onMessage(UpdateNBTTagOnClient message, Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(() -> {
			ItemStack stackToUpdate = PlayerUtils.getSelectedItemStack(SecurityCraft.proxy.getClientPlayer(), message.stack.getItem());

			if(!stackToUpdate.isEmpty())
				stackToUpdate.setTag(message.stack.getTag());
		});

		ctx.get().setPacketHandled(true);
	}
}
