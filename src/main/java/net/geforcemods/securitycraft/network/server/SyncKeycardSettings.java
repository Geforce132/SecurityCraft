package net.geforcemods.securitycraft.network.server;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.blockentities.KeycardReaderBlockEntity;
import net.geforcemods.securitycraft.inventory.KeycardReaderMenu;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

public class SyncKeycardSettings {
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

	public SyncKeycardSettings(PacketBuffer buf) {
		pos = buf.readBlockPos();
		signature = buf.readVarInt();
		link = buf.readBoolean();
		acceptedLevels = new boolean[5];

		for (int i = 0; i < 5; i++) {
			acceptedLevels[i] = buf.readBoolean();
		}

		usableBy = buf.readUtf();
	}

	public void encode(PacketBuffer buf) {
		buf.writeBlockPos(pos);
		buf.writeVarInt(signature);
		buf.writeBoolean(link);

		for (int i = 0; i < 5; i++) {
			buf.writeBoolean(acceptedLevels[i]);
		}

		buf.writeUtf(usableBy);
	}

	public void handle(Supplier<NetworkEvent.Context> ctx) {
		PlayerEntity player = ctx.get().getSender();
		TileEntity te = player.level.getBlockEntity(pos);

		if (!player.isSpectator() && te instanceof KeycardReaderBlockEntity) {
			KeycardReaderBlockEntity be = (KeycardReaderBlockEntity) te;
			boolean isOwner = be.isOwnedBy(player);

			if (isOwner || be.isAllowed(player)) {
				if (isOwner) {
					be.setAcceptedLevels(acceptedLevels);
					be.setSignature(signature);
				}

				if (link && player.containerMenu instanceof KeycardReaderMenu)
					((KeycardReaderMenu) player.containerMenu).link(usableBy);
			}
		}
	}
}
