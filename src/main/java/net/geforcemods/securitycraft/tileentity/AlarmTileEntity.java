package net.geforcemods.securitycraft.tileentity;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.blocks.AlarmBlock;
import net.geforcemods.securitycraft.misc.SCSounds;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;

public class AlarmTileEntity extends OwnableTileEntity {

	private int cooldown = 0;
	private boolean isPowered = false;

	public AlarmTileEntity()
	{
		super(SCContent.teTypeAlarm);
	}

	@Override
	public void tick(){
		if(world.isRemote)
			return;
		else{
			if(cooldown > 0){
				cooldown--;
			}

			if(isPowered && cooldown == 0){
				AlarmTileEntity te = (AlarmTileEntity) world.getTileEntity(pos);
				getWorld().playSound(null, new BlockPos(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D), SCSounds.ALARM.event, SoundCategory.PLAYERS, ConfigHandler.CONFIG.alarmSoundVolume.get().floatValue(), 1.0F);
				te.setCooldown((ConfigHandler.CONFIG.alarmTickDelay.get() * 20));
				world.setBlockState(pos, world.getBlockState(pos).with(AlarmBlock.FACING, world.getBlockState(pos).get(AlarmBlock.FACING)), 2);
				world.setTileEntity(pos, te);
			}
		}
	}

	/**
	 * Writes a tile entity to NBT.
	 * @return
	 */
	@Override
	public CompoundNBT write(CompoundNBT tag)
	{
		super.write(tag);
		tag.putInt("cooldown", cooldown);
		tag.putBoolean("isPowered", isPowered);
		return tag;
	}

	/**
	 * Reads a tile entity from NBT.
	 */
	@Override
	public void read(CompoundNBT tag)
	{
		super.read(tag);

		if (tag.contains("cooldown"))
			cooldown = tag.getInt("cooldown");

		if (tag.contains("isPowered"))
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

}
