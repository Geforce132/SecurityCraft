package net.geforcemods.securitycraft.blockentities;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.CustomizableBlockEntity;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.IntOption;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.misc.SCSounds;
import net.geforcemods.securitycraft.util.ITickingBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class AlarmBlockEntity extends CustomizableBlockEntity implements ITickingBlockEntity {
	private IntOption range = new IntOption(this::getBlockPos, "range", 17, 0, ConfigHandler.SERVER.maxAlarmRange.get(), 1, true);
	private IntOption delay = new IntOption(this::getBlockPos, "delay", 2, 1, 30, 1, true);
	private int cooldown = 0;
	private boolean isPowered = false;

	public AlarmBlockEntity(BlockPos pos, BlockState state) {
		super(SCContent.ALARM_BLOCK_ENTITY.get(), pos, state);
	}

	@Override
	public void tick(Level level, BlockPos pos, BlockState state) { //server only as per AlarmBlock
		if (isPowered && --cooldown <= 0) {
			double rangeSqr = Math.pow(range.get(), 2);

			for (ServerPlayer player : ((ServerLevel) level).getPlayers(p -> p.blockPosition().distSqr(pos) <= rangeSqr)) {
				float volume = (float) (1.0F - ((player.blockPosition().distSqr(pos)) / rangeSqr));

				player.connection.send(new ClientboundSoundPacket(SCSounds.ALARM.event, SoundSource.BLOCKS, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), volume, 1.0F));
			}

			setCooldown(delay.get() * 20);
		}
	}

	@Override
	public void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		tag.putInt("cooldown", cooldown);
		tag.putBoolean("isPowered", isPowered);
	}

	@Override
	public void load(CompoundTag tag) {
		super.load(tag);

		cooldown = tag.getInt("cooldown");
		isPowered = tag.getBoolean("isPowered");
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
		return new ModuleType[] {};
	}

	@Override
	public Option<?>[] customOptions() {
		return new Option[] {
				range, delay
		};
	}
}
