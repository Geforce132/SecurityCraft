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
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.registries.ForgeRegistries;

public class AlarmBlockEntity extends CustomizableBlockEntity implements ITickableTileEntity {
	public static final int MAXIMUM_ALARM_SOUND_LENGTH = 3600; //one hour
	private IntOption range = new IntOption(this::getBlockPos, "range", 17, 0, ConfigHandler.SERVER.maxAlarmRange.get(), 1, true);
	private DisabledOption disabled = new DisabledOption(false);
	private BooleanOption resetCooldown = new BooleanOption("resetCooldown", false);
	private int cooldown = 0;
	private boolean isPowered = false;
	private SoundEvent sound = SCSounds.ALARM.event;
	private boolean soundPlaying = false;
	private int soundLength = 2;

	public AlarmBlockEntity() {
		super(SCContent.ALARM_BLOCK_ENTITY.get());
	}

	@Override
	public void tick() {
		if (level.isClientSide) {
			if (soundPlaying && (isDisabled() || !isPowered))
				stopPlayingSound();
		}
		else {
			if (!isDisabled() && --cooldown <= 0) {
				if (isPowered) {
					double rangeSqr = Math.pow(range.get(), 2);
					SoundEvent soundEvent = isModuleEnabled(ModuleType.SMART) ? sound : SCSounds.ALARM.event;

					for (ServerPlayerEntity player : ((ServerWorld) level).getPlayers(p -> p.blockPosition().distSqr(worldPosition) <= rangeSqr)) {
						float volume = (float) (1.0F - ((player.blockPosition().distSqr(worldPosition)) / rangeSqr));

						SecurityCraft.channel.send(PacketDistributor.PLAYER.with(() -> player), new PlayAlarmSound(worldPosition, soundEvent, volume));
					}
				}

				setCooldown(soundLength * 20);
			}
		}
	}

	@Override
	public CompoundNBT save(CompoundNBT tag) {
		super.save(tag);
		tag.putInt("cooldown", cooldown);
		tag.putBoolean("isPowered", isPowered);
		tag.putString("sound", sound.location.toString());
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

		soundLength = tag.getInt("delay");
	}

	public void setSound(ResourceLocation soundEvent) {
		sound = ForgeRegistries.SOUND_EVENTS.getValue(soundEvent);
		setChanged();
	}

	public SoundEvent getSound() {
		return isModuleEnabled(ModuleType.SMART) ? sound : SCSounds.ALARM.event;
	}

	public int getSoundLength() {
		return soundLength;
	}

	public void setSoundLength(int soundLength) {
		this.soundLength = MathHelper.clamp(soundLength, 1, MAXIMUM_ALARM_SOUND_LENGTH);
		stopPlayingSound();
		setCooldown(0);
	}

	public void setCooldown(int cooldown) {
		this.cooldown = cooldown;
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

	public void playSound(World level, double x, double y, double z, SoundEvent sound, float volume) {
		AlarmSoundHandler.playSound(this, level, x, y, z, sound, SoundCategory.BLOCKS, volume, 1.0F);
		soundPlaying = true;
	}

	public void stopPlayingSound() {
		AlarmSoundHandler.stopCurrentSound(this);
		soundPlaying = false;
	}
}
