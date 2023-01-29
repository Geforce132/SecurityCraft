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

	public static void encode(OpenLaserScreen message, PacketBuffer buf) {
		buf.writeBlockPos(message.pos);
		buf.writeNbt(message.sideConfig);
	}

	public static OpenLaserScreen decode(PacketBuffer buf) {
		OpenLaserScreen message = new OpenLaserScreen();

		message.pos = buf.readBlockPos();
		message.sideConfig = buf.readNbt();
		return message;
	}

	public static void onMessage(OpenLaserScreen message, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			TileEntity te = Minecraft.getInstance().level.getBlockEntity(message.pos);

			if (te instanceof LaserBlockBlockEntity)
				ClientHandler.displayLaserScreen((LaserBlockBlockEntity) te, LaserBlockBlockEntity.loadSideConfig(message.sideConfig));
		});
		ctx.get().setPacketHandled(true);
	}
}
