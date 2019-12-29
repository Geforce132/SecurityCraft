package net.geforcemods.securitycraft.network.client;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.misc.CustomModules;
import net.geforcemods.securitycraft.tileentity.DisguisableTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
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

	public static void encode(RefreshDisguisableModel message, PacketBuffer packet)
	{
		message.toBytes(packet);
	}

	public static RefreshDisguisableModel decode(PacketBuffer packet)
	{
		RefreshDisguisableModel message = new RefreshDisguisableModel();

		message.fromBytes(packet);
		return message;
	}

	public static void onMessage(RefreshDisguisableModel message, Supplier<NetworkEvent.Context> ctx)
	{
		DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> handleMessage(message, ctx));
	}

	@OnlyIn(Dist.CLIENT)
	public static void handleMessage(RefreshDisguisableModel message, Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(() -> {
			DisguisableTileEntity te = (DisguisableTileEntity)Minecraft.getInstance().world.getTileEntity(message.pos);

			if(te != null)
			{
				if(message.insert)
					te.insertModule(message.stack);
				else
					te.removeModule(CustomModules.DISGUISE);

				te.refreshModel();
			}
		});

		ctx.get().setPacketHandled(true);
	}
}
