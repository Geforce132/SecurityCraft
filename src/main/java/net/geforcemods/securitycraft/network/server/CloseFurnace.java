package net.geforcemods.securitycraft.network.server;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.api.CustomizableSCTE;
import net.geforcemods.securitycraft.api.IPasswordProtected;
import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.blocks.BlockKeypadFurnace;
import net.geforcemods.securitycraft.tileentity.TileEntityKeypadFurnace;
import net.geforcemods.securitycraft.tileentity.TileEntityOwnable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;

public class CloseFurnace
{
	private BlockPos pos;

	public CloseFurnace() {}

	public CloseFurnace(BlockPos pos)
	{
		this.pos = pos;
	}

	public void toBytes(PacketBuffer buf)
	{
		buf.writeBlockPos(pos);
	}

	public void fromBytes(PacketBuffer buf)
	{
		pos = buf.readBlockPos();
	}

	public static void encode(CloseFurnace message, PacketBuffer packet)
	{
		message.toBytes(packet);
	}

	public static CloseFurnace decode(PacketBuffer packet)
	{
		CloseFurnace message = new CloseFurnace();

		message.fromBytes(packet);
		return message;
	}

	public static void onMessage(CloseFurnace message, Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(() -> {
			BlockPos pos = message.pos;
			EntityPlayer player = ctx.get().getSender();
			World world = player.world;
			TileEntity te = world.getTileEntity(pos);
			NonNullList<ItemStack> modules = ((CustomizableSCTE) te).modules;
			NonNullList<ItemStack> inventory = ((TileEntityKeypadFurnace) te).furnaceItemStacks;
			int[] times = {
					((TileEntityKeypadFurnace) te).furnaceBurnTime,
					((TileEntityKeypadFurnace) te).currentItemBurnTime,
					((TileEntityKeypadFurnace) te).cookTime,
					((TileEntityKeypadFurnace) te).totalCookTime
			};
			String password = ((IPasswordProtected) te).getPassword();
			Owner owner = ((TileEntityOwnable) te).getOwner();

			world.setBlockState(pos, world.getBlockState(pos).with(BlockKeypadFurnace.OPEN, false));
			((CustomizableSCTE) te).modules = modules;
			((TileEntityKeypadFurnace) te).furnaceItemStacks = inventory;
			((TileEntityKeypadFurnace) te).furnaceBurnTime = times[0];
			((TileEntityKeypadFurnace) te).currentItemBurnTime = times[1];
			((TileEntityKeypadFurnace) te).cookTime = times[2];
			((TileEntityKeypadFurnace) te).totalCookTime = times[3];
			((TileEntityOwnable) te).getOwner().set(owner.getUUID(), owner.getName());
			((IPasswordProtected) te).setPassword(password);
		});
		ctx.get().setPacketHandled(true);
	}
}
