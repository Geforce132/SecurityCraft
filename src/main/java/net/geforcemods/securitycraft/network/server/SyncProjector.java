package net.geforcemods.securitycraft.network.server;

import io.netty.buffer.ByteBuf;
import net.geforcemods.securitycraft.blockentities.ProjectorBlockEntity;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class SyncProjector implements IMessage {
	private BlockPos pos;
	private int data;
	private DataType dataType;

	public SyncProjector() {}

	public SyncProjector(BlockPos pos, int data, DataType dataType) {
		this.pos = pos;
		this.data = data;
		this.dataType = dataType;
	}

	public SyncProjector(BlockPos pos, IBlockState state) {
		this.pos = pos;
		this.data = Block.getStateId(state);
		this.dataType = DataType.BLOCK_STATE;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		pos = BlockPos.fromLong(buf.readLong());
		dataType = DataType.values()[ByteBufUtils.readVarInt(buf, 5)];

		if (dataType.isBoolean)
			data = buf.readBoolean() ? 1 : 0;
		else
			data = ByteBufUtils.readVarInt(buf, 5);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeLong(pos.toLong());
		ByteBufUtils.writeVarInt(buf, dataType.ordinal(), 5);

		if (dataType.isBoolean)
			buf.writeBoolean(data == 1);
		else
			ByteBufUtils.writeVarInt(buf, data, 5);
	}

	public static class Handler implements IMessageHandler<SyncProjector, IMessage> {
		@Override
		public IMessage onMessage(SyncProjector message, MessageContext ctx) {
			Utils.addScheduledTask(ctx.getServerHandler().player.world, () -> {
				EntityPlayer player = ctx.getServerHandler().player;
				World world = player.world;
				TileEntity te = world.getTileEntity(message.pos);

				if (world.isBlockLoaded(message.pos) && te instanceof ProjectorBlockEntity && ((ProjectorBlockEntity) te).isOwnedBy(player)) {
					ProjectorBlockEntity projector = (ProjectorBlockEntity) te;
					IBlockState state = world.getBlockState(message.pos);

					switch (message.dataType) {
						case WIDTH:
							projector.setProjectionWidth(message.data);
							break;
						case HEIGHT:
							projector.setProjectionHeight(message.data);
							break;
						case RANGE:
							projector.setProjectionRange(message.data);
							break;
						case OFFSET:
							projector.setProjectionOffset(message.data);
							break;
						case HORIZONTAL:
							projector.setHorizontal(message.data == 1);
							break;
						case OVERRIDING_BLOCKS:
							projector.setOverridingBlocks(message.data == 1);
							break;
						case BLOCK_STATE:
							projector.setProjectedState(Block.getStateById(message.data));
							break;
						case INVALID:
							break;
					}

					world.notifyBlockUpdate(message.pos, state, state, 2);
				}
			});

			return null;
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
