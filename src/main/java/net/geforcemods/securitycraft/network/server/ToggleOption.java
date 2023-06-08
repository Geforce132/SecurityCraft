package net.geforcemods.securitycraft.network.server;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.api.ICustomizable;
import net.geforcemods.securitycraft.api.IOwnable;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

public class ToggleOption {
	private int x, y, z, id;

	public ToggleOption() {}

	public ToggleOption(int x, int y, int z, int id) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.id = id;
	}

	public ToggleOption(FriendlyByteBuf buf) {
		x = buf.readInt();
		y = buf.readInt();
		z = buf.readInt();
		id = buf.readInt();
	}

	public void encode(FriendlyByteBuf buf) {
		buf.writeInt(x);
		buf.writeInt(y);
		buf.writeInt(z);
		buf.writeInt(id);
	}

	public void handle(Supplier<NetworkEvent.Context> ctx) {
		BlockPos pos = new BlockPos(x, y, z);
		Player player = ctx.get().getSender();
		BlockEntity be = player.level().getBlockEntity(pos);

		if (be instanceof ICustomizable customizable && (!(be instanceof IOwnable ownable) || ownable.isOwnedBy(player))) {
			customizable.customOptions()[id].toggle();
			customizable.onOptionChanged(customizable.customOptions()[id]);
			player.level().sendBlockUpdated(pos, be.getBlockState(), be.getBlockState(), 3);
		}
	}
}
