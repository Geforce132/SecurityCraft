package net.geforcemods.securitycraft.network.server;

import io.netty.buffer.ByteBuf;
import net.geforcemods.securitycraft.blockentities.AlarmBlockEntity;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class SyncAlarmSettings implements IMessage {
	private BlockPos pos;
	private ResourceLocation soundEvent;
	private float pitch;
	private int soundLength;

	public SyncAlarmSettings() {}

	public SyncAlarmSettings(BlockPos pos, ResourceLocation soundEvent, float pitch, int soundLength) {
		this.pos = pos;
		this.soundEvent = soundEvent;
		this.pitch = pitch;
		this.soundLength = soundLength;
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeLong(pos.toLong());
		ByteBufUtils.writeUTF8String(buf, soundEvent.toString());
		buf.writeFloat(pitch);
		ByteBufUtils.writeVarInt(buf, soundLength, 5);
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		pos = BlockPos.fromLong(buf.readLong());
		soundEvent = new ResourceLocation(ByteBufUtils.readUTF8String(buf));
		pitch = buf.readFloat();
		soundLength = ByteBufUtils.readVarInt(buf, 5);
	}

	public static class Handler implements IMessageHandler<SyncAlarmSettings, IMessage> {
		@Override
		public IMessage onMessage(SyncAlarmSettings message, MessageContext context) {
			Utils.addScheduledTask(context.getServerHandler().player.world, () -> {
				EntityPlayer player = context.getServerHandler().player;
				TileEntity tile = player.world.getTileEntity(message.pos);

				if (!player.isSpectator() && tile instanceof AlarmBlockEntity) {
					AlarmBlockEntity be = (AlarmBlockEntity) tile;

					if (be.isOwnedBy(player)) {
						if (!message.soundEvent.equals(be.getSound()))
							be.setSound(message.soundEvent);

						if (message.pitch != be.getPitch())
							be.setPitch(message.pitch);

						if (message.soundLength != be.getSoundLength())
							be.setSoundLength(message.soundLength);
					}
				}
			});

			return null;
		}
	}
}
