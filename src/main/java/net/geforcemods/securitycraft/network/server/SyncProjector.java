package net.geforcemods.securitycraft.network.server;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blockentities.ProjectorBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SyncProjector(BlockPos pos, int data, DataType dataType) implements CustomPacketPayload {

	public static final Type<SyncProjector> TYPE = new Type<>(new ResourceLocation(SecurityCraft.MODID, "sync_projector"));
	public static final StreamCodec<RegistryFriendlyByteBuf, SyncProjector> STREAM_CODEC = new StreamCodec<>() {
		public SyncProjector decode(RegistryFriendlyByteBuf buf) {
			BlockPos pos = buf.readBlockPos();
			DataType dataType = buf.readEnum(DataType.class);

			if (dataType.isBoolean)
				return new SyncProjector(pos, buf.readBoolean() ? 1 : 0, dataType);
			else
				return new SyncProjector(pos, buf.readVarInt(), dataType);
		}

		@Override
		public void encode(RegistryFriendlyByteBuf buf, SyncProjector packet) {
			buf.writeBlockPos(packet.pos);
			buf.writeEnum(packet.dataType);

			if (packet.dataType.isBoolean)
				buf.writeBoolean(packet.data == 1);
			else
				buf.writeVarInt(packet.data);
		}
	};

	public SyncProjector(BlockPos pos, BlockState state) {
		this(pos, Block.getId(state), DataType.BLOCK_STATE);
	}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public void handle(IPayloadContext ctx) {
		Player player = ctx.player();
		Level level = player.level();

		if (!player.isSpectator() && level.isLoaded(pos) && level.getBlockEntity(pos) instanceof ProjectorBlockEntity be && be.isOwnedBy(player)) {
			BlockState state = level.getBlockState(pos);

			switch (dataType) {
				case WIDTH -> be.setProjectionWidth(data);
				case HEIGHT -> be.setProjectionHeight(data);
				case RANGE -> be.setProjectionRange(data);
				case OFFSET -> be.setProjectionOffset(data);
				case HORIZONTAL -> be.setHorizontal(data == 1);
				case OVERRIDING_BLOCKS -> be.setOverridingBlocks(data == 1);
				case BLOCK_STATE -> be.setProjectedState(Block.stateById(data));
				case INVALID -> throw new UnsupportedOperationException("Invalid sync projector payload received!");
			}

			level.sendBlockUpdated(pos, state, state, 2);
		}
	}
	public enum DataType {
		WIDTH,
		HEIGHT,
		RANGE,
		OFFSET,
		HORIZONTAL(true),
		OVERRIDING_BLOCKS(true),
		BLOCK_STATE,
		INVALID;

		public final boolean isBoolean;

		DataType() {
			this(false);
		}

		DataType(boolean isBoolean) {
			this.isBoolean = isBoolean;
		}
	}
}
