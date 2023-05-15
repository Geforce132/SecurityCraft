package net.geforcemods.securitycraft.network.client;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.ClientHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IPasscodeProtected;
import net.geforcemods.securitycraft.blockentities.AlarmBlockEntity;
import net.geforcemods.securitycraft.blockentities.IMSBlockEntity;
import net.geforcemods.securitycraft.blockentities.RiftStabilizerBlockEntity;
import net.geforcemods.securitycraft.blockentities.SonicSecuritySystemBlockEntity;
import net.geforcemods.securitycraft.blockentities.TrophySystemBlockEntity;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
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

	public static void encode(OpenScreen message, FriendlyByteBuf buf) {
		buf.writeEnum(message.dataType);
		buf.writeBlockPos(message.pos);
	}

	public static OpenScreen decode(FriendlyByteBuf buf) {
		DataType dataType = buf.readEnum(DataType.class);
		BlockPos pos = buf.readBlockPos();

		return new OpenScreen(dataType, pos);
	}

	public static void onMessage(OpenScreen message, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			switch (message.dataType) {
				case ALARM:
					if (Minecraft.getInstance().level.getBlockEntity(message.pos) instanceof AlarmBlockEntity be)
						ClientHandler.displayAlarmScreen(be);

					break;
				case CHECK_PASSCODE:
					if (Minecraft.getInstance().level.getBlockEntity(message.pos) instanceof IPasscodeProtected be)
						ClientHandler.displayCheckPasscodeScreen((BlockEntity) be);

					break;
				case IMS:
					if (Minecraft.getInstance().level.getBlockEntity(message.pos) instanceof IMSBlockEntity ims)
						ClientHandler.displayIMSScreen(ims);

					break;
				case RIFT_STABILIZER:
					if (Minecraft.getInstance().level.getBlockEntity(message.pos) instanceof RiftStabilizerBlockEntity riftStabilizer)
						ClientHandler.displayRiftStabilizerScreen(riftStabilizer);

					break;
				case SET_PASSCODE:
					if (Minecraft.getInstance().level.getBlockEntity(message.pos) instanceof IPasscodeProtected be)
						ClientHandler.displaySetPasscodeScreen((BlockEntity) be);

					break;
				case SONIC_SECURITY_SYSTEM:
					if (Minecraft.getInstance().level.getBlockEntity(message.pos) instanceof SonicSecuritySystemBlockEntity sss)
						ClientHandler.displaySonicSecuritySystemScreen(sss);

					break;
				case TROPHY_SYSTEM:
					if (Minecraft.getInstance().level.getBlockEntity(message.pos) instanceof TrophySystemBlockEntity trophySystem)
						ClientHandler.displayTrophySystemScreen(trophySystem);

					break;
				case UNIVERSAL_KEY_CHANGER:
					if (Minecraft.getInstance().level.getBlockEntity(message.pos) instanceof IPasscodeProtected passcodeProtected)
						ClientHandler.displayUniversalKeyChangerScreen((BlockEntity) passcodeProtected);

					break;
				case SET_BRIEFCASE_PASSCODE:
					ItemStack briefcase = PlayerUtils.getSelectedItemStack(ClientHandler.getClientPlayer(), SCContent.BRIEFCASE.get());

					if (!briefcase.isEmpty())
						ClientHandler.displayBriefcaseSetupScreen(briefcase.getHoverName());

					break;
				case CHECK_BRIEFCASE_PASSCODE:
					ItemStack briefcaseStack = PlayerUtils.getSelectedItemStack(ClientHandler.getClientPlayer(), SCContent.BRIEFCASE.get());

					if (!briefcaseStack.isEmpty())
						ClientHandler.displayBriefcasePasscodeScreen(briefcaseStack.getHoverName());
			}
		});
		ctx.get().setPacketHandled(true);
	}

	public enum DataType {
		ALARM,
		CHECK_PASSCODE,
		IMS,
		RIFT_STABILIZER,
		SET_PASSCODE,
		SONIC_SECURITY_SYSTEM,
		TROPHY_SYSTEM,
		UNIVERSAL_KEY_CHANGER,
		SET_BRIEFCASE_PASSCODE,
		CHECK_BRIEFCASE_PASSCODE;
	}
}
