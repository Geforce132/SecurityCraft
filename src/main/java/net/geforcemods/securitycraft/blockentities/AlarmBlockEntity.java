package net.geforcemods.securitycraft.blockentities;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.CustomizableBlockEntity;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.BooleanOption;
import net.geforcemods.securitycraft.api.Option.DisabledOption;
import net.geforcemods.securitycraft.api.Option.IntOption;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.misc.SCSounds;
import net.geforcemods.securitycraft.network.client.PlayAlarmSound;
import net.geforcemods.securitycraft.util.AlarmSoundHandler;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.registries.ForgeRegistries;

public class AlarmBlockEntity extends CustomizableBlockEntity implements ITickableTileEntity {
	public static final int MAXIMUM_ALARM_SOUND_LENGTH = 3600; //one hour
	private IntOption range = new IntOption(this::getBlockPos, "range", 17, 0, ConfigHandler.SERVER.maxAlarmRange.get(), 1);
	private DisabledOption disabled = new DisabledOption(false);
	private BooleanOption resetCooldown = new BooleanOption("resetCooldown", true);
	private int cooldown = 0;
	private boolean isPowered = false;
	private SoundEvent sound = SCSounds.ALARM.event;
	private float pitch = 1.0F;
	private boolean soundPlaying = false;
	private int soundLength = 2;

	public AlarmBlockEntity() {
		super(SCContent.ALARM_BLOCK_ENTITY.get());
	}

	@Override
	public void tick() {
		if (level.isClientSide && soundPlaying && (isDisabled() || !isPowered))
			stopPlayingSound();

		if (!isDisabled() && isPowered && --cooldown <= 0) { //Even though isPowered is only explicitly set serverside, it is always synched to the client via NBT due to the block state change
			if (!level.isClientSide) {
				double rangeSqr = Math.pow(range.get(), 2);
				SoundEvent soundEvent = isModuleEnabled(ModuleType.SMART) ? sound : SCSounds.ALARM.event;

				for (ServerPlayerEntity player : ((ServerWorld) level).getPlayers(p -> p.blockPosition().distSqr(worldPosition) <= rangeSqr)) {
					float volume = (float) (1.0F - ((player.blockPosition().distSqr(worldPosition)) / rangeSqr));

					SecurityCraft.channel.send(PacketDistributor.PLAYER.with(() -> player), new PlayAlarmSound(worldPosition, soundEvent, volume, pitch));
				}
			}

			setCooldown(soundLength * 20);
		}
	}

	@Override
	public CompoundNBT save(CompoundNBT tag) {
		super.save(tag);
		tag.putInt("cooldown", cooldown);
		tag.putBoolean("isPowered", isPowered);
		tag.putString("sound", sound.location.toString());
		tag.putFloat("pitch", pitch);
		tag.putInt("delay", soundLength);
		return tag;
	}

	@Override
	public void load(BlockState state, CompoundNBT tag) {
		super.load(state, tag);

		cooldown = tag.getInt("cooldown");
		isPowered = tag.getBoolean("isPowered");

		if (tag.contains("sound", Constants.NBT.TAG_STRING))
			setSound(new ResourceLocation(tag.getString("sound")));
		else
			setSound(SCSounds.ALARM.location);

		if (tag.contains("pitch"))
			pitch = tag.getFloat("pitch");
		else
			pitch = 1.0F;

		soundLength = tag.getInt("delay");
	}

	public void setSound(ResourceLocation soundEvent) {
		sound = ForgeRegistries.SOUND_EVENTS.getValue(soundEvent);
		setChanged();
	}

	public SoundEvent getSound() {
		return isModuleEnabled(ModuleType.SMART) ? sound : SCSounds.ALARM.event;
	}

	public void setPitch(float pitch) {
		this.pitch = pitch;
	}

	public float getPitch() {
		return pitch;
	}

	public int getSoundLength() {
		return soundLength;
	}

	public void setSoundLength(int soundLength) {
		this.soundLength = MathHelper.clamp(soundLength, 1, MAXIMUM_ALARM_SOUND_LENGTH);
		setCooldown(0);

		if (level.isClientSide)
			stopPlayingSound();
	}

	public void setCooldown(int cooldown) {
		this.cooldown = cooldown;
	}

	public int getCooldown() {
		return cooldown;
	}

	public boolean isPowered() {
		return isPowered;
	}

	public void setPowered(boolean isPowered) {
		this.isPowered = isPowered;

		if (isPowered && resetCooldown.get())
			setCooldown(0);

		setChanged();
	}

	public boolean isDisabled() {
		return disabled.get();
	}

	@Override
	public ModuleType[] acceptedModules() {
		return new ModuleType[] {
				ModuleType.SMART
		};
	}

	@Override
	public Option<?>[] customOptions() {
		return new Option[] {
				range, disabled, resetCooldown
		};
	}

	@Override
	public void setRemoved() {
		super.setRemoved();

		if (level.isClientSide && soundPlaying)
			stopPlayingSound();
	}

	public void playSound(double x, double y, double z, SoundEvent sound, float volume, float pitch) {
		AlarmSoundHandler.playSound(this, x, y, z, sound, SoundCategory.BLOCKS, volume, pitch);
		soundPlaying = true;
	}

	public void stopPlayingSound() {
		AlarmSoundHandler.stopCurrentSound(this);
		soundPlaying = false;
	}
}
