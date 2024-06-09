package net.geforcemods.securitycraft.network.server;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blockentities.BlockPocketManagerBlockEntity;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record AssembleBlockPocket(BlockPos pos, int size) implements CustomPacketPayload {
	public static final Type<AssembleBlockPocket> TYPE = new Type<>(SecurityCraft.resLoc("assemble_block_pocket"));
	//@formatter:off
	public static final StreamCodec<RegistryFriendlyByteBuf, AssembleBlockPocket> STREAM_CODEC = StreamCodec.composite(
			BlockPos.STREAM_CODEC, AssembleBlockPocket::pos,
			ByteBufCodecs.VAR_INT, AssembleBlockPocket::size,
			AssembleBlockPocket::new);
	//@formatter:on

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public void handle(IPayloadContext ctx) {
		Player player = ctx.player();

		if (player.level().getBlockEntity(pos) instanceof BlockPocketManagerBlockEntity be && be.isOwnedBy(player)) {
			MutableComponent feedback;

			be.setSize(size);
			feedback = be.autoAssembleMultiblock();
			be.setChanged();

			if (feedback != null)
				PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.BLOCK_POCKET_MANAGER.get().getDescriptionId()), feedback, ChatFormatting.DARK_AQUA);
		}
	}
}
