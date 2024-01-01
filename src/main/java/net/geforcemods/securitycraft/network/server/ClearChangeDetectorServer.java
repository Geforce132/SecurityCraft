package net.geforcemods.securitycraft.network.server;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blockentities.BlockChangeDetectorBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

public class ClearChangeDetectorServer implements CustomPacketPayload {
	public static final ResourceLocation ID = new ResourceLocation(SecurityCraft.MODID, "clear_change_detector_server");
	private BlockPos pos;

	public ClearChangeDetectorServer() {}

	public ClearChangeDetectorServer(BlockPos pos) {
		this.pos = pos;
	}

	public ClearChangeDetectorServer(FriendlyByteBuf buf) {
		pos = buf.readBlockPos();
	}

	@Override
	public void write(FriendlyByteBuf buf) {
		buf.writeBlockPos(pos);
	}

	@Override
	public ResourceLocation id() {
		return ID;
	}

	public void handle(PlayPayloadContext ctx) {
		Player player = ctx.player().orElseThrow();

		if (player.level().getBlockEntity(pos) instanceof BlockChangeDetectorBlockEntity be && be.isOwnedBy(player)) {
			be.getEntries().clear();
			be.setChanged();
			be.getLevel().sendBlockUpdated(be.getBlockPos(), be.getBlockState(), be.getBlockState(), 2);
		}
	}
}
