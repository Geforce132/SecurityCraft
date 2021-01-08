package net.geforcemods.securitycraft.network.server;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.tileentity.ProjectorTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;

public class SyncProjector {

	private BlockPos pos;
	private int data;
	private DataType dataType;

	public SyncProjector() {}

	public SyncProjector(BlockPos pos, int data, DataType dataType){
		this.pos = pos;
		this.data = data;
		this.dataType = dataType;
	}

	public void toBytes(PacketBuffer buf) {
		buf.writeBlockPos(pos);
		buf.writeEnumValue(dataType);

		if(dataType == DataType.HORIZONTAL)
			buf.writeBoolean(data == 1);
		else
			buf.writeVarInt(data);
	}

	public void fromBytes(PacketBuffer buf) {
		pos = buf.readBlockPos();
		dataType = buf.readEnumValue(DataType.class);

		if(dataType == DataType.HORIZONTAL)
			data = buf.readBoolean() ? 1 : 0;
		else
			data = buf.readVarInt();
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
			BlockPos pos = message.pos;
			World world = ctx.get().getSender().world;
			TileEntity te = world.getTileEntity(pos);

			if(world.isBlockPresent(pos) && te instanceof ProjectorTileEntity)
			{
				ProjectorTileEntity projector = (ProjectorTileEntity)te;
				BlockState state = world.getBlockState(pos);

				switch(message.dataType)
				{
					case WIDTH:
						projector.setProjectionWidth(message.data);
						break;
					case HEIGHT:
						projector.setProjectionHeight(message.data);
						break;
					case RANGE:
						projector.setProjectionRange(message.data);
						break;
					case OFFSET:
						projector.setProjectionOffset(message.data);
						break;
					case HORIZONTAL:
						projector.setHorizontal(message.data == 1);
						break;
					case INVALID: break;
				}

				world.notifyBlockUpdate(pos, state, state, 2);
			}
		});

		ctx.get().setPacketHandled(true);
	}

	public enum DataType
	{
		WIDTH, HEIGHT, RANGE, OFFSET, HORIZONTAL, INVALID;
	}
}
