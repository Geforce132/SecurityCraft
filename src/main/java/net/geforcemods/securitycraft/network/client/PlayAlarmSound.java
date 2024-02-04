package net.geforcemods.securitycraft.network.client;

import io.netty.buffer.ByteBuf;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blockentities.AlarmBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PlayAlarmSound implements IMessage {
	private BlockPos bePos;
	private ResourceLocation sound;
	private int soundX, soundY, soundZ;
	private float volume, pitch;

	public PlayAlarmSound() {}

	public PlayAlarmSound(BlockPos bePos, ResourceLocation sound, float volume, float pitch) {
		this.bePos = bePos;
		this.sound = sound;
		this.soundX = (int) (bePos.getX() * 8.0F);
		this.soundY = (int) (bePos.getY() * 8.0F);
		this.soundZ = (int) (bePos.getZ() * 8.0F);
		this.volume = volume;
		this.pitch = pitch;
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeLong(bePos.toLong());
		ByteBufUtils.writeUTF8String(buf, sound.toString());
		buf.writeInt(soundX);
		buf.writeInt(soundY);
		buf.writeInt(soundZ);
		buf.writeFloat(volume);
		buf.writeFloat(pitch);
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		bePos = BlockPos.fromLong(buf.readLong());
		sound = new ResourceLocation(ByteBufUtils.readUTF8String(buf));
		soundX = buf.readInt();
		soundY = buf.readInt();
		soundZ = buf.readInt();
		volume = buf.readFloat();
		pitch = buf.readFloat();
	}

	public double getX() {
		return soundX / 8.0F;
	}

	public double getY() {
		return soundY / 8.0F;
	}

	public double getZ() {
		return soundZ / 8.0F;
	}

	public static class Handler implements IMessageHandler<PlayAlarmSound, IMessage> {
		@Override
		@SideOnly(Side.CLIENT)
		public IMessage onMessage(PlayAlarmSound message, MessageContext context) {
			Minecraft.getMinecraft().addScheduledTask(() -> {
				World level = SecurityCraft.proxy.getClientLevel();
				TileEntity te = level.getTileEntity(message.bePos);

				if (te instanceof AlarmBlockEntity) {
					((AlarmBlockEntity) te).setPowered(true);
					((AlarmBlockEntity) te).playSound(message.getX(), message.getY(), message.getZ(), new SoundEvent(message.sound), message.volume, message.pitch);
				}
			});

			return null;
		}
	}
}
