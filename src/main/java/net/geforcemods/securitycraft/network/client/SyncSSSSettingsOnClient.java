package net.geforcemods.securitycraft.network.client;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.tileentity.SonicSecuritySystemTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;

public class SyncSSSSettingsOnClient {

	private BlockPos pos;
	private DataType dataType;

	public SyncSSSSettingsOnClient() {}

	public SyncSSSSettingsOnClient(BlockPos pos, DataType dataType){
		this.pos = pos;
		this.dataType = dataType;
	}

	public static void encode(SyncSSSSettingsOnClient message, PacketBuffer buf)
	{
		buf.writeBlockPos(message.pos);
		buf.writeEnumValue(message.dataType);
	}

	public static SyncSSSSettingsOnClient decode(PacketBuffer buf)
	{
		SyncSSSSettingsOnClient message = new SyncSSSSettingsOnClient();

		message.pos = buf.readBlockPos();
		message.dataType = buf.readEnumValue(DataType.class);

		return message;
	}

	public static void onMessage(SyncSSSSettingsOnClient message, Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(() -> {
			BlockPos pos = message.pos;

			PlayerEntity player = Minecraft.getInstance().player;
			World world = player.world;
			TileEntity te = world.getTileEntity(pos);

			if(te instanceof SonicSecuritySystemTileEntity && ((SonicSecuritySystemTileEntity) te).getOwner().isOwner(player))
			{
				SonicSecuritySystemTileEntity sss = (SonicSecuritySystemTileEntity) te;

				switch(message.dataType)
				{
				case RECORDING_ON:
					sss.setRecording(true);
					break;
				case RECORDING_OFF:
					sss.setRecording(false);
					break;
				case LISTENING_ON:
					sss.setListening(true);
					break;
				case LISTENING_OFF:
					sss.setListening(false);
					break;
				case CLEAR_NOTES:
					sss.clearNotes();
					break;
				}
			}
		});

		ctx.get().setPacketHandled(true);
	}

	public enum DataType
	{
		RECORDING_ON, RECORDING_OFF, LISTENING_ON, LISTENING_OFF, CLEAR_NOTES;
	}
}
