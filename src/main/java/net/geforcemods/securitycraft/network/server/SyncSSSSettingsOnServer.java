package net.geforcemods.securitycraft.network.server;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blockentities.SonicSecuritySystemBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class SyncSSSSettingsOnServer implements CustomPacketPayload {
	public static final Type<SyncSSSSettingsOnServer> TYPE = new Type<>(new ResourceLocation(SecurityCraft.MODID, "sync_sss_settings_on_server"));
	public static final StreamCodec<RegistryFriendlyByteBuf, SyncSSSSettingsOnServer> STREAM_CODEC = new StreamCodec<>() {
		public SyncSSSSettingsOnServer decode(RegistryFriendlyByteBuf buf) {
			BlockPos pos = buf.readBlockPos();
			DataType dataType = buf.readEnum(DataType.class);

			if (dataType == DataType.REMOVE_POS)
				return new SyncSSSSettingsOnServer(pos, dataType, buf.readGlobalPos());
			else
				return new SyncSSSSettingsOnServer(pos, dataType);
		}

		@Override
		public void encode(RegistryFriendlyByteBuf buf, SyncSSSSettingsOnServer packet) {
			buf.writeBlockPos(packet.pos);
			buf.writeEnum(packet.dataType);

			if (packet.dataType == DataType.REMOVE_POS)
				buf.writeGlobalPos(packet.posToRemove);
		}
	};
	private BlockPos pos;
	private DataType dataType;
	private GlobalPos posToRemove;

	public SyncSSSSettingsOnServer() {}

	public SyncSSSSettingsOnServer(BlockPos pos, DataType dataType) {
		this(pos, dataType, null);
	}

	public SyncSSSSettingsOnServer(BlockPos pos, DataType dataType, GlobalPos posToRemove) {
		this.pos = pos;
		this.dataType = dataType;
		this.posToRemove = posToRemove;
	}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public void handle(IPayloadContext ctx) {
		Player player = ctx.player();
		Level level = player.level();

		if (level.getBlockEntity(pos) instanceof SonicSecuritySystemBlockEntity sss && sss.isOwnedBy(player)) {
			switch (dataType) {
				case POWER_ON -> sss.setActive(true);
				case POWER_OFF -> {
					sss.setActive(false);

					if (sss.isRecording())
						sss.setRecording(false);
				}
				case SOUND_ON -> sss.setPings(true);
				case SOUND_OFF -> sss.setPings(false);
				case RECORDING_ON -> sss.setRecording(true);
				case RECORDING_OFF -> sss.setRecording(false);
				case CLEAR_NOTES -> sss.clearNotes();
				case REMOVE_POS -> sss.delink(posToRemove, false);
				case INVERT_FUNCTIONALITY -> sss.setDisableBlocksWhenTuneIsPlayed(!sss.disablesBlocksWhenTuneIsPlayed());
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
