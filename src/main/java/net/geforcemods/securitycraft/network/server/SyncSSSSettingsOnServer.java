package net.geforcemods.securitycraft.network.server;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.blockentities.SonicSecuritySystemBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

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

	public static void encode(SyncSSSSettingsOnServer message, FriendlyByteBuf buf) {
		buf.writeBlockPos(message.pos);
		buf.writeEnum(message.dataType);

		if (message.dataType == DataType.REMOVE_POS)
			buf.writeBlockPos(message.posToRemove);
	}

	public static SyncSSSSettingsOnServer decode(FriendlyByteBuf buf) {
		SyncSSSSettingsOnServer message = new SyncSSSSettingsOnServer();

		message.pos = buf.readBlockPos();
		message.dataType = buf.readEnum(DataType.class);

		if (message.dataType == DataType.REMOVE_POS)
			message.posToRemove = buf.readBlockPos();

		return message;
	}

	public static void onMessage(SyncSSSSettingsOnServer message, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			BlockPos pos = message.pos;
			Level level = ctx.get().getSender().level;

			if (level.getBlockEntity(pos) instanceof SonicSecuritySystemBlockEntity sss && sss.isOwnedBy(ctx.get().getSender())) {
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
				}
			}
		});

		ctx.get().setPacketHandled(true);
	}

	public enum DataType {
		POWER_ON,
		POWER_OFF,
		SOUND_ON,
		SOUND_OFF,
		RECORDING_ON,
		RECORDING_OFF,
		CLEAR_NOTES,
		REMOVE_POS;
	}
}
