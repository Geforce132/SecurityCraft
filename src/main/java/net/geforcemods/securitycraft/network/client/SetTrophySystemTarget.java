package net.geforcemods.securitycraft.network.client;

import net.geforcemods.securitycraft.blockentities.TrophySystemBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.NetworkEvent;

public class SetTrophySystemTarget {
	private BlockPos trophyPos;
	private int targetID;

	public SetTrophySystemTarget() {}

	public SetTrophySystemTarget(BlockPos trophyPos, int targetID) {
		this.trophyPos = trophyPos;
		this.targetID = targetID;
	}

	public SetTrophySystemTarget(FriendlyByteBuf buf) {
		trophyPos = buf.readBlockPos();
		targetID = buf.readInt();
	}

	public void encode(FriendlyByteBuf buf) {
		buf.writeBlockPos(trophyPos);
		buf.writeInt(targetID);
	}

	public void handle(NetworkEvent.Context ctx) {
		BlockEntity blockEntity = Minecraft.getInstance().level.getBlockEntity(trophyPos);

		if (blockEntity instanceof TrophySystemBlockEntity be && Minecraft.getInstance().level.getEntity(targetID) instanceof Projectile projectile)
			be.setTarget(projectile);
	}
}
