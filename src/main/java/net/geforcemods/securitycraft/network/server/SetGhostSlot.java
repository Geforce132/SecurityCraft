package net.geforcemods.securitycraft.network.server;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.inventory.InventoryScannerMenu;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SetGhostSlot(int slotIndex, ItemStack stack) implements CustomPacketPayload {
	public static final Type<SetGhostSlot> TYPE = new Type<>(SecurityCraft.resLoc("set_ghost_slot"));
	//@formatter:off
	public static final StreamCodec<RegistryFriendlyByteBuf, SetGhostSlot> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.VAR_INT, SetGhostSlot::slotIndex,
			ItemStack.STREAM_CODEC, SetGhostSlot::stack,
			SetGhostSlot::new);
	//@formatter:on

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public void handle(IPayloadContext ctx) {
		Player player = ctx.player();

		if (player.containerMenu instanceof InventoryScannerMenu menu && menu.be.isOwnedBy(player))
			menu.be.getContents().set(slotIndex, stack);
	}
}
