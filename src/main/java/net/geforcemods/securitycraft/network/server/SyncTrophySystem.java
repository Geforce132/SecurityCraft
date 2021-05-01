package net.geforcemods.securitycraft.network.server;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.tileentity.TrophySystemTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;

public class SyncTrophySystem {

	private BlockPos pos;
	private int projectileIndex;
	private boolean allowed;

	public SyncTrophySystem() {

	}

	public SyncTrophySystem(BlockPos pos, int projectileIndex, boolean allowed) {
		this.pos = pos;
		this.projectileIndex = projectileIndex;
		this.allowed = allowed;
	}

	public static void encode(SyncTrophySystem message, PacketBuffer buf) {
		buf.writeBlockPos(message.pos);
		buf.writeInt(message.projectileIndex);
		buf.writeBoolean(message.allowed);
	}

	public static SyncTrophySystem decode(PacketBuffer buf) {
		SyncTrophySystem message = new SyncTrophySystem();

		message.pos = buf.readBlockPos();
		message.projectileIndex = buf.readInt();
		message.allowed = buf.readBoolean();
		return message;
	}

	public static void onMessage(SyncTrophySystem message, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			World world = ctx.get().getSender().world;
			BlockPos pos = message.pos;
			BlockState state = world.getBlockState(pos);
			int projectileIndex = message.projectileIndex;
			boolean allowed = message.allowed;
			TileEntity te = world.getTileEntity(pos);

			if(te instanceof TrophySystemTileEntity && ((TrophySystemTileEntity)te).getOwner().isOwner(ctx.get().getSender())) {
				((TrophySystemTileEntity)te).setFilter(projectileIndex, allowed);
				world.notifyBlockUpdate(pos, state, state, 2);
			}
		});

		ctx.get().setPacketHandled(true);
	}
}
