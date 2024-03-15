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
	private boolean isEntity;
	private BlockPos pos;
	private int entityId;
	private String passcode;

	public CheckPasscode() {}

	public CheckPasscode(BlockPos pos, String passcode) {
		this.isEntity = false;
		this.pos = pos;
		this.passcode = PasscodeUtils.hashPasscodeWithoutSalt(passcode);
	}

	public CheckPasscode(int entityId, String passcode) {
		this.isEntity = true;
		this.entityId = entityId;
		this.passcode = PasscodeUtils.hashPasscodeWithoutSalt(passcode);
	}

	public CheckPasscode(FriendlyByteBuf buf) {
		isEntity = buf.readBoolean();

		if (isEntity)
			entityId = buf.readVarInt();
		else
			pos = buf.readBlockPos();

		passcode = buf.readUtf(Integer.MAX_VALUE / 4);
	}

	@Override
	public void write(FriendlyByteBuf buf) {
		buf.writeBoolean(isEntity);

		if (isEntity)
			buf.writeVarInt(entityId);
		else
			buf.writeBlockPos(pos);

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
		if (isEntity) {
			if (level.getEntity(entityId) instanceof IPasscodeProtected pp)
				return pp;
		}
		else if (level.getBlockEntity(pos) instanceof IPasscodeProtected pp)
			return pp;

		return null;
	}
}
