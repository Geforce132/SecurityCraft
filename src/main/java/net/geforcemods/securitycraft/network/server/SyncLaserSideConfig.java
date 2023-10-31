package net.geforcemods.securitycraft.network.server;

import java.util.Map;

import net.geforcemods.securitycraft.blockentities.LaserBlockBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.network.NetworkEvent;

public class SyncLaserSideConfig {
	private BlockPos pos;
	private CompoundTag sideConfig;

	public SyncLaserSideConfig() {}

	public SyncLaserSideConfig(BlockPos pos, Map<Direction, Boolean> sideConfig) {
		this.pos = pos;
		this.sideConfig = LaserBlockBlockEntity.saveSideConfig(sideConfig);
	}

	public SyncLaserSideConfig(FriendlyByteBuf buf) {
		pos = buf.readBlockPos();
		sideConfig = buf.readNbt();
	}

	public void encode(FriendlyByteBuf buf) {
		buf.writeBlockPos(pos);
		buf.writeNbt(sideConfig);
	}

	public void handle(NetworkEvent.Context ctx) {
		Player player = ctx.getSender();
		Level level = player.level();

		if (level.getBlockEntity(pos) instanceof LaserBlockBlockEntity be && be.isOwnedBy(player)) {
			BlockState state = level.getBlockState(pos);

			be.applyNewSideConfig(LaserBlockBlockEntity.loadSideConfig(sideConfig), player);
			level.sendBlockUpdated(pos, state, state, 2);
		}
	}
}
