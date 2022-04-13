package net.geforcemods.securitycraft.network.client;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.ClientHandler;
import net.geforcemods.securitycraft.api.IPasswordProtected;
import net.geforcemods.securitycraft.blockentities.IMSBlockEntity;
import net.geforcemods.securitycraft.blockentities.SonicSecuritySystemBlockEntity;
import net.geforcemods.securitycraft.blockentities.TrophySystemBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

public class OpenScreen {
	private DataType dataType;
	private BlockPos pos;

	public OpenScreen() {}

	public OpenScreen(DataType dataType, BlockPos pos) {
		this.dataType = dataType;
		this.pos = pos;
	}

	public static void encode(OpenScreen message, PacketBuffer buf) {
		buf.writeEnum(message.dataType);
		buf.writeBlockPos(message.pos);
	}

	public static OpenScreen decode(PacketBuffer buf) {
		DataType dataType = buf.readEnum(DataType.class);
		BlockPos pos = buf.readBlockPos();

		return new OpenScreen(dataType, pos);
	}

	public static void onMessage(OpenScreen message, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			TileEntity te = Minecraft.getInstance().level.getBlockEntity(message.pos);
			switch (message.dataType) {
				case CHECK_PASSWORD:
					if (te instanceof IPasswordProtected)
						ClientHandler.displayCheckPasswordScreen(te);

					break;
				case IMS:
					if (te instanceof IMSBlockEntity)
						ClientHandler.displayIMSScreen((IMSBlockEntity) te);

					break;
				case SET_PASSWORD:
					if (te instanceof IPasswordProtected)
						ClientHandler.displaySetPasswordScreen(te);

					break;
				case SONIC_SECURITY_SYSTEM:
					if (te instanceof SonicSecuritySystemBlockEntity)
						ClientHandler.displaySonicSecuritySystemScreen((SonicSecuritySystemBlockEntity) te);

					break;
				case TROPHY_SYSTEM:
					if (te instanceof TrophySystemBlockEntity)
						ClientHandler.displayTrophySystemScreen((TrophySystemBlockEntity) te);

					break;
				case UNIVERSAL_KEY_CHANGER:
					if (te instanceof IPasswordProtected)
						ClientHandler.displayUniversalKeyChangerScreen(te);
			}
		});
		ctx.get().setPacketHandled(true);
	}

	public enum DataType {
		CHECK_PASSWORD,
		IMS,
		SET_PASSWORD,
		SONIC_SECURITY_SYSTEM,
		TROPHY_SYSTEM,
		UNIVERSAL_KEY_CHANGER;
	}
}
