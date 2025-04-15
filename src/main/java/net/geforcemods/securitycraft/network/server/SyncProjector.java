package net.geforcemods.securitycraft.network.server;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.blockentities.ProjectorBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;

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

	public SyncProjector(PacketBuffer buf) {
		pos = buf.readBlockPos();
		dataType = buf.readEnum(DataType.class);

		if (dataType.isBoolean)
			data = buf.readBoolean() ? 1 : 0;
		else
			data = buf.readVarInt();
	}

	public void encode(PacketBuffer buf) {
		buf.writeBlockPos(pos);
		buf.writeEnum(dataType);

		if (dataType.isBoolean)
			buf.writeBoolean(data == 1);
		else
			buf.writeVarInt(data);
	}

	public void handle(Supplier<NetworkEvent.Context> ctx) {
		PlayerEntity player = ctx.get().getSender();
		World level = player.level;
		TileEntity te = level.getBlockEntity(pos);

		if (!player.isSpectator() && level.isLoaded(pos) && te instanceof ProjectorBlockEntity && ((ProjectorBlockEntity) te).isOwnedBy(player)) {
			ProjectorBlockEntity be = (ProjectorBlockEntity) te;
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
