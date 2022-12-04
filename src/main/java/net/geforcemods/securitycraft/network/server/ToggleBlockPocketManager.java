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

	public static void encode(ToggleBlockPocketManager message, PacketBuffer buf) {
		buf.writeLong(message.pos.asLong());
		buf.writeBoolean(message.enabling);
		buf.writeInt(message.size);
	}

	public static ToggleBlockPocketManager decode(PacketBuffer buf) {
		ToggleBlockPocketManager message = new ToggleBlockPocketManager();

		message.pos = BlockPos.of(buf.readLong());
		message.enabling = buf.readBoolean();
		message.size = buf.readInt();
		return message;
	}

	public static void onMessage(ToggleBlockPocketManager message, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			PlayerEntity player = ctx.get().getSender();
			TileEntity te = player.level.getBlockEntity(message.pos);

			if (te instanceof BlockPocketManagerBlockEntity && ((BlockPocketManagerBlockEntity) te).isOwnedBy(player)) {
				((BlockPocketManagerBlockEntity) te).size = message.size;

				if (message.enabling)
					((BlockPocketManagerBlockEntity) te).enableMultiblock();
				else
					((BlockPocketManagerBlockEntity) te).disableMultiblock();
			}
		});

		ctx.get().setPacketHandled(true);
	}
}
