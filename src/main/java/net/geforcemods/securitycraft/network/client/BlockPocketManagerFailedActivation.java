package net.geforcemods.securitycraft.network.client;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blockentities.BlockPocketManagerBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

public class BlockPocketManagerFailedActivation implements CustomPacketPayload {
	public static final ResourceLocation ID = new ResourceLocation(SecurityCraft.MODID, "block_pocket_manager_failed_activation");
	private BlockPos pos;

	public BlockPocketManagerFailedActivation() {}

	public BlockPocketManagerFailedActivation(BlockPos pos) {
		this.pos = pos;
	}

	public BlockPocketManagerFailedActivation(FriendlyByteBuf buf) {
		pos = buf.readBlockPos();
	}

	@Override
	public void write(FriendlyByteBuf buf) {
		buf.writeBlockPos(pos);
	}

	@Override
	public ResourceLocation id() {
		return ID;
	}

	public void handle(PlayPayloadContext ctx) {
		Minecraft mc = Minecraft.getInstance();

		if (mc.level.getBlockEntity(pos) instanceof BlockPocketManagerBlockEntity be)
			be.setEnabled(false);
	}
}
