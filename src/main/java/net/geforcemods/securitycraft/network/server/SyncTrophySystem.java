package net.geforcemods.securitycraft.network.server;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.blockentities.TrophySystemBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fmllegacy.network.NetworkEvent;
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

	public static void encode(SyncTrophySystem message, FriendlyByteBuf buf) {
		buf.writeBlockPos(message.pos);
		buf.writeResourceLocation(message.projectileType);
		buf.writeBoolean(message.allowed);
	}

	public static SyncTrophySystem decode(FriendlyByteBuf buf) {
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
				Level world = ctx.get().getSender().level;
				BlockPos pos = message.pos;
				boolean allowed = message.allowed;

				if(world.getBlockEntity(pos) instanceof TrophySystemBlockEntity te && te.getOwner().isOwner(ctx.get().getSender())) {
					BlockState state = world.getBlockState(pos);

					te.setFilter(projectileType, allowed);
					world.sendBlockUpdated(pos, state, state, 2);
				}
			}
		});

		ctx.get().setPacketHandled(true);
	}
}
