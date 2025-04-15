package net.geforcemods.securitycraft.network.server;

import io.netty.buffer.ByteBuf;
import net.geforcemods.securitycraft.blockentities.RiftStabilizerBlockEntity;
import net.geforcemods.securitycraft.blockentities.RiftStabilizerBlockEntity.TeleportationType;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class SyncRiftStabilizer implements IMessage {
	private BlockPos pos;
	private TeleportationType teleportationType;
	private boolean allowed;

	public SyncRiftStabilizer() {}

	public SyncRiftStabilizer(BlockPos pos, TeleportationType teleportationType, boolean allowed) {
		this.pos = pos;
		this.teleportationType = teleportationType;
		this.allowed = allowed;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		pos = BlockPos.fromLong(buf.readLong());
		teleportationType = TeleportationType.values()[buf.readInt()];
		allowed = buf.readBoolean();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeLong(pos.toLong());
		buf.writeInt(teleportationType.ordinal());
		buf.writeBoolean(allowed);
	}

	public static class Handler implements IMessageHandler<SyncRiftStabilizer, IMessage> {
		@Override
		public IMessage onMessage(SyncRiftStabilizer message, MessageContext ctx) {
			Utils.addScheduledTask(ctx.getServerHandler().player.world, () -> {
				if (message.teleportationType != null) {
					EntityPlayer player = ctx.getServerHandler().player;
					World world = player.world;

					if (!player.isSpectator() && world.getTileEntity(message.pos) instanceof RiftStabilizerBlockEntity) {
						RiftStabilizerBlockEntity te = ((RiftStabilizerBlockEntity) world.getTileEntity(message.pos));

						if (te.isOwnedBy(player)) {
							IBlockState state = world.getBlockState(message.pos);

							te.setFilter(message.teleportationType, message.allowed);
							world.notifyBlockUpdate(message.pos, state, state, 2);
						}
					}
				}
			});

			return null;
		}
	}
}
