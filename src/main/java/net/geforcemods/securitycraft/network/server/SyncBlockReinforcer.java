package net.geforcemods.securitycraft.network.server;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.items.UniversalBlockReinforcerItem;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SyncBlockReinforcer(boolean isReinforcing) implements CustomPacketPayload {
	public static final Type<SyncBlockReinforcer> TYPE = new Type<>(new ResourceLocation(SecurityCraft.MODID, "sync_block_reinforcer"));
	//@formatter:off
	public static final StreamCodec<RegistryFriendlyByteBuf, SyncBlockReinforcer> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.BOOL, SyncBlockReinforcer::isReinforcing,
			SyncBlockReinforcer::new);
	//@formatter:on

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public void handle(IPayloadContext ctx) {
		Player player = ctx.player();
		Inventory inventory = player.getInventory();
		ItemStack reinforcer = inventory.getSelected().getItem() instanceof UniversalBlockReinforcerItem ? inventory.getSelected() : inventory.offhand.get(0);

		if (!reinforcer.isEmpty() && !reinforcer.is(SCContent.UNIVERSAL_BLOCK_REINFORCER_LVL_1.get()))
			CustomData.update(DataComponents.CUSTOM_DATA, reinforcer, tag -> tag.putBoolean("is_unreinforcing", !isReinforcing));
	}
}
