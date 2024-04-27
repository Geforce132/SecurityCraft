package net.geforcemods.securitycraft.network.server;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.components.IndexedPositions;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record RemoveMineFromMRAT(GlobalPos globalPos) implements CustomPacketPayload {
	public static final Type<RemoveMineFromMRAT> TYPE = new Type<>(new ResourceLocation(SecurityCraft.MODID, "remove_mine_from_mrat"));
	//@formatter:off
	public static final StreamCodec<RegistryFriendlyByteBuf, RemoveMineFromMRAT> STREAM_CODEC = StreamCodec.composite(
			GlobalPos.STREAM_CODEC, RemoveMineFromMRAT::globalPos,
			RemoveMineFromMRAT::new);
	//@formatter:on

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public void handle(IPayloadContext ctx) {
		ItemStack stack = PlayerUtils.getItemStackFromAnyHand(ctx.player(), SCContent.MINE_REMOTE_ACCESS_TOOL.get());

		if (!stack.isEmpty())
			IndexedPositions.remove(stack, stack.getOrDefault(SCContent.INDEXED_POSITIONS, IndexedPositions.EMPTY), globalPos);
	}
}
