package net.geforcemods.securitycraft.network.client;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.blockentities.UsernameLoggerBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public class UpdateLogger {
	private BlockPos pos;
	private int index;
	private String username;
	private String uuid;
	private long timestamp;

	public UpdateLogger() {}

	public UpdateLogger(BlockPos pos, int index, String username, String uuid, long timestamp) {
		this.index = index;
		this.username = username;
		this.uuid = uuid;
		this.timestamp = timestamp;
	}

	public UpdateLogger(FriendlyByteBuf buf) {
		pos = buf.readBlockPos();
		index = buf.readInt();
		username = buf.readUtf(Integer.MAX_VALUE / 4);
		uuid = buf.readUtf(Integer.MAX_VALUE / 4);
		timestamp = buf.readLong();
	}

	public void encode(FriendlyByteBuf buf) {
		buf.writeBlockPos(pos);
		buf.writeInt(index);
		buf.writeUtf(username);
		buf.writeUtf(uuid);
		buf.writeLong(timestamp);
	}

	public void handle(Supplier<NetworkEvent.Context> ctx) {
		UsernameLoggerBlockEntity be = (UsernameLoggerBlockEntity) Minecraft.getInstance().player.level.getBlockEntity(pos);

		if (be != null) {
			be.getPlayers()[index] = username;
			be.getUuids()[index] = uuid;
			be.getTimestamps()[index] = timestamp;
		}
	}
}
