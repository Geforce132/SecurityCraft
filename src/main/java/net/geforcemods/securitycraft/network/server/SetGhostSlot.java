package net.geforcemods.securitycraft.network.server;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.inventory.InventoryScannerMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

public class SetGhostSlot implements CustomPacketPayload {
	public static final ResourceLocation ID = new ResourceLocation(SecurityCraft.MODID, "set_ghost_slot");
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

	@Override
	public void write(FriendlyByteBuf buf) {
		buf.writeVarInt(slotIndex);
		buf.writeItem(stack);
	}

	@Override
	public ResourceLocation id() {
		return ID;
	}

	public void handle(PlayPayloadContext ctx) {
		Player player = ctx.player().orElseThrow();

		if (!player.isSpectator() && player.containerMenu instanceof InventoryScannerMenu menu && menu.be.isOwnedBy(player))
			menu.be.getContents().set(slotIndex, stack);
	}
}
