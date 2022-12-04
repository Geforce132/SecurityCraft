package net.geforcemods.securitycraft.network.server;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.blockentities.KeycardReaderBlockEntity;
import net.geforcemods.securitycraft.inventory.KeycardReaderMenu;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

public class SyncKeycardSettings {
	private BlockPos pos;
	private int signature;
	private boolean[] acceptedLevels;
	private boolean link;

	public SyncKeycardSettings() {}

	public SyncKeycardSettings(BlockPos pos, boolean[] acceptedLevels, int signature, boolean link) {
		this.pos = pos;
		this.acceptedLevels = acceptedLevels;
		this.signature = signature;
		this.link = link;
	}

	public static void encode(SyncKeycardSettings message, PacketBuffer buf) {
		buf.writeBlockPos(message.pos);
		buf.writeVarInt(message.signature);
		buf.writeBoolean(message.link);

		for (int i = 0; i < 5; i++) {
			buf.writeBoolean(message.acceptedLevels[i]);
		}
	}

	public static SyncKeycardSettings decode(PacketBuffer buf) {
		SyncKeycardSettings message = new SyncKeycardSettings();

		message.pos = buf.readBlockPos();
		message.signature = buf.readVarInt();
		message.link = buf.readBoolean();
		message.acceptedLevels = new boolean[5];

		for (int i = 0; i < 5; i++) {
			message.acceptedLevels[i] = buf.readBoolean();
		}

		return message;
	}

	public static void onMessage(SyncKeycardSettings message, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			BlockPos pos = message.pos;
			PlayerEntity player = ctx.get().getSender();
			TileEntity tile = player.level.getBlockEntity(pos);

			if (tile instanceof KeycardReaderBlockEntity) {
				KeycardReaderBlockEntity te = (KeycardReaderBlockEntity) tile;
				boolean isOwner = te.isOwnedBy(player);

				if (isOwner || te.isAllowed(player)) {
					if (isOwner) {
						te.setAcceptedLevels(message.acceptedLevels);
						te.setSignature(message.signature);
					}

					if (message.link) {
						Container container = player.containerMenu;

						if (container instanceof KeycardReaderMenu)
							((KeycardReaderMenu) container).link();
					}
				}
			}
		});

		ctx.get().setPacketHandled(true);
	}
}
