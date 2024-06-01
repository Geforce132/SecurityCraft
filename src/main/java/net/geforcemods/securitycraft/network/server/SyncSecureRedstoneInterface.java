package net.geforcemods.securitycraft.network.server;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blockentities.SecureRedstoneInterfaceBlockEntity;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SyncSecureRedstoneInterface(BlockPos pos, boolean sender) implements CustomPacketPayload {
	public static final Type<SyncSecureRedstoneInterface> TYPE = new Type<>(new ResourceLocation(SecurityCraft.MODID, "sync_secure_redstone_interface"));
	//@formatter:off
	public static final StreamCodec<RegistryFriendlyByteBuf, SyncSecureRedstoneInterface> STREAM_CODEC = StreamCodec.composite(
			BlockPos.STREAM_CODEC, SyncSecureRedstoneInterface::pos,
			ByteBufCodecs.BOOL, SyncSecureRedstoneInterface::sender,
			SyncSecureRedstoneInterface::new);
	//@formatter:on

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public void handle(IPayloadContext ctx) {
		Player player = ctx.player();
		Level level = player.level();

		if (level.getBlockEntity(pos) instanceof SecureRedstoneInterfaceBlockEntity be && be.isOwnedBy(player) && sender != be.isSender()) {
			be.setSender(sender);
			BlockUtils.updateIndirectNeighbors(level, be.getBlockPos(), be.getBlockState().getBlock());
		}
	}
}
