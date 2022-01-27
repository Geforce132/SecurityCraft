package net.geforcemods.securitycraft.tileentity;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.CustomizableTileEntity;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.IntOption;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.misc.SCSounds;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.server.ServerWorld;

public class AlarmTileEntity extends CustomizableTileEntity implements ITickableTileEntity {
	private IntOption range = new IntOption(this::getBlockPos, "range", 17, 0, ConfigHandler.SERVER.maxAlarmRange.get(), 1, true);
	private IntOption delay = new IntOption(this::getBlockPos, "delay", 2, 1, 30, 1, true);
	private int cooldown = 0;
	private boolean isPowered = false;

	public AlarmTileEntity() {
		super(SCContent.teTypeAlarm);
	}

	@Override
	public void tick() {
		if (!level.isClientSide) {
			if (isPowered && --cooldown <= 0) {
				for (ServerPlayerEntity player : ((ServerWorld) level).getPlayers(p -> p.getCommandSenderBlockPosition().distSqr(worldPosition) <= Math.pow(range.get(), 2))) {
					player.playNotifySound(SCSounds.ALARM.event, SoundCategory.BLOCKS, 0.3F, 1.0F);
				}

				setCooldown(delay.get() * 20);
			}
		}
	}

	@Override
	public CompoundNBT save(CompoundNBT tag) {
		super.save(tag);
		tag.putInt("cooldown", cooldown);
		tag.putBoolean("isPowered", isPowered);
		return tag;
	}

	@Override
	public void load(CompoundNBT tag) {
		super.load(tag);

		cooldown = tag.getInt("cooldown");
		isPowered = tag.getBoolean("isPowered");
	}

	public void setCooldown(int cooldown) {
		this.cooldown = cooldown;
	}

	public boolean isPowered() {
		return isPowered;
	}

	public void setPowered(boolean isPowered) {
		this.isPowered = isPowered;
	}

	@Override
	public ModuleType[] acceptedModules() {
		return new ModuleType[] {};
	}

	@Override
	public Option<?>[] customOptions() {
		return new Option[] {
				range, delay
		};
	}
}
