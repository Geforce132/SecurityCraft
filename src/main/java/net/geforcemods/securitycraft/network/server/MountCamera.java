package net.geforcemods.securitycraft.network.server;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.blocks.SecurityCameraBlock;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.tileentity.SecurityCameraTileEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
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

	public static void encode(MountCamera message, FriendlyByteBuf buf)
	{
		buf.writeBlockPos(message.pos);
		buf.writeInt(message.id);
	}

	public static MountCamera decode(FriendlyByteBuf buf)
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
			ServerPlayer player = ctx.get().getSender();
			Level world = player.level;
			BlockState state = world.getBlockState(pos);

			if(state.getBlock() == SCContent.SECURITY_CAMERA.get())
			{
				BlockEntity te = world.getBlockEntity(pos);

				if(te instanceof SecurityCameraTileEntity && (((SecurityCameraTileEntity)te).getOwner().isOwner(player) || ((SecurityCameraTileEntity)te).hasModule(ModuleType.SMART)))
					((SecurityCameraBlock)state.getBlock()).mountCamera(world, pos.getX(), pos.getY(), pos.getZ(), id, player);
			}
		});

		ctx.get().setPacketHandled(true);
	}
}
