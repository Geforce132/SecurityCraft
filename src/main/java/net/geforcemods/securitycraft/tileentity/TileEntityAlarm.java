package net.geforcemods.securitycraft.tileentity;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.CustomizableSCTE;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.OptionInt;
import net.geforcemods.securitycraft.blocks.BlockAlarm;
import net.geforcemods.securitycraft.blocks.BlockOldLitAlarm;
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
		//convert the old lit alarm block to the old unlit alarm block, which now has a LIT property
		if(getBlockType() == SCContent.alarmLit)
		{
			world.setBlockState(pos, SCContent.alarm.getDefaultState().withProperty(BlockAlarm.FACING, world.getBlockState(pos).getValue(BlockOldLitAlarm.FACING)).withProperty(BlockAlarm.LIT, false));

			TileEntityAlarm newTe = (TileEntityAlarm)world.getTileEntity(pos);

			newTe.getOwner().set(getOwner().getUUID(), getOwner().getName());
			newTe.range.copy(range);
			newTe.delay.copy(delay);
			newTe.setPowered(false);
			invalidate();
			return;
		}

		if(isPowered && --cooldown <= 0)
		{
			for(EntityPlayer player : world.getPlayers(EntityPlayer.class, p -> p.getPosition().distanceSq(pos) <= Math.pow(range.get(), 2)))
			{
				world.playSound(player, player.getPosition(), SCSounds.ALARM.event, SoundCategory.BLOCKS, 0.3F, 1.0F);
			}

			setCooldown(delay.get() * 20);
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
