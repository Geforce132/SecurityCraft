package net.geforcemods.securitycraft.network.client;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blockentities.TrophySystemBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SetTrophySystemTarget(BlockPos pos, int targetID) implements CustomPacketPayload {
	public static final Type<SetTrophySystemTarget> TYPE = new Type<>(new ResourceLocation(SecurityCraft.MODID, "set_trophy_system_target"));
	//@formatter:off
	public static final StreamCodec<RegistryFriendlyByteBuf, SetTrophySystemTarget> STREAM_CODEC = StreamCodec.composite(
			BlockPos.STREAM_CODEC, SetTrophySystemTarget::pos,
			ByteBufCodecs.VAR_INT, SetTrophySystemTarget::targetID,
			SetTrophySystemTarget::new);
	//@formatter:on

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public void handle(IPayloadContext ctx) {
		BlockEntity blockEntity = ctx.player().level().getBlockEntity(pos);

		if (blockEntity instanceof TrophySystemBlockEntity be && Minecraft.getInstance().level.getEntity(targetID) instanceof Projectile projectile)
			be.setTarget(projectile);
	}
}
