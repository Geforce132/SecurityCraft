package net.geforcemods.securitycraft.network.server;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.blockentities.TrophySystemBlockEntity;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.registries.ForgeRegistries;

public class SyncTrophySystem {
	private BlockPos pos;
	private ResourceLocation projectileType;
	private boolean allowed;

	public SyncTrophySystem() {}

	public SyncTrophySystem(BlockPos pos, EntityType<?> projectileType, boolean allowed) {
		this.pos = pos;
		this.projectileType = Utils.getRegistryName(projectileType);
		this.allowed = allowed;
	}

	public SyncTrophySystem(FriendlyByteBuf buf) {
		pos = buf.readBlockPos();
		projectileType = buf.readResourceLocation();
		allowed = buf.readBoolean();
	}

	public void encode(FriendlyByteBuf buf) {
		buf.writeBlockPos(pos);
		buf.writeResourceLocation(projectileType);
		buf.writeBoolean(allowed);
	}

	public void handle(Supplier<NetworkEvent.Context> ctx) {
		EntityType<?> projectileType = ForgeRegistries.ENTITY_TYPES.getValue(this.projectileType);

		if (projectileType != null) {
			Level level = ctx.get().getSender().level();

			if (level.getBlockEntity(pos) instanceof TrophySystemBlockEntity be && be.isOwnedBy(ctx.get().getSender())) {
				BlockState state = level.getBlockState(pos);

				be.setFilter(projectileType, allowed);
				level.sendBlockUpdated(pos, state, state, 2);
			}
		}
	}
}
