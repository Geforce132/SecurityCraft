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
import net.geforcemods.securitycraft.blocks.OldLitAlarmBlock;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.misc.SCSounds;
import net.geforcemods.securitycraft.network.client.PlayAlarmSound;
import net.geforcemods.securitycraft.util.AlarmSoundHandler;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.util.Constants;

public class AlarmBlockEntity extends CustomizableBlockEntity implements ITickable {
	public static final int MAXIMUM_ALARM_SOUND_LENGTH = 3600; //one hour
	private IntOption range = new IntOption(this::getPos, "range", 17, 0, ConfigHandler.maxAlarmRange, 1);
	private DisabledOption disabled = new DisabledOption(false);
	private BooleanOption resetCooldown = new BooleanOption("resetCooldown", true);
	private int cooldown = 0;
	private boolean isPowered = false;
	private ResourceLocation sound = SCSounds.ALARM.location;
	private float pitch = 1.0F;
	private boolean soundPlaying = false;
	private int soundLength = 2;

	@Override
	public void update() {
		//convert the old lit alarm block to the old unlit alarm block, which now has a LIT property
		if (getBlockType() == SCContent.alarmLit) {
			world.setBlockState(pos, SCContent.alarm.getDefaultState().withProperty(AlarmBlock.FACING, world.getBlockState(pos).getValue(OldLitAlarmBlock.FACING)).withProperty(AlarmBlock.LIT, false));

			AlarmBlockEntity newTe = (AlarmBlockEntity) world.getTileEntity(pos);

			newTe.getOwner().set(getOwner().getUUID(), getOwner().getName());
			newTe.range.copy(range);
			newTe.soundLength = soundLength;
			newTe.setPowered(false);
			invalidate();
			return;
		}

		if (world.isRemote && soundPlaying && (isDisabled() || !isPowered))
			stopPlayingSound();

		if (!isDisabled() && isPowered && --cooldown <= 0) { //Even though isPowered is only explicitly set serverside, it is always synched to the client via NBT due to the block state change
			if (!world.isRemote) {
				double rangeSqr = Math.pow(range.get(), 2);
				ResourceLocation soundEvent = getSound();

				for (EntityPlayerMP player : world.getPlayers(EntityPlayerMP.class, p -> p.getPosition().distanceSq(pos) <= rangeSqr)) {
					float volume = (float) (1.0F - ((player.getPosition().distanceSq(pos)) / rangeSqr));

					SecurityCraft.network.sendTo(new PlayAlarmSound(pos, soundEvent, volume, pitch), player);
				}
			}

			setCooldown(soundLength * 20);
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setInteger("cooldown", cooldown);
		tag.setBoolean("isPowered", isPowered);
		tag.setString("sound", sound.toString());
		tag.setFloat("pitch", pitch);
		tag.setInteger("delay", soundLength);
		return tag;
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		cooldown = tag.getInteger("cooldown");
		isPowered = tag.getBoolean("isPowered");

		if (tag.hasKey("sound", Constants.NBT.TAG_STRING))
			setSound(new ResourceLocation(tag.getString("sound")));
		else
			setSound(SCSounds.ALARM.location);

		if (tag.hasKey("pitch"))
			pitch = tag.getFloat("pitch");
		else
			pitch = 1.0F;

		soundLength = tag.getInteger("delay");
	}

	public void setSound(ResourceLocation soundEvent) {
		sound = soundEvent;
		markDirty();
	}

	public ResourceLocation getSound() {
		return isModuleEnabled(ModuleType.SMART) ? sound : SCSounds.ALARM.location;
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

		if (world.isRemote)
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

		markDirty();
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
	public void invalidate() {
		super.invalidate();

		if (world.isRemote && soundPlaying)
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
