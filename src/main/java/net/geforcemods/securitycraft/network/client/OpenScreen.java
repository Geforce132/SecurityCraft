package net.geforcemods.securitycraft.network.client;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.ClientHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IPasscodeProtected;
import net.geforcemods.securitycraft.blockentities.AlarmBlockEntity;
import net.geforcemods.securitycraft.blockentities.IMSBlockEntity;
import net.geforcemods.securitycraft.blockentities.RiftStabilizerBlockEntity;
import net.geforcemods.securitycraft.blockentities.SonicSecuritySystemBlockEntity;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.network.NetworkEvent;

public class OpenScreen {
	private DataType dataType;
	private BlockPos pos;

	public OpenScreen() {}

	public OpenScreen(DataType dataType) {
		this.dataType = dataType;

		if (dataType.needsPosition)
			throw new IllegalArgumentException(String.format("The DataType %s needs a position, but none was supplied.", dataType.name()));
	}

	public OpenScreen(DataType dataType, BlockPos pos) {
		this.dataType = dataType;
		this.pos = pos;
	}

	public OpenScreen(PacketBuffer buf) {
		dataType = buf.readEnum(DataType.class);

		if (dataType.needsPosition)
			pos = buf.readBlockPos();
		else
			pos = BlockPos.ZERO;
	}

	public void encode(PacketBuffer buf) {
		buf.writeEnum(dataType);

		if (dataType.needsPosition)
			buf.writeBlockPos(pos);
	}

	public void handle(Supplier<NetworkEvent.Context> ctx) {
		TileEntity te = Minecraft.getInstance().level.getBlockEntity(pos);

		switch (dataType) {
			case ALARM:
				if (te instanceof AlarmBlockEntity)
					ClientHandler.displayAlarmScreen((AlarmBlockEntity) te);

				break;
			case CHECK_BRIEFCASE_PASSCODE:
				ItemStack briefcaseStack = PlayerUtils.getItemStackFromAnyHand(ClientHandler.getClientPlayer(), SCContent.BRIEFCASE.get());

				if (!briefcaseStack.isEmpty())
					ClientHandler.displayBriefcasePasscodeScreen(briefcaseStack.getHoverName());

				break;
			case CHECK_PASSCODE:
				if (te instanceof IPasscodeProtected)
					ClientHandler.displayCheckPasscodeScreen(te);

				break;
			case IMS:
				if (te instanceof IMSBlockEntity)
					ClientHandler.displayIMSScreen((IMSBlockEntity) te);

				break;
			case RIFT_STABILIZER:
				if (Minecraft.getInstance().level.getBlockEntity(pos) instanceof RiftStabilizerBlockEntity)
					ClientHandler.displayRiftStabilizerScreen(((RiftStabilizerBlockEntity) te));

				break;
			case SENTRY_REMOTE_ACCESS_TOOL:
				ItemStack srat = PlayerUtils.getItemStackFromAnyHand(ClientHandler.getClientPlayer(), SCContent.SENTRY_REMOTE_ACCESS_TOOL.get());

				if (!srat.isEmpty())
					ClientHandler.displaySRATScreen(srat);
				break;
			case SET_BRIEFCASE_PASSCODE:
				ItemStack briefcase = PlayerUtils.getItemStackFromAnyHand(ClientHandler.getClientPlayer(), SCContent.BRIEFCASE.get());

				if (!briefcase.isEmpty())
					ClientHandler.displayBriefcaseSetupScreen(briefcase.getHoverName().plainCopy().append(new StringTextComponent(" ")).append(Utils.localize("gui.securitycraft:passcode.setup")));

				break;
			case SET_PASSCODE:
				if (te instanceof IPasscodeProtected)
					ClientHandler.displaySetPasscodeScreen(te);

				break;
			case SONIC_SECURITY_SYSTEM:
				if (te instanceof SonicSecuritySystemBlockEntity)
					ClientHandler.displaySonicSecuritySystemScreen((SonicSecuritySystemBlockEntity) te);

				break;
			case UNIVERSAL_KEY_CHANGER:
				if (te instanceof IPasscodeProtected)
					ClientHandler.displayUniversalKeyChangerScreen(te);
		}
	}

	public enum DataType {
		ALARM(true),
		CHECK_BRIEFCASE_PASSCODE(false),
		CHECK_PASSCODE(true),
		IMS(true),
		RIFT_STABILIZER(true),
		SENTRY_REMOTE_ACCESS_TOOL(false),
		SET_BRIEFCASE_PASSCODE(false),
		SET_PASSCODE(true),
		SONIC_SECURITY_SYSTEM(true),
		UNIVERSAL_KEY_CHANGER(true);

		public final boolean needsPosition;

		DataType(boolean needsPosition) {
			this.needsPosition = needsPosition;
		}
	}
}
