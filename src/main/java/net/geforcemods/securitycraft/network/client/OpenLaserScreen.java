package net.geforcemods.securitycraft.network.client;

import java.util.EnumMap;
import java.util.function.Supplier;

import net.geforcemods.securitycraft.ClientHandler;
import net.geforcemods.securitycraft.blockentities.LaserBlockBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public class OpenLaserScreen {
	private BlockPos pos;
	private CompoundTag sideConfig;

	public OpenLaserScreen() {}

	public OpenLaserScreen(BlockPos pos, EnumMap<Direction, Boolean> sideConfig) {
		this.pos = pos;
		this.sideConfig = LaserBlockBlockEntity.saveSideConfig(sideConfig);
	}

	public static void encode(OpenLaserScreen message, FriendlyByteBuf buf) {
		buf.writeBlockPos(message.pos);
		buf.writeNbt(message.sideConfig);
	}

	public static OpenLaserScreen decode(FriendlyByteBuf buf) {
		OpenLaserScreen message = new OpenLaserScreen();

		message.pos = buf.readBlockPos();
		message.sideConfig = buf.readNbt();
		return message;
	}

	public static void onMessage(OpenLaserScreen message, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			if (Minecraft.getInstance().level.getBlockEntity(message.pos) instanceof LaserBlockBlockEntity laser)
				ClientHandler.displayLaserScreen(laser, LaserBlockBlockEntity.loadSideConfig(message.sideConfig));
		});
		ctx.get().setPacketHandled(true);
	}
}
