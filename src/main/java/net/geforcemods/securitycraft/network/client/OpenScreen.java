package net.geforcemods.securitycraft.network.client;

import net.geforcemods.securitycraft.ClientHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.IPasscodeProtected;
import net.geforcemods.securitycraft.blockentities.AlarmBlockEntity;
import net.geforcemods.securitycraft.blockentities.RiftStabilizerBlockEntity;
import net.geforcemods.securitycraft.blockentities.SonicSecuritySystemBlockEntity;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class OpenScreen implements CustomPacketPayload {
	public static final Type<OpenScreen> TYPE = new Type<>(new ResourceLocation(SecurityCraft.MODID, "open_screen"));
	public static final StreamCodec<RegistryFriendlyByteBuf, OpenScreen> STREAM_CODEC = new StreamCodec<>() {
		public OpenScreen decode(RegistryFriendlyByteBuf buf) {
			DataType dataType = buf.readEnum(DataType.class);

			if (dataType.needsPosition)
				return new OpenScreen(dataType, buf.readBlockPos());
			else if (dataType == DataType.SENTRY_REMOTE_ACCESS_TOOL)
				return new OpenScreen(dataType, buf.readNbt());
			else
				return new OpenScreen(dataType);
		}

		@Override
		public void encode(RegistryFriendlyByteBuf buf, OpenScreen packet) {
			buf.writeEnum(packet.dataType);

			if (packet.dataType.needsPosition)
				buf.writeBlockPos(packet.pos);

			if (packet.dataType == DataType.SENTRY_REMOTE_ACCESS_TOOL)
				buf.writeNbt(packet.tag);
		}
	};
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
			case CHECK_BRIEFCASE_PASSCODE:
				ItemStack briefcaseStack = PlayerUtils.getItemStackFromAnyHand(player, SCContent.BRIEFCASE.get());

				if (!briefcaseStack.isEmpty())
					ClientHandler.displayBriefcasePasscodeScreen(briefcaseStack.getHoverName());

				break;
			case CHECK_PASSCODE:
				if (level.getBlockEntity(pos) instanceof IPasscodeProtected be)
					ClientHandler.displayCheckPasscodeScreen((BlockEntity) be);

				break;
			case RIFT_STABILIZER:
				if (level.getBlockEntity(pos) instanceof RiftStabilizerBlockEntity riftStabilizer)
					ClientHandler.displayRiftStabilizerScreen(riftStabilizer);

				break;
			case SENTRY_REMOTE_ACCESS_TOOL:
				ItemStack srat = PlayerUtils.getItemStackFromAnyHand(player, SCContent.SENTRY_REMOTE_ACCESS_TOOL.get());

				if (!srat.isEmpty()) {
					CustomData.set(DataComponents.CUSTOM_DATA, srat, tag);
					ClientHandler.displaySRATScreen(srat);
				}

				break;
			case SET_BRIEFCASE_PASSCODE:
				ItemStack briefcase = PlayerUtils.getItemStackFromAnyHand(player, SCContent.BRIEFCASE.get());

				if (!briefcase.isEmpty())
					ClientHandler.displayBriefcaseSetupScreen(briefcase.getHoverName().plainCopy().append(Component.literal(" ")).append(Utils.localize("gui.securitycraft:passcode.setup")));

				break;
			case SET_PASSCODE:
				if (level.getBlockEntity(pos) instanceof IPasscodeProtected be)
					ClientHandler.displaySetPasscodeScreen((BlockEntity) be);

				break;
			case SONIC_SECURITY_SYSTEM:
				if (level.getBlockEntity(pos) instanceof SonicSecuritySystemBlockEntity sss)
					ClientHandler.displaySonicSecuritySystemScreen(sss);

				break;
			case UNIVERSAL_KEY_CHANGER:
				if (level.getBlockEntity(pos) instanceof IPasscodeProtected passcodeProtected)
					ClientHandler.displayUniversalKeyChangerScreen((BlockEntity) passcodeProtected);

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
