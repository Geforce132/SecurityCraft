package net.geforcemods.securitycraft.network.server;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.api.CustomizableTileEntity;
import net.geforcemods.securitycraft.api.IPasswordProtected;
import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.blocks.KeypadFurnaceBlock;
import net.geforcemods.securitycraft.tileentity.KeypadFurnaceTileEntity;
import net.geforcemods.securitycraft.tileentity.OwnableTileEntity;
import net.minecraft.entity.player.PlayerEntity;
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
			PlayerEntity player = ctx.get().getSender();
			World world = player.world;
			TileEntity te = world.getTileEntity(pos);
			NonNullList<ItemStack> modules = ((CustomizableTileEntity) te).modules;
			NonNullList<ItemStack> inventory = ((KeypadFurnaceTileEntity) te).furnaceItemStacks;
			int[] times = {
					((KeypadFurnaceTileEntity) te).furnaceBurnTime,
					((KeypadFurnaceTileEntity) te).currentItemBurnTime,
					((KeypadFurnaceTileEntity) te).cookTime,
					((KeypadFurnaceTileEntity) te).totalCookTime
			};
			String password = ((IPasswordProtected) te).getPassword();
			Owner owner = ((OwnableTileEntity) te).getOwner();

			world.setBlockState(pos, world.getBlockState(pos).with(KeypadFurnaceBlock.OPEN, false));
			((CustomizableTileEntity) te).modules = modules;
			((KeypadFurnaceTileEntity) te).furnaceItemStacks = inventory;
			((KeypadFurnaceTileEntity) te).furnaceBurnTime = times[0];
			((KeypadFurnaceTileEntity) te).currentItemBurnTime = times[1];
			((KeypadFurnaceTileEntity) te).cookTime = times[2];
			((KeypadFurnaceTileEntity) te).totalCookTime = times[3];
			((OwnableTileEntity) te).getOwner().set(owner.getUUID(), owner.getName());
			((IPasswordProtected) te).setPassword(password);
		});
		ctx.get().setPacketHandled(true);
	}
}
