package net.geforcemods.securitycraft.network.client;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.ClientHandler;
import net.geforcemods.securitycraft.api.IPasswordProtected;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

public class OpenUKCScreen {
	private BlockPos pos;

	public OpenUKCScreen() {}

	public OpenUKCScreen(BlockPos pos) {
		this.pos = pos;
	}

	public static void encode(OpenUKCScreen message, FriendlyByteBuf buf) {
		buf.writeBlockPos(message.pos);
	}

	public static OpenUKCScreen decode(FriendlyByteBuf buf) {
		return new OpenUKCScreen(buf.readBlockPos());
	}

	public static void onMessage(OpenUKCScreen message, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			if (Minecraft.getInstance().level.getBlockEntity(message.pos) instanceof IPasswordProtected passwordProtected)
				ClientHandler.displayUniversalKeyChangerGui((BlockEntity) passwordProtected);
		});
		ctx.get().setPacketHandled(true);
	}
}
