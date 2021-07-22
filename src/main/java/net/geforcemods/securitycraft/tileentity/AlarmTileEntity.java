package net.geforcemods.securitycraft.tileentity;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.CustomizableTileEntity;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.IntOption;
import net.geforcemods.securitycraft.blocks.AlarmBlock;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.misc.SCSounds;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.block.state.BlockState;

public class AlarmTileEntity extends CustomizableTileEntity {

	private IntOption range = new IntOption(this::getBlockPos, "range", 17, 0, ConfigHandler.SERVER.maxAlarmRange.get(), 1, true);
	private IntOption delay = new IntOption(this::getBlockPos, "delay", 2, 1, 30, 1, true);
	private int cooldown = 0;
	private boolean isPowered = false;

	public AlarmTileEntity()
	{
		super(SCContent.teTypeAlarm);
	}

	@Override
	public void tick(){
		if(!level.isClientSide)
		{
			if(cooldown > 0)
				cooldown--;

			if(isPowered && cooldown == 0)
			{
				AlarmTileEntity te = (AlarmTileEntity) level.getBlockEntity(worldPosition);

				for(ServerPlayer player : ((ServerLevel)level).getPlayers(p -> p.blockPosition().distSqr(worldPosition) <= Math.pow(range.get(), 2)))
				{
					player.playNotifySound(SCSounds.ALARM.event, SoundSource.BLOCKS, 0.3F, 1.0F);
				}

				te.setCooldown(delay.get() * 20);
				level.setBlock(worldPosition, level.getBlockState(worldPosition).setValue(AlarmBlock.FACING, level.getBlockState(worldPosition).getValue(AlarmBlock.FACING)), 2);
				level.setBlockEntity(worldPosition, te);
			}
		}

		requestModelDataUpdate();
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
	public void load(BlockState state, CompoundTag tag)
	{
		super.load(state, tag);

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
