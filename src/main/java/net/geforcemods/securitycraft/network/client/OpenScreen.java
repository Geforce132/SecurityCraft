package net.geforcemods.securitycraft.network.client;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.ClientHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IPasscodeProtected;
import net.geforcemods.securitycraft.blockentities.AlarmBlockEntity;
import net.geforcemods.securitycraft.blockentities.RiftStabilizerBlockEntity;
import net.geforcemods.securitycraft.blockentities.SecureRedstoneInterfaceBlockEntity;
import net.geforcemods.securitycraft.blockentities.SonicSecuritySystemBlockEntity;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.network.NetworkEvent;

public class OpenScreen {
	private DataType dataType;
	private BlockPos pos;
	private CompoundNBT tag;

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

	public OpenScreen(DataType dataType, CompoundNBT tag) {
		this.dataType = dataType;
		this.tag = tag;
	}

	public OpenScreen(PacketBuffer buf) {
		dataType = buf.readEnum(DataType.class);

		if (dataType.needsPosition)
			pos = buf.readBlockPos();
		else
			pos = BlockPos.ZERO;

		if (dataType == DataType.SENTRY_REMOTE_ACCESS_TOOL)
			tag = buf.readNbt();
	}

	public void encode(PacketBuffer buf) {
		buf.writeEnum(dataType);

		if (dataType.needsPosition)
			buf.writeBlockPos(pos);

		if (dataType == DataType.SENTRY_REMOTE_ACCESS_TOOL)
			buf.writeNbt(tag);
	}

	public void handle(Supplier<NetworkEvent.Context> ctx) {
		TileEntity be = Minecraft.getInstance().level.getBlockEntity(pos);

		switch (dataType) {
			case ALARM:
				if (be instanceof AlarmBlockEntity)
					ClientHandler.displayAlarmScreen((AlarmBlockEntity) be);

				break;
			case CHECK_BRIEFCASE_PASSCODE:
				ItemStack briefcaseStack = PlayerUtils.getItemStackFromAnyHand(ClientHandler.getClientPlayer(), SCContent.BRIEFCASE.get());

				if (!briefcaseStack.isEmpty())
					ClientHandler.displayBriefcasePasscodeScreen(briefcaseStack.getHoverName());

				break;
			case CHECK_PASSCODE:
				if (be instanceof IPasscodeProtected)
					ClientHandler.displayCheckPasscodeScreen(be);

				break;
			case RIFT_STABILIZER:
				if (be instanceof RiftStabilizerBlockEntity)
					ClientHandler.displayRiftStabilizerScreen(((RiftStabilizerBlockEntity) be));

				break;
			case SECURE_REDSTONE_INTERFACE:
				if (be instanceof SecureRedstoneInterfaceBlockEntity)
					ClientHandler.displaySecureRedstoneInterfaceScreen((SecureRedstoneInterfaceBlockEntity) be);

				break;
			case SENTRY_REMOTE_ACCESS_TOOL:
				ItemStack srat = PlayerUtils.getItemStackFromAnyHand(ClientHandler.getClientPlayer(), SCContent.SENTRY_REMOTE_ACCESS_TOOL.get());

				if (!srat.isEmpty()) {
					srat.setTag(tag);
					ClientHandler.displaySRATScreen(srat);
				}

				break;
			case SET_BRIEFCASE_PASSCODE:
				ItemStack briefcase = PlayerUtils.getItemStackFromAnyHand(ClientHandler.getClientPlayer(), SCContent.BRIEFCASE.get());

				if (!briefcase.isEmpty())
					ClientHandler.displayBriefcaseSetupScreen(briefcase.getHoverName().plainCopy().append(new StringTextComponent(" ")).append(Utils.localize("gui.securitycraft:passcode.setup")));

				break;
			case SET_PASSCODE:
				if (be instanceof IPasscodeProtected)
					ClientHandler.displaySetPasscodeScreen(be);

				break;
			case SONIC_SECURITY_SYSTEM:
				if (be instanceof SonicSecuritySystemBlockEntity)
					ClientHandler.displaySonicSecuritySystemScreen((SonicSecuritySystemBlockEntity) be);

				break;
			case UNIVERSAL_KEY_CHANGER:
				if (be instanceof IPasscodeProtected)
					ClientHandler.displayUniversalKeyChangerScreen(be);

				break;
			default:
				throw new IllegalStateException("Unhandled data type: " + dataType.name());
		}
	}

	public enum DataType {
		ALARM(true),
		CHECK_BRIEFCASE_PASSCODE(false),
		CHECK_PASSCODE(true),
		RIFT_STABILIZER(true),
		SENTRY_REMOTE_ACCESS_TOOL(false),
		SECURE_REDSTONE_INTERFACE(true),
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
