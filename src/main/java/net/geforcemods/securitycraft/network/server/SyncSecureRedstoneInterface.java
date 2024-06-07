package net.geforcemods.securitycraft.network.server;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.blockentities.SecureRedstoneInterfaceBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class SyncSecureRedstoneInterface {
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

	public SyncSecureRedstoneInterface(PacketBuffer buf) {
		this.pos = buf.readBlockPos();
		this.sender = buf.readBoolean();
		this.protectedSignal = buf.readBoolean();
		this.frequency = buf.readVarInt();
		this.sendExactPower = buf.readBoolean();
		this.receiveInvertedPower = buf.readBoolean();
		this.senderRange = buf.readVarInt();
		this.highlightConnections = buf.readBoolean();
	}

	public void encode(PacketBuffer buf) {
		buf.writeBlockPos(pos);
		buf.writeBoolean(sender);
		buf.writeBoolean(protectedSignal);
		buf.writeVarInt(frequency);
		buf.writeBoolean(sendExactPower);
		buf.writeBoolean(receiveInvertedPower);
		buf.writeVarInt(senderRange);
		buf.writeBoolean(highlightConnections);
	}

	public void handle(Supplier<Context> ctx) {
		PlayerEntity player = ctx.get().getSender();
		World level = player.level;
		TileEntity te = level.getBlockEntity(pos);

		if (te instanceof SecureRedstoneInterfaceBlockEntity) {
			SecureRedstoneInterfaceBlockEntity be = (SecureRedstoneInterfaceBlockEntity) te;

			if (!be.isOwnedBy(player))
				return;

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
