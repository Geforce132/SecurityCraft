package net.geforcemods.securitycraft.network.client;

import net.geforcemods.securitycraft.ClientHandler;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.NetworkEvent;

public class UpdateNBTTagOnClient {
	private ItemStack stack;

	public UpdateNBTTagOnClient() {}

	public UpdateNBTTagOnClient(ItemStack stack) {
		this.stack = stack;
	}

	public UpdateNBTTagOnClient(FriendlyByteBuf buf) {
		stack = buf.readItem();
	}

	public void encode(FriendlyByteBuf buf) {
		buf.writeItem(stack);
	}

	public void handle(NetworkEvent.Context ctx) {
		ItemStack stackToUpdate = PlayerUtils.getItemStackFromAnyHand(ClientHandler.getClientPlayer(), stack.getItem());

		if (!stackToUpdate.isEmpty())
			stackToUpdate.setTag(stack.getTag());
	}
}
