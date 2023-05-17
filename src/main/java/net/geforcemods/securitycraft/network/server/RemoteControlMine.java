package net.geforcemods.securitycraft.network.server;

import java.util.function.Supplier;

import org.apache.logging.log4j.util.TriConsumer;

import net.geforcemods.securitycraft.api.IExplosive;
import net.geforcemods.securitycraft.api.IOwnable;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.network.NetworkEvent;

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

	public RemoteControlMine(FriendlyByteBuf buf) {
		x = buf.readInt();
		y = buf.readInt();
		z = buf.readInt();
		action = buf.readEnum(Action.class);
	}

	public void encode(FriendlyByteBuf buf) {
		buf.writeInt(x);
		buf.writeInt(y);
		buf.writeInt(z);
		buf.writeEnum(action);
	}

	public void handle(Supplier<NetworkEvent.Context> ctx) {
		Player player = ctx.get().getSender();
		Level level = player.level;
		BlockPos pos = new BlockPos(x, y, z);
		BlockState state = level.getBlockState(pos);

		if (state.getBlock() instanceof IExplosive explosive && level.getBlockEntity(pos) instanceof IOwnable be && be.isOwnedBy(player))
			action.act(explosive, level, pos);
	}

	public static enum Action {
		ACTIVATE(IExplosive::activateMine),
		DEFUSE(IExplosive::defuseMine),
		DETONATE(IExplosive::explode);

		private final TriConsumer<IExplosive, Level, BlockPos> action;

		Action(TriConsumer<IExplosive, Level, BlockPos> action) {
			this.action = action;
		}

		public void act(IExplosive explosive, Level level, BlockPos pos) {
			action.accept(explosive, level, pos);
		}
	}
}
