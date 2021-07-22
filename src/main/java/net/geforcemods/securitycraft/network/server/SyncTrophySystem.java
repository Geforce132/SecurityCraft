package net.geforcemods.securitycraft.network.server;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.tileentity.TrophySystemTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.registries.ForgeRegistries;

public class SyncTrophySystem {

	private BlockPos pos;
	private ResourceLocation projectileType;
	private boolean allowed;

	public SyncTrophySystem() {

	}

	public SyncTrophySystem(BlockPos pos, EntityType<?> projectileType, boolean allowed) {
		this.pos = pos;
		this.projectileType = projectileType.getRegistryName();
		this.allowed = allowed;
	}

	public static void encode(SyncTrophySystem message, PacketBuffer buf) {
		buf.writeBlockPos(message.pos);
		buf.writeResourceLocation(message.projectileType);
		buf.writeBoolean(message.allowed);
	}

	public static SyncTrophySystem decode(PacketBuffer buf) {
		SyncTrophySystem message = new SyncTrophySystem();

		message.pos = buf.readBlockPos();
		message.projectileType = buf.readResourceLocation();
		message.allowed = buf.readBoolean();
		return message;
	}

	public static void onMessage(SyncTrophySystem message, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			EntityType<?> projectileType = ForgeRegistries.ENTITIES.getValue(message.projectileType);

			if(projectileType != null)
			{
				World world = ctx.get().getSender().level;
				BlockPos pos = message.pos;
				boolean allowed = message.allowed;
				TileEntity te = world.getBlockEntity(pos);

				if(te instanceof TrophySystemTileEntity && ((TrophySystemTileEntity)te).getOwner().isOwner(ctx.get().getSender())) {
					BlockState state = world.getBlockState(pos);

					((TrophySystemTileEntity)te).setFilter(projectileType, allowed);
					world.sendBlockUpdated(pos, state, state, 2);
				}
			}
		});

		ctx.get().setPacketHandled(true);
	}
}
