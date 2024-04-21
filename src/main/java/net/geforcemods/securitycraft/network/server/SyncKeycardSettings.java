package net.geforcemods.securitycraft.network.server;

import java.util.Arrays;
import java.util.Objects;

import net.geforcemods.securitycraft.SCStreamCodecs;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blockentities.KeycardReaderBlockEntity;
import net.geforcemods.securitycraft.inventory.KeycardReaderMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SyncKeycardSettings(BlockPos pos, boolean[] acceptedLevels, int signature, boolean link) implements CustomPacketPayload {

	public static final Type<SyncKeycardSettings> TYPE = new Type<>(new ResourceLocation(SecurityCraft.MODID, "sync_keycard_settings"));
	//@formatter:off
	public static final StreamCodec<RegistryFriendlyByteBuf, SyncKeycardSettings> STREAM_CODEC = StreamCodec.composite(
			BlockPos.STREAM_CODEC, SyncKeycardSettings::pos,
			SCStreamCodecs.BOOLEAN_ARRAY, SyncKeycardSettings::acceptedLevels,
			ByteBufCodecs.VAR_INT, SyncKeycardSettings::signature,
			ByteBufCodecs.BOOL, SyncKeycardSettings::link,
			SyncKeycardSettings::new);
	//@formatter:on
	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public void handle(IPayloadContext ctx) {
		Player player = ctx.player();

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

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;

		if (obj == null || getClass() != obj.getClass())
			return false;

		SyncKeycardSettings skc = (SyncKeycardSettings) obj;

		return pos != null && pos.equals(skc.pos) && Arrays.equals(acceptedLevels, skc.acceptedLevels) && signature == skc.signature && link == skc.link;
	}

	@Override
	public int hashCode() {
		int arrayHash = Arrays.hashCode(acceptedLevels);

		return Objects.hash(pos, arrayHash, signature, link);
	}

	@Override
	public String toString() {
		return "SyncKeycardSettings{" + "pos=" + pos + ", acceptedLevels=" + Arrays.toString(acceptedLevels) + ", signature=" + signature + ", link=" + link + "}";
	}
}
