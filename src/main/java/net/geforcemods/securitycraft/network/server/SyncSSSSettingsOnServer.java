package net.geforcemods.securitycraft.network.server;

import io.netty.buffer.ByteBuf;
import net.geforcemods.securitycraft.blockentities.SonicSecuritySystemBlockEntity;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class SyncSSSSettingsOnServer implements IMessage {
	private BlockPos pos;
	private DataType dataType;
	private BlockPos posToRemove;

	public SyncSSSSettingsOnServer() {}

	public SyncSSSSettingsOnServer(BlockPos pos, DataType dataType) {
		this(pos, dataType, null);
	}

	public SyncSSSSettingsOnServer(BlockPos pos, DataType dataType, BlockPos posToRemove) {
		this.pos = pos;
		this.dataType = dataType;
		this.posToRemove = posToRemove;
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeLong(pos.toLong());
		buf.writeInt(dataType.ordinal());

		if (dataType == DataType.REMOVE_POS)
			buf.writeLong(posToRemove.toLong());
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		pos = BlockPos.fromLong(buf.readLong());
		dataType = DataType.values()[buf.readInt()];

		if (dataType == DataType.REMOVE_POS)
			posToRemove = BlockPos.fromLong(buf.readLong());
	}

	public static class Handler implements IMessageHandler<SyncSSSSettingsOnServer, IMessage> {
		@Override
		public IMessage onMessage(SyncSSSSettingsOnServer message, MessageContext ctx) {
			Utils.addScheduledTask(ctx.getServerHandler().player.world, () -> {
				EntityPlayer player = ctx.getServerHandler().player;
				World world = player.world;
				TileEntity te = world.getTileEntity(message.pos);

				if (!player.isSpectator() && te instanceof SonicSecuritySystemBlockEntity && ((SonicSecuritySystemBlockEntity) te).isOwnedBy(player)) {
					SonicSecuritySystemBlockEntity sss = (SonicSecuritySystemBlockEntity) te;

					switch (message.dataType) {
						case POWER_ON:
							sss.setActive(true);
							break;
						case POWER_OFF:
							sss.setActive(false);

							if (sss.isRecording())
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
						case REMOVE_POS:
							sss.delink(message.posToRemove, false);
							break;
						case INVERT_FUNCTIONALITY:
							sss.setDisableBlocksWhenTuneIsPlayed(!sss.disablesBlocksWhenTuneIsPlayed());
					}

					sss.markDirty();
				}
			});

			return null;
		}
	}

	public enum DataType {
		POWER_ON,
		POWER_OFF,
		SOUND_ON,
		SOUND_OFF,
		RECORDING_ON,
		RECORDING_OFF,
		CLEAR_NOTES,
		REMOVE_POS,
		INVERT_FUNCTIONALITY
	}
}
