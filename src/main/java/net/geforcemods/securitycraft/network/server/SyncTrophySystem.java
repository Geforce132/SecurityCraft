package net.geforcemods.securitycraft.network.server;

import net.geforcemods.securitycraft.blockentities.TrophySystemBlockEntity;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.network.NetworkEvent;

public class SyncTrophySystem {
	private BlockPos pos;
	private ResourceLocation projectileTypeLocation;
	private boolean allowed;

	public SyncTrophySystem() {}

	public SyncTrophySystem(BlockPos pos, EntityType<?> projectileType, boolean allowed) {
		this.pos = pos;
		this.projectileTypeLocation = Utils.getRegistryName(projectileType);
		this.allowed = allowed;
	}

	public SyncTrophySystem(FriendlyByteBuf buf) {
		pos = buf.readBlockPos();
		projectileTypeLocation = buf.readResourceLocation();
		allowed = buf.readBoolean();
	}

	public void encode(FriendlyByteBuf buf) {
		buf.writeBlockPos(pos);
		buf.writeResourceLocation(projectileTypeLocation);
		buf.writeBoolean(allowed);
	}

	public void handle(NetworkEvent.Context ctx) {
		EntityType<?> projectileType = BuiltInRegistries.ENTITY_TYPE.get(projectileTypeLocation);

		if (projectileType != null) {
			Level level = ctx.getSender().level();

			if (level.getBlockEntity(pos) instanceof TrophySystemBlockEntity be && be.isOwnedBy(ctx.getSender())) {
				BlockState state = level.getBlockState(pos);

				be.setFilter(projectileType, allowed);
				level.sendBlockUpdated(pos, state, state, 2);
			}
		}
	}
}
