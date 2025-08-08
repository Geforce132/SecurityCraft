package net.geforcemods.securitycraft.network.client;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.ClientHandler;
import net.geforcemods.securitycraft.blockentities.FrameBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;

public class InteractWithFrame {
	private BlockPos pos;
	private boolean owner;

	public InteractWithFrame() {}

	public InteractWithFrame(BlockPos pos, boolean owner) {
		this.pos = pos;
		this.owner = owner;
	}

	public InteractWithFrame(PacketBuffer buf) {
		pos = buf.readBlockPos();
		owner = buf.readBoolean();
	}

	public void encode(PacketBuffer buf) {
		buf.writeBlockPos(pos);
		buf.writeBoolean(owner);
	}

	public void handle(Supplier<NetworkEvent.Context> ctx) {
		World level = Minecraft.getInstance().player.level;
		TileEntity te = level.getBlockEntity(pos);

		if (te instanceof FrameBlockEntity) {
			FrameBlockEntity be = (FrameBlockEntity) te;

			if (!be.redstoneSignalDisabled() && !be.hasClientInteracted() && be.getCurrentCamera() != null)
				be.setCameraOnClientAndUpdate(be.getCurrentCamera());
			else
				ClientHandler.displayFrameScreen(be, !owner);
		}
	}
}
