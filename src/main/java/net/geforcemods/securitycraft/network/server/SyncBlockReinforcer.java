package net.geforcemods.securitycraft.network.server;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.items.UniversalBlockReinforcerItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.NetworkEvent;

public class SyncBlockReinforcer {
	private boolean isReinforcing;

	public SyncBlockReinforcer() {}

	public SyncBlockReinforcer(boolean isReinforcing) {
		this.isReinforcing = isReinforcing;
	}

	public SyncBlockReinforcer(FriendlyByteBuf buf) {
		isReinforcing = buf.readBoolean();
	}

	public void encode(FriendlyByteBuf buf) {
		buf.writeBoolean(isReinforcing);
	}

	public void handle(Supplier<NetworkEvent.Context> ctx) {
		ServerPlayer player = ctx.get().getSender();
		Inventory inventory = player.getInventory();
		ItemStack reinforcer = inventory.getSelected().getItem() instanceof UniversalBlockReinforcerItem ? inventory.getSelected() : inventory.offhand.get(0);

		if (!reinforcer.isEmpty() && !reinforcer.is(SCContent.UNIVERSAL_BLOCK_REINFORCER_LVL_1.get()))
			reinforcer.getOrCreateTag().putBoolean("is_unreinforcing", !isReinforcing);
	}
}
