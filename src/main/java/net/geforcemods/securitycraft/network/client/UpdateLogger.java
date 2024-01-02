package net.geforcemods.securitycraft.network.client;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blockentities.UsernameLoggerBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

public class UpdateLogger implements CustomPacketPayload {
	public static final ResourceLocation ID = new ResourceLocation(SecurityCraft.MODID, "update_logger");
	private int x, y, z, i;
	private String username;
	private String uuid;
	private long timestamp;

	public UpdateLogger() {}

	public UpdateLogger(int x, int y, int z, int i, String username, String uuid, long timestamp) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.i = i;
		this.username = username;
		this.uuid = uuid;
		this.timestamp = timestamp;
	}

	public UpdateLogger(FriendlyByteBuf buf) {
		x = buf.readInt();
		y = buf.readInt();
		z = buf.readInt();
		i = buf.readInt();
		username = buf.readUtf(Integer.MAX_VALUE / 4);
		uuid = buf.readUtf(Integer.MAX_VALUE / 4);
		timestamp = buf.readLong();
	}

	@Override
	public void write(FriendlyByteBuf buf) {
		buf.writeInt(x);
		buf.writeInt(y);
		buf.writeInt(z);
		buf.writeInt(i);
		buf.writeUtf(username);
		buf.writeUtf(uuid);
		buf.writeLong(timestamp);
	}

	@Override
	public ResourceLocation id() {
		return ID;
	}

	public void handle(PlayPayloadContext ctx) {
		BlockPos pos = new BlockPos(x, y, z);
		UsernameLoggerBlockEntity be = (UsernameLoggerBlockEntity) Minecraft.getInstance().player.level().getBlockEntity(pos);

		if (be != null) {
			be.getPlayers()[i] = username;
			be.getUuids()[i] = uuid;
			be.getTimestamps()[i] = timestamp;
		}
	}
}
