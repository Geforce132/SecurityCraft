package net.geforcemods.securitycraft.network.server;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blockentities.KeycardReaderBlockEntity;
import net.geforcemods.securitycraft.inventory.KeycardReaderMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SetKeycardUses(BlockPos pos, int uses) implements CustomPacketPayload {
	public static final Type<SetKeycardUses> TYPE = new Type<>(SecurityCraft.resLoc("set_keycard_uses"));
	//@formatter:off
	public static final StreamCodec<RegistryFriendlyByteBuf, SetKeycardUses> STREAM_CODEC = StreamCodec.composite(
			BlockPos.STREAM_CODEC, SetKeycardUses::pos,
			ByteBufCodecs.VAR_INT, SetKeycardUses::uses,
			SetKeycardUses::new);
	//@formatter:on

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public void handle(IPayloadContext ctx) {
		Player player = ctx.player();

		if (!player.isSpectator() && player.level().getBlockEntity(pos) instanceof KeycardReaderBlockEntity be && (be.isOwnedBy(player) || be.isAllowed(player)) && player.containerMenu instanceof KeycardReaderMenu keycardReaderContainer)
			keycardReaderContainer.setKeycardUsesLeft(uses);
	}
}
