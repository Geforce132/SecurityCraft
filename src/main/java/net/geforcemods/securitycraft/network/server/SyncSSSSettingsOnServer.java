package net.geforcemods.securitycraft.network.server;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blockentities.SonicSecuritySystemBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

public class SyncSSSSettingsOnServer implements CustomPacketPayload {
	public static final ResourceLocation ID = new ResourceLocation(SecurityCraft.MODID, "sync_sss_settings_on_server");
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

	public SyncSSSSettingsOnServer(FriendlyByteBuf buf) {
		pos = buf.readBlockPos();
		dataType = buf.readEnum(DataType.class);

		if (dataType == DataType.REMOVE_POS)
			posToRemove = buf.readBlockPos();
	}

	@Override
	public void write(FriendlyByteBuf buf) {
		buf.writeBlockPos(pos);
		buf.writeEnum(dataType);

		if (dataType == DataType.REMOVE_POS)
			buf.writeBlockPos(posToRemove);
	}

	@Override
	public ResourceLocation id() {
		return ID;
	}

	public void handle(PlayPayloadContext ctx) {
		Player player = ctx.player().orElseThrow();
		Level level = player.level();

		if (level.getBlockEntity(pos) instanceof SonicSecuritySystemBlockEntity sss && sss.isOwnedBy(player)) {
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
