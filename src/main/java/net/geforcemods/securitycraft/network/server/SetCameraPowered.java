package net.geforcemods.securitycraft.network.server;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.api.CustomizableTileEntity;
import net.geforcemods.securitycraft.api.OwnableTileEntity;
import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.blocks.SecurityCameraBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;

public class SetCameraPowered
{
	private BlockPos pos;
	private boolean powered;

	public SetCameraPowered() {}

	public SetCameraPowered(BlockPos pos, boolean powered)
	{
		this.pos = pos;
		this.powered = powered;
	}

	public void toBytes(PacketBuffer buf)
	{
		buf.writeBlockPos(pos);
		buf.writeBoolean(powered);
	}

	public void fromBytes(PacketBuffer buf)
	{
		pos = buf.readBlockPos();
		powered = buf.readBoolean();
	}

	public static void encode(SetCameraPowered message, PacketBuffer packet)
	{
		message.toBytes(packet);
	}

	public static SetCameraPowered decode(PacketBuffer packet)
	{
		SetCameraPowered message = new SetCameraPowered();

		message.fromBytes(packet);
		return message;
	}

	public static void onMessage(SetCameraPowered message, Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(() -> {
			BlockPos pos = message.pos;
			PlayerEntity player = ctx.get().getSender();
			World world = player.world;
			TileEntity te = world.getTileEntity(pos);
			NonNullList<ItemStack> modules = ((CustomizableTileEntity) te).modules;
			Owner owner = ((OwnableTileEntity) te).getOwner();

			world.setBlockState(pos, world.getBlockState(pos).with(SecurityCameraBlock.POWERED, message.powered));
			((CustomizableTileEntity) te).modules = modules;
			((OwnableTileEntity) te).getOwner().set(owner.getUUID(), owner.getName());
			world.notifyNeighborsOfStateChange(pos.offset(world.getBlockState(pos).get(SecurityCameraBlock.FACING), -1), world.getBlockState(pos).getBlock());
		});
		ctx.get().setPacketHandled(true);
	}
}
