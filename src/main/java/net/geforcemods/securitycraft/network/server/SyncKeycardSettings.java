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

	public SyncKeycardSettings(PacketBuffer buf) {
		pos = buf.readBlockPos();
		signature = buf.readVarInt();
		link = buf.readBoolean();
		acceptedLevels = new boolean[5];

		for (int i = 0; i < 5; i++) {
			acceptedLevels[i] = buf.readBoolean();
		}
	}

	public void encode(PacketBuffer buf) {
		buf.writeBlockPos(pos);
		buf.writeVarInt(signature);
		buf.writeBoolean(link);

		for (int i = 0; i < 5; i++) {
			buf.writeBoolean(acceptedLevels[i]);
		}
	}

	public void handle(Supplier<NetworkEvent.Context> ctx) {
		PlayerEntity player = ctx.get().getSender();
		TileEntity tile = player.level.getBlockEntity(pos);

		if (tile instanceof KeycardReaderBlockEntity) {
			KeycardReaderBlockEntity te = (KeycardReaderBlockEntity) tile;
			boolean isOwner = te.isOwnedBy(player);

			if (isOwner || te.isAllowed(player)) {
				if (isOwner) {
					te.setAcceptedLevels(acceptedLevels);
					te.setSignature(signature);
				}

				if (link) {
					Container container = player.containerMenu;

					if (container instanceof KeycardReaderMenu)
						((KeycardReaderMenu) container).link();
				}
			}
		}
	}
}
