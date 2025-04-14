package net.geforcemods.securitycraft.network.server;

import org.apache.logging.log4j.util.TriConsumer;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.IExplosive;
import net.geforcemods.securitycraft.api.IOwnable;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record RemoteControlMine(BlockPos pos, Action action) implements CustomPacketPayload {

	public static final Type<RemoteControlMine> TYPE = new Type<>(new ResourceLocation(SecurityCraft.MODID, "remote_control_mine"));
	//@formatter:off
	public static final StreamCodec<RegistryFriendlyByteBuf, RemoteControlMine> STREAM_CODEC = StreamCodec.composite(
			BlockPos.STREAM_CODEC, RemoteControlMine::pos,
			NeoForgeStreamCodecs.enumCodec(Action.class), RemoteControlMine::action,
			RemoteControlMine::new);
	//@formatter:on
	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public void handle(IPayloadContext ctx) {
		Player player = ctx.player();
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
