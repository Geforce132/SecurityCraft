package net.geforcemods.securitycraft.network.server;

import java.util.Arrays;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.IPasscodeProtected;
import net.geforcemods.securitycraft.util.PasscodeUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

public class CheckPasscode implements CustomPacketPayload {
	public static final ResourceLocation ID = new ResourceLocation(SecurityCraft.MODID, "check_passcode");
	private BlockPos pos;
	private int entityId;
	private String passcode;

	public CheckPasscode() {}

	public CheckPasscode(BlockPos pos, String passcode) {
		this.pos = pos;
		this.passcode = PasscodeUtils.hashPasscodeWithoutSalt(passcode);
	}

	public CheckPasscode(int entityId, String passcode) {
		this.entityId = entityId;
		this.passcode = PasscodeUtils.hashPasscodeWithoutSalt(passcode);
	}

	public CheckPasscode(FriendlyByteBuf buf) {
		if (buf.readBoolean())
			pos = buf.readBlockPos();
		else
			entityId = buf.readVarInt();

		passcode = buf.readUtf(Integer.MAX_VALUE / 4);
	}

	@Override
	public void write(FriendlyByteBuf buf) {
		boolean hasPos = pos != null;

		buf.writeBoolean(hasPos);

		if (hasPos)
			buf.writeBlockPos(pos);
		else
			buf.writeVarInt(entityId);

		buf.writeUtf(passcode);
	}

	@Override
	public ResourceLocation id() {
		return ID;
	}

	public void handle(PlayPayloadContext ctx) {
		Player player = ctx.player().orElseThrow();
		IPasscodeProtected passcodeProtected = getPasscodeProtected(player.level());

		if (passcodeProtected != null) {
			if (passcodeProtected.isOnCooldown())
				return;

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
}
