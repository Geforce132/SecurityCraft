package net.geforcemods.securitycraft.network.server;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.items.UniversalBlockReinforcerItem;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class SyncBlockReinforcer {
	private boolean isReinforcing;

	public SyncBlockReinforcer() {}

	public SyncBlockReinforcer(boolean isReinforcing) {
		this.isReinforcing = isReinforcing;
	}

	public SyncBlockReinforcer(PacketBuffer buf) {
		isReinforcing = buf.readBoolean();
	}

	public void encode(PacketBuffer buf) {
		buf.writeBoolean(isReinforcing);
	}

	public void handle(Supplier<NetworkEvent.Context> ctx) {
		ServerPlayerEntity player = ctx.get().getSender();
		ItemStack reinforcer = player.inventory.getSelected().getItem() instanceof UniversalBlockReinforcerItem ? player.inventory.getSelected() : player.inventory.offhand.get(0);

		if (!reinforcer.isEmpty() && reinforcer.getItem() != SCContent.UNIVERSAL_BLOCK_REINFORCER_LVL_1.get())
			reinforcer.getOrCreateTag().putBoolean("is_unreinforcing", !isReinforcing);
	}
}
