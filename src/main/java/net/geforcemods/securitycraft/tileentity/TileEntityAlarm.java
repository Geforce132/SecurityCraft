package net.geforcemods.securitycraft.tileentity;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.api.CustomizableSCTE;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.OptionInt;
import net.geforcemods.securitycraft.blocks.BlockAlarm;
import net.geforcemods.securitycraft.misc.EnumModuleType;
import net.geforcemods.securitycraft.misc.SCSounds;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.SoundCategory;

public class TileEntityAlarm extends CustomizableSCTE {

	public OptionInt range = new OptionInt(this::getPos, "range", 17, 0, ConfigHandler.maxAlarmRange, 1, true);
	private OptionInt delay = new OptionInt(this::getPos, "delay", 2, 1, 30, 1, true);
	private int cooldown = 0;
	private boolean isPowered = false;

	@Override
	public void update(){
		if(cooldown > 0)
			cooldown--;

		if(isPowered && cooldown == 0)
		{
			TileEntityAlarm te = (TileEntityAlarm) world.getTileEntity(pos);

			for(EntityPlayer player : world.getPlayers(EntityPlayer.class, p -> p.getPosition().distanceSq(pos) <= Math.pow(range.get(), 2)))
			{
				world.playSound(player, player.getPosition(), SCSounds.ALARM.event, SoundCategory.BLOCKS, 0.3F, 1.0F);
			}

			te.setCooldown(delay.get() * 20);
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
		cooldown = tag.getInteger("cooldown");
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
	public EnumModuleType[] acceptedModules()
	{
		return new EnumModuleType[]{};
	}

	@Override
	public Option<?>[] customOptions()
	{
		return new Option[]{ range, delay };
	}
}
