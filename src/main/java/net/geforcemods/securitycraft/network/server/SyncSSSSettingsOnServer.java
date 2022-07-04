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

	public static void encode(SyncSSSSettingsOnServer message, PacketBuffer buf) {
		buf.writeBlockPos(message.pos);
		buf.writeEnum(message.dataType);

		if (message.dataType == DataType.REMOVE_POS)
			buf.writeBlockPos(message.posToRemove);
	}

	public static SyncSSSSettingsOnServer decode(PacketBuffer buf) {
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
			World world = ctx.get().getSender().level;
			TileEntity te = world.getBlockEntity(pos);

			if (te instanceof SonicSecuritySystemBlockEntity && ((SonicSecuritySystemBlockEntity) te).getOwner().isOwner(ctx.get().getSender())) {
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
