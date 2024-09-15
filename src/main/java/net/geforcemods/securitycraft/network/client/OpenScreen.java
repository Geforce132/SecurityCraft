package net.geforcemods.securitycraft.network.client;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.ClientHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IPasscodeProtected;
import net.geforcemods.securitycraft.blockentities.AlarmBlockEntity;
import net.geforcemods.securitycraft.blockentities.RiftStabilizerBlockEntity;
import net.geforcemods.securitycraft.blockentities.SecureRedstoneInterfaceBlockEntity;
import net.geforcemods.securitycraft.blockentities.SonicSecuritySystemBlockEntity;
import net.geforcemods.securitycraft.blockentities.UsernameLoggerBlockEntity;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

public class OpenScreen {
	private DataType dataType;
	private BlockPos pos;
	private CompoundTag tag;

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

	public OpenScreen(DataType dataType, CompoundTag tag) {
		this.dataType = dataType;
		this.tag = tag;
	}

	public OpenScreen(FriendlyByteBuf buf) {
		dataType = buf.readEnum(DataType.class);

		if (dataType.needsPosition)
			pos = buf.readBlockPos();

		if (dataType == DataType.SENTRY_REMOTE_ACCESS_TOOL)
			tag = buf.readNbt();
	}

	public void encode(FriendlyByteBuf buf) {
		buf.writeEnum(dataType);

		if (dataType.needsPosition)
			buf.writeBlockPos(pos);

		if (dataType == DataType.SENTRY_REMOTE_ACCESS_TOOL)
			buf.writeNbt(tag);
	}

	public void handle(Supplier<NetworkEvent.Context> ctx) {
		switch (dataType) {
			case ALARM:
				if (Minecraft.getInstance().level.getBlockEntity(pos) instanceof AlarmBlockEntity be)
					ClientHandler.displayAlarmScreen(be);

				break;
			case CHECK_BRIEFCASE_PASSCODE:
				ItemStack briefcaseStack = PlayerUtils.getItemStackFromAnyHand(ClientHandler.getClientPlayer(), SCContent.BRIEFCASE.get());

				if (!briefcaseStack.isEmpty())
					ClientHandler.displayBriefcasePasscodeScreen(briefcaseStack.getHoverName());

				break;
			case CHECK_PASSCODE:
				if (Minecraft.getInstance().level.getBlockEntity(pos) instanceof IPasscodeProtected be)
					ClientHandler.displayCheckPasscodeScreen((BlockEntity) be);

				break;
			case RIFT_STABILIZER:
				if (Minecraft.getInstance().level.getBlockEntity(pos) instanceof RiftStabilizerBlockEntity riftStabilizer)
					ClientHandler.displayRiftStabilizerScreen(riftStabilizer);

				break;
			case SECURE_REDSTONE_INTERFACE:
				if (Minecraft.getInstance().level.getBlockEntity(pos) instanceof SecureRedstoneInterfaceBlockEntity secureRedstoneInterface)
					ClientHandler.displaySecureRedstoneInterfaceScreen(secureRedstoneInterface);

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
					ClientHandler.displayBriefcaseSetupScreen(briefcase.getHoverName().plainCopy().append(new TextComponent(" ")).append(Utils.localize("gui.securitycraft:passcode.setup")));

				break;
			case SET_PASSCODE:
				if (Minecraft.getInstance().level.getBlockEntity(pos) instanceof IPasscodeProtected be)
					ClientHandler.displaySetPasscodeScreen((BlockEntity) be);

				break;
			case SONIC_SECURITY_SYSTEM:
				if (Minecraft.getInstance().level.getBlockEntity(pos) instanceof SonicSecuritySystemBlockEntity sss)
					ClientHandler.displaySonicSecuritySystemScreen(sss);

				break;
			case UNIVERSAL_KEY_CHANGER:
				if (Minecraft.getInstance().level.getBlockEntity(pos) instanceof IPasscodeProtected passcodeProtected)
					ClientHandler.displayUniversalKeyChangerScreen((BlockEntity) passcodeProtected);

				break;
			case USERNAME_LOGGER:
				if (Minecraft.getInstance().level.getBlockEntity(pos) instanceof UsernameLoggerBlockEntity logger)
					ClientHandler.displayUsernameLoggerScreen(logger);

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
		UNIVERSAL_KEY_CHANGER(true),
		USERNAME_LOGGER(true);

		public final boolean needsPosition;

		DataType(boolean needsPosition) {
			this.needsPosition = needsPosition;
		}
	}
}
