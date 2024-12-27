package net.geforcemods.securitycraft.network.client;

import net.geforcemods.securitycraft.ClientHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.IPasscodeProtected;
import net.geforcemods.securitycraft.blockentities.AlarmBlockEntity;
import net.geforcemods.securitycraft.blockentities.FrameBlockEntity;
import net.geforcemods.securitycraft.blockentities.RiftStabilizerBlockEntity;
import net.geforcemods.securitycraft.blockentities.SecureRedstoneInterfaceBlockEntity;
import net.geforcemods.securitycraft.blockentities.SonicSecuritySystemBlockEntity;
import net.geforcemods.securitycraft.blockentities.UsernameLoggerBlockEntity;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class OpenScreen implements CustomPacketPayload {
	public static final Type<OpenScreen> TYPE = new Type<>(SecurityCraft.resLoc("open_screen"));
	public static final StreamCodec<RegistryFriendlyByteBuf, OpenScreen> STREAM_CODEC = new StreamCodec<>() {
		public OpenScreen decode(RegistryFriendlyByteBuf buf) {
			DataType dataType = buf.readEnum(DataType.class);

			if (dataType.needsPosition)
				return new OpenScreen(dataType, buf.readBlockPos());
			else if (dataType == DataType.CHANGE_PASSCODE_FOR_ENTITY || dataType == DataType.CHECK_PASSCODE_FOR_ENTITY || dataType == DataType.SET_PASSCODE_FOR_ENTITY)
				return new OpenScreen(dataType, buf.readVarInt());
			else
				return new OpenScreen(dataType);
		}

		@Override
		public void encode(RegistryFriendlyByteBuf buf, OpenScreen packet) {
			buf.writeEnum(packet.dataType);

			if (packet.dataType.needsPosition)
				buf.writeBlockPos(packet.pos);
			else if (packet.dataType == DataType.CHANGE_PASSCODE_FOR_ENTITY || packet.dataType == DataType.CHECK_PASSCODE_FOR_ENTITY || packet.dataType == DataType.SET_PASSCODE_FOR_ENTITY)
				buf.writeVarInt(packet.entityId);
		}
	};
	private DataType dataType;
	private BlockPos pos;
	private int entityId;

	public OpenScreen() {}

	public OpenScreen(DataType dataType) {
		this.dataType = dataType;

		if (dataType.needsPosition)
			throw new IllegalArgumentException(String.format("The DataType %s needs a position, but none was supplied.", dataType.name()));
	}

	public OpenScreen(DataType dataType, int entityId) {
		this.dataType = dataType;
		this.entityId = entityId;
	}

	public OpenScreen(DataType dataType, BlockPos pos) {
		this.dataType = dataType;
		this.pos = pos;
	}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public void handle(IPayloadContext ctx) {
		Player player = ctx.player();
		Level level = player.level();

		switch (dataType) {
			case ALARM:
				if (level.getBlockEntity(pos) instanceof AlarmBlockEntity be)
					ClientHandler.displayAlarmScreen(be);

				break;
			case CHANGE_PASSCODE:
				if (level.getBlockEntity(pos) instanceof IPasscodeProtected passcodeProtected)
					ClientHandler.displayUniversalKeyChangerScreen((BlockEntity) passcodeProtected);

				break;
			case CHANGE_PASSCODE_FOR_ENTITY:
				if (level.getEntity(entityId) instanceof IPasscodeProtected entity)
					ClientHandler.displayUniversalKeyChangerScreen((Entity) entity);

				break;
			case CHECK_PASSCODE:
				if (level.getBlockEntity(pos) instanceof IPasscodeProtected be)
					ClientHandler.displayCheckPasscodeScreen((BlockEntity) be);

				break;
			case CHECK_PASSCODE_FOR_BRIEFCASE:
				ItemStack briefcaseStack = PlayerUtils.getItemStackFromAnyHand(player, SCContent.BRIEFCASE.get());

				if (!briefcaseStack.isEmpty())
					ClientHandler.displayBriefcasePasscodeScreen(briefcaseStack.getHoverName());

				break;
			case CHECK_PASSCODE_FOR_ENTITY:
				if (level.getEntity(entityId) instanceof IPasscodeProtected entity)
					ClientHandler.displayCheckPasscodeScreen((Entity) entity);

				break;
			case FRAME:
				if (level.getBlockEntity(pos) instanceof FrameBlockEntity frame)
					ClientHandler.displayFrameScreen(frame, true);

				break;
			case FRAME_OWNER:
				if (level.getBlockEntity(pos) instanceof FrameBlockEntity frame)
					ClientHandler.displayFrameScreen(frame, false);

				break;
			case RIFT_STABILIZER:
				if (level.getBlockEntity(pos) instanceof RiftStabilizerBlockEntity riftStabilizer)
					ClientHandler.displayRiftStabilizerScreen(riftStabilizer);

				break;
			case SECURE_REDSTONE_INTERFACE:
				if (level.getBlockEntity(pos) instanceof SecureRedstoneInterfaceBlockEntity secureRedstoneInterface)
					ClientHandler.displaySecureRedstoneInterfaceScreen(secureRedstoneInterface);

				break;
			case SENTRY_REMOTE_ACCESS_TOOL:
				ItemStack srat = PlayerUtils.getItemStackFromAnyHand(player, SCContent.SENTRY_REMOTE_ACCESS_TOOL.get());

				if (!srat.isEmpty())
					ClientHandler.displaySRATScreen(srat);

				break;
			case SET_PASSCODE:
				if (level.getBlockEntity(pos) instanceof IPasscodeProtected be)
					ClientHandler.displaySetPasscodeScreen((BlockEntity) be);

				break;
			case SET_PASSCODE_FOR_BRIEFCASE:
				ItemStack briefcase = PlayerUtils.getItemStackFromAnyHand(player, SCContent.BRIEFCASE.get());

				if (!briefcase.isEmpty())
					ClientHandler.displayBriefcaseSetupScreen(briefcase.getHoverName().plainCopy().append(Component.literal(" ")).append(Utils.localize("gui.securitycraft:passcode.setup")));

				break;
			case SET_PASSCODE_FOR_ENTITY:
				if (level.getEntity(entityId) instanceof IPasscodeProtected entity)
					ClientHandler.displaySetPasscodeScreen((Entity) entity);

				break;
			case SONIC_SECURITY_SYSTEM:
				if (level.getBlockEntity(pos) instanceof SonicSecuritySystemBlockEntity sss)
					ClientHandler.displaySonicSecuritySystemScreen(sss);

				break;
			case USERNAME_LOGGER:
				if (level.getBlockEntity(pos) instanceof UsernameLoggerBlockEntity logger)
					ClientHandler.displayUsernameLoggerScreen(logger);

				break;
			default:
				throw new IllegalStateException("Unhandled data type: " + dataType.name());
		}
	}

	public enum DataType {
		ALARM(true),
		CHANGE_PASSCODE(true),
		CHANGE_PASSCODE_FOR_ENTITY(false),
		CHECK_PASSCODE(true),
		CHECK_PASSCODE_FOR_BRIEFCASE(false),
		CHECK_PASSCODE_FOR_ENTITY(false),
		FRAME(true),
		FRAME_OWNER(true),
		RIFT_STABILIZER(true),
		SENTRY_REMOTE_ACCESS_TOOL(false),
		SECURE_REDSTONE_INTERFACE(true),
		SET_PASSCODE(true),
		SET_PASSCODE_FOR_BRIEFCASE(false),
		SET_PASSCODE_FOR_ENTITY(false),
		SONIC_SECURITY_SYSTEM(true),
		USERNAME_LOGGER(true);

		public final boolean needsPosition;

		DataType(boolean needsPosition) {
			this.needsPosition = needsPosition;
		}
	}
}
