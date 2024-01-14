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

	@Override
	public void write(FriendlyByteBuf buf) {
		buf.writeBlockPos(pos);
		buf.writeInt(index);
		buf.writeUtf(username);
		buf.writeUtf(uuid);
		buf.writeLong(timestamp);
	}

	@Override
	public ResourceLocation id() {
		return ID;
	}

	public void handle(PlayPayloadContext ctx) {
		UsernameLoggerBlockEntity be = (UsernameLoggerBlockEntity) Minecraft.getInstance().player.level().getBlockEntity(pos);

		if (be != null) {
			be.getPlayers()[index] = username;
			be.getUuids()[index] = uuid;
			be.getTimestamps()[index] = timestamp;
		}
	}
}
