package net.geforcemods.securitycraft.network.client;

import java.util.List;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.items.SCManualItem;
import net.geforcemods.securitycraft.misc.SCManualPage;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SendManualPages(List<SCManualPage> pages) implements CustomPacketPayload {
	public static final Type<SendManualPages> TYPE = new Type<>(SecurityCraft.resLoc("send_manual_pages"));
	//@formatter:off
	public static final StreamCodec<RegistryFriendlyByteBuf, SendManualPages> STREAM_CODEC = StreamCodec.composite(
			SCManualPage.LIST_STREAM_CODEC, SendManualPages::pages,
			SendManualPages::new);
	//@formatter:on

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public void handle(IPayloadContext ctx) {
		SCManualItem.PAGES.clear();
		SCManualItem.PAGES.addAll(pages);
		SCManualItem.lastOpenPage = -1;
		SecurityCraft.collectSCContentData(false);
	}
}
