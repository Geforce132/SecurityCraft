package net.geforcemods.securitycraft.network.server;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.blockentities.IMSBlockEntity;
import net.geforcemods.securitycraft.blockentities.IMSBlockEntity.IMSTargetingMode;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

public class SyncIMSTargetingOption {
	private BlockPos pos;
	private IMSTargetingMode targetingMode;

	public SyncIMSTargetingOption() {}

	public SyncIMSTargetingOption(BlockPos pos, IMSTargetingMode targetingMode) {
		this.pos = pos;
		this.targetingMode = targetingMode;
	}

	public SyncIMSTargetingOption(FriendlyByteBuf buf) {
		pos = buf.readBlockPos();
		targetingMode = buf.readEnum(IMSTargetingMode.class);
	}

	public void encode(FriendlyByteBuf buf) {
		buf.writeBlockPos(pos);
		buf.writeEnum(targetingMode);
	}

	public void handle(Supplier<NetworkEvent.Context> ctx) {
		Player player = ctx.get().getSender();

		if (player.level().getBlockEntity(pos) instanceof IMSBlockEntity be && be.isOwnedBy(player))
			be.setTargetingMode(targetingMode);
	}
}
