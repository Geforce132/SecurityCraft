package net.geforcemods.securitycraft.network.client;

import net.geforcemods.securitycraft.ClientHandler;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blockentities.FrameBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class InteractWithFrame implements CustomPacketPayload {
	public static final ResourceLocation ID = new ResourceLocation(SecurityCraft.MODID, "interact_with_frame");
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

	@Override
	public void write(FriendlyByteBuf buf) {
		buf.writeBlockPos(pos);
		buf.writeBoolean(owner);
	}

	@Override
	public ResourceLocation id() {
		return ID;
	}

	public void handle(IPayloadContext ctx) {
		Level level = Minecraft.getInstance().player.level();

		if (level.getBlockEntity(pos) instanceof FrameBlockEntity be) {
			if (!be.redstoneSignalDisabled() && !be.hasClientInteracted() && be.getCurrentCamera() != null)
				be.setCurrentCameraAndUpdate(be.getCurrentCamera());
			else
				ClientHandler.displayFrameScreen(be, !owner);
		}
	}
}
