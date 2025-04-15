package net.geforcemods.securitycraft.network.server;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

import net.geforcemods.securitycraft.blockentities.FrameBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;

public class SyncFrame {
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

	public SyncFrame(PacketBuffer buf) {
		pos = buf.readBlockPos();
		requestedRenderDistance = buf.readVarInt();
		removedCamera = readOptional(buf, this::readGlobalPos);
		currentCamera = readOptional(buf, this::readGlobalPos);
		disableCurrentCamera = buf.readBoolean();
	}

	public void encode(PacketBuffer buf) {
		buf.writeBlockPos(pos);
		buf.writeVarInt(requestedRenderDistance);
		writeOptional(buf, removedCamera, this::writeGlobalPos);
		writeOptional(buf, currentCamera, this::writeGlobalPos);
		buf.writeBoolean(disableCurrentCamera);
	}

	private <T> Optional<T> readOptional(PacketBuffer buf, Function<PacketBuffer, T> reader) {
		if (buf.readBoolean())
			return Optional.of(reader.apply(buf));
		else
			return Optional.empty();
	}

	private GlobalPos readGlobalPos(PacketBuffer buf) {
		RegistryKey<World> dimension = RegistryKey.create(Registry.DIMENSION_REGISTRY, buf.readResourceLocation());

		return GlobalPos.of(dimension, buf.readBlockPos());
	}

	private <T> void writeOptional(PacketBuffer buf, Optional<T> optional, BiConsumer<PacketBuffer, T> writer) {
		if (optional.isPresent()) {
			buf.writeBoolean(true);
			writer.accept(buf, optional.get());
		}
		else
			buf.writeBoolean(false);
	}

	private void writeGlobalPos(PacketBuffer buf, GlobalPos globalPos) {
		buf.writeResourceLocation(globalPos.dimension().location());
		buf.writeBlockPos(globalPos.pos());
	}

	public void handle(Supplier<NetworkEvent.Context> ctx) {
		PlayerEntity player = ctx.get().getSender();
		World level = player.level;
		GlobalPos currentCamera = this.currentCamera.orElse(null);
		TileEntity te = level.getBlockEntity(pos);

		if (!player.isSpectator() && te instanceof FrameBlockEntity) {
			FrameBlockEntity be = (FrameBlockEntity) te;

			if (be.isDisabled())
				return;

			boolean isOwner = be.isOwnedBy(player);

			if (isOwner)
				removedCamera.ifPresent(be::removeCamera);

			if (isOwner || be.isAllowed(player))
				be.switchCameras(currentCamera, player, requestedRenderDistance, disableCurrentCamera);
		}
	}
}
