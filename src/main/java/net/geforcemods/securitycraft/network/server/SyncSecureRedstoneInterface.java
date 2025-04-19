package net.geforcemods.securitycraft.network.server;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blockentities.SecureRedstoneInterfaceBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SyncSecureRedstoneInterface(BlockPos pos, boolean sender, boolean protectedSignal, int frequency, boolean sendExactPower, boolean receiveInvertedPower, int senderRange, boolean highlightConnections) implements CustomPacketPayload {

	public static final Type<SyncSecureRedstoneInterface> TYPE = new Type<>(SecurityCraft.resLoc("sync_secure_redstone_interface"));
	//@formatter:off
	public static final StreamCodec<RegistryFriendlyByteBuf, SyncSecureRedstoneInterface> STREAM_CODEC = new StreamCodec<>() {
		@Override
		public void encode(RegistryFriendlyByteBuf buf, SyncSecureRedstoneInterface packet) {
			buf.writeBlockPos(packet.pos);
			buf.writeBoolean(packet.sender);
			buf.writeBoolean(packet.protectedSignal);
			buf.writeVarInt(packet.frequency);
			buf.writeBoolean(packet.sendExactPower);
			buf.writeBoolean(packet.receiveInvertedPower);
			buf.writeVarInt(packet.senderRange);
			buf.writeBoolean(packet.highlightConnections);
		}

		@Override
		public SyncSecureRedstoneInterface decode(RegistryFriendlyByteBuf buf) {
			return new SyncSecureRedstoneInterface(buf.readBlockPos(), buf.readBoolean(), buf.readBoolean(), buf.readVarInt(), buf.readBoolean(), buf.readBoolean(), buf.readVarInt(), buf.readBoolean());
		}
	};
	//@formatter:on
	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public void handle(IPayloadContext ctx) {
		Player player = ctx.player();
		Level level = player.level();

		if (!player.isSpectator() && level.getBlockEntity(pos) instanceof SecureRedstoneInterfaceBlockEntity be && be.isOwnedBy(player)) {
			if (sender != be.isSender())
				be.setSender(sender);

			if (protectedSignal != be.isProtectedSignal())
				be.setProtectedSignal(protectedSignal);

			if (frequency != be.getFrequency())
				be.setFrequency(frequency);

			if (sendExactPower != be.sendsExactPower())
				be.setSendExactPower(sendExactPower);

			if (receiveInvertedPower != be.receivesInvertedPower())
				be.setReceiveInvertedPower(receiveInvertedPower);

			if (senderRange != be.getSenderRange())
				be.setSenderRange(senderRange);

			be.setHighlightConnections(highlightConnections);
		}
	}
}
