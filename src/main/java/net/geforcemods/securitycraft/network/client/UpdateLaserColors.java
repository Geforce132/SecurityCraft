package net.geforcemods.securitycraft.network.client;

import java.util.ArrayList;
import java.util.List;

import net.geforcemods.securitycraft.ClientHandler;
import net.geforcemods.securitycraft.SecurityCraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

public class UpdateLaserColors implements CustomPacketPayload {
	public static final ResourceLocation ID = new ResourceLocation(SecurityCraft.MODID, "update_laser_colors");
	private List<BlockPos> positionsToUpdate;

	public UpdateLaserColors() {}

	public UpdateLaserColors(List<BlockPos> positionsToUpdate) {
		this.positionsToUpdate = positionsToUpdate;
	}

	public UpdateLaserColors(FriendlyByteBuf buf) {
		int size = buf.readVarInt();

		positionsToUpdate = new ArrayList<>();

		for (int i = 0; i < size; i++) {
			positionsToUpdate.add(BlockPos.of(buf.readLong()));
		}
	}

	@Override
	public void write(FriendlyByteBuf buf) {
		buf.writeVarInt(positionsToUpdate.size());

		for (BlockPos pos : positionsToUpdate) {
			buf.writeLong(pos.asLong());
		}
	}

	@Override
	public ResourceLocation id() {
		return ID;
	}

	public void handle(PlayPayloadContext ctx) {
		for (BlockPos pos : positionsToUpdate) {
			ClientHandler.updateBlockColorAroundPosition(pos);
		}
	}
}
