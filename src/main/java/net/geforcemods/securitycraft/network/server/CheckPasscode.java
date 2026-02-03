package net.geforcemods.securitycraft.network.server;

import java.util.Arrays;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.IPasscodeProtected;
import net.geforcemods.securitycraft.util.PasscodeUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class CheckPasscode implements CustomPacketPayload {
	public static final Type<CheckPasscode> TYPE = new Type<>(SecurityCraft.resLoc("check_passcode"));
	public static final StreamCodec<RegistryFriendlyByteBuf, CheckPasscode> STREAM_CODEC = new StreamCodec<>() {
		@Override
		public CheckPasscode decode(RegistryFriendlyByteBuf buf) {
			return new CheckPasscode(buf);
		}

		@Override
		public void encode(RegistryFriendlyByteBuf buf, CheckPasscode packet) {
			boolean hasPos = packet.pos != null;

			buf.writeBoolean(hasPos);

			if (hasPos)
				buf.writeBlockPos(packet.pos);
			else
				buf.writeVarInt(packet.entityId);

			buf.writeUtf(packet.passcode);
		}
	};
	private BlockPos pos;
	private int entityId;
	private String passcode;

	public CheckPasscode(BlockPos pos, String passcode) {
		this.pos = pos;
		this.passcode = PasscodeUtils.hashPasscodeWithoutSalt(passcode);
	}

	public CheckPasscode(int entityId, String passcode) {
		this.entityId = entityId;
		this.passcode = PasscodeUtils.hashPasscodeWithoutSalt(passcode);
	}

	private CheckPasscode(RegistryFriendlyByteBuf buf) {
		if (buf.readBoolean())
			pos = buf.readBlockPos();
		else
			entityId = buf.readVarInt();

		passcode = buf.readUtf(Integer.MAX_VALUE / 4);
	}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public void handle(IPayloadContext ctx) {
		Player player = ctx.player();
		IPasscodeProtected passcodeProtected = getPasscodeProtected(player.level());

		if (passcodeProtected != null) {
			if (PasscodeUtils.isOnCooldown(player)) {
				PlayerUtils.sendMessageToPlayer(player, Component.literal("SecurityCraft"), Component.translatable("messages.securitycraft:passcodeProtected.onCooldown"), ChatFormatting.RED);

				if (ConfigHandler.SERVER.passcodeSpamLogWarningEnabled.get())
					SecurityCraft.LOGGER.warn(formatForPasscodeProtected(ConfigHandler.SERVER.passcodeSpamLogWarning.get(), player, passcodeProtected));

				return;
			}

			if (passcodeProtected.isOnCooldown())
				return;

			PasscodeUtils.setOnCooldown(player);
			PasscodeUtils.hashPasscode(passcode, passcodeProtected.getSalt(), p -> {
				if (Arrays.equals(passcodeProtected.getPasscode(), p)) {
					player.closeContainer();
					passcodeProtected.activate(player);
				}
				else
					passcodeProtected.onIncorrectPasscodeEntered(player, passcode);
			});
		}
	}

	private IPasscodeProtected getPasscodeProtected(Level level) {
		if (pos != null) {
			if (level.getBlockEntity(pos) instanceof IPasscodeProtected pp)
				return pp;
		}
		else if (level.getEntity(entityId) instanceof IPasscodeProtected pp)
			return pp;

		return null;
	}

	private String formatForPasscodeProtected(String logMessage, Player player, IPasscodeProtected passcodeProtected) {
		Level level = player.level();
		BlockPos pos = BlockPos.ZERO;
		String name = "undefined";

		if (passcodeProtected instanceof BlockEntity be) {
			pos = be.getBlockPos();
			name = level.getBlockState(pos).getBlock().getName().getString();
		}
		else if (passcodeProtected instanceof Entity entity) {
			pos = entity.blockPosition();
			name = entity.getType().getDescription().getString();
		}

		return String.format(logMessage, player.getGameProfile().name(), name, new GlobalPos(level.dimension(), pos));
	}
}
