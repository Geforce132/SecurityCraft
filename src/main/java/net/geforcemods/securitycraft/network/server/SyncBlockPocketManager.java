package net.geforcemods.securitycraft.network.server;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blockentities.BlockPocketManagerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SyncBlockPocketManager(BlockPos pos, int size, boolean showOutline, int autoBuildOffset, int color) implements CustomPacketPayload {

	public static final Type<SyncBlockPocketManager> TYPE = new Type<>(new ResourceLocation(SecurityCraft.MODID, "sync_block_pocket_manager"));
	//@formatter:off
	public static final StreamCodec<RegistryFriendlyByteBuf, SyncBlockPocketManager> STREAM_CODEC = StreamCodec.composite(
			BlockPos.STREAM_CODEC, SyncBlockPocketManager::pos,
			ByteBufCodecs.VAR_INT, SyncBlockPocketManager::size,
			ByteBufCodecs.BOOL, SyncBlockPocketManager::showOutline,
			ByteBufCodecs.VAR_INT, SyncBlockPocketManager::autoBuildOffset,
			ByteBufCodecs.VAR_INT, SyncBlockPocketManager::color,
			SyncBlockPocketManager::new);
	//@formatter:on
	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public void handle(IPayloadContext ctx) {
		Player player = ctx.player();
		Level level = player.level();

		if (level.isLoaded(pos) && level.getBlockEntity(pos) instanceof BlockPocketManagerBlockEntity bpm && bpm.isOwnedBy(player)) {
			BlockState state = level.getBlockState(pos);

			bpm.setSize(size);
			bpm.setShowOutline(showOutline);
			bpm.setAutoBuildOffset(autoBuildOffset);
			bpm.setColor(color);
			bpm.setChanged();
			level.sendBlockUpdated(pos, state, state, 2);
		}
	}
}
