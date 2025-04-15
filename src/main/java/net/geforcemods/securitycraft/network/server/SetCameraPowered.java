package net.geforcemods.securitycraft.network.server;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.blocks.SecurityCameraBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;

public class SetCameraPowered {
	private BlockPos pos;
	private boolean powered;

	public SetCameraPowered() {}

	public SetCameraPowered(BlockPos pos, boolean powered) {
		this.pos = pos;
		this.powered = powered;
	}

	public SetCameraPowered(PacketBuffer buf) {
		pos = buf.readBlockPos();
		powered = buf.readBoolean();
	}

	public void encode(PacketBuffer buf) {
		buf.writeBlockPos(pos);
		buf.writeBoolean(powered);
	}

	public void handle(Supplier<NetworkEvent.Context> ctx) {
		PlayerEntity player = ctx.get().getSender();
		World level = player.level;
		TileEntity be = level.getBlockEntity(pos);

		if (!player.isSpectator() && (be instanceof IOwnable && ((IOwnable) be).isOwnedBy(player)) || (be instanceof IModuleInventory && ((IModuleInventory) be).isAllowed(player))) {
			BlockState state = level.getBlockState(pos);

			level.setBlockAndUpdate(pos, state.setValue(SecurityCameraBlock.POWERED, powered));
			level.updateNeighborsAt(pos.relative(state.getValue(SecurityCameraBlock.FACING), -1), state.getBlock());
		}
	}
}
