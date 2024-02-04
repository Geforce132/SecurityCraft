package net.geforcemods.securitycraft.network.client;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.blockentities.BlockPocketManagerBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

public class BlockPocketManagerFailedActivation {
	private BlockPos pos;

	public BlockPocketManagerFailedActivation() {}

	public BlockPocketManagerFailedActivation(BlockPos pos) {
		this.pos = pos;
	}

	public BlockPocketManagerFailedActivation(PacketBuffer buf) {
		pos = buf.readBlockPos();
	}

	public void encode(PacketBuffer buf) {
		buf.writeBlockPos(pos);
	}

	public void handle(Supplier<NetworkEvent.Context> ctx) {
		TileEntity be = Minecraft.getInstance().level.getBlockEntity(pos);

		if (be instanceof BlockPocketManagerBlockEntity)
			((BlockPocketManagerBlockEntity) be).setEnabled(false);
	}
}
