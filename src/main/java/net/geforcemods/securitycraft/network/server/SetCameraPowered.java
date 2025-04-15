package net.geforcemods.securitycraft.network.server;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.blocks.SecurityCameraBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.network.NetworkEvent;

public class SetCameraPowered {
	private BlockPos pos;
	private boolean powered;

	public SetCameraPowered() {}

	public SetCameraPowered(BlockPos pos, boolean powered) {
		this.pos = pos;
		this.powered = powered;
	}

	public SetCameraPowered(FriendlyByteBuf buf) {
		pos = buf.readBlockPos();
		powered = buf.readBoolean();
	}

	public void encode(FriendlyByteBuf buf) {
		buf.writeBlockPos(pos);
		buf.writeBoolean(powered);
	}

	public void handle(Supplier<NetworkEvent.Context> ctx) {
		Player player = ctx.get().getSender();
		Level level = player.level();
		BlockEntity be = level.getBlockEntity(pos);

		if (!player.isSpectator() && (be instanceof IOwnable ownable && ownable.isOwnedBy(player)) || (be instanceof IModuleInventory moduleInv && moduleInv.isAllowed(player))) {
			BlockState state = level.getBlockState(pos);

			level.setBlockAndUpdate(pos, state.setValue(SecurityCameraBlock.POWERED, powered));
			level.updateNeighborsAt(pos.relative(state.getValue(SecurityCameraBlock.FACING), -1), state.getBlock());
		}
	}
}
