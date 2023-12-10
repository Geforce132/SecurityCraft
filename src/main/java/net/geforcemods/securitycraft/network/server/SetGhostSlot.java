package net.geforcemods.securitycraft.network.server;

import net.geforcemods.securitycraft.inventory.InventoryScannerMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.NetworkEvent;

public class SetGhostSlot {
	private int slotIndex;
	private ItemStack stack;

	public SetGhostSlot() {}

	public SetGhostSlot(int slot, ItemStack stack) {
		this.slotIndex = slot;
		this.stack = stack;
	}

	public SetGhostSlot(FriendlyByteBuf buf) {
		slotIndex = buf.readVarInt();
		stack = buf.readItem();
	}

	public void encode(FriendlyByteBuf buf) {
		buf.writeVarInt(slotIndex);
		buf.writeItem(stack);
	}

	public void handle(NetworkEvent.Context ctx) {
		Player player = ctx.getSender();

		if (player.containerMenu instanceof InventoryScannerMenu menu && menu.be.isOwnedBy(player))
			menu.be.getContents().set(slotIndex, stack);
	}
}
