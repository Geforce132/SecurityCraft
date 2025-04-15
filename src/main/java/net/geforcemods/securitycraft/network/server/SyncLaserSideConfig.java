package net.geforcemods.securitycraft.network.server;

import java.util.Map;
import java.util.function.Supplier;

import net.geforcemods.securitycraft.blockentities.LaserBlockBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;

public class SyncLaserSideConfig {
	private BlockPos pos;
	private CompoundNBT sideConfig;

	public SyncLaserSideConfig() {}

	public SyncLaserSideConfig(BlockPos pos, Map<Direction, Boolean> sideConfig) {
		this.pos = pos;
		this.sideConfig = LaserBlockBlockEntity.saveSideConfig(sideConfig);
	}

	public SyncLaserSideConfig(PacketBuffer buf) {
		pos = buf.readBlockPos();
		sideConfig = buf.readNbt();
	}

	public void encode(PacketBuffer buf) {
		buf.writeBlockPos(pos);
		buf.writeNbt(sideConfig);
	}

	public void handle(Supplier<NetworkEvent.Context> ctx) {
		PlayerEntity player = ctx.get().getSender();
		World level = player.level;
		TileEntity te = level.getBlockEntity(pos);

		if (!player.isSpectator() && te instanceof LaserBlockBlockEntity) {
			LaserBlockBlockEntity be = (LaserBlockBlockEntity) te;

			if (be.isOwnedBy(player)) {
				BlockState state = level.getBlockState(pos);

				be.applyNewSideConfig(LaserBlockBlockEntity.loadSideConfig(sideConfig), player);
				level.sendBlockUpdated(pos, state, state, 2);
			}
		}
	}
}
