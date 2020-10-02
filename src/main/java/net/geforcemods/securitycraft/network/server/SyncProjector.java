package net.geforcemods.securitycraft.network.server;

import java.util.function.Supplier;

import io.netty.buffer.ByteBuf;
import net.geforcemods.securitycraft.tileentity.ProjectorTileEntity;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.block.BlockState;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;

public class SyncProjector {

	private int x, y, z;
	private int width, range, offset;

	public SyncProjector(){

	}

	public SyncProjector(int x, int y, int z, int width, int range, int offset){
		this.x = x;
		this.y = y;
		this.z = z;
		this.width = width;
		this.range = range;
		this.offset = offset;
	}

	public void toBytes(ByteBuf buf) {
		buf.writeInt(x);
		buf.writeInt(y);
		buf.writeInt(z);
		buf.writeInt(width);
		buf.writeInt(range);
		buf.writeInt(offset);
	}

	public void fromBytes(ByteBuf buf) {
		x = buf.readInt();
		y = buf.readInt();
		z = buf.readInt();
		width = buf.readInt();
		range = buf.readInt();
		offset = buf.readInt();
	}

	public static void encode(SyncProjector message, PacketBuffer packet)
	{
		message.toBytes(packet);
	}

	public static SyncProjector decode(PacketBuffer packet)
	{
		SyncProjector message = new SyncProjector();

		message.fromBytes(packet);
		return message;
	}

	public static void onMessage(SyncProjector message, Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(() -> {
			BlockPos pos = BlockUtils.toPos(message.x, message.y, message.z);
			World world = ctx.get().getSender().world;
			TileEntity te = world.getTileEntity(pos);

			if(world.isBlockPresent(pos) && te instanceof ProjectorTileEntity)
			{
				ProjectorTileEntity projector = (ProjectorTileEntity)te;
				BlockState state = world.getBlockState(pos);

				projector.setProjectionWidth(message.width);
				projector.setProjectionRange(message.range);
				projector.setProjectionOffset(message.offset);
				world.notifyBlockUpdate(pos, state, state, 2);
			}
		});

		ctx.get().setPacketHandled(true);
	}
}
