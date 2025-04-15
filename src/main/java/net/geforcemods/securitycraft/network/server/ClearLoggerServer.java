package net.geforcemods.securitycraft.network.server;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.blockentities.UsernameLoggerBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

public class ClearLoggerServer {
	private BlockPos pos;

	public ClearLoggerServer() {}

	public ClearLoggerServer(BlockPos pos) {
		this.pos = pos;
	}

	public ClearLoggerServer(PacketBuffer buf) {
		pos = buf.readBlockPos();
	}

	public void encode(PacketBuffer buf) {
		buf.writeBlockPos(pos);
	}

	public void handle(Supplier<NetworkEvent.Context> ctx) {
		PlayerEntity player = ctx.get().getSender();
		TileEntity te = player.level.getBlockEntity(pos);

		if (!player.isSpectator() && te instanceof UsernameLoggerBlockEntity) {
			UsernameLoggerBlockEntity be = (UsernameLoggerBlockEntity) te;

			if (be.isOwnedBy(player)) {
				be.setPlayers(new String[100]);
				be.getLevel().sendBlockUpdated(be.getBlockPos(), be.getBlockState(), be.getBlockState(), 2);
			}
		}
	}
}
