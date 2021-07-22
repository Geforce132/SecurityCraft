package net.geforcemods.securitycraft.network.server;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.containers.KeycardReaderContainer;
import net.geforcemods.securitycraft.tileentity.KeycardReaderTileEntity;
import net.geforcemods.securitycraft.util.ModuleUtils;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

public class SetKeycardUses
{
	private BlockPos pos;
	private int uses;

	public SetKeycardUses() {}

	public SetKeycardUses(BlockPos pos, int uses)
	{
		this.pos = pos;
		this.uses = uses;
	}

	public static void encode(SetKeycardUses message, FriendlyByteBuf buf)
	{
		buf.writeBlockPos(message.pos);
		buf.writeVarInt(message.uses);
	}

	public static SetKeycardUses decode(FriendlyByteBuf buf)
	{
		SetKeycardUses message = new SetKeycardUses();

		message.pos = buf.readBlockPos();
		message.uses = buf.readVarInt();
		return message;
	}

	public static void onMessage(SetKeycardUses message, Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(() -> {
			BlockPos pos = message.pos;
			Player player = ctx.get().getSender();
			BlockEntity tile = player.level.getBlockEntity(pos);

			if(tile instanceof KeycardReaderTileEntity)
			{
				KeycardReaderTileEntity te = (KeycardReaderTileEntity)tile;

				if(te.getOwner().isOwner(player) || ModuleUtils.isAllowed(te, player))
				{
					AbstractContainerMenu container = player.containerMenu;

					if(container instanceof KeycardReaderContainer)
						((KeycardReaderContainer)container).setKeycardUses(message.uses);
				}
			}
		});

		ctx.get().setPacketHandled(true);
	}
}
