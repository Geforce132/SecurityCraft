package net.geforcemods.securitycraft.network.client;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.misc.EnumCustomModules;
import net.geforcemods.securitycraft.tileentity.TileEntityKeypad;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkEvent;

public class RefreshKeypadModel
{
	private BlockPos pos;
	private boolean insert;
	private ItemStack stack;

	public RefreshKeypadModel() {}

	public RefreshKeypadModel(BlockPos pos, boolean insert, ItemStack stack)
	{
		this.pos = pos;
		this.insert = insert;
		this.stack = stack;
	}

	public void toBytes(PacketBuffer buf)
	{
		buf.writeBlockPos(pos);
		buf.writeBoolean(insert);
		buf.writeItemStack(stack);
	}

	public void fromBytes(PacketBuffer buf)
	{
		pos = buf.readBlockPos();
		insert = buf.readBoolean();
		stack = buf.readItemStack();
	}

	public static void encode(RefreshKeypadModel message, PacketBuffer packet)
	{
		message.toBytes(packet);
	}

	public static RefreshKeypadModel decode(PacketBuffer packet)
	{
		RefreshKeypadModel message = new RefreshKeypadModel();

		message.fromBytes(packet);
		return message;
	}

	public static void onMessage(RefreshKeypadModel message, Supplier<NetworkEvent.Context> ctx)
	{
		DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> handleMessage(message, ctx));
	}

	@OnlyIn(Dist.CLIENT)
	public static void handleMessage(RefreshKeypadModel message, Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(() -> {
			TileEntityKeypad te = (TileEntityKeypad)Minecraft.getInstance().world.getTileEntity(message.pos);

			if(te != null)
			{
				if(message.insert)
					te.insertModule(message.stack);
				else
					te.removeModule(EnumCustomModules.DISGUISE);

				te.refreshModel();
			}
		});

		ctx.get().setPacketHandled(true);
	}
}
