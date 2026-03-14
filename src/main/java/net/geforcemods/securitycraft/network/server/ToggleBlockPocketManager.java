package net.geforcemods.securitycraft.network.server;

import java.util.function.Function;
import java.util.function.Supplier;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.blockentities.BlockPocketManagerBlockEntity;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.network.NetworkEvent;

public class ToggleBlockPocketManager {
	private BlockPos pos;
	private int size;
	private Action action;

	public ToggleBlockPocketManager() {}

	public ToggleBlockPocketManager(BlockPocketManagerBlockEntity be, Action action) {
		pos = be.getBlockPos();
		size = be.getSize();
		this.action = action;
	}

	public ToggleBlockPocketManager(FriendlyByteBuf buf) {
		pos = BlockPos.of(buf.readLong());
		size = buf.readInt();
		action = buf.readEnum(Action.class);
	}

	public void encode(FriendlyByteBuf buf) {
		buf.writeLong(pos.asLong());
		buf.writeInt(size);
		buf.writeEnum(action);
	}

	public void handle(Supplier<NetworkEvent.Context> ctx) {
		Player player = ctx.get().getSender();
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
