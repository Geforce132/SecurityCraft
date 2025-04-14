package net.geforcemods.securitycraft.network.server;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blockentities.BlockPocketManagerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

public class SyncBlockPocketManager implements CustomPacketPayload {
	public static final ResourceLocation ID = new ResourceLocation(SecurityCraft.MODID, "sync_block_pocket_manager");
	private BlockPos pos;
	private int size;
	private boolean showOutline;
	private int autoBuildOffset;
	private int color;

	public SyncBlockPocketManager() {}

	public SyncBlockPocketManager(BlockPos pos, int size, boolean showOutline, int autoBuildOffset, int color) {
		this.pos = pos;
		this.size = size;
		this.showOutline = showOutline;
		this.autoBuildOffset = autoBuildOffset;
		this.color = color;
	}

	public SyncBlockPocketManager(FriendlyByteBuf buf) {
		pos = buf.readBlockPos();
		size = buf.readVarInt();
		showOutline = buf.readBoolean();
		autoBuildOffset = buf.readVarInt();
		color = buf.readInt();
	}

	@Override
	public void write(FriendlyByteBuf buf) {
		buf.writeBlockPos(pos);
		buf.writeVarInt(size);
		buf.writeBoolean(showOutline);
		buf.writeVarInt(autoBuildOffset);
		buf.writeInt(color);
	}

	@Override
	public ResourceLocation id() {
		return ID;
	}

	public void handle(PlayPayloadContext ctx) {
		Player player = ctx.player().orElseThrow();
		Level level = player.level();

		if (!player.isSpectator() && level.isLoaded(pos) && level.getBlockEntity(pos) instanceof BlockPocketManagerBlockEntity bpm && bpm.isOwnedBy(player)) {
			BlockState state = level.getBlockState(pos);

			bpm.setSize(size);
			bpm.setShowOutline(showOutline);
			bpm.setAutoBuildOffset(autoBuildOffset);
			bpm.setColor(color);
			bpm.setChanged();
			level.sendBlockUpdated(pos, state, state, 2);
		}
	}
}
