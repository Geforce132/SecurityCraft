package net.geforcemods.securitycraft.network.client;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blockentities.TrophySystemBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

public class SetTrophySystemTarget implements CustomPacketPayload {
	public static final ResourceLocation ID = new ResourceLocation(SecurityCraft.MODID, "set_trophy_system_target");
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

	@Override
	public void write(FriendlyByteBuf buf) {
		buf.writeBlockPos(trophyPos);
		buf.writeInt(targetID);
	}

	@Override
	public ResourceLocation id() {
		return ID;
	}

	public void handle(PlayPayloadContext ctx) {
		BlockEntity blockEntity = Minecraft.getInstance().level.getBlockEntity(trophyPos);

		if (blockEntity instanceof TrophySystemBlockEntity be && Minecraft.getInstance().level.getEntity(targetID) instanceof Projectile projectile)
			be.setTarget(projectile);
	}
}
