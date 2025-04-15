package net.geforcemods.securitycraft.network.server;

import java.util.function.Supplier;

import org.apache.logging.log4j.util.TriConsumer;

import net.geforcemods.securitycraft.api.IExplosive;
import net.geforcemods.securitycraft.api.IOwnable;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;

public class RemoteControlMine {
	private int x, y, z;
	private Action action;

	public RemoteControlMine() {}

	public RemoteControlMine(int x, int y, int z, Action action) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.action = action;
	}

	public RemoteControlMine(PacketBuffer buf) {
		x = buf.readInt();
		y = buf.readInt();
		z = buf.readInt();
		action = buf.readEnum(Action.class);
	}

	public void encode(PacketBuffer buf) {
		buf.writeInt(x);
		buf.writeInt(y);
		buf.writeInt(z);
		buf.writeEnum(action);
	}

	public void handle(Supplier<NetworkEvent.Context> ctx) {
		PlayerEntity player = ctx.get().getSender();
		World level = player.level;
		BlockPos pos = new BlockPos(x, y, z);
		BlockState state = level.getBlockState(pos);

		if (!player.isSpectator() && state.getBlock() instanceof IExplosive) {
			IExplosive explosive = ((IExplosive) state.getBlock());
			TileEntity te = level.getBlockEntity(pos);

			if (te instanceof IOwnable && ((IOwnable) te).isOwnedBy(player))
				action.act(explosive, level, pos);
		}
	}

	public enum Action {
		ACTIVATE(IExplosive::activateMine),
		DEFUSE(IExplosive::defuseMine),
		DETONATE(IExplosive::explode);

		private final TriConsumer<IExplosive, World, BlockPos> action;

		Action(TriConsumer<IExplosive, World, BlockPos> action) {
			this.action = action;
		}

		public void act(IExplosive explosive, World level, BlockPos pos) {
			action.accept(explosive, level, pos);
		}
	}
}
