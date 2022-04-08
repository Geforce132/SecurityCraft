package net.geforcemods.securitycraft.network.client;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.ClientHandler;
import net.geforcemods.securitycraft.api.IPasswordProtected;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

public class OpenCheckPasswordScreen {
	private BlockPos pos;

	public OpenCheckPasswordScreen() {}

	public OpenCheckPasswordScreen(BlockPos pos) {
		this.pos = pos;
	}

	public static void encode(OpenCheckPasswordScreen message, FriendlyByteBuf buf) {
		buf.writeBlockPos(message.pos);
	}

	public static OpenCheckPasswordScreen decode(FriendlyByteBuf buf) {
		return new OpenCheckPasswordScreen(buf.readBlockPos());
	}

	public static void onMessage(OpenCheckPasswordScreen message, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			if (Minecraft.getInstance().level.getBlockEntity(message.pos) instanceof IPasswordProtected be)
				ClientHandler.displayCheckPasswordGui((BlockEntity) be);
		});
		ctx.get().setPacketHandled(true);
	}
}
