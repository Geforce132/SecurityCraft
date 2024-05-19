package net.geforcemods.securitycraft.network.server;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.blockentities.SonicSecuritySystemBlockEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;

public class SyncSSSSettingsOnServer {
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

	public SyncSSSSettingsOnServer(PacketBuffer buf) {
		pos = buf.readBlockPos();
		dataType = buf.readEnum(DataType.class);

		if (dataType == DataType.REMOVE_POS)
			posToRemove = buf.readBlockPos();
	}

	public void encode(PacketBuffer buf) {
		buf.writeBlockPos(pos);
		buf.writeEnum(dataType);

		if (dataType == DataType.REMOVE_POS)
			buf.writeBlockPos(posToRemove);
	}

	public void handle(Supplier<NetworkEvent.Context> ctx) {
		World level = ctx.get().getSender().level;
		TileEntity te = level.getBlockEntity(pos);

		if (te instanceof SonicSecuritySystemBlockEntity && ((SonicSecuritySystemBlockEntity) te).isOwnedBy(ctx.get().getSender())) {
			SonicSecuritySystemBlockEntity sss = (SonicSecuritySystemBlockEntity) te;

			switch (dataType) {
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
					sss.delink(posToRemove, false);
					break;
				case INVERT_FUNCTIONALITY:
					sss.setDisableBlocksWhenTuneIsPlayed(!sss.disablesBlocksWhenTuneIsPlayed());
			}

			sss.setChanged();
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
