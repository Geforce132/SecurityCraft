package net.geforcemods.securitycraft.network.client;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.ClientHandler;
import net.geforcemods.securitycraft.api.IPasswordProtected;
import net.geforcemods.securitycraft.blockentities.AlarmBlockEntity;
import net.geforcemods.securitycraft.blockentities.IMSBlockEntity;
import net.geforcemods.securitycraft.blockentities.RiftStabilizerBlockEntity;
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

	public OpenScreen(PacketBuffer buf) {
		dataType = buf.readEnum(DataType.class);
		pos = buf.readBlockPos();
	}

	public void encode(PacketBuffer buf) {
		buf.writeEnum(dataType);
		buf.writeBlockPos(pos);
	}

	public void handle(Supplier<NetworkEvent.Context> ctx) {
		TileEntity te = Minecraft.getInstance().level.getBlockEntity(pos);

		switch (dataType) {
			case ALARM:
				if (te instanceof AlarmBlockEntity)
					ClientHandler.displayAlarmScreen((AlarmBlockEntity) te);

				break;
			case CHECK_PASSWORD:
				if (te instanceof IPasswordProtected)
					ClientHandler.displayCheckPasswordScreen(te);

				break;
			case IMS:
				if (te instanceof IMSBlockEntity)
					ClientHandler.displayIMSScreen((IMSBlockEntity) te);

				break;
			case RIFT_STABILIZER:
				if (Minecraft.getInstance().level.getBlockEntity(pos) instanceof RiftStabilizerBlockEntity)
					ClientHandler.displayRiftStabilizerScreen(((RiftStabilizerBlockEntity) te));

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
	}

	public enum DataType {
		ALARM,
		CHECK_PASSWORD,
		IMS,
		RIFT_STABILIZER,
		SET_PASSWORD,
		SONIC_SECURITY_SYSTEM,
		TROPHY_SYSTEM,
		UNIVERSAL_KEY_CHANGER;
	}
}
