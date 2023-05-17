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

	public OpenLaserScreen(FriendlyByteBuf buf) {
		pos = buf.readBlockPos();
		sideConfig = buf.readNbt();
	}

	public void encode(FriendlyByteBuf buf) {
		buf.writeBlockPos(pos);
		buf.writeNbt(sideConfig);
	}

	public void handle(Supplier<NetworkEvent.Context> ctx) {
		if (Minecraft.getInstance().level.getBlockEntity(pos) instanceof LaserBlockBlockEntity laser)
			ClientHandler.displayLaserScreen(laser, LaserBlockBlockEntity.loadSideConfig(sideConfig));
	}
}
