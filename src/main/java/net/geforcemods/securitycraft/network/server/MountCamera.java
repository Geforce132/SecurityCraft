package net.geforcemods.securitycraft.network.server;

import java.util.function.Supplier;

import io.netty.buffer.ByteBuf;
import net.geforcemods.securitycraft.blocks.SecurityCameraBlock;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;

public class MountCamera {

	private int x;
	private int y;
	private int z;
	private int id;

	public MountCamera(){

	}

	public MountCamera(int x, int y, int z, int id){
		this.x = x;
		this.y = y;
		this.z = z;
		this.id = id;
	}

	public void toBytes(ByteBuf buf) {
	}

	public void fromBytes(ByteBuf buf) {
	}

	public static void encode(MountCamera message, PacketBuffer buf)
	{
		buf.writeInt(message.x);
		buf.writeInt(message.y);
		buf.writeInt(message.z);
		buf.writeInt(message.id);
	}

	public static MountCamera decode(PacketBuffer buf)
	{
		MountCamera message = new MountCamera();

		message.x = buf.readInt();
		message.y = buf.readInt();
		message.z = buf.readInt();
		message.id = buf.readInt();
		return message;
	}

	public static void onMessage(MountCamera message, Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(() -> {
			int x = message.x;
			int y = message.y;
			int z = message.z;
			int id = message.id;
			ServerPlayerEntity player = ctx.get().getSender();
			World world = player.world;

			if((BlockUtils.getBlock(world, BlockUtils.toPos(x, y, z)) instanceof SecurityCameraBlock))
				((SecurityCameraBlock) BlockUtils.getBlock(world, x, y, z)).mountCamera(world, x, y, z, id, player);
		});

		ctx.get().setPacketHandled(true);
	}
}
