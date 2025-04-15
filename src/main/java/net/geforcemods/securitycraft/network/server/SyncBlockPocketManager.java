package net.geforcemods.securitycraft.network.server;

import io.netty.buffer.ByteBuf;
import net.geforcemods.securitycraft.blockentities.BlockPocketManagerBlockEntity;
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

public class SyncBlockPocketManager implements IMessage {
	private BlockPos pos;
	private int size;
	private boolean showOutline;
	private int autoBuildOffset;
	private int color;

	public SyncBlockPocketManager() {}

	public SyncBlockPocketManager(BlockPos pos, int size, boolean showOutline, int autoBuildOffset, int color) {
		this.pos = pos;
		this.size = size;
		this.showOutline = showOutline;
		this.autoBuildOffset = autoBuildOffset;
		this.color = color;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		pos = BlockPos.fromLong(buf.readLong());
		size = ByteBufUtils.readVarInt(buf, 5);
		showOutline = buf.readBoolean();
		autoBuildOffset = ByteBufUtils.readVarInt(buf, 5);
		color = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeLong(pos.toLong());
		ByteBufUtils.writeVarInt(buf, size, 5);
		buf.writeBoolean(showOutline);
		ByteBufUtils.writeVarInt(buf, autoBuildOffset, 5);
		buf.writeInt(color);
	}

	public static class Handler implements IMessageHandler<SyncBlockPocketManager, IMessage> {
		@Override
		public IMessage onMessage(SyncBlockPocketManager message, MessageContext ctx) {
			Utils.addScheduledTask(ctx.getServerHandler().player.world, () -> {
				EntityPlayer player = ctx.getServerHandler().player;
				World world = player.world;
				TileEntity te = world.getTileEntity(message.pos);

				if (!player.isSpectator() && world.isBlockLoaded(message.pos) && te instanceof BlockPocketManagerBlockEntity && ((BlockPocketManagerBlockEntity) te).isOwnedBy(player)) {
					BlockPocketManagerBlockEntity bpm = (BlockPocketManagerBlockEntity) te;
					IBlockState state = world.getBlockState(message.pos);

					bpm.setSize(message.size);
					bpm.setShowOutline(message.showOutline);
					bpm.setAutoBuildOffset(message.autoBuildOffset);
					bpm.setColor(message.color);
					bpm.markDirty();
					world.notifyBlockUpdate(message.pos, state, state, 2);
				}
			});

			return null;
		}
	}
}
