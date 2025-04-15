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

	public SetGhostSlot(PacketBuffer buf) {
		slotIndex = buf.readVarInt();
		stack = buf.readItem();
	}

	public void encode(PacketBuffer buf) {
		buf.writeVarInt(slotIndex);
		buf.writeItemStack(stack, false);
	}

	public void handle(Supplier<NetworkEvent.Context> ctx) {
		PlayerEntity player = ctx.get().getSender();

		if (!player.isSpectator() && player.containerMenu instanceof InventoryScannerMenu) {
			InventoryScannerMenu menu = (InventoryScannerMenu) player.containerMenu;

			if (menu.be.isOwnedBy(player))
				menu.be.getContents().set(slotIndex, stack);
		}
	}
}
