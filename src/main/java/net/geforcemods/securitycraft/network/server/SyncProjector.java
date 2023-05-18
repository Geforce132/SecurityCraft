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

		if (dataType == DataType.HORIZONTAL)
			data = buf.readBoolean() ? 1 : 0;
		else
			data = buf.readVarInt();
	}

	public void encode(PacketBuffer buf) {
		buf.writeBlockPos(pos);
		buf.writeEnum(dataType);

		if (dataType == DataType.HORIZONTAL)
			buf.writeBoolean(data == 1);
		else
			buf.writeVarInt(data);
	}

	public void handle(Supplier<NetworkEvent.Context> ctx) {
		PlayerEntity player = ctx.get().getSender();
		World world = player.level;
		TileEntity te = world.getBlockEntity(pos);

		if (world.isLoaded(pos) && te instanceof ProjectorBlockEntity && ((ProjectorBlockEntity) te).isOwnedBy(player)) {
			ProjectorBlockEntity projector = (ProjectorBlockEntity) te;
			BlockState state = world.getBlockState(pos);

			switch (dataType) {
				case WIDTH:
					projector.setProjectionWidth(data);
					break;
				case HEIGHT:
					projector.setProjectionHeight(data);
					break;
				case RANGE:
					projector.setProjectionRange(data);
					break;
				case OFFSET:
					projector.setProjectionOffset(data);
					break;
				case HORIZONTAL:
					projector.setHorizontal(data == 1);
					break;
				case BLOCK_STATE:
					projector.setProjectedState(Block.stateById(data));
					break;
				case INVALID:
					break;
			}

			world.sendBlockUpdated(pos, state, state, 2);
		}
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
