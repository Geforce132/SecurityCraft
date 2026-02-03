package net.geforcemods.securitycraft.network.client;

import java.util.ArrayList;
import java.util.List;

import net.geforcemods.securitycraft.ClientHandler;
import net.geforcemods.securitycraft.SecurityCraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record UpdateLaserColors(List<BlockPos> positionsToUpdate) implements CustomPacketPayload {
	public static final Type<UpdateLaserColors> TYPE = new Type<>(SecurityCraft.resLoc("update_laser_colors"));
	//@formatter:off
	public static final StreamCodec<RegistryFriendlyByteBuf, UpdateLaserColors> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.collection(ArrayList::new, BlockPos.STREAM_CODEC), UpdateLaserColors::positionsToUpdate,
			UpdateLaserColors::new);
	//@formatter:on

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public void handle(IPayloadContext ctx) {
		for (BlockPos pos : positionsToUpdate) {
			ClientHandler.updateBlockColorAroundPosition(pos);
		}
	}
}
