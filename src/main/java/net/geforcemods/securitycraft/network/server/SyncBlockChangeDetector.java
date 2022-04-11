package net.geforcemods.securitycraft.network.server;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.blockentities.BlockChangeDetectorBlockEntity;
import net.geforcemods.securitycraft.blockentities.BlockChangeDetectorBlockEntity.DetectionMode;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;

public class SyncBlockChangeDetector {
	private BlockPos pos;
	private DetectionMode mode;

	public SyncBlockChangeDetector() {}

	public SyncBlockChangeDetector(BlockPos pos, DetectionMode mode) {
		this.pos = pos;
		this.mode = mode;
	}

	public static void encode(SyncBlockChangeDetector message, PacketBuffer buf) {
		buf.writeBlockPos(message.pos);
		buf.writeEnum(message.mode);
	}

	public static SyncBlockChangeDetector decode(PacketBuffer buf) {
		SyncBlockChangeDetector message = new SyncBlockChangeDetector();

		message.pos = buf.readBlockPos();
		message.mode = buf.readEnum(DetectionMode.class);
		return message;
	}

	public static void onMessage(SyncBlockChangeDetector message, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			ServerPlayerEntity player = ctx.get().getSender();
			World level = player.level;
			BlockPos pos = message.pos;
			TileEntity tile = level.getBlockEntity(message.pos);

			if (tile instanceof BlockChangeDetectorBlockEntity) {
				BlockChangeDetectorBlockEntity be = (BlockChangeDetectorBlockEntity) tile;

				if (be.getOwner().isOwner(player)) {
					BlockState state = level.getBlockState(pos);

					be.setMode(message.mode);
					level.sendBlockUpdated(pos, state, state, 2);
				}
			}
		});

		ctx.get().setPacketHandled(true);
	}
}
