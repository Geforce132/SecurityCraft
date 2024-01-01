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
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

public class CheckPasscode implements CustomPacketPayload {
	public static final ResourceLocation ID = new ResourceLocation(SecurityCraft.MODID, "check_passcode");
	private String passcode;
	private int x, y, z;

	public CheckPasscode() {}

	public CheckPasscode(int x, int y, int z, String passcode) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.passcode = PasscodeUtils.hashPasscodeWithoutSalt(passcode);
	}

	public CheckPasscode(FriendlyByteBuf buf) {
		x = buf.readInt();
		y = buf.readInt();
		z = buf.readInt();
		passcode = buf.readUtf(Integer.MAX_VALUE / 4);
	}

	@Override
	public void write(FriendlyByteBuf buf) {
		buf.writeInt(x);
		buf.writeInt(y);
		buf.writeInt(z);
		buf.writeUtf(passcode);
	}

	@Override
	public ResourceLocation id() {
		return ID;
	}

	public void handle(PlayPayloadContext ctx) {
		BlockPos pos = new BlockPos(x, y, z);
		Player player = ctx.player().orElseThrow();

		if (player.level().getBlockEntity(pos) instanceof IPasscodeProtected be) {
			if (be.isOnCooldown())
				return;

			PasscodeUtils.hashPasscode(passcode, be.getSalt(), p -> {
				if (Arrays.equals(be.getPasscode(), p)) {
					player.closeContainer();
					be.activate(player);
				}
				else
					be.onIncorrectPasscodeEntered(player, passcode);
			});
		}
	}
}
