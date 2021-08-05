package net.geforcemods.securitycraft.network.client;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.ClientHandler;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.tileentity.DisguisableTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

public class RefreshDisguisableModel
{
	private BlockPos pos;
	private boolean insert;
	private ItemStack stack;

	public RefreshDisguisableModel() {}

	public RefreshDisguisableModel(BlockPos pos, boolean insert, ItemStack stack)
	{
		this.pos = pos;
		this.insert = insert;
		this.stack = stack;
	}

	public static void encode(RefreshDisguisableModel message, PacketBuffer buf)
	{
		buf.writeBlockPos(message.pos);
		buf.writeBoolean(message.insert);
		buf.writeItemStack(message.stack);
	}

	public static RefreshDisguisableModel decode(PacketBuffer buf)
	{
		RefreshDisguisableModel message = new RefreshDisguisableModel();

		message.pos = buf.readBlockPos();
		message.insert = buf.readBoolean();
		message.stack = buf.readItemStack();
		return message;
	}

	public static void onMessage(RefreshDisguisableModel message, Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(() -> {
			DisguisableTileEntity te = (DisguisableTileEntity)Minecraft.getInstance().world.getTileEntity(message.pos);

			if(te != null)
			{
				if(message.insert)
					te.insertModule(message.stack);
				else
					te.removeModule(ModuleType.DISGUISE);

				ClientHandler.refreshModelData(te);
			}
		});

		ctx.get().setPacketHandled(true);
	}
}
