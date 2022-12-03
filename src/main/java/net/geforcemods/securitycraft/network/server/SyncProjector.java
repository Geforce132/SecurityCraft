package net.geforcemods.securitycraft.network.server;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.blockentities.ProjectorBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

public class SyncProjector {
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

	public static void encode(SyncProjector message, FriendlyByteBuf buf) {
		buf.writeBlockPos(message.pos);
		buf.writeEnum(message.dataType);

		if (message.dataType == DataType.HORIZONTAL)
			buf.writeBoolean(message.data == 1);
		else
			buf.writeVarInt(message.data);
	}

	public static SyncProjector decode(FriendlyByteBuf buf) {
		SyncProjector message = new SyncProjector();

		message.pos = buf.readBlockPos();
		message.dataType = buf.readEnum(DataType.class);

		if (message.dataType == DataType.HORIZONTAL)
			message.data = buf.readBoolean() ? 1 : 0;
		else
			message.data = buf.readVarInt();

		return message;
	}

	public static void onMessage(SyncProjector message, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			BlockPos pos = message.pos;
			Player player = ctx.get().getSender();
			Level level = player.level;

			if (level.isLoaded(pos) && level.getBlockEntity(pos) instanceof ProjectorBlockEntity be && be.isOwnedBy(player)) {
				BlockState state = level.getBlockState(pos);

				switch (message.dataType) {
					case WIDTH:
						be.setProjectionWidth(message.data);
						break;
					case HEIGHT:
						be.setProjectionHeight(message.data);
						break;
					case RANGE:
						be.setProjectionRange(message.data);
						break;
					case OFFSET:
						be.setProjectionOffset(message.data);
						break;
					case HORIZONTAL:
						be.setHorizontal(message.data == 1);
						break;
					case BLOCK_STATE:
						be.setProjectedState(Block.stateById(message.data));
						break;
					case INVALID:
						break;
				}

				level.sendBlockUpdated(pos, state, state, 2);
			}
		});

		ctx.get().setPacketHandled(true);
	}

	public enum DataType {
		WIDTH,
		HEIGHT,
		RANGE,
		OFFSET,
		HORIZONTAL,
		BLOCK_STATE,
		INVALID;
	}
}
