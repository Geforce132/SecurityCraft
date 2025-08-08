package net.geforcemods.securitycraft.network.server;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;

import io.netty.buffer.ByteBuf;
import net.geforcemods.securitycraft.blockentities.FrameBlockEntity;
import net.geforcemods.securitycraft.misc.GlobalPos;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class SyncFrame implements IMessage {
	private BlockPos pos;
	private int requestedRenderDistance;
	private Optional<GlobalPos> removedCamera;
	private Optional<GlobalPos> currentCamera;
	boolean disableCurrentCamera;

	public SyncFrame() {}

	public SyncFrame(BlockPos pos, int requestedRenderDistance, Optional<GlobalPos> removedCamera, Optional<GlobalPos> currentCamera, boolean disableCurrentCamera) {
		this.pos = pos;
		this.requestedRenderDistance = requestedRenderDistance;
		this.removedCamera = removedCamera;
		this.currentCamera = currentCamera;
		this.disableCurrentCamera = disableCurrentCamera;
	}

	@Override
	public void fromBytes(ByteBuf _buf) {
		PacketBuffer buf = new PacketBuffer(_buf);

		pos = buf.readBlockPos();
		requestedRenderDistance = buf.readVarInt();
		removedCamera = readOptional(buf, this::readCameraView);
		currentCamera = readOptional(buf, this::readCameraView);
		disableCurrentCamera = buf.readBoolean();
	}

	@Override
	public void toBytes(ByteBuf _buf) {
		PacketBuffer buf = new PacketBuffer(_buf);

		buf.writeBlockPos(pos);
		buf.writeVarInt(requestedRenderDistance);
		writeOptional(buf, removedCamera, this::writeCameraView);
		writeOptional(buf, currentCamera, this::writeCameraView);
		buf.writeBoolean(disableCurrentCamera);
	}

	private <T> Optional<T> readOptional(PacketBuffer buf, Function<PacketBuffer, T> reader) {
		if (buf.readBoolean())
			return Optional.of(reader.apply(buf));
		else
			return Optional.empty();
	}

	private GlobalPos readCameraView(PacketBuffer buf) {
		return GlobalPos.of(buf.readVarInt(), buf.readBlockPos());
	}

	private <T> void writeOptional(PacketBuffer buf, Optional<T> optional, BiConsumer<PacketBuffer, T> writer) {
		if (optional.isPresent()) {
			buf.writeBoolean(true);
			writer.accept(buf, optional.get());
		}
		else
			buf.writeBoolean(false);
	}

	private void writeCameraView(PacketBuffer buf, GlobalPos cameraView) {
		buf.writeVarInt(cameraView.dimension());
		buf.writeBlockPos(cameraView.pos());
	}

	public static class Handler implements IMessageHandler<SyncFrame, IMessage> {
		@Override
		public IMessage onMessage(SyncFrame message, MessageContext ctx) {
			Utils.addScheduledTask(ctx.getServerHandler().player.world, () -> {
				EntityPlayer player = ctx.getServerHandler().player;
				World level = player.world;
				GlobalPos currentCamera = message.currentCamera.orElse(null);
				TileEntity te = level.getTileEntity(message.pos);

				if (!player.isSpectator() && te instanceof FrameBlockEntity) {
					FrameBlockEntity be = (FrameBlockEntity) te;

					if (be.isDisabled())
						return;

					boolean isOwner = be.isOwnedBy(player);

					if (isOwner)
						message.removedCamera.ifPresent(be::removeCamera);

					if (isOwner || be.isAllowed(player))
						be.switchCameraOnServer(currentCamera, player, message.requestedRenderDistance, message.disableCurrentCamera);
				}
			});

			return null;
		}
	}
}
