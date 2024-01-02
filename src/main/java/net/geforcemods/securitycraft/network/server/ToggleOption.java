package net.geforcemods.securitycraft.network.server;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.ICustomizable;
import net.geforcemods.securitycraft.api.IOwnable;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

public class ToggleOption implements CustomPacketPayload {
	public static final ResourceLocation ID = new ResourceLocation(SecurityCraft.MODID, "toggle_option");
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

	@Override
	public void write(FriendlyByteBuf buf) {
		buf.writeInt(x);
		buf.writeInt(y);
		buf.writeInt(z);
		buf.writeInt(id);
	}

	@Override
	public ResourceLocation id() {
		return ID;
	}

	public void handle(PlayPayloadContext ctx) {
		BlockPos pos = new BlockPos(x, y, z);
		Player player = ctx.player().orElseThrow();
		BlockEntity be = player.level().getBlockEntity(pos);

		if (be instanceof ICustomizable customizable && (!(be instanceof IOwnable ownable) || ownable.isOwnedBy(player))) {
			customizable.customOptions()[id].toggle();
			customizable.onOptionChanged(customizable.customOptions()[id]);
			player.level().sendBlockUpdated(pos, be.getBlockState(), be.getBlockState(), 3);
		}
	}
}
