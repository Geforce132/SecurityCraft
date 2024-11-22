package net.geforcemods.securitycraft.network.server;

import io.netty.buffer.ByteBuf;
import net.geforcemods.securitycraft.blockentities.KeycardReaderBlockEntity;
import net.geforcemods.securitycraft.inventory.KeycardReaderMenu;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class SyncKeycardSettings implements IMessage {
	private BlockPos pos;
	private int signature;
	private boolean[] acceptedLevels;
	private boolean link;
	private String usableBy;

	public SyncKeycardSettings() {}

	public SyncKeycardSettings(BlockPos pos, boolean[] acceptedLevels, int signature, boolean link, String usableBy) {
		this.pos = pos;
		this.acceptedLevels = acceptedLevels;
		this.signature = signature;
		this.link = link;
		this.usableBy = usableBy;
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeLong(pos.toLong());
		buf.writeInt(signature);
		buf.writeBoolean(link);

		for (int i = 0; i < 5; i++) {
			buf.writeBoolean(acceptedLevels[i]);
		}

		ByteBufUtils.writeUTF8String(buf, usableBy);
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		pos = BlockPos.fromLong(buf.readLong());
		signature = buf.readInt();
		link = buf.readBoolean();
		acceptedLevels = new boolean[5];

		for (int i = 0; i < 5; i++) {
			acceptedLevels[i] = buf.readBoolean();
		}

		usableBy = ByteBufUtils.readUTF8String(buf);
	}

	public static class Handler implements IMessageHandler<SyncKeycardSettings, IMessage> {
		@Override
		public IMessage onMessage(SyncKeycardSettings message, MessageContext context) {
			Utils.addScheduledTask(context.getServerHandler().player.world, () -> {
				EntityPlayer player = context.getServerHandler().player;
				TileEntity tile = player.world.getTileEntity(message.pos);

				if (tile instanceof KeycardReaderBlockEntity) {
					KeycardReaderBlockEntity te = (KeycardReaderBlockEntity) tile;
					boolean isOwner = te.isOwnedBy(player);

					if (te.isOwnedBy(player) || te.isAllowed(player)) {
						if (isOwner) {
							te.setAcceptedLevels(message.acceptedLevels);
							te.setSignature(message.signature);
						}

						if (message.link) {
							Container container = player.openContainer;

							if (container instanceof KeycardReaderMenu)
								((KeycardReaderMenu) container).link(message.usableBy);
						}
					}
				}
			});
			return null;
		}
	}
}
