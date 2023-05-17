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
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

public class OpenScreen {
	private DataType dataType;
	private BlockPos pos;

	public OpenScreen() {}

	public OpenScreen(DataType dataType, BlockPos pos) {
		this.dataType = dataType;
		this.pos = pos;
	}

	public OpenScreen(FriendlyByteBuf buf) {
		dataType = buf.readEnum(DataType.class);
		pos = buf.readBlockPos();
	}

	public void encode(FriendlyByteBuf buf) {
		buf.writeEnum(dataType);
		buf.writeBlockPos(pos);
	}

	public void handle(Supplier<NetworkEvent.Context> ctx) {
		switch (dataType) {
			case ALARM:
				if (Minecraft.getInstance().level.getBlockEntity(pos) instanceof AlarmBlockEntity be)
					ClientHandler.displayAlarmScreen(be);

				break;
			case CHECK_PASSWORD:
				if (Minecraft.getInstance().level.getBlockEntity(pos) instanceof IPasswordProtected be)
					ClientHandler.displayCheckPasswordScreen((BlockEntity) be);

				break;
			case IMS:
				if (Minecraft.getInstance().level.getBlockEntity(pos) instanceof IMSBlockEntity ims)
					ClientHandler.displayIMSScreen(ims);

				break;
			case RIFT_STABILIZER:
				if (Minecraft.getInstance().level.getBlockEntity(pos) instanceof RiftStabilizerBlockEntity riftStabilizer)
					ClientHandler.displayRiftStabilizerScreen(riftStabilizer);

				break;
			case SET_PASSWORD:
				if (Minecraft.getInstance().level.getBlockEntity(pos) instanceof IPasswordProtected be)
					ClientHandler.displaySetPasswordScreen((BlockEntity) be);

				break;
			case SONIC_SECURITY_SYSTEM:
				if (Minecraft.getInstance().level.getBlockEntity(pos) instanceof SonicSecuritySystemBlockEntity sss)
					ClientHandler.displaySonicSecuritySystemScreen(sss);

				break;
			case TROPHY_SYSTEM:
				if (Minecraft.getInstance().level.getBlockEntity(pos) instanceof TrophySystemBlockEntity trophySystem)
					ClientHandler.displayTrophySystemScreen(trophySystem);

				break;
			case UNIVERSAL_KEY_CHANGER:
				if (Minecraft.getInstance().level.getBlockEntity(pos) instanceof IPasswordProtected passwordProtected)
					ClientHandler.displayUniversalKeyChangerScreen((BlockEntity) passwordProtected);
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
