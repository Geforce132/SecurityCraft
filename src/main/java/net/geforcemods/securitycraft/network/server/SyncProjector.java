package net.geforcemods.securitycraft.network.server;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blockentities.ProjectorBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

public class SyncProjector implements CustomPacketPayload {
	public static final ResourceLocation ID = new ResourceLocation(SecurityCraft.MODID, "sync_projector");
	private BlockPos pos;
	private int data;
	private DataType dataType;

	public SyncProjector() {}

	public SyncProjector(BlockPos pos, int data, DataType dataType) {
		this.pos = pos;
		this.data = data;
		this.dataType = dataType;
	}

	public SyncProjector(BlockPos pos, BlockState state) {
		this.pos = pos;
		this.data = Block.getId(state);
		this.dataType = DataType.BLOCK_STATE;
	}

	public SyncProjector(FriendlyByteBuf buf) {
		pos = buf.readBlockPos();
		dataType = buf.readEnum(DataType.class);

		if (dataType.isBoolean)
			data = buf.readBoolean() ? 1 : 0;
		else
			data = buf.readVarInt();
	}

	@Override
	public void write(FriendlyByteBuf buf) {
		buf.writeBlockPos(pos);
		buf.writeEnum(dataType);

		if (dataType.isBoolean)
			buf.writeBoolean(data == 1);
		else
			buf.writeVarInt(data);
	}

	@Override
	public ResourceLocation id() {
		return ID;
	}

	public void handle(PlayPayloadContext ctx) {
		Player player = ctx.player().orElseThrow();
		Level level = player.level();

		if (!player.isSpectator() && level.isLoaded(pos) && level.getBlockEntity(pos) instanceof ProjectorBlockEntity be && be.isOwnedBy(player)) {
			BlockState state = level.getBlockState(pos);

			switch (dataType) {
				case WIDTH:
					be.setProjectionWidth(data);
					break;
				case HEIGHT:
					be.setProjectionHeight(data);
					break;
				case RANGE:
					be.setProjectionRange(data);
					break;
				case OFFSET:
					be.setProjectionOffset(data);
					break;
				case HORIZONTAL:
					be.setHorizontal(data == 1);
					break;
				case OVERRIDING_BLOCKS:
					be.setOverridingBlocks(data == 1);
					break;
				case BLOCK_STATE:
					be.setProjectedState(Block.stateById(data));
					break;
				case INVALID:
					break;
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
