package net.geforcemods.securitycraft.network.server;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.IPasscodeProtected;
import net.geforcemods.securitycraft.util.PasscodeUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class SetPasscode implements CustomPacketPayload {
	public static final Type<SetPasscode> TYPE = new Type<>(new ResourceLocation(SecurityCraft.MODID, "set_passcode"));
	public static final StreamCodec<RegistryFriendlyByteBuf, SetPasscode> STREAM_CODEC = new StreamCodec<>() {
		@Override
		public SetPasscode decode(RegistryFriendlyByteBuf buf) {
			return new SetPasscode(buf);
		}

		@Override
		public void encode(RegistryFriendlyByteBuf buf, SetPasscode packet) {
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

	public SetPasscode(BlockPos pos, String passcode) {
		this.pos = pos;
		this.passcode = PasscodeUtils.hashPasscodeWithoutSalt(passcode);
	}

	public SetPasscode(int entityId, String passcode) {
		this.entityId = entityId;
		this.passcode = PasscodeUtils.hashPasscodeWithoutSalt(passcode);
	}

	private SetPasscode(RegistryFriendlyByteBuf buf) {
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
		Level level = player.level();
		IPasscodeProtected passcodeProtected = getPasscodeProtected(level);

		if (passcodeProtected != null && (!(passcodeProtected instanceof IOwnable ownable) || ownable.isOwnedBy(player))) {
			passcodeProtected.hashAndSetPasscode(passcode, b -> passcodeProtected.openPasscodeGUI(level, pos, player));
			passcodeProtected.setPasscodeInAdjacentBlock(passcode);
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
}
