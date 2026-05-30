package net.geforcemods.securitycraft.network.server;

import java.util.function.Function;

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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ToggleBlockPocketManager(BlockPos pos, int size, Action action) implements CustomPacketPayload {
	public static final Type<ToggleBlockPocketManager> TYPE = new Type<>(SecurityCraft.resLoc("toggle_block_pocket_manager"));
	//@formatter:off
	public static final StreamCodec<RegistryFriendlyByteBuf, ToggleBlockPocketManager> STREAM_CODEC = StreamCodec.composite(
			BlockPos.STREAM_CODEC, ToggleBlockPocketManager::pos,
			ByteBufCodecs.VAR_INT, ToggleBlockPocketManager::size,
			NeoForgeStreamCodecs.enumCodec(Action.class), ToggleBlockPocketManager::action,
			ToggleBlockPocketManager::new);
	//@formatter:on

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public void handle(IPayloadContext ctx) {
		Player player = ctx.player();
		Level level = player.level();

		if (!player.isSpectator() && level.getBlockEntity(pos) instanceof BlockPocketManagerBlockEntity be && be.isOwnedBy(player)) {
			BlockState state = be.getBlockState();
			MutableComponent feedback;

			be.setSize(size);
			feedback = action.act(be);
			be.setChanged();
			level.sendBlockUpdated(pos, state, state, Block.UPDATE_ALL);

			if (feedback != null)
				PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.BLOCK_POCKET_MANAGER.get().getDescriptionId()), feedback, ChatFormatting.DARK_AQUA);
		}
	}

	public enum Action {
		ENABLE(BlockPocketManagerBlockEntity::enableMultiblock),
		DISABLE(BlockPocketManagerBlockEntity::disableMultiblock),
		ASSEMBLE(BlockPocketManagerBlockEntity::autoAssembleMultiblock),
		DISASSEMBLE(BlockPocketManagerBlockEntity::disassembleMultiblock);

		private final Function<BlockPocketManagerBlockEntity, MutableComponent> action;

		Action(Function<BlockPocketManagerBlockEntity, MutableComponent> action) {
			this.action = action;
		}

		public MutableComponent act(BlockPocketManagerBlockEntity be) {
			return action.apply(be);
		}
	}
}
