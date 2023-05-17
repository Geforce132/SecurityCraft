package net.geforcemods.securitycraft.network.server;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.IPasswordProtected;
import net.geforcemods.securitycraft.blockentities.KeypadChestBlockEntity;
import net.geforcemods.securitycraft.blocks.KeypadChestBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraftforge.network.NetworkEvent;

public class SetPassword {
	private String password;
	private int x, y, z;

	public SetPassword() {}

	public SetPassword(int x, int y, int z, String code) {
		this.x = x;
		this.y = y;
		this.z = z;
		password = code;
	}

	public SetPassword(FriendlyByteBuf buf) {
		x = buf.readInt();
		y = buf.readInt();
		z = buf.readInt();
		password = buf.readUtf(Integer.MAX_VALUE / 4);
	}

	public void encode(FriendlyByteBuf buf) {
		buf.writeInt(x);
		buf.writeInt(y);
		buf.writeInt(z);
		buf.writeUtf(password);
	}

	public void handle(Supplier<NetworkEvent.Context> ctx) {
		BlockPos pos = new BlockPos(x, y, z);
		Player player = ctx.get().getSender();
		Level level = player.level;

		if (level.getBlockEntity(pos) instanceof IPasswordProtected be && (!(be instanceof IOwnable ownable) || ownable.isOwnedBy(player))) {
			be.setPassword(password);

			if (be instanceof KeypadChestBlockEntity chestBe)
				checkAndUpdateAdjacentChest(chestBe, level, pos, password, player);
		}
	}

	private void checkAndUpdateAdjacentChest(KeypadChestBlockEntity be, Level level, BlockPos pos, String codeToSet, Player player) {
		if (be.getBlockState().getValue(KeypadChestBlock.TYPE) != ChestType.SINGLE) {
			BlockPos offsetPos = pos.relative(KeypadChestBlock.getConnectedDirection(be.getBlockState()));
			BlockEntity otherBe = level.getBlockEntity(offsetPos);

			if (otherBe instanceof KeypadChestBlockEntity chestBe && be.getOwner().owns(chestBe)) {
				chestBe.setPassword(codeToSet);
				level.sendBlockUpdated(offsetPos, otherBe.getBlockState(), otherBe.getBlockState(), 2);
			}
		}
	}
}
