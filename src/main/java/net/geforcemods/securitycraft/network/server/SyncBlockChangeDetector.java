package net.geforcemods.securitycraft.network.server;

import io.netty.buffer.ByteBuf;
import net.geforcemods.securitycraft.blockentities.BlockChangeDetectorBlockEntity;
import net.geforcemods.securitycraft.blockentities.BlockChangeDetectorBlockEntity.EnumDetectionMode;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class SyncBlockChangeDetector implements IMessage {
	private BlockPos pos;
	private EnumDetectionMode mode;
	private boolean showHighlights;
	private int color;

	public SyncBlockChangeDetector() {}

	public SyncBlockChangeDetector(BlockPos pos, EnumDetectionMode mode, boolean showHighlights, int color) {
		this.pos = pos;
		this.mode = mode;
		this.showHighlights = showHighlights;
		this.color = color;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		pos = BlockPos.fromLong(buf.readLong());
		mode = EnumDetectionMode.values()[ByteBufUtils.readVarInt(buf, 5)];
		showHighlights = buf.readBoolean();
		color = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeLong(pos.toLong());
		ByteBufUtils.writeVarInt(buf, mode.ordinal(), 5);
		buf.writeBoolean(showHighlights);
		buf.writeInt(color);
	}

	public static class Handler implements IMessageHandler<SyncBlockChangeDetector, IMessage> {
		@Override
		public IMessage onMessage(SyncBlockChangeDetector message, MessageContext ctx) {
			Utils.addScheduledTask(ctx.getServerHandler().player.world, () -> {
				EntityPlayer player = ctx.getServerHandler().player;
				World level = player.world;
				TileEntity tile = level.getTileEntity(message.pos);

				if (!player.isSpectator() && tile instanceof BlockChangeDetectorBlockEntity) {
					BlockChangeDetectorBlockEntity te = (BlockChangeDetectorBlockEntity) tile;

					if (te.isOwnedBy(player)) {
						IBlockState state = level.getBlockState(message.pos);

						te.setMode(message.mode);
						te.showHighlights(message.showHighlights);
						te.setColor(message.color);
						te.markDirty();
						level.notifyBlockUpdate(message.pos, state, state, 2);
					}
				}
			});

			return null;
		}
	}
}
