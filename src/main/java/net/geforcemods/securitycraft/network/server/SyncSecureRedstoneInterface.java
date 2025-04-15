package net.geforcemods.securitycraft.network.server;

import io.netty.buffer.ByteBuf;
import net.geforcemods.securitycraft.blockentities.SecureRedstoneInterfaceBlockEntity;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class SyncSecureRedstoneInterface implements IMessage {
	private BlockPos pos;
	private boolean sender;
	private boolean protectedSignal;
	private int frequency;
	private boolean sendExactPower;
	private boolean receiveInvertedPower;
	private int senderRange;
	private boolean highlightConnections;

	public SyncSecureRedstoneInterface() {}

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

	@Override
	public void fromBytes(ByteBuf buf) {
		pos = BlockPos.fromLong(buf.readLong());
		sender = buf.readBoolean();
		protectedSignal = buf.readBoolean();
		frequency = ByteBufUtils.readVarInt(buf, 5);
		sendExactPower = buf.readBoolean();
		receiveInvertedPower = buf.readBoolean();
		senderRange = ByteBufUtils.readVarInt(buf, 5);
		highlightConnections = buf.readBoolean();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeLong(pos.toLong());
		buf.writeBoolean(sender);
		buf.writeBoolean(protectedSignal);
		ByteBufUtils.writeVarInt(buf, frequency, 5);
		buf.writeBoolean(sendExactPower);
		buf.writeBoolean(receiveInvertedPower);
		ByteBufUtils.writeVarInt(buf, senderRange, 5);
		buf.writeBoolean(highlightConnections);
	}

	public static class Handler implements IMessageHandler<SyncSecureRedstoneInterface, IMessage> {
		@Override
		public IMessage onMessage(SyncSecureRedstoneInterface message, MessageContext ctx) {
			Utils.addScheduledTask(ctx.getServerHandler().player.world, () -> {
				EntityPlayerMP player = ctx.getServerHandler().player;
				World level = player.world;
				TileEntity te = level.getTileEntity(message.pos);

				if (!player.isSpectator() && te instanceof SecureRedstoneInterfaceBlockEntity) {
					SecureRedstoneInterfaceBlockEntity be = (SecureRedstoneInterfaceBlockEntity) te;

					if (!be.isOwnedBy(player))
						return;

					if (message.sender != be.isSender())
						be.setSender(message.sender);

					if (message.protectedSignal != be.isProtectedSignal())
						be.setProtectedSignal(message.protectedSignal);

					if (message.frequency != be.getFrequency())
						be.setFrequency(message.frequency);

					if (message.sendExactPower != be.sendsExactPower())
						be.setSendExactPower(message.sendExactPower);

					if (message.receiveInvertedPower != be.receivesInvertedPower())
						be.setReceiveInvertedPower(message.receiveInvertedPower);

					if (message.senderRange != be.getSenderRange())
						be.setSenderRange(message.senderRange);

					be.setHighlightConnections(message.highlightConnections);
				}
			});

			return null;
		}
	}
}
