package net.geforcemods.securitycraft.network.server;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blockentities.BlockPocketManagerBlockEntity;
import net.geforcemods.securitycraft.network.client.BlockPocketManagerFailedActivation;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.network.NetworkEvent;

public class ToggleBlockPocketManager {
	private BlockPos pos;
	private int size;
	private boolean enabling;

	public ToggleBlockPocketManager() {}

	public ToggleBlockPocketManager(BlockPocketManagerBlockEntity be, boolean enabling) {
		pos = be.getBlockPos();
		this.enabling = enabling;
		size = be.getSize();
	}

	public ToggleBlockPocketManager(PacketBuffer buf) {
		pos = BlockPos.of(buf.readLong());
		enabling = buf.readBoolean();
		size = buf.readInt();
	}

	public void encode(PacketBuffer buf) {
		buf.writeLong(pos.asLong());
		buf.writeBoolean(enabling);
		buf.writeInt(size);
	}

	public void handle(Supplier<NetworkEvent.Context> ctx) {
		PlayerEntity player = ctx.get().getSender();
		TileEntity te = player.level.getBlockEntity(pos);

		if (!player.isSpectator() && te instanceof BlockPocketManagerBlockEntity && ((BlockPocketManagerBlockEntity) te).isOwnedBy(player)) {
			TranslationTextComponent feedback;

			((BlockPocketManagerBlockEntity) te).setSize(size);

			if (enabling)
				feedback = ((BlockPocketManagerBlockEntity) te).enableMultiblock();
			else
				feedback = ((BlockPocketManagerBlockEntity) te).disableMultiblock();

			if (feedback != null) {
				if (enabling && !((BlockPocketManagerBlockEntity) te).isEnabled())
					SecurityCraft.channel.reply(new BlockPocketManagerFailedActivation(pos), ctx.get());

				PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.BLOCK_POCKET_MANAGER.get().getDescriptionId()), feedback, TextFormatting.DARK_AQUA, false);
			}
		}
	}
}
