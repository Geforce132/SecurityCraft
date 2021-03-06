package net.geforcemods.securitycraft.network.server;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.tileentity.ProjectorTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
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

	public static void encode(SyncProjector message, PacketBuffer buf)
	{
		buf.writeBlockPos(message.pos);
		buf.writeEnumValue(message.dataType);

		if(message.dataType == DataType.HORIZONTAL)
			buf.writeBoolean(message.data == 1);
		else
			buf.writeVarInt(message.data);
	}

	public static SyncProjector decode(PacketBuffer buf)
	{
		SyncProjector message = new SyncProjector();

		message.pos = buf.readBlockPos();
		message.dataType = buf.readEnumValue(DataType.class);

		if(message.dataType == DataType.HORIZONTAL)
			message.data = buf.readBoolean() ? 1 : 0;
		else
			message.data = buf.readVarInt();

		return message;
	}

	public static void onMessage(SyncProjector message, Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(() -> {
			BlockPos pos = message.pos;
			PlayerEntity player = ctx.get().getSender();
			World world = player.world;
			TileEntity te = world.getTileEntity(pos);

			if(world.isBlockPresent(pos) && te instanceof ProjectorTileEntity && ((ProjectorTileEntity)te).getOwner().isOwner(player))
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
