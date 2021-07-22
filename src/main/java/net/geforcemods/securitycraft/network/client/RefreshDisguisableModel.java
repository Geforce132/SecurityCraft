package net.geforcemods.securitycraft.network.client;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.tileentity.DisguisableTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

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

	public static void encode(RefreshDisguisableModel message, FriendlyByteBuf buf)
	{
		buf.writeBlockPos(message.pos);
		buf.writeBoolean(message.insert);
		buf.writeItem(message.stack);
	}

	public static RefreshDisguisableModel decode(FriendlyByteBuf buf)
	{
		RefreshDisguisableModel message = new RefreshDisguisableModel();

		message.pos = buf.readBlockPos();
		message.insert = buf.readBoolean();
		message.stack = buf.readItem();
		return message;
	}

	public static void onMessage(RefreshDisguisableModel message, Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(() -> {
			DisguisableTileEntity te = (DisguisableTileEntity)Minecraft.getInstance().level.getBlockEntity(message.pos);

			if(te != null)
			{
				if(message.insert)
					te.insertModule(message.stack);
				else
					te.removeModule(ModuleType.DISGUISE);

				te.refreshModel();
			}
		});

		ctx.get().setPacketHandled(true);
	}
}
