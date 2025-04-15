package net.geforcemods.securitycraft.network.server;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.blockentities.RiftStabilizerBlockEntity;
import net.geforcemods.securitycraft.blockentities.RiftStabilizerBlockEntity.TeleportationType;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class SyncRiftStabilizer {
	private BlockPos pos;
	private TeleportationType teleportationType;
	private boolean allowed;

	public SyncRiftStabilizer() {}

	public SyncRiftStabilizer(BlockPos pos, TeleportationType teleportationType, boolean allowed) {
		this.pos = pos;
		this.teleportationType = teleportationType;
		this.allowed = allowed;
	}

	public SyncRiftStabilizer(PacketBuffer buf) {
		pos = buf.readBlockPos();
		teleportationType = buf.readEnum(TeleportationType.class);
		allowed = buf.readBoolean();
	}

	public void encode(PacketBuffer buf) {
		buf.writeBlockPos(pos);
		buf.writeEnum(teleportationType);
		buf.writeBoolean(allowed);
	}

	public void handle(Supplier<Context> ctx) {
		if (teleportationType != null) {
			PlayerEntity player = ctx.get().getSender();
			World level = player.level;
			TileEntity te = level.getBlockEntity(pos);

			if (!player.isSpectator() && te instanceof RiftStabilizerBlockEntity) {
				RiftStabilizerBlockEntity be = (RiftStabilizerBlockEntity) te;

				if (be.isOwnedBy(player)) {
					BlockState state = level.getBlockState(pos);

					be.setFilter(teleportationType, allowed);
					level.sendBlockUpdated(pos, state, state, 2);
				}
			}
		}
	}
}
