package net.geforcemods.securitycraft.network.server;

import java.util.EnumMap;
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

	public SyncLaserSideConfig(BlockPos pos, EnumMap<Direction, Boolean> sideConfig) {
		this.pos = pos;
		this.sideConfig = LaserBlockBlockEntity.saveSideConfig(sideConfig);
	}

	public static void encode(SyncLaserSideConfig message, PacketBuffer buf) {
		buf.writeBlockPos(message.pos);
		buf.writeNbt(message.sideConfig);
	}

	public static SyncLaserSideConfig decode(PacketBuffer buf) {
		SyncLaserSideConfig message = new SyncLaserSideConfig();

		message.pos = buf.readBlockPos();
		message.sideConfig = buf.readNbt();
		return message;
	}

	public static void onMessage(SyncLaserSideConfig message, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			PlayerEntity player = ctx.get().getSender();
			World level = player.level;
			BlockPos pos = message.pos;
			TileEntity te = level.getBlockEntity(pos);

			if (te instanceof LaserBlockBlockEntity) {
				LaserBlockBlockEntity be = (LaserBlockBlockEntity) te;

				if (be.isOwnedBy(player)) {
					BlockState state = level.getBlockState(pos);

					be.applyNewSideConfig(LaserBlockBlockEntity.loadSideConfig(message.sideConfig), player);
					level.sendBlockUpdated(pos, state, state, 2);
				}
			}
		});

		ctx.get().setPacketHandled(true);
	}
}
