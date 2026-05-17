package net.geforcemods.securitycraft.network.client;

import java.util.UUID;
import java.util.function.Supplier;

import net.geforcemods.securitycraft.ClientHandler;
import net.geforcemods.securitycraft.blockentities.UsernameLoggerBlockEntity;
import net.geforcemods.securitycraft.blockentities.UsernameLoggerBlockEntity.UsernameLoggerEntry;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public class UpdateLogger {
	private BlockPos pos;
	private int index;
	private String username;
	private UUID uuid;
	private long timestamp;

	public UpdateLogger() {}

	public UpdateLogger(BlockPos pos, int index, String username, UUID uuid, long timestamp) {
		this.pos = pos;
		this.index = index;
		this.username = username;
		this.uuid = uuid;
		this.timestamp = timestamp;
	}

	public UpdateLogger(FriendlyByteBuf buf) {
		pos = buf.readBlockPos();
		index = buf.readVarInt();
		username = buf.readUtf(Integer.MAX_VALUE / 4);
		uuid = buf.readUUID();
		timestamp = buf.readVarLong();
	}

	public void encode(FriendlyByteBuf buf) {
		buf.writeBlockPos(pos);
		buf.writeVarInt(index);
		buf.writeUtf(username);
		buf.writeUUID(uuid);
		buf.writeVarLong(timestamp);
	}

	public void handle(Supplier<NetworkEvent.Context> ctx) {
		UsernameLoggerBlockEntity be = (UsernameLoggerBlockEntity) ClientHandler.getClientLevel().getBlockEntity(pos);

		if (be != null)
			be.getEntries()[index] = new UsernameLoggerEntry(username, uuid, timestamp);
	}
}
