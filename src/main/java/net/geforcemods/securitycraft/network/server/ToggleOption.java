package net.geforcemods.securitycraft.network.server;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.api.ICustomizable;
import net.geforcemods.securitycraft.api.IOwnable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

public class ToggleOption {
	private int x, y, z, id;

	public ToggleOption() {}

	public ToggleOption(int x, int y, int z, int id) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.id = id;
	}

	public ToggleOption(PacketBuffer buf) {
		x = buf.readInt();
		y = buf.readInt();
		z = buf.readInt();
		id = buf.readInt();
	}

	public void encode(PacketBuffer buf) {
		buf.writeInt(x);
		buf.writeInt(y);
		buf.writeInt(z);
		buf.writeInt(id);
	}

	public void handle(Supplier<NetworkEvent.Context> ctx) {
		BlockPos pos = new BlockPos(x, y, z);
		PlayerEntity player = ctx.get().getSender();
		TileEntity te = player.level.getBlockEntity(pos);

		if (te instanceof ICustomizable && (!(te instanceof IOwnable) || ((IOwnable) te).isOwnedBy(player))) {
			((ICustomizable) te).customOptions()[id].toggle();
			((ICustomizable) te).onOptionChanged(((ICustomizable) te).customOptions()[id]);
			player.level.sendBlockUpdated(pos, te.getBlockState(), te.getBlockState(), 3);
		}
	}
}
