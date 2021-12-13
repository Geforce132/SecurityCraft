package net.geforcemods.securitycraft.network.server;

import io.netty.buffer.ByteBuf;
import net.geforcemods.securitycraft.tileentity.TileEntitySonicSecuritySystem;
import net.geforcemods.securitycraft.util.WorldUtils;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class SyncSSSSettingsOnServer implements IMessage {

	private BlockPos pos;
	private DataType dataType;

	public SyncSSSSettingsOnServer() {}

	public SyncSSSSettingsOnServer(BlockPos pos, DataType dataType){
		this.pos = pos;
		this.dataType = dataType;
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeLong(pos.toLong());
		buf.writeInt(dataType.ordinal());
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		pos = BlockPos.fromLong(buf.readLong());
		dataType = DataType.values()[buf.readInt()];
	}

	public static class Handler implements IMessageHandler<SyncSSSSettingsOnServer, IMessage> {
		@Override
		public IMessage onMessage(SyncSSSSettingsOnServer message, MessageContext ctx)
		{
			WorldUtils.addScheduledTask(ctx.getServerHandler().player.world, () -> {
				BlockPos pos = message.pos;
				World world = ctx.getServerHandler().player.world;
				TileEntity te = world.getTileEntity(pos);

				if(te instanceof TileEntitySonicSecuritySystem && ((TileEntitySonicSecuritySystem) te).getOwner().isOwner(ctx.getServerHandler().player))
				{
					TileEntitySonicSecuritySystem sss = (TileEntitySonicSecuritySystem) te;

					switch(message.dataType)
					{
						case POWER_ON:
							sss.setActive(true);
							break;
						case POWER_OFF:
							sss.setActive(false);

							if(sss.isRecording())
								sss.setRecording(false);
							break;
						case SOUND_ON:
							sss.setPings(true);
							break;
						case SOUND_OFF:
							sss.setPings(false);
							break;
						case RECORDING_ON:
							sss.setRecording(true);
							break;
						case RECORDING_OFF:
							sss.setRecording(false);
							break;
						case CLEAR_NOTES:
							sss.clearNotes();
							break;
					}
				}
			});
			return null;
		}
	}

	public enum DataType
	{
		POWER_ON, POWER_OFF, SOUND_ON, SOUND_OFF, RECORDING_ON, RECORDING_OFF, CLEAR_NOTES;
	}
}
