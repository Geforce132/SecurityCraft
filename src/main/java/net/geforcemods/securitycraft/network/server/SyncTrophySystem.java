package net.geforcemods.securitycraft.network.server;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blockentities.TrophySystemBlockEntity;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

public class SyncTrophySystem implements CustomPacketPayload {
	public static final ResourceLocation ID = new ResourceLocation(SecurityCraft.MODID, "sync_trophy_system");
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

	@Override
	public void write(FriendlyByteBuf buf) {
		buf.writeBlockPos(pos);
		buf.writeResourceLocation(projectileTypeLocation);
		buf.writeBoolean(allowed);
	}

	@Override
	public ResourceLocation id() {
		return ID;
	}

	public void handle(PlayPayloadContext ctx) {
		EntityType<?> projectileType = BuiltInRegistries.ENTITY_TYPE.get(projectileTypeLocation);

		Player player = ctx.player().orElseThrow();
		Level level = player.level();

		if (level.getBlockEntity(pos) instanceof TrophySystemBlockEntity be && be.isOwnedBy(player)) {
			BlockState state = level.getBlockState(pos);

			be.setFilter(projectileType, allowed);
			level.sendBlockUpdated(pos, state, state, 2);
		}
	}
}
