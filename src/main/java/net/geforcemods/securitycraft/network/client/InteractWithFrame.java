package net.geforcemods.securitycraft.network.client;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.ClientHandler;
import net.geforcemods.securitycraft.blockentities.FrameBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;

public class InteractWithFrame {
	private BlockPos pos;
	private boolean owner;

	public InteractWithFrame() {}

	public InteractWithFrame(BlockPos pos, boolean owner) {
		this.pos = pos;
		this.owner = owner;
	}

	public InteractWithFrame(FriendlyByteBuf buf) {
		pos = buf.readBlockPos();
		owner = buf.readBoolean();
	}

	public void encode(FriendlyByteBuf buf) {
		buf.writeBlockPos(pos);
		buf.writeBoolean(owner);
	}

	public void handle(Supplier<NetworkEvent.Context> ctx) {
		Level level = Minecraft.getInstance().player.level();

		if (level.getBlockEntity(pos) instanceof FrameBlockEntity be) {
			if (!be.redstoneSignalDisabled() && !be.hasClientInteracted() && be.getCurrentCamera() != null)
				be.setCameraOnClientAndUpdate(be.getCurrentCamera());
			else
				ClientHandler.displayFrameScreen(be, !owner);
		}
	}
}
