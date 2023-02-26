package net.geforcemods.securitycraft.blockentities;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.CustomizableBlockEntity;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.IntOption;
import net.geforcemods.securitycraft.blocks.AlarmBlock;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.misc.SCSounds;
import net.geforcemods.securitycraft.network.client.PlayAlarmSound;
import net.geforcemods.securitycraft.util.AlarmSoundHandler;
import net.geforcemods.securitycraft.util.ITickingBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.network.PacketDistributor;

public class AlarmBlockEntity extends CustomizableBlockEntity implements ITickingBlockEntity {
	private IntOption range = new IntOption(this::getBlockPos, "range", 17, 0, ConfigHandler.SERVER.maxAlarmRange.get(), 1, true);
	private IntOption delay = new IntOption(this::getBlockPos, "delay", 2, 1, 30, 1, true);
	private int cooldown = 0;
	private boolean isPowered = false;
	private SoundEvent sound = SCSounds.ALARM.event;
	private boolean soundPlaying = false;

	public AlarmBlockEntity(BlockPos pos, BlockState state) {
		super(SCContent.ALARM_BLOCK_ENTITY.get(), pos, state);
	}

	@Override
	public void tick(Level level, BlockPos pos, BlockState state) {
		if (level.isClientSide) {
			if (soundPlaying && !getBlockState().getValue(AlarmBlock.LIT))
				stopPlayingSound();
		}
		else if (isPowered && --cooldown <= 0) {
			double rangeSqr = Math.pow(range.get(), 2);
			Holder<SoundEvent> soundEventHolder = BuiltInRegistries.SOUND_EVENT.wrapAsHolder(isModuleEnabled(ModuleType.SMART) ? sound : SCSounds.ALARM.event);

			for (ServerPlayer player : ((ServerLevel) level).getPlayers(p -> p.blockPosition().distSqr(pos) <= rangeSqr)) {
				float volume = (float) (1.0F - ((player.blockPosition().distSqr(pos)) / rangeSqr));

				SecurityCraft.channel.send(PacketDistributor.PLAYER.with(() -> player), new PlayAlarmSound(worldPosition, soundEventHolder, volume, player.getCommandSenderWorld().random.nextLong()));
			}

			setCooldown(delay.get() * 20);
		}
	}

	@Override
	public void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		tag.putInt("cooldown", cooldown);
		tag.putBoolean("isPowered", isPowered);
		tag.putString("sound", sound.getLocation().toString());
	}

	@Override
	public void load(CompoundTag tag) {
		super.load(tag);

		cooldown = tag.getInt("cooldown");
		isPowered = tag.getBoolean("isPowered");
		setSound(new ResourceLocation(tag.getString("sound")));
	}

	public void setSound(ResourceLocation soundEvent) {
		sound = SoundEvent.createVariableRangeEvent(soundEvent);
		setChanged();
	}

	public SoundEvent getSound() {
		return isModuleEnabled(ModuleType.SMART) ? sound : SCSounds.ALARM.event;
	}

	public void setCooldown(int cooldown) {
		this.cooldown = cooldown;
		setChanged();
	}

	public boolean isPowered() {
		return isPowered;
	}

	public void setPowered(boolean isPowered) {
		this.isPowered = isPowered;
		setChanged();
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
				range, delay
		};
	}

	@Override
	public void setRemoved() {
		super.setRemoved();

		if (level.isClientSide && soundPlaying)
			stopPlayingSound();
	}

	public void playSound(Level level, double x, double y, double z, Holder<SoundEvent> sound, float volume, long seed) {
		AlarmSoundHandler.playSound(this, level, x, y, z, sound, SoundSource.BLOCKS, volume, 1.0F, seed);
		soundPlaying = true;
	}

	public void stopPlayingSound() {
		AlarmSoundHandler.stopCurrentSound(this);
		soundPlaying = false;
	}
}
