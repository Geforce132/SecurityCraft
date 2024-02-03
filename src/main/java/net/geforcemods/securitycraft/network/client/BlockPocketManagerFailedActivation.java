package net.geforcemods.securitycraft.network.client;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.blockentities.BlockPocketManagerBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public class BlockPocketManagerFailedActivation {
	private BlockPos pos;

	public BlockPocketManagerFailedActivation() {}

	public BlockPocketManagerFailedActivation(BlockPos pos) {
		this.pos = pos;
	}

	public BlockPocketManagerFailedActivation(FriendlyByteBuf buf) {
		pos = buf.readBlockPos();
	}

	public void encode(FriendlyByteBuf buf) {
		buf.writeBlockPos(pos);
	}

	public void handle(Supplier<NetworkEvent.Context> ctx) {
		Minecraft mc = Minecraft.getInstance();

		if (mc.level.getBlockEntity(pos) instanceof BlockPocketManagerBlockEntity be)
			be.setEnabled(false);
	}
}
