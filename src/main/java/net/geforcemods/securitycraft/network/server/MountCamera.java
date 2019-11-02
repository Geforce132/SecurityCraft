package net.geforcemods.securitycraft.network.server;

import java.util.function.Supplier;

import io.netty.buffer.ByteBuf;
import net.geforcemods.securitycraft.blocks.BlockSecurityCamera;
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
		buf.writeInt(x);
		buf.writeInt(y);
		buf.writeInt(z);
		buf.writeInt(id);
	}

	public void fromBytes(ByteBuf buf) {
		x = buf.readInt();
		y = buf.readInt();
		z = buf.readInt();
		id = buf.readInt();
	}

	public static void encode(MountCamera message, PacketBuffer packet)
	{
		message.toBytes(packet);
	}

	public static MountCamera decode(PacketBuffer packet)
	{
		MountCamera message = new MountCamera();

		message.fromBytes(packet);
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

			if((BlockUtils.getBlock(world, BlockUtils.toPos(x, y, z)) instanceof BlockSecurityCamera))
				((BlockSecurityCamera) BlockUtils.getBlock(world, x, y, z)).mountCamera(world, x, y, z, id, player);
		});

		ctx.get().setPacketHandled(true);
	}
}
