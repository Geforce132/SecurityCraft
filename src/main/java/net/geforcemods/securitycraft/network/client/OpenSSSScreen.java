package net.geforcemods.securitycraft.network.client;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.ClientHandler;
import net.geforcemods.securitycraft.blockentity.SonicSecuritySystemBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

public class OpenSSSScreen {
	private BlockPos pos;

	public OpenSSSScreen() {}

	public OpenSSSScreen(BlockPos pos) {
		this.pos = pos;
	}

	public static void encode(OpenSSSScreen message, PacketBuffer buf) {
		buf.writeBlockPos(message.pos);
	}

	public static OpenSSSScreen decode(PacketBuffer buf) {
		return new OpenSSSScreen(buf.readBlockPos());
	}

	public static void onMessage(OpenSSSScreen message, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			TileEntity te = Minecraft.getInstance().level.getBlockEntity(message.pos);

			if (te instanceof SonicSecuritySystemBlockEntity)
				ClientHandler.displaySonicSecuritySystemGui((SonicSecuritySystemBlockEntity) te);
		});
		ctx.get().setPacketHandled(true);
	}
}
