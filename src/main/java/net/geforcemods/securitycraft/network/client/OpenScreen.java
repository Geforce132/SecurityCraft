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
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

public class OpenScreen implements CustomPacketPayload {
	public static final ResourceLocation ID = new ResourceLocation(SecurityCraft.MODID, "open_screen");
	private DataType dataType;
	private BlockPos pos;
	private CompoundTag tag;
	private int entityId;

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

	public OpenScreen(DataType dataType, int entityId) {
		this.dataType = dataType;
		this.entityId = entityId;
	}

	public OpenScreen(FriendlyByteBuf buf) {
		dataType = buf.readEnum(DataType.class);

		if (dataType.needsPosition)
			pos = buf.readBlockPos();
		else if (dataType == DataType.SENTRY_REMOTE_ACCESS_TOOL)
			tag = buf.readNbt();
		else if (dataType == DataType.CHANGE_PASSCODE_FOR_ENTITY || dataType == DataType.CHECK_PASSCODE_FOR_ENTITY || dataType == DataType.SET_PASSCODE_FOR_ENTITY)
			entityId = buf.readVarInt();
	}

	@Override
	public void write(FriendlyByteBuf buf) {
		buf.writeEnum(dataType);

		if (dataType.needsPosition)
			buf.writeBlockPos(pos);
		else if (dataType == DataType.SENTRY_REMOTE_ACCESS_TOOL)
			buf.writeNbt(tag);
		else if (dataType == DataType.CHANGE_PASSCODE_FOR_ENTITY || dataType == DataType.CHECK_PASSCODE_FOR_ENTITY || dataType == DataType.SET_PASSCODE_FOR_ENTITY)
			buf.writeVarInt(entityId);
	}

	@Override
	public ResourceLocation id() {
		return ID;
	}

	public void handle(PlayPayloadContext ctx) {
		switch (dataType) {
			case ALARM:
				if (Minecraft.getInstance().level.getBlockEntity(pos) instanceof AlarmBlockEntity be)
					ClientHandler.displayAlarmScreen(be);

				break;
			case CHANGE_PASSCODE:
				if (Minecraft.getInstance().level.getBlockEntity(pos) instanceof IPasscodeProtected be)
					ClientHandler.displayUniversalKeyChangerScreen((BlockEntity) be);

				break;
			case CHANGE_PASSCODE_FOR_ENTITY:
				if (Minecraft.getInstance().level.getEntity(entityId) instanceof IPasscodeProtected be)
					ClientHandler.displayUniversalKeyChangerScreen((Entity) be);

				break;
			case CHECK_PASSCODE:
				if (Minecraft.getInstance().level.getBlockEntity(pos) instanceof IPasscodeProtected be)
					ClientHandler.displayCheckPasscodeScreen((BlockEntity) be);

				break;
			case CHECK_PASSCODE_FOR_BRIEFCASE:
				ItemStack briefcaseStack = PlayerUtils.getItemStackFromAnyHand(ClientHandler.getClientPlayer(), SCContent.BRIEFCASE.get());

				if (!briefcaseStack.isEmpty())
					ClientHandler.displayBriefcasePasscodeScreen(briefcaseStack.getHoverName());

				break;
			case CHECK_PASSCODE_FOR_ENTITY:
				if (Minecraft.getInstance().level.getEntity(entityId) instanceof IPasscodeProtected be)
					ClientHandler.displayCheckPasscodeScreen((Entity) be);

				break;
			case RIFT_STABILIZER:
				if (Minecraft.getInstance().level.getBlockEntity(pos) instanceof RiftStabilizerBlockEntity riftStabilizer)
					ClientHandler.displayRiftStabilizerScreen(riftStabilizer);

				break;
			case SENTRY_REMOTE_ACCESS_TOOL:
				ItemStack srat = PlayerUtils.getItemStackFromAnyHand(ClientHandler.getClientPlayer(), SCContent.SENTRY_REMOTE_ACCESS_TOOL.get());

				if (!srat.isEmpty()) {
					srat.setTag(tag);
					ClientHandler.displaySRATScreen(srat);
				}

				break;
			case SET_PASSCODE:
				if (Minecraft.getInstance().level.getBlockEntity(pos) instanceof IPasscodeProtected be)
					ClientHandler.displaySetPasscodeScreen((BlockEntity) be);

				break;
			case SET_PASSCODE_FOR_BRIEFCASE:
				ItemStack briefcase = PlayerUtils.getItemStackFromAnyHand(ClientHandler.getClientPlayer(), SCContent.BRIEFCASE.get());

				if (!briefcase.isEmpty())
					ClientHandler.displayBriefcaseSetupScreen(briefcase.getHoverName().plainCopy().append(Component.literal(" ")).append(Utils.localize("gui.securitycraft:passcode.setup")));

				break;
			case SET_PASSCODE_FOR_ENTITY:
				if (Minecraft.getInstance().level.getEntity(entityId) instanceof IPasscodeProtected be)
					ClientHandler.displaySetPasscodeScreen((Entity) be);

				break;
			case SONIC_SECURITY_SYSTEM:
				if (Minecraft.getInstance().level.getBlockEntity(pos) instanceof SonicSecuritySystemBlockEntity sss)
					ClientHandler.displaySonicSecuritySystemScreen(sss);

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
		RIFT_STABILIZER(true),
		SENTRY_REMOTE_ACCESS_TOOL(false),
		SET_PASSCODE(true),
		SET_PASSCODE_FOR_BRIEFCASE(false),
		SET_PASSCODE_FOR_ENTITY(false),
		SONIC_SECURITY_SYSTEM(true);

		public final boolean needsPosition;

		DataType(boolean needsPosition) {
			this.needsPosition = needsPosition;
		}
	}
}
