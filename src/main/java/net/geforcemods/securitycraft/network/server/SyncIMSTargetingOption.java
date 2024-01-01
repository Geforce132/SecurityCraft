package net.geforcemods.securitycraft.network.server;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blockentities.IMSBlockEntity;
import net.geforcemods.securitycraft.misc.TargetingMode;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

public class SyncIMSTargetingOption implements CustomPacketPayload {
	public static final ResourceLocation ID = new ResourceLocation(SecurityCraft.MODID, "sync_ims_targeting_option");
	private BlockPos pos;
	private TargetingMode targetingMode;

	public SyncIMSTargetingOption() {}

	public SyncIMSTargetingOption(BlockPos pos, TargetingMode targetingMode) {
		this.pos = pos;
		this.targetingMode = targetingMode;
	}

	public SyncIMSTargetingOption(FriendlyByteBuf buf) {
		pos = buf.readBlockPos();
		targetingMode = buf.readEnum(TargetingMode.class);
	}

	@Override
	public void write(FriendlyByteBuf buf) {
		buf.writeBlockPos(pos);
		buf.writeEnum(targetingMode);
	}

	@Override
	public ResourceLocation id() {
		return ID;
	}

	public void handle(PlayPayloadContext ctx) {
		Player player = ctx.player().orElseThrow();

		if (player.level().getBlockEntity(pos) instanceof IMSBlockEntity be && be.isOwnedBy(player))
			be.setTargetingMode(targetingMode);
	}
}
