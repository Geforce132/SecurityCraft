package net.geforcemods.securitycraft.network.server;

import java.util.EnumMap;
import java.util.function.Supplier;

import net.geforcemods.securitycraft.blockentities.LaserBlockBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.network.NetworkEvent;

public class SyncLaserSideConfig {
	private BlockPos pos;
	private CompoundTag sideConfig;

	public SyncLaserSideConfig() {}

	public SyncLaserSideConfig(BlockPos pos, EnumMap<Direction, Boolean> sideConfig) {
		this.pos = pos;
		this.sideConfig = LaserBlockBlockEntity.saveSideConfig(sideConfig);
	}

	public static void encode(SyncLaserSideConfig message, FriendlyByteBuf buf) {
		buf.writeBlockPos(message.pos);
		buf.writeNbt(message.sideConfig);
	}

	public static SyncLaserSideConfig decode(FriendlyByteBuf buf) {
		SyncLaserSideConfig message = new SyncLaserSideConfig();

		message.pos = buf.readBlockPos();
		message.sideConfig = buf.readNbt();
		return message;
	}

	public static void onMessage(SyncLaserSideConfig message, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			Level level = ctx.get().getSender().level;
			BlockPos pos = message.pos;

			if (level.getBlockEntity(pos) instanceof LaserBlockBlockEntity be && be.isOwnedBy(ctx.get().getSender())) {
				BlockState state = level.getBlockState(pos);

				be.applySideConfig(LaserBlockBlockEntity.loadSideConfig(message.sideConfig));
				level.sendBlockUpdated(pos, state, state, 2);
			}
		});

		ctx.get().setPacketHandled(true);
	}
}
