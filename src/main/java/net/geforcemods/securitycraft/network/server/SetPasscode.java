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
	private String passcode;

	public SetPasscode() {}

	public SetPasscode(BlockPos pos, String code) {
		this.pos = pos;
		passcode = PasscodeUtils.hashPasscodeWithoutSalt(code);
	}

	public SetPasscode(FriendlyByteBuf buf) {
		pos = buf.readBlockPos();
		passcode = buf.readUtf(Integer.MAX_VALUE / 4);
	}

	public void encode(FriendlyByteBuf buf) {
		buf.writeBlockPos(pos);
		buf.writeUtf(passcode);
	}

	public void handle(Supplier<NetworkEvent.Context> ctx) {
		Player player = ctx.get().getSender();
		Level level = player.level;

		if (!player.isSpectator() && level.getBlockEntity(pos) instanceof IPasscodeProtected passcodeProtected && (!(passcodeProtected instanceof IOwnable ownable) || ownable.isOwnedBy(player))) {
			passcodeProtected.hashAndSetPasscode(passcode, b -> passcodeProtected.openPasscodeGUI(level, pos, player));
			passcodeProtected.setPasscodeInAdjacentBlock(passcode);
		}
	}
}
