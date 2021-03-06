package net.geforcemods.securitycraft.network.server;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.blocks.SecurityCameraBlock;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.tileentity.SecurityCameraTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;

public class MountCamera
{
	private BlockPos pos;
	private int id;

	public MountCamera() {}

	public MountCamera(BlockPos pos, int id)
	{
		this.pos = pos;
		this.id = id;
	}

	public static void encode(MountCamera message, PacketBuffer buf)
	{
		buf.writeBlockPos(message.pos);
		buf.writeInt(message.id);
	}

	public static MountCamera decode(PacketBuffer buf)
	{
		MountCamera message = new MountCamera();

		message.pos = buf.readBlockPos();
		message.id = buf.readInt();
		return message;
	}

	public static void onMessage(MountCamera message, Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(() -> {
			BlockPos pos = message.pos;
			int id = message.id;
			ServerPlayerEntity player = ctx.get().getSender();
			World world = player.world;
			BlockState state = world.getBlockState(pos);

			if(state.getBlock() == SCContent.SECURITY_CAMERA.get())
			{
				TileEntity te = world.getTileEntity(pos);

				if(te instanceof SecurityCameraTileEntity && (((SecurityCameraTileEntity)te).getOwner().isOwner(player) || ((SecurityCameraTileEntity)te).hasModule(ModuleType.SMART)))
					((SecurityCameraBlock)state.getBlock()).mountCamera(world, pos.getX(), pos.getY(), pos.getZ(), id, player);
			}
		});

		ctx.get().setPacketHandled(true);
	}
}
