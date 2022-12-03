package net.geforcemods.securitycraft.network.server;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.inventory.InventoryScannerMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

public class SetGhostSlot {
	private int slotIndex;
	private ItemStack stack;

	public SetGhostSlot() {}

	public SetGhostSlot(int slot, ItemStack stack) {
		this.slotIndex = slot;
		this.stack = stack;
	}

	public static void encode(SetGhostSlot message, FriendlyByteBuf buf) {
		buf.writeVarInt(message.slotIndex);
		buf.writeItemStack(message.stack, false);
	}

	public static SetGhostSlot decode(FriendlyByteBuf buf) {
		SetGhostSlot message = new SetGhostSlot();

		message.slotIndex = buf.readVarInt();
		message.stack = buf.readItem();
		return message;
	}

	public static void onMessage(SetGhostSlot message, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			Player player = ctx.get().getSender();

			if (player.containerMenu instanceof InventoryScannerMenu menu && menu.be.isOwnedBy(player))
				menu.be.getContents().set(message.slotIndex, message.stack);
		});
		ctx.get().setPacketHandled(true);
	}
}
