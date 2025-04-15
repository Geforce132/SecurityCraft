package net.geforcemods.securitycraft.network.server;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.blockentities.BlockPocketManagerBlockEntity;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.network.NetworkEvent;

public class AssembleBlockPocket {
	private BlockPos pos;
	private int size;

	public AssembleBlockPocket() {}

	public AssembleBlockPocket(BlockPocketManagerBlockEntity be) {
		pos = be.getBlockPos();
		size = be.getSize();
	}

	public AssembleBlockPocket(PacketBuffer buf) {
		pos = BlockPos.of(buf.readLong());
		size = buf.readInt();
	}

	public void encode(PacketBuffer buf) {
		buf.writeLong(pos.asLong());
		buf.writeInt(size);
	}

	public void handle(Supplier<NetworkEvent.Context> ctx) {
		PlayerEntity player = ctx.get().getSender();
		TileEntity be = player.level.getBlockEntity(pos);

		if (!player.isSpectator() && be instanceof BlockPocketManagerBlockEntity && ((BlockPocketManagerBlockEntity) be).isOwnedBy(player)) {
			TranslationTextComponent feedback;

			((BlockPocketManagerBlockEntity) be).setSize(size);
			feedback = ((BlockPocketManagerBlockEntity) be).autoAssembleMultiblock();

			if (feedback != null)
				PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.BLOCK_POCKET_MANAGER.get().getDescriptionId()), feedback, TextFormatting.DARK_AQUA);
		}
	}
}
