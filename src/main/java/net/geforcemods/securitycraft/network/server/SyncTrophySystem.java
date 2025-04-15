package net.geforcemods.securitycraft.network.server;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.blockentities.TrophySystemBlockEntity;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.registries.ForgeRegistries;

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

	public void handle(Supplier<NetworkEvent.Context> ctx) {
		EntityType<?> projectileType = ForgeRegistries.ENTITY_TYPES.getValue(projectileTypeLocation);

		if (projectileType != null) {
			Player player = ctx.get().getSender();
			Level level = player.level;

			if (!player.isSpectator() && level.getBlockEntity(pos) instanceof TrophySystemBlockEntity be && be.isOwnedBy(player)) {
				BlockState state = level.getBlockState(pos);

				be.setFilter(projectileType, allowed);
				level.sendBlockUpdated(pos, state, state, 2);
			}
		}
	}
}
