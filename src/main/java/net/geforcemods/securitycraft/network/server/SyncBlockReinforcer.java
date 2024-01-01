package net.geforcemods.securitycraft.network.server;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.items.UniversalBlockReinforcerItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

public class SyncBlockReinforcer implements CustomPacketPayload {
	public static final ResourceLocation ID = new ResourceLocation(SecurityCraft.MODID, "sync_block_reinforcer");
	private boolean isReinforcing;

	public SyncBlockReinforcer() {}

	public SyncBlockReinforcer(boolean isReinforcing) {
		this.isReinforcing = isReinforcing;
	}

	public SyncBlockReinforcer(FriendlyByteBuf buf) {
		isReinforcing = buf.readBoolean();
	}

	@Override
	public void write(FriendlyByteBuf buf) {
		buf.writeBoolean(isReinforcing);
	}

	@Override
	public ResourceLocation id() {
		return ID;
	}

	public void handle(PlayPayloadContext ctx) {
		Player player = ctx.player().orElseThrow();
		Inventory inventory = player.getInventory();
		ItemStack reinforcer = inventory.getSelected().getItem() instanceof UniversalBlockReinforcerItem ? inventory.getSelected() : inventory.offhand.get(0);

		if (!reinforcer.isEmpty() && !reinforcer.is(SCContent.UNIVERSAL_BLOCK_REINFORCER_LVL_1.get()))
			reinforcer.getOrCreateTag().putBoolean("is_unreinforcing", !isReinforcing);
	}
}
