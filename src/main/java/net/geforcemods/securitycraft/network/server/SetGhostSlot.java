package net.geforcemods.securitycraft.network.server;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.inventory.InventoryScannerMenu;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class SetGhostSlot {
	private int slotIndex;
	private ItemStack stack;

	public SetGhostSlot() {}

	public SetGhostSlot(int slot, ItemStack stack) {
		this.slotIndex = slot;
		this.stack = stack;
	}

	public static void encode(SetGhostSlot message, PacketBuffer buf) {
		buf.writeVarInt(message.slotIndex);
		buf.writeItemStack(message.stack, false);
	}

	public static SetGhostSlot decode(PacketBuffer buf) {
		SetGhostSlot message = new SetGhostSlot();

		message.slotIndex = buf.readVarInt();
		message.stack = buf.readItem();
		return message;
	}

	public static void onMessage(SetGhostSlot message, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			PlayerEntity player = ctx.get().getSender();

			if (player.containerMenu instanceof InventoryScannerMenu) {
				InventoryScannerMenu menu = (InventoryScannerMenu) player.containerMenu;

				if (menu.te.isOwnedBy(player))
					menu.te.getContents().set(message.slotIndex, message.stack);
			}
		});
		ctx.get().setPacketHandled(true);
	}
}
