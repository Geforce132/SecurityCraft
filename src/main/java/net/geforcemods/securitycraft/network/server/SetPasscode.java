package net.geforcemods.securitycraft.network.server;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.IPasscodeProtected;
import net.geforcemods.securitycraft.blockentities.KeypadChestBlockEntity;
import net.geforcemods.securitycraft.blocks.KeypadChestBlock;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraftforge.network.NetworkEvent;

public class SetPasscode {
	private String passcode;
	private int x, y, z;

	public SetPasscode() {}

	public SetPasscode(int x, int y, int z, String code) {
		this.x = x;
		this.y = y;
		this.z = z;
		passcode = Utils.hashPasscode(code, null);
	}

	public static void encode(SetPasscode message, FriendlyByteBuf buf) {
		buf.writeInt(message.x);
		buf.writeInt(message.y);
		buf.writeInt(message.z);
		buf.writeUtf(message.passcode);
	}

	public static SetPasscode decode(FriendlyByteBuf buf) {
		SetPasscode message = new SetPasscode();

		message.x = buf.readInt();
		message.y = buf.readInt();
		message.z = buf.readInt();
		message.passcode = buf.readUtf(Integer.MAX_VALUE / 4);
		return message;
	}

	public static void onMessage(SetPasscode message, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			BlockPos pos = new BlockPos(message.x, message.y, message.z);
			String passcode = message.passcode;
			Player player = ctx.get().getSender();
			Level level = player.level;

			if (level.getBlockEntity(pos) instanceof IPasscodeProtected be && (!(be instanceof IOwnable ownable) || ownable.isOwnedBy(player))) {
				be.hashAndSetPasscode(passcode);

				if (be instanceof KeypadChestBlockEntity chestBe)
					checkAndUpdateAdjacentChest(chestBe, level, pos, passcode, be.getSalt());
			}
		});

		ctx.get().setPacketHandled(true);
	}

	private static void checkAndUpdateAdjacentChest(KeypadChestBlockEntity te, Level level, BlockPos pos, String codeToSet, byte[] salt) {
		if (te.getBlockState().getValue(KeypadChestBlock.TYPE) != ChestType.SINGLE) {
			BlockPos offsetPos = pos.relative(KeypadChestBlock.getConnectedDirection(te.getBlockState()));
			BlockEntity otherBe = level.getBlockEntity(offsetPos);

			if (otherBe instanceof KeypadChestBlockEntity chestBe && te.getOwner().owns(chestBe)) {
				chestBe.hashAndSetPasscode(codeToSet, salt);
				level.sendBlockUpdated(offsetPos, otherBe.getBlockState(), otherBe.getBlockState(), 2);
			}
		}
	}
}
