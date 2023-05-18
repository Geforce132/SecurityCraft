package net.geforcemods.securitycraft.network.client;

import java.util.EnumMap;
import java.util.function.Supplier;

import net.geforcemods.securitycraft.ClientHandler;
import net.geforcemods.securitycraft.blockentities.LaserBlockBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

public class OpenLaserScreen {
	private BlockPos pos;
	private CompoundNBT sideConfig;

	public OpenLaserScreen() {}

	public OpenLaserScreen(BlockPos pos, EnumMap<Direction, Boolean> sideConfig) {
		this.pos = pos;
		this.sideConfig = LaserBlockBlockEntity.saveSideConfig(sideConfig);
	}

	public OpenLaserScreen(PacketBuffer buf) {
		pos = buf.readBlockPos();
		sideConfig = buf.readNbt();
	}

	public void encode(PacketBuffer buf) {
		buf.writeBlockPos(pos);
		buf.writeNbt(sideConfig);
	}

	public void handle(Supplier<NetworkEvent.Context> ctx) {
		TileEntity te = Minecraft.getInstance().level.getBlockEntity(pos);

		if (te instanceof LaserBlockBlockEntity)
			ClientHandler.displayLaserScreen((LaserBlockBlockEntity) te, LaserBlockBlockEntity.loadSideConfig(sideConfig));
	}
}
