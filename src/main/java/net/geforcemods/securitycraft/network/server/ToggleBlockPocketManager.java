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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

public record ToggleBlockPocketManager(BlockPos pos, int size, boolean enabling) implements CustomPacketPayload {

	public static final Type<ToggleBlockPocketManager> TYPE = new Type<>(new ResourceLocation(SecurityCraft.MODID, "toggle_block_pocket_manager"));
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

	public void handle(PlayPayloadContext ctx) {
		Player player = ctx.player().orElseThrow();

		if (player.level().getBlockEntity(pos) instanceof BlockPocketManagerBlockEntity be && be.isOwnedBy(player)) {
			MutableComponent feedback;

			be.setSize(size);

			if (enabling)
				feedback = be.enableMultiblock();
			else
				feedback = be.disableMultiblock();

			if (feedback != null) {
				if (enabling && !be.isEnabled())
					ctx.replyHandler().send(new BlockPocketManagerFailedActivation(pos));

				PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.BLOCK_POCKET_MANAGER.get().getDescriptionId()), feedback, ChatFormatting.DARK_AQUA, false);
			}

			be.setChanged();
		}
	}
}
