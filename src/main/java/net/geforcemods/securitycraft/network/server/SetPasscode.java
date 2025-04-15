package net.geforcemods.securitycraft.network.server;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.IPasscodeProtected;
import net.geforcemods.securitycraft.util.PasscodeUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;

public class SetPasscode {
	private BlockPos pos;
	private int entityId;
	private String passcode;

	public SetPasscode() {}

	public SetPasscode(BlockPos pos, String code) {
		this.pos = pos;
		passcode = PasscodeUtils.hashPasscodeWithoutSalt(code);
	}

	public SetPasscode(int entityId, String code) {
		this.entityId = entityId;
		passcode = PasscodeUtils.hashPasscodeWithoutSalt(code);
	}

	public SetPasscode(FriendlyByteBuf buf) {
		if (buf.readBoolean())
			pos = buf.readBlockPos();
		else
			entityId = buf.readVarInt();

		passcode = buf.readUtf(Integer.MAX_VALUE / 4);
	}

	public void encode(FriendlyByteBuf buf) {
		boolean hasPos = pos != null;

		buf.writeBoolean(hasPos);

		if (hasPos)
			buf.writeBlockPos(pos);
		else
			buf.writeVarInt(entityId);

		buf.writeUtf(passcode);
	}

	public void handle(Supplier<NetworkEvent.Context> ctx) {
		Player player = ctx.get().getSender();
		Level level = player.level();
		IPasscodeProtected passcodeProtected = getPasscodeProtected(level);

		if (!player.isSpectator() && passcodeProtected != null && (!(passcodeProtected instanceof IOwnable ownable) || ownable.isOwnedBy(player))) {
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
