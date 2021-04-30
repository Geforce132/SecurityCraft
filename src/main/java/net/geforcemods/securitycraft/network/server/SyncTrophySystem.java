package net.geforcemods.securitycraft.network.server;

import java.util.List;
import java.util.function.Supplier;

import org.apache.commons.lang3.tuple.Pair;

import net.geforcemods.securitycraft.tileentity.TrophySystemTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;

public class SyncTrophySystem {

	private BlockPos pos;
	private int projectileType;
	private boolean allowed;

	public SyncTrophySystem() {

	}

	public SyncTrophySystem(BlockPos pos, int projectileType, boolean allowed) {
		this.pos = pos;
		this.projectileType = projectileType;
		this.allowed = allowed;
	}

	public static void encode(SyncTrophySystem message, PacketBuffer buf) {
		buf.writeBlockPos(message.pos);
		buf.writeInt(message.projectileType);
		buf.writeBoolean(message.allowed);
	}

	public static SyncTrophySystem decode(PacketBuffer buf) {
		SyncTrophySystem message = new SyncTrophySystem();

		message.pos = buf.readBlockPos();
		message.projectileType = buf.readInt();
		message.allowed = buf.readBoolean();
		return message;
	}

	public static void onMessage(SyncTrophySystem message, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			World world = ctx.get().getSender().world;
			BlockPos pos = message.pos;
			int projectileType = message.projectileType;
			boolean allowed = message.allowed;
			TrophySystemTileEntity te = (TrophySystemTileEntity) world.getTileEntity(pos);

			if(te != null) {
				List<Pair<EntityType<?>, Boolean>> projectileFilter = te.projectileFilter;
				BlockState state = world.getBlockState(pos);

				projectileFilter.set(projectileType, Pair.of(projectileFilter.get(projectileType).getLeft(), allowed));
				world.notifyBlockUpdate(pos, state, state, 2);
			}
		});

		ctx.get().setPacketHandled(true);
	}
}
