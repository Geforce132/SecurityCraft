package net.geforcemods.securitycraft.network.server;

import io.netty.buffer.ByteBuf;
import net.geforcemods.securitycraft.blockentities.AlarmBlockEntity;
import net.geforcemods.securitycraft.util.LevelUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class SyncAlarmSettings implements IMessage {
	private BlockPos pos;
	private SoundEvent soundEvent;
	private float pitch;
	private int soundLength;

	public SyncAlarmSettings() {}

	public SyncAlarmSettings(BlockPos pos, ResourceLocation soundEvent, float pitch, int soundLength) {
		this.pos = pos;
		this.soundEvent = ForgeRegistries.SOUND_EVENTS.getValue(soundEvent);
		this.pitch = pitch;
		this.soundLength = soundLength;
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeLong(pos.toLong());
		ByteBufUtils.writeRegistryEntry(buf, soundEvent);
		buf.writeFloat(pitch);
		ByteBufUtils.writeVarInt(buf, soundLength, 5);
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		pos = BlockPos.fromLong(buf.readLong());
		soundEvent = ByteBufUtils.readRegistryEntry(buf, ForgeRegistries.SOUND_EVENTS);
		pitch = buf.readFloat();
		soundLength = ByteBufUtils.readVarInt(buf, 5);
	}

	public static class Handler implements IMessageHandler<SyncAlarmSettings, IMessage> {
		@Override
		public IMessage onMessage(SyncAlarmSettings message, MessageContext context) {
			LevelUtils.addScheduledTask(context.getServerHandler().player.world, () -> {
				EntityPlayer player = context.getServerHandler().player;
				TileEntity tile = player.world.getTileEntity(message.pos);

				if (tile instanceof AlarmBlockEntity) {
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
