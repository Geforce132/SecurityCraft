package net.geforcemods.securitycraft.network.server;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blockentities.SecureRedstoneInterfaceBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class SyncSecureRedstoneInterface implements CustomPacketPayload {
	public static final ResourceLocation ID = new ResourceLocation(SecurityCraft.MODID, "sync_secure_redstone_interface");
	private BlockPos pos;
	private boolean sender;
	private boolean protectedSignal;
	private int frequency;
	private boolean sendExactPower;
	private boolean receiveInvertedPower;
	private int senderRange;
	private boolean highlightConnections;

	public SyncSecureRedstoneInterface(BlockPos pos, boolean sender, boolean protectedSignal, int frequency, boolean sendExactPower, boolean receiveInvertedPower, int senderRange, boolean highlightConnections) {
		this.pos = pos;
		this.sender = sender;
		this.protectedSignal = protectedSignal;
		this.frequency = frequency;
		this.sendExactPower = sendExactPower;
		this.receiveInvertedPower = receiveInvertedPower;
		this.senderRange = senderRange;
		this.highlightConnections = highlightConnections;
	}

	public SyncSecureRedstoneInterface(FriendlyByteBuf buf) {
		pos = buf.readBlockPos();
		sender = buf.readBoolean();
		protectedSignal = buf.readBoolean();
		frequency = buf.readVarInt();
		sendExactPower = buf.readBoolean();
		receiveInvertedPower = buf.readBoolean();
		senderRange = buf.readVarInt();
		highlightConnections = buf.readBoolean();
	}

	@Override
	public void write(FriendlyByteBuf buf) {
		buf.writeBlockPos(pos);
		buf.writeBoolean(sender);
		buf.writeBoolean(protectedSignal);
		buf.writeVarInt(frequency);
		buf.writeBoolean(sendExactPower);
		buf.writeBoolean(receiveInvertedPower);
		buf.writeVarInt(senderRange);
		buf.writeBoolean(highlightConnections);
	}

	@Override
	public ResourceLocation id() {
		return ID;
	}

	public void handle(IPayloadContext ctx) {
		Player player = ctx.player().orElseThrow();
		Level level = player.level();

		if (level.getBlockEntity(pos) instanceof SecureRedstoneInterfaceBlockEntity be && be.isOwnedBy(player)) {
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
