package net.geforcemods.securitycraft.network.server;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blockentities.BlockPocketManagerBlockEntity;
import net.geforcemods.securitycraft.network.client.BlockPocketManagerFailedActivation;
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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ToggleBlockPocketManager(BlockPos pos, int size, boolean enabling) implements CustomPacketPayload {

	public static final Type<ToggleBlockPocketManager> TYPE = new Type<>(SecurityCraft.resLoc("toggle_block_pocket_manager"));
	//@formatter:off
	public static final StreamCodec<RegistryFriendlyByteBuf, ToggleBlockPocketManager> STREAM_CODEC = StreamCodec.composite(
			BlockPos.STREAM_CODEC, ToggleBlockPocketManager::pos,
			ByteBufCodecs.VAR_INT, ToggleBlockPocketManager::size,
			ByteBufCodecs.BOOL, ToggleBlockPocketManager::enabling,
			ToggleBlockPocketManager::new);
	//@formatter:on
	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public void handle(IPayloadContext ctx) {
		Player player = ctx.player();
		Level level = player.level();

		if (!player.isSpectator() && player.level().getBlockEntity(pos) instanceof BlockPocketManagerBlockEntity be && be.isOwnedBy(player)) {
			BlockState state = be.getBlockState();
			MutableComponent feedback;

			be.setSize(size);

			if (enabling)
				feedback = be.enableMultiblock();
			else
				feedback = be.disableMultiblock();

			if (feedback != null) {
				if (enabling && !be.isEnabled())
					ctx.reply(new BlockPocketManagerFailedActivation(pos));

				PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.BLOCK_POCKET_MANAGER.get().getDescriptionId()), feedback, ChatFormatting.DARK_AQUA, false);
			}

			be.setChanged();
			level.sendBlockUpdated(pos, state, state, 2);
		}
	}
}
