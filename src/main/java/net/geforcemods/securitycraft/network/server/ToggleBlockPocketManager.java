package net.geforcemods.securitycraft.network.server;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.tileentity.BlockPocketManagerTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

public class ToggleBlockPocketManager
{
	private BlockPos pos;
	private int size;
	private boolean enabling;

	public ToggleBlockPocketManager() {}

	public ToggleBlockPocketManager(BlockPocketManagerTileEntity te, boolean enabling, int size)
	{
		pos = te.getBlockPos();
		this.enabling = enabling;
		this.size = size;
	}

	public static void encode(ToggleBlockPocketManager message, FriendlyByteBuf buf)
	{
		buf.writeLong(message.pos.asLong());
		buf.writeBoolean(message.enabling);
		buf.writeInt(message.size);
	}

	public static ToggleBlockPocketManager decode(FriendlyByteBuf buf)
	{
		ToggleBlockPocketManager message = new ToggleBlockPocketManager();

		message.pos = BlockPos.of(buf.readLong());
		message.enabling = buf.readBoolean();
		message.size = buf.readInt();
		return message;
	}

	public static void onMessage(ToggleBlockPocketManager message, Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(() -> {
			Player player = ctx.get().getSender();

			if(player.level.getBlockEntity(message.pos) instanceof BlockPocketManagerTileEntity te && te.getOwner().isOwner(player))
			{
				te.size = message.size;

				if(message.enabling)
					te.enableMultiblock();
				else
					te.disableMultiblock();
			}
		});

		ctx.get().setPacketHandled(true);
	}
}
