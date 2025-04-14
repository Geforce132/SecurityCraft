package net.geforcemods.securitycraft.network.server;

import org.apache.logging.log4j.util.TriConsumer;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.IExplosive;
import net.geforcemods.securitycraft.api.IOwnable;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

public class RemoteControlMine implements CustomPacketPayload {
	public static final ResourceLocation ID = new ResourceLocation(SecurityCraft.MODID, "remote_control_mine");
	private BlockPos pos;
	private Action action;

	public RemoteControlMine() {}

	public RemoteControlMine(BlockPos pos, Action action) {
		this.pos = pos;
		this.action = action;
	}

	public RemoteControlMine(FriendlyByteBuf buf) {
		pos = buf.readBlockPos();
		action = buf.readEnum(Action.class);
	}

	@Override
	public void write(FriendlyByteBuf buf) {
		buf.writeBlockPos(pos);
		buf.writeEnum(action);
	}

	@Override
	public ResourceLocation id() {
		return ID;
	}

	public void handle(PlayPayloadContext ctx) {
		Player player = ctx.player().orElseThrow();
		Level level = player.level();
		BlockState state = level.getBlockState(pos);

		if (!player.isSpectator() && state.getBlock() instanceof IExplosive explosive && level.getBlockEntity(pos) instanceof IOwnable be && be.isOwnedBy(player))
			action.act(explosive, level, pos);
	}

	public enum Action {
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
