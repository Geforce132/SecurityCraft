package net.geforcemods.securitycraft.blockentities;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.CustomizableBlockEntity;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.IntOption;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.misc.SCSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class AlarmBlockEntity extends CustomizableBlockEntity {

	private IntOption range = new IntOption(this::getBlockPos, "range", 17, 0, ConfigHandler.SERVER.maxAlarmRange.get(), 1, true);
	private IntOption delay = new IntOption(this::getBlockPos, "delay", 2, 1, 30, 1, true);
	private int cooldown = 0;
	private boolean isPowered = false;

	public AlarmBlockEntity(BlockPos pos, BlockState state)
	{
		super(SCContent.beTypeAlarm, pos, state);
	}

	@Override
	public void tick(Level world, BlockPos pos, BlockState state){ //server only as per AlarmBlock
		if(isPowered && --cooldown <= 0)
		{
			for(ServerPlayer player : ((ServerLevel)world).getPlayers(p -> p.blockPosition().distSqr(pos) <= Math.pow(range.get(), 2)))
			{
				player.playNotifySound(SCSounds.ALARM.event, SoundSource.BLOCKS, 0.3F, 1.0F);
			}

			setCooldown(delay.get() * 20);
		}
	}

	/**
	 * Writes a tile entity to NBT.
	 * @return
	 */
	@Override
	public CompoundTag save(CompoundTag tag)
	{
		super.save(tag);
		tag.putInt("cooldown", cooldown);
		tag.putBoolean("isPowered", isPowered);
		return tag;
	}

	/**
	 * Reads a tile entity from NBT.
	 */
	@Override
	public void load(CompoundTag tag)
	{
		super.load(tag);

		cooldown = tag.getInt("cooldown");
		isPowered = tag.getBoolean("isPowered");

	}

	public void setCooldown(int cooldown){
		this.cooldown = cooldown;
	}

	public boolean isPowered() {
		return isPowered;
	}

	public void setPowered(boolean isPowered) {
		this.isPowered = isPowered;
	}

	@Override
	public ModuleType[] acceptedModules()
	{
		return new ModuleType[]{};
	}

	@Override
	public Option<?>[] customOptions()
	{
		return new Option[]{ range, delay };
	}
}
