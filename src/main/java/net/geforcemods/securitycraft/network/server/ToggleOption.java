package net.geforcemods.securitycraft.network.server;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.ICustomizable;
import net.geforcemods.securitycraft.api.IOwnable;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ToggleOption(BlockPos pos, int optionId) implements CustomPacketPayload {
	public static final Type<ToggleOption> TYPE = new Type<>(new ResourceLocation(SecurityCraft.MODID, "toggle_option"));
	//@formatter:off
	public static final StreamCodec<RegistryFriendlyByteBuf, ToggleOption> STREAM_CODEC = StreamCodec.composite(
			BlockPos.STREAM_CODEC, ToggleOption::pos,
			ByteBufCodecs.VAR_INT, ToggleOption::optionId,
			ToggleOption::new);
	//@formatter:on

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public void handle(IPayloadContext ctx) {
		Player player = ctx.player();
		BlockEntity be = player.level().getBlockEntity(pos);

		if (be instanceof ICustomizable customizable && (!(be instanceof IOwnable ownable) || ownable.isOwnedBy(player))) {
			customizable.customOptions()[optionId].toggle();
			customizable.onOptionChanged(customizable.customOptions()[optionId]);
			player.level().sendBlockUpdated(pos, be.getBlockState(), be.getBlockState(), 3);
		}
	}
}
