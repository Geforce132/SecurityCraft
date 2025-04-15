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
	private boolean showHighlights;
	private int color;

	public SyncBlockChangeDetector() {}

	public SyncBlockChangeDetector(BlockPos pos, DetectionMode mode, boolean showHighlights, int color) {
		this.pos = pos;
		this.mode = mode;
		this.showHighlights = showHighlights;
		this.color = color;
	}

	public SyncBlockChangeDetector(PacketBuffer buf) {
		pos = buf.readBlockPos();
		mode = buf.readEnum(DetectionMode.class);
		showHighlights = buf.readBoolean();
		color = buf.readInt();
	}

	public void encode(PacketBuffer buf) {
		buf.writeBlockPos(pos);
		buf.writeEnum(mode);
		buf.writeBoolean(showHighlights);
		buf.writeInt(color);
	}

	public void handle(Supplier<NetworkEvent.Context> ctx) {
		ServerPlayerEntity player = ctx.get().getSender();
		World level = player.level;
		TileEntity te = level.getBlockEntity(pos);

		if (!player.isSpectator() && te instanceof BlockChangeDetectorBlockEntity) {
			BlockChangeDetectorBlockEntity be = (BlockChangeDetectorBlockEntity) te;

			if (be.isOwnedBy(player)) {
				BlockState state = level.getBlockState(pos);

				be.setMode(mode);
				be.showHighlights(showHighlights);
				be.setColor(color);
				be.setChanged();
				level.sendBlockUpdated(pos, state, state, 2);
			}
		}
	}
}
