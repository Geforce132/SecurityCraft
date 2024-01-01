package net.geforcemods.securitycraft.network.server;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blockentities.BlockPocketManagerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

public class ToggleBlockPocketManager implements CustomPacketPayload {
	public static final ResourceLocation ID = new ResourceLocation(SecurityCraft.MODID, "toggle_block_pocket_manager");
	private BlockPos pos;
	private int size;
	private boolean enabling;

	public ToggleBlockPocketManager() {}

	public ToggleBlockPocketManager(BlockPocketManagerBlockEntity be, boolean enabling, int size) {
		pos = be.getBlockPos();
		this.enabling = enabling;
		this.size = size;
	}

	public ToggleBlockPocketManager(FriendlyByteBuf buf) {
		pos = BlockPos.of(buf.readLong());
		enabling = buf.readBoolean();
		size = buf.readInt();
	}

	@Override
	public void write(FriendlyByteBuf buf) {
		buf.writeLong(pos.asLong());
		buf.writeBoolean(enabling);
		buf.writeInt(size);
	}

	@Override
	public ResourceLocation id() {
		return ID;
	}

	public void handle(PlayPayloadContext ctx) {
		Player player = ctx.player().orElseThrow();

		if (player.level().getBlockEntity(pos) instanceof BlockPocketManagerBlockEntity be && be.isOwnedBy(player)) {
			be.setSize(size);

			if (enabling)
				be.enableMultiblock();
			else
				be.disableMultiblock();

			be.setChanged();
		}
	}
}
