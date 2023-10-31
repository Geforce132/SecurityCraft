package net.geforcemods.securitycraft.network.server;

import net.geforcemods.securitycraft.blockentities.KeycardReaderBlockEntity;
import net.geforcemods.securitycraft.inventory.KeycardReaderMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.NetworkEvent;

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

	public SyncKeycardSettings(FriendlyByteBuf buf) {
		pos = buf.readBlockPos();
		signature = buf.readVarInt();
		link = buf.readBoolean();
		acceptedLevels = new boolean[5];

		for (int i = 0; i < 5; i++) {
			acceptedLevels[i] = buf.readBoolean();
		}
	}

	public void encode(FriendlyByteBuf buf) {
		buf.writeBlockPos(pos);
		buf.writeVarInt(signature);
		buf.writeBoolean(link);

		for (int i = 0; i < 5; i++) {
			buf.writeBoolean(acceptedLevels[i]);
		}
	}

	public void handle(NetworkEvent.Context ctx) {
		Player player = ctx.getSender();

		if (player.level().getBlockEntity(pos) instanceof KeycardReaderBlockEntity be) {
			boolean isOwner = be.isOwnedBy(player);

			if (isOwner || be.isAllowed(player)) {
				if (isOwner) {
					be.setAcceptedLevels(acceptedLevels);
					be.setSignature(signature);
				}

				if (link && player.containerMenu instanceof KeycardReaderMenu keycardReaderContainer)
					keycardReaderContainer.link();
			}
		}
	}
}
