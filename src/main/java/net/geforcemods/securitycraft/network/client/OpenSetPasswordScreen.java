package net.geforcemods.securitycraft.network.client;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.ClientHandler;
import net.geforcemods.securitycraft.api.IPasswordProtected;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

public class OpenSetPasswordScreen {
	private BlockPos pos;

	public OpenSetPasswordScreen() {}

	public OpenSetPasswordScreen(BlockPos pos) {
		this.pos = pos;
	}

	public static void encode(OpenSetPasswordScreen message, FriendlyByteBuf buf) {
		buf.writeBlockPos(message.pos);
	}

	public static OpenSetPasswordScreen decode(FriendlyByteBuf buf) {
		return new OpenSetPasswordScreen(buf.readBlockPos());
	}

	public static void onMessage(OpenSetPasswordScreen message, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			if (Minecraft.getInstance().level.getBlockEntity(message.pos) instanceof IPasswordProtected be)
				ClientHandler.displaySetPasswordGui((BlockEntity) be);
		});
		ctx.get().setPacketHandled(true);
	}
}
