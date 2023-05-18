package net.geforcemods.securitycraft.network.client;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.ClientHandler;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class UpdateNBTTagOnClient {
	private ItemStack stack;

	public UpdateNBTTagOnClient() {}

	public UpdateNBTTagOnClient(ItemStack stack) {
		if (!stack.isEmpty() && stack.hasTag())
			this.stack = stack;
	}

	public UpdateNBTTagOnClient(PacketBuffer buf) {
		stack = buf.readItem();
	}

	public void encode(PacketBuffer buf) {
		buf.writeItem(stack);
	}

	public void handle(Supplier<NetworkEvent.Context> ctx) {
		ItemStack stackToUpdate = PlayerUtils.getSelectedItemStack(ClientHandler.getClientPlayer(), stack.getItem());

		if (!stackToUpdate.isEmpty())
			stackToUpdate.setTag(stack.getTag());
	}
}
