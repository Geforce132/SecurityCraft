package net.geforcemods.securitycraft.network.server;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blockentities.KeycardReaderBlockEntity;
import net.geforcemods.securitycraft.inventory.KeycardReaderMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

public class SyncKeycardSettings implements CustomPacketPayload {
	public static final ResourceLocation ID = new ResourceLocation(SecurityCraft.MODID, "sync_keycard_settings");
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

	public SyncKeycardSettings(FriendlyByteBuf buf) {
		pos = buf.readBlockPos();
		signature = buf.readVarInt();
		link = buf.readBoolean();
		acceptedLevels = new boolean[5];

		for (int i = 0; i < 5; i++) {
			acceptedLevels[i] = buf.readBoolean();
		}

		usableBy = buf.readUtf();
	}

	@Override
	public void write(FriendlyByteBuf buf) {
		buf.writeBlockPos(pos);
		buf.writeVarInt(signature);
		buf.writeBoolean(link);

		for (int i = 0; i < 5; i++) {
			buf.writeBoolean(acceptedLevels[i]);
		}

		buf.writeUtf(usableBy);
	}

	@Override
	public ResourceLocation id() {
		return ID;
	}

	public void handle(PlayPayloadContext ctx) {
		Player player = ctx.player().orElseThrow();

		if (!player.isSpectator() && player.level().getBlockEntity(pos) instanceof KeycardReaderBlockEntity be) {
			boolean isOwner = be.isOwnedBy(player);

			if (isOwner || be.isAllowed(player)) {
				if (isOwner) {
					be.setAcceptedLevels(acceptedLevels);
					be.setSignature(signature);
				}

				if (link && player.containerMenu instanceof KeycardReaderMenu keycardReaderContainer)
					keycardReaderContainer.link(usableBy);
			}
		}
	}
}
