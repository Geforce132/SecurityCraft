package net.geforcemods.securitycraft.tileentity;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.api.CustomizableSCTE;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.OptionInt;
import net.geforcemods.securitycraft.blocks.BlockAlarm;
import net.geforcemods.securitycraft.misc.EnumCustomModules;
import net.geforcemods.securitycraft.misc.SCSounds;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.SoundCategory;

public class TileEntityAlarm extends CustomizableSCTE {

	public OptionIntConfig range = new OptionIntConfig(this, "range", 17, 0, 1, true);
	private int cooldown = 0;
	private boolean isPowered = false;

	@Override
	public void update(){
		if(cooldown > 0)
			cooldown--;

		if(isPowered && cooldown == 0)
		{
			TileEntityAlarm te = (TileEntityAlarm) world.getTileEntity(pos);

			for(EntityPlayer player : world.getPlayers(EntityPlayer.class, p -> p.getPosition().distanceSq(pos) <= Math.pow(range.asInteger(), 2)))
			{
				world.playSound(player, player.getPosition(), SCSounds.ALARM.event, SoundCategory.BLOCKS, ConfigHandler.alarmSoundVolume, 1.0F);
			}

			te.setCooldown((ConfigHandler.alarmTickDelay * 20));
			world.setBlockState(pos, world.getBlockState(pos).withProperty(BlockAlarm.FACING, world.getBlockState(pos).getValue(BlockAlarm.FACING)), 2);
			world.setTileEntity(pos, te);
		}
	}

	/**
	 * Writes a tile entity to NBT.
	 * @return
	 */
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag)
	{
		super.writeToNBT(tag);
		tag.setInteger("cooldown", cooldown);
		tag.setBoolean("isPowered", isPowered);
		return tag;
	}

	/**
	 * Reads a tile entity from NBT.
	 */
	@Override
	public void readFromNBT(NBTTagCompound tag)
	{
		super.readFromNBT(tag);

		if (tag.hasKey("cooldown"))
			cooldown = tag.getInteger("cooldown");

		if (tag.hasKey("isPowered"))
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
	public EnumCustomModules[] acceptedModules()
	{
		return new EnumCustomModules[]{};
	}

	@Override
	public Option<?>[] customOptions()
	{
		return new Option[]{ range };
	}

	public static class OptionIntConfig extends OptionInt
	{
		public OptionIntConfig(CustomizableSCTE te, String optionName, Integer value, Integer min, Integer increment, boolean s)
		{
			super(te, optionName, value, min, 0, increment, s);
		}

		@Override
		public Integer getMax()
		{
			return ConfigHandler.maxAlarmRange;
		}
	}
}
