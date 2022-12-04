package net.geforcemods.securitycraft.network.server;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.blockentities.BlockChangeDetectorBlockEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

public class ClearChangeDetectorServer {
	private BlockPos pos;

	public ClearChangeDetectorServer() {}

	public ClearChangeDetectorServer(BlockPos pos) {
		this.pos = pos;
	}

	public static void encode(ClearChangeDetectorServer message, PacketBuffer buf) {
		buf.writeBlockPos(message.pos);
	}

	public static ClearChangeDetectorServer decode(PacketBuffer buf) {
		ClearChangeDetectorServer message = new ClearChangeDetectorServer();

		message.pos = buf.readBlockPos();
		return message;
	}

	public static void onMessage(ClearChangeDetectorServer message, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			ServerPlayerEntity player = ctx.get().getSender();
			TileEntity tile = player.level.getBlockEntity(message.pos);

			if (tile instanceof BlockChangeDetectorBlockEntity) {
				BlockChangeDetectorBlockEntity be = (BlockChangeDetectorBlockEntity) tile;

				if (be.isOwnedBy(player)) {
					be.getEntries().clear();
					be.setChanged();
					be.getLevel().sendBlockUpdated(be.getBlockPos(), be.getBlockState(), be.getBlockState(), 2);
				}
			}
		});

		ctx.get().setPacketHandled(true);
	}
}
