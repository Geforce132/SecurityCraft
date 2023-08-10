package net.geforcemods.securitycraft.network.server;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.blockentities.BlockPocketManagerBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

public class ToggleBlockPocketManager {
	private BlockPos pos;
	private int size;
	private boolean enabling;

	public ToggleBlockPocketManager() {}

	public ToggleBlockPocketManager(BlockPocketManagerBlockEntity te, boolean enabling, int size) {
		pos = te.getBlockPos();
		this.enabling = enabling;
		this.size = size;
	}

	public ToggleBlockPocketManager(PacketBuffer buf) {
		pos = BlockPos.of(buf.readLong());
		enabling = buf.readBoolean();
		size = buf.readInt();
	}

	public void encode(PacketBuffer buf) {
		buf.writeLong(pos.asLong());
		buf.writeBoolean(enabling);
		buf.writeInt(size);
	}

	public void handle(Supplier<NetworkEvent.Context> ctx) {
		PlayerEntity player = ctx.get().getSender();
		TileEntity te = player.level.getBlockEntity(pos);

		if (te instanceof BlockPocketManagerBlockEntity && ((BlockPocketManagerBlockEntity) te).isOwnedBy(player)) {
			((BlockPocketManagerBlockEntity) te).setSize(size);

			if (enabling)
				((BlockPocketManagerBlockEntity) te).enableMultiblock();
			else
				((BlockPocketManagerBlockEntity) te).disableMultiblock();
		}
	}
}
