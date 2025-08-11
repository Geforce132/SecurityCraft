package net.geforcemods.securitycraft.blockentities;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.CustomizableBlockEntity;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.BooleanOption;
import net.geforcemods.securitycraft.api.Option.DisabledOption;
import net.geforcemods.securitycraft.api.Option.IntOption;
import net.geforcemods.securitycraft.blocks.AlarmBlock;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.misc.SCSounds;
import net.geforcemods.securitycraft.network.client.PlayAlarmSound;
import net.geforcemods.securitycraft.util.AlarmSoundHandler;
import net.geforcemods.securitycraft.util.ITickingBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.registries.ForgeRegistries;

public class AlarmBlockEntity extends CustomizableBlockEntity implements ITickingBlockEntity {
	public static final int MAXIMUM_ALARM_SOUND_LENGTH = 3600; //one hour
	private IntOption range = new IntOption("range", 17, 0, ConfigHandler.SERVER.maxAlarmRange.get(), 1);
	private DisabledOption disabled = new DisabledOption(false);
	private BooleanOption resetCooldown = new BooleanOption("resetCooldown", true);
	private int cooldown = 0;
	private boolean isPowered = false;
	private SoundEvent sound = SCSounds.ALARM.event;
	private float pitch = 1.0F;
	private boolean soundPlaying = false;
	private int soundLength = 2;

	public AlarmBlockEntity(BlockPos pos, BlockState state) {
		super(SCContent.ALARM_BLOCK_ENTITY.get(), pos, state);
	}

	@Override
	public void tick(Level level, BlockPos pos, BlockState state) {
		if (level.isClientSide && soundPlaying && (isDisabled() || !getBlockState().getValue(AlarmBlock.LIT)))
			stopPlayingSound();

		if (!isDisabled() && isPowered && --cooldown <= 0) { //Even though isPowered is only explicitly set serverside, it is always synched to the client via NBT due to the block state change
			if (!level.isClientSide) {
				double rangeSqr = Math.pow(range.get(), 2);
				SoundEvent soundEvent = isModuleEnabled(ModuleType.SMART) ? sound : SCSounds.ALARM.event;

				for (ServerPlayer player : ((ServerLevel) level).getPlayers(p -> p.blockPosition().distSqr(pos) <= rangeSqr)) {
					float volume = (float) (1.0F - ((player.blockPosition().distSqr(pos)) / rangeSqr));

					SecurityCraft.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new PlayAlarmSound(worldPosition, soundEvent, volume, getPitch()));
				}
			}

			setCooldown(soundLength * 20);
		}
	}

	@Override
	public void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		tag.putInt("cooldown", cooldown);
		tag.putBoolean("isPowered", isPowered);
		tag.putString("sound", sound.getLocation().toString());
		tag.putFloat("pitch", pitch);
		tag.putInt("delay", soundLength);
	}

	@Override
	public void load(CompoundTag tag) {
		super.load(tag);

		cooldown = tag.getInt("cooldown");
		isPowered = tag.getBoolean("isPowered");

		if (tag.contains("sound", Tag.TAG_STRING))
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
		this.soundLength = Mth.clamp(soundLength, 1, MAXIMUM_ALARM_SOUND_LENGTH);
		setCooldown(0);

		if (level.isClientSide)
			stopPlayingSound();
	}

	public void setCooldown(int cooldown) {
		this.cooldown = cooldown;
		setChanged();
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
		AlarmSoundHandler.playSound(this, x, y, z, sound, SoundSource.BLOCKS, volume, pitch);
		soundPlaying = true;
	}

	public void stopPlayingSound() {
		AlarmSoundHandler.stopCurrentSound(this);
		soundPlaying = false;
	}
}
