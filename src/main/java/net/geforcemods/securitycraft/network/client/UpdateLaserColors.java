package net.geforcemods.securitycraft.network.client;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import net.geforcemods.securitycraft.ClientHandler;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

public class UpdateLaserColors {
	private List<BlockPos> positionsToUpdate;

	public UpdateLaserColors() {}

	public UpdateLaserColors(List<BlockPos> positionsToUpdate) {
		this.positionsToUpdate = positionsToUpdate;
	}

	public UpdateLaserColors(PacketBuffer buf) {
		int size = buf.readVarInt();

		positionsToUpdate = new ArrayList<>();

		for (int i = 0; i < size; i++) {
			positionsToUpdate.add(BlockPos.of(buf.readLong()));
		}
	}

	public void encode(PacketBuffer buf) {
		buf.writeVarInt(positionsToUpdate.size());

		for (BlockPos pos : positionsToUpdate) {
			buf.writeLong(pos.asLong());
		}
	}

	public void handle(Supplier<NetworkEvent.Context> ctx) {
		for (BlockPos pos : positionsToUpdate) {
			ClientHandler.updateBlockColorAroundPosition(pos);
		}
	}
}
